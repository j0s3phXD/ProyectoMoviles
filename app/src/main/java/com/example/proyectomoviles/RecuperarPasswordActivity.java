package com.example.proyectomoviles;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.ActivityRecuperarPasswordBinding;
import com.example.proyectomoviles.model.auth.GeneralResponse;
import com.example.proyectomoviles.model.auth.RestablecerPasswordRequest;
import com.example.proyectomoviles.model.auth.SmsRequest; // Requerido para solicitar código
import com.example.proyectomoviles.model.auth.VerificationRequest; // Requerido para verificar código

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuperarPasswordActivity extends AppCompatActivity {

    private ActivityRecuperarPasswordBinding binding;
    private Swaply api;

    // Variables para guardar los datos
    private String telefono, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecuperarPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        api = RetrofitClient.getApiService();

        binding.btnEnviarCodigo.setOnClickListener(v -> iniciarRecuperacion());
    }

    private void iniciarRecuperacion() {
        email = binding.txtEmailRecuperar.getText().toString().trim();

        // Obtener y validar el teléfono (asumiendo lógica de +51 y 9 dígitos)
        String rawPhone = binding.txtTelefonoRecuperar.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa el correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rawPhone.length() != 9) {
            binding.ilTelefono.setError("El teléfono debe tener 9 dígitos");
            return;
        } else {
            binding.ilTelefono.setError(null);
        }

        // Concatenar y asignar el teléfono final
        telefono = "+51" + rawPhone;

        // LLAMADA 1: Solicitar código SMS al Backend
        Toast.makeText(this, "Enviando código SMS...", Toast.LENGTH_SHORT).show();
        SmsRequest request = new SmsRequest(telefono, "recuperacion");

        api.solicitarCodigo(request).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    mostrarDialogoCodigo(); // Muestra el Dialog para ingresar el código
                } else {
                    Toast.makeText(RecuperarPasswordActivity.this, "Error SMS: " + (response.body() != null ? response.body().getMessage() : "Fallo de servidor"), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(RecuperarPasswordActivity.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoCodigo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seguridad y Nueva Contraseña");
        builder.setMessage("Ingresa el código SMS y tu nueva contraseña.");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText inputCodigo = new EditText(this);
        inputCodigo.setHint("Código SMS (6 dígitos)");
        inputCodigo.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputCodigo);

        final EditText inputPass = new EditText(this);
        inputPass.setHint("Nueva Contraseña");
        inputPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputPass);

        builder.setView(layout);

        builder.setPositiveButton("Restablecer", (dialog, which) -> {
            String codigo = inputCodigo.getText().toString().trim();
            String nuevaPass = inputPass.getText().toString().trim();

            if (!codigo.isEmpty() && !nuevaPass.isEmpty()) {
                verificarCodigoEnBackend(codigo, nuevaPass);
            } else {
                Toast.makeText(this, "Faltan datos", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void verificarCodigoEnBackend(String codigo, String nuevaPass) {
        // LLAMADA 2: Verificar si el código es correcto
        VerificationRequest request = new VerificationRequest(telefono, codigo, "recuperacion");

        api.verificarCodigo(request).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    // CÓDIGO CORRECTO: LLAMADA 3: Guardar la nueva contraseña
                    enviarCambioAlBackend(codigo, nuevaPass);
                } else {
                    Toast.makeText(RecuperarPasswordActivity.this, "Código incorrecto o expirado", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(RecuperarPasswordActivity.this, "Error al verificar código: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarCambioAlBackend(String codigo, String nuevaPass) {
        // LLAMADA 3: Mandar la nueva contraseña al Backend para que haga el UPDATE
        RestablecerPasswordRequest request = new RestablecerPasswordRequest(email, codigo, nuevaPass, telefono);

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