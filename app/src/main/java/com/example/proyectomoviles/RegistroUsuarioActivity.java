package com.example.proyectomoviles;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.ActivityRegistroUsuarioBinding;
import com.example.proyectomoviles.model.auth.AuthRequest;
import com.example.proyectomoviles.model.auth.AuthResponse;
import com.example.proyectomoviles.model.auth.GeneralResponse;
import com.example.proyectomoviles.model.auth.RegistroRequest;
import com.example.proyectomoviles.model.auth.RegistroResponse;
import com.example.proyectomoviles.model.auth.SmsRequest;
import com.example.proyectomoviles.model.auth.VerificationRequest;
import com.example.proyectomoviles.model.usuario.Usuario;
import com.example.proyectomoviles.model.usuario.UsuarioResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private ActivityRegistroUsuarioBinding binding;
    private Swaply api;

    // Variables temporales para reutilizarlas en login automático
    private String tempNombre, tempApellido, tempEmail, tempPassword, tempTelefono, tempDni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        api = RetrofitClient.getApiService();

        binding.btnRegistrar.setOnClickListener(v -> iniciarFlujoRegistro());
    }

    private void iniciarFlujoRegistro() {
        // CAPTURA DE DATOS DEL FORM
        tempNombre = binding.txtNombre.getText().toString().trim();
        tempApellido = binding.txtApellidos.getText().toString().trim();
        tempEmail = binding.txtEmail.getText().toString().trim();
        tempPassword = binding.txtPassword.getText().toString().trim();
        String confirmPassword = binding.txtConfirmPassword.getText().toString().trim();
        String rawPhone = binding.txtTelefono.getText().toString().trim();
        tempDni = binding.txtDni.getText().toString().trim();  // 👈 asumiendo que creaste txtDni en el XML

        // VALIDACIONES
        if (tempNombre.isEmpty() || tempApellido.isEmpty() || tempEmail.isEmpty()
                || tempPassword.isEmpty() || rawPhone.isEmpty()
                || confirmPassword.isEmpty() || tempDni.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!tempPassword.equals(confirmPassword)) {
            binding.ilConfirmPassword.setError("Las contraseñas no coinciden");
            return;
        } else {
            binding.ilConfirmPassword.setError(null);
        }

        if (rawPhone.length() != 9) {
            binding.txtILTelefono.setError("El teléfono debe tener 9 dígitos");
            return;
        } else {
            binding.txtILTelefono.setError(null);
        }

        // Validación DNI: 8 dígitos numéricos
        if (tempDni.length() != 8 || !tempDni.matches("\\d+")) {
            Toast.makeText(this, "El DNI debe tener 8 dígitos numéricos", Toast.LENGTH_SHORT).show();
            return;
        }

        tempTelefono = "+51" + rawPhone;

        // 1) SOLICITAR CÓDIGO SMS
        Toast.makeText(this, "Enviando código SMS...", Toast.LENGTH_SHORT).show();
        SmsRequest request = new SmsRequest(tempTelefono, "registro");

        api.solicitarCodigo(request).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getCode() == 1) {
                        mostrarDialogoVerificacion();
                    } else {
                        Toast.makeText(RegistroUsuarioActivity.this,
                                "Error SMS: " + response.body().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegistroUsuarioActivity.this,
                            "Error de servidor al enviar SMS",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(RegistroUsuarioActivity.this,
                        "Fallo de conexión al enviar SMS",
                        Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RegistroUsuarioActivity.this,
                        "Debes ingresar el código",
                        Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void verificarCodigoEnBackend(String codigo) {
        VerificationRequest request = new VerificationRequest(tempTelefono, codigo, "registro");

        api.verificarCodigo(request).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 1) {
                    registrarUsuarioFinal();
                } else {
                    Toast.makeText(RegistroUsuarioActivity.this,
                            "Código incorrecto o expirado",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(RegistroUsuarioActivity.this,
                        "Error al verificar código",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrarUsuarioFinal() {
        // 3) REGISTRO FINAL EN BD (AHORA CON DNI)
        RegistroRequest registroRequest =
                new RegistroRequest(tempNombre, tempApellido, tempEmail,
                        tempPassword, tempTelefono, tempDni);

        Log.d("REGISTRO_DEBUG", "Enviando a Python: " + tempEmail);

        api.registrarUsuario(registroRequest).enqueue(new Callback<RegistroResponse>() {
            @Override
            public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegistroUsuarioActivity.this,
                            "¡Registro Exitoso! Iniciando sesión...",
                            Toast.LENGTH_LONG).show();

                    // 🔥 LOGIN AUTOMÁTICO DESPUÉS DEL REGISTRO
                    loginAutomatico();
                } else {
                    Toast.makeText(RegistroUsuarioActivity.this,
                            "Error guardando en BD: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegistroResponse> call, Throwable t) {
                Toast.makeText(RegistroUsuarioActivity.this,
                        "Fallo conexión BD",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================================
    // LOGIN AUTOMÁTICO POST-REGISTRO
    // ================================
    private void loginAutomatico() {

        Swaply apiNoAuth = RetrofitClient.getApiService();

        AuthRequest authRequest = new AuthRequest(tempEmail, tempPassword);

        apiNoAuth.obtenerToken(authRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RegistroUsuarioActivity.this,
                            "No se pudo iniciar sesión automáticamente",
                            Toast.LENGTH_SHORT).show();
                    // Si falla, lo mandamos al login normal
                    startActivity(new Intent(RegistroUsuarioActivity.this, LoginActivity.class));
                    finish();
                    return;
                }

                AuthResponse auth = response.body();

                SharedPreferences sp = getSharedPreferences("SP_SWAPLY", MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("tokenJWT", auth.getAccess_token());
                ed.putInt("idUsuario", auth.getId_usuario());
                ed.apply();

                // Ahora cargamos los datos del usuario (incluye DNI)
                cargarInfoUsuario(auth.getId_usuario());
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegistroUsuarioActivity.this,
                        "Error de conexión al iniciar sesión",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegistroUsuarioActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    // ================================
    // CARGAR USUARIO Y GUARDAR DNI
    // ================================
    private void cargarInfoUsuario(int idUsuario) {

        Swaply apiNoAuth = RetrofitClient.getApiService();

        apiNoAuth.obtenerUsuario(idUsuario).enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RegistroUsuarioActivity.this,
                            "No se pudo cargar datos del usuario",
                            Toast.LENGTH_SHORT).show();
                    irAlHome();
                    return;
                }

                Usuario u = response.body().getUsuario();
                if (u != null) {
                    SharedPreferences sp = getSharedPreferences("SP_SWAPLY", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putString("nombreUsuario", u.getNombre());
                    ed.putString("apellidoUsuario", u.getApellido());
                    ed.putString("emailUsuario", u.getEmail());
                    ed.putString("dniUsuario", u.getDni());  // 👈 CLAVE PARA LOGIN CON FOTO
                    ed.apply();
                }

                irAlHome();
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                Toast.makeText(RegistroUsuarioActivity.this,
                        "Error cargando información del usuario",
                        Toast.LENGTH_SHORT).show();
                irAlHome();
            }
        });
    }

    private void irAlHome() {
        Intent intent = new Intent(RegistroUsuarioActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
