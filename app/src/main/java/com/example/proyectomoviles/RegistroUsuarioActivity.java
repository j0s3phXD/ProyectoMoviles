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
import com.example.proyectomoviles.databinding.ActivityRegistroUsuarioBinding;
import com.example.proyectomoviles.model.auth.RegistroRequest;
import com.example.proyectomoviles.model.auth.RegistroResponse;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private ActivityRegistroUsuarioBinding binding;
    private FirebaseAuth mAuth; // Cliente de Firebase
    private Swaply api;         // Tu API de Python
    private String mVerificationId; // Guardará el ID que nos da Google

    // Variables temporales
    private String tempNombre, tempApellido, tempEmail, tempPassword, tempTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Inicializamos Firebase y Retrofit
        mAuth = FirebaseAuth.getInstance();
        api = RetrofitClient.getApiService();

        // Al hacer clic, iniciamos el flujo con Firebase
        binding.btnRegistrar.setOnClickListener(v -> iniciarValidacionFirebase());
    }

    private void iniciarValidacionFirebase() {
        tempNombre = binding.txtNombre.getText().toString().trim();
        tempApellido = binding.txtApellidos.getText().toString().trim();
        tempEmail = binding.txtEmail.getText().toString().trim();
        tempPassword = binding.txtPassword.getText().toString().trim();

        // 1. Capturar la confirmación de contraseña
        String confirmPassword = binding.txtConfirmPassword.getText().toString().trim();

        // 2. Capturamos lo que escribió el usuario (Solo los 9 dígitos)
        String rawPhone = binding.txtTelefono.getText().toString().trim();

        // Validaciones básicas de campos vacíos (Agregamos confirmPassword)
        if (tempNombre.isEmpty() || tempApellido.isEmpty() || tempEmail.isEmpty() || tempPassword.isEmpty() || rawPhone.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- NUEVA VALIDACIÓN: ¿LAS CONTRASEÑAS COINCIDEN? ---
        if (!tempPassword.equals(confirmPassword)) {
            // Marcamos error en el campo de confirmación
            binding.ilConfirmPassword.setError("Las contraseñas no coinciden");
            return; // Cortamos aquí, no seguimos
        } else {
            binding.ilConfirmPassword.setError(null); // Limpiamos error si ya lo arregló
        }
        // -----------------------------------------------------

        // 3. VALIDAR LONGITUD EXACTA (9 DÍGITOS)
        if (rawPhone.length() != 9) {
            binding.txtILTelefono.setError("El teléfono debe tener 9 dígitos");
            return;
        } else {
            binding.txtILTelefono.setError(null);
        }

        // 4. CONCATENAR PREFIJO MANUALMENTE
        tempTelefono = "+51" + rawPhone;

        // 5. PEDIR A FIREBASE QUE ENVÍE EL SMS
        Toast.makeText(this, "Validando número con Google...", Toast.LENGTH_SHORT).show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(tempTelefono)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // --- EVENTOS DE FIREBASE ---
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            // Verificación automática (a veces pasa). Iniciamos sesión directo.
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // Falló (SHA-1 mal puesto, sin internet, consola mal configurada)
            Toast.makeText(RegistroUsuarioActivity.this, "Error Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("FIREBASE_ERROR", "Detalle: " + e.getMessage());
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // 5. SMS ENVIADO CORRECTAMENTE
            // Guardamos el ID y mostramos el cuadro para que el usuario ponga el código
            mVerificationId = verificationId;
            mostrarDialogoCodigo();
        }
    };

    private void mostrarDialogoCodigo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Código de Verificación");
        builder.setMessage("Ingresa el código SMS de 6 dígitos enviado por Google");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Verificar", (dialog, which) -> {
            String code = input.getText().toString().trim();
            if (!code.isEmpty()) {
                // Creamos la credencial con el código del usuario y el ID guardado
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        // 6. VERIFICAMOS EL CÓDIGO CON FIREBASE
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // ====================================================
                        // 7. ¡ÉXITO TOTAL! EL NÚMERO ES REAL.
                        // AHORA SÍ GUARDAMOS EN TU BASE DE DATOS (PYTHON)
                        // ====================================================
                        registrarUsuarioEnBackend();
                    } else {
                        Toast.makeText(RegistroUsuarioActivity.this, "Código Incorrecto", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Este método envía los datos a tu servidor Python
    private void registrarUsuarioEnBackend() {
        RegistroRequest request = new RegistroRequest(tempNombre, tempApellido, tempEmail, tempPassword, tempTelefono);

        android.util.Log.d("REGISTRO_DEBUG", "Enviando a Python: " + tempEmail);

        api.registrarUsuario(request).enqueue(new Callback<RegistroResponse>() {
            @Override
            public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegistroUsuarioActivity.this, "¡Registro Exitoso!", Toast.LENGTH_LONG).show();

                    // Ir al Login
                    startActivity(new Intent(RegistroUsuarioActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegistroUsuarioActivity.this, "Error guardando en BD: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegistroResponse> call, Throwable t) {
                Toast.makeText(RegistroUsuarioActivity.this, "Fallo conexión BD", Toast.LENGTH_SHORT).show();
                Log.e("REGISTRO_BD", t.getMessage());
            }
        });
    }

}