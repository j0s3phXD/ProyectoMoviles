package com.example.proyectomoviles;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.ActivityRecuperarPasswordBinding;
import com.example.proyectomoviles.model.auth.GeneralResponse;
import com.example.proyectomoviles.model.auth.RestablecerPasswordRequest;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuperarPasswordActivity extends AppCompatActivity {

    private ActivityRecuperarPasswordBinding binding;
    private FirebaseAuth mAuth;
    private Swaply api;
    private String mVerificationId;

    // Variables para guardar los datos
    private String telefono, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecuperarPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        api = RetrofitClient.getApiService();

        // Botón principal
        binding.btnEnviarCodigo.setOnClickListener(v -> iniciarRecuperacion());
    }

    private void iniciarRecuperacion() {
        email = binding.txtEmailRecuperar.getText().toString().trim();

        // 1. Obtener lo que escribió el usuario (Solo los 9 dígitos)
        String rawPhone = binding.txtTelefonoRecuperar.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa el correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Validar que sean exactamente 9 dígitos
        if (rawPhone.length() != 9) {
            binding.ilTelefono.setError("El teléfono debe tener 9 dígitos");
            return;
        } else {
            binding.ilTelefono.setError(null); // Limpiar error visual
        }

        // 3. Concatenar manualmente el prefijo +51
        telefono = "+51" + rawPhone;

        // 4. INICIAR VERIFICACIÓN CON FIREBASE
        Toast.makeText(this, "Verificando número...", Toast.LENGTH_SHORT).show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(telefono)          // Enviamos el número completo (+519...)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // --- CALLBACKS DE FIREBASE ---
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            // Verificación automática
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(RecuperarPasswordActivity.this, "Error Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("RECUPERAR_ERROR", e.getMessage());
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // 2. SMS ENVIADO -> PEDIR CÓDIGO
            mVerificationId = verificationId;
            mostrarDialogoCodigo();
        }
    };

    private void mostrarDialogoCodigo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Código de Seguridad");
        builder.setMessage("Ingresa el código SMS que te llegó");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Verificar", (dialog, which) -> {
            String code = input.getText().toString().trim();
            if (!code.isEmpty()) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 3. ¡ÉXITO! ERES EL DUEÑO DEL TELÉFONO.
                        // AHORA TE PIDO LA NUEVA CONTRASEÑA
                        mostrarDialogoNuevaPassword();
                    } else {
                        Toast.makeText(RecuperarPasswordActivity.this, "Código Incorrecto", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- EL PASO EXTRA: PEDIR NUEVA CONTRASEÑA ---
    private void mostrarDialogoNuevaPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restablecer Contraseña");
        builder.setMessage("Escribe tu nueva contraseña");

        final EditText inputPass = new EditText(this);
        inputPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(inputPass);

        builder.setPositiveButton("Cambiar", (dialog, which) -> {
            String nuevaPass = inputPass.getText().toString().trim();
            if (!nuevaPass.isEmpty()) {
                // 4. MANDAR AL BACKEND PYTHON
                enviarCambioAlBackend(nuevaPass);
            } else {
                Toast.makeText(RecuperarPasswordActivity.this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setCancelable(false); // Obligamos a que cambie o cierre la app
        builder.show();
    }

    private void enviarCambioAlBackend(String nuevaPass) {
        // Creamos el objeto con los datos que Python espera
        RestablecerPasswordRequest request = new RestablecerPasswordRequest(email, "FIREBASE_VERIFIED", nuevaPass, telefono);

        api.restablecerPassword(request).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().getCode() == 1) {
                        Toast.makeText(RecuperarPasswordActivity.this, "¡Contraseña Actualizada!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RecuperarPasswordActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RecuperarPasswordActivity.this, "Error: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RecuperarPasswordActivity.this, "Error del Servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(RecuperarPasswordActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}