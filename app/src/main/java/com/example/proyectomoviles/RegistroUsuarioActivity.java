package com.example.proyectomoviles;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.ActivityRegistroUsuarioBinding;
import com.example.proyectomoviles.model.auth.GeneralResponse;
import com.example.proyectomoviles.model.auth.RegistroRequest;
import com.example.proyectomoviles.model.auth.RegistroResponse;
import com.example.proyectomoviles.model.auth.SmsRequest; // <-- TWILIO MODEL
import com.example.proyectomoviles.model.auth.VerificationRequest; // <-- TWILIO MODEL

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private ActivityRegistroUsuarioBinding binding;
    private Swaply api;

    private String tempNombre, tempApellido, tempEmail, tempPassword, tempTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        api = RetrofitClient.getApiService();

        binding.btnRegistrar.setOnClickListener(v -> iniciarFlujoRegistro());
    }

    private void iniciarFlujoRegistro() {
        // CAPTURA DE DATOS RAW
        tempNombre = binding.txtNombre.getText().toString().trim();
        tempApellido = binding.txtApellidos.getText().toString().trim();
        tempEmail = binding.txtEmail.getText().toString().trim();
        tempPassword = binding.txtPassword.getText().toString().trim();

        String confirmPassword = binding.txtConfirmPassword.getText().toString().trim();
        String rawPhone = binding.txtTelefono.getText().toString().trim();

        // VALIDACIONES DE FORMULARIO
        if (tempNombre.isEmpty() || tempApellido.isEmpty() || tempEmail.isEmpty() || tempPassword.isEmpty() || rawPhone.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // VALIDACIÓN DE COINCIDENCIA DE CONTRASEÑAS
        if (!tempPassword.equals(confirmPassword)) {
            binding.ilConfirmPassword.setError("Las contraseñas no coinciden");
            return;
        } else {
            binding.ilConfirmPassword.setError(null);
        }

        // VALIDACIÓN DE LONGITUD DEL TELÉFONO
        if (rawPhone.length() != 9) {
            binding.txtILTelefono.setError("El teléfono debe tener 9 dígitos");
            return;
        } else {
            binding.txtILTelefono.setError(null);
        }

        // CONCATENAR PREFIJO MANUALMENTE
        tempTelefono = "+51" + rawPhone;

        // LLAMADA 1: Solicitar código SMS al Backend (Twilio)
        Toast.makeText(this, "Enviando código SMS...", Toast.LENGTH_SHORT).show();
        SmsRequest request = new SmsRequest(tempTelefono, "registro");

        api.solicitarCodigo(request).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getCode() == 1) {
                        mostrarDialogoVerificacion(); // Muestra el Dialog para ingresar el código
                    } else {
                        Toast.makeText(RegistroUsuarioActivity.this, "Error SMS: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegistroUsuarioActivity.this, "Error de servidor.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(RegistroUsuarioActivity.this, "Fallo de conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoVerificacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verificación SMS");
        builder.setMessage("Ingresa el código de 6 dígitos enviado a " + tempTelefono);

        final EditText inputCodigo = new EditText(this);
        inputCodigo.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(inputCodigo);

        builder.setPositiveButton("Verificar", (dialog, which) -> {
            String codigoIngresado = inputCodigo.getText().toString().trim();
            if (!codigoIngresado.isEmpty()) {
                verificarCodigoEnBackend(codigoIngresado);
            } else {
                Toast.makeText(RegistroUsuarioActivity.this, "Debes ingresar el código", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void verificarCodigoEnBackend(String codigo) {
        // LLAMADA 2: Verificar si el código es correcto
        VerificationRequest request = new VerificationRequest(tempTelefono, codigo, "registro");

        api.verificarCodigo(request).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    registrarUsuarioFinal();
                } else {
                    Toast.makeText(RegistroUsuarioActivity.this, "Código incorrecto o expirado", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(RegistroUsuarioActivity.this, "Error al verificar código", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrarUsuarioFinal() {
        // LLAMADA 3: Registro final en la Base de Datos
        RegistroRequest registroRequest = new RegistroRequest(tempNombre, tempApellido, tempEmail, tempPassword, tempTelefono);

        Log.d("REGISTRO_DEBUG", "Enviando a Python: " + tempEmail);

        api.registrarUsuario(registroRequest).enqueue(new Callback<RegistroResponse>() {
            @Override
            public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegistroUsuarioActivity.this, "¡Registro Exitoso!", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(RegistroUsuarioActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegistroUsuarioActivity.this, "Error guardando en BD: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegistroResponse> call, Throwable t) {
                Toast.makeText(RegistroUsuarioActivity.this, "Fallo conexión BD", Toast.LENGTH_SHORT).show();
            }
        });
    }
}