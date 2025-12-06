package com.example.proyectomoviles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.ActivityLoginBinding;
import com.example.proyectomoviles.model.auth.AuthRequest;
import com.example.proyectomoviles.model.auth.AuthResponse;
import com.example.proyectomoviles.model.usuario.Usuario;
import com.example.proyectomoviles.model.usuario.UsuarioResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnLogin.setOnClickListener(v -> getToken());

        binding.btnResgistrar.setOnClickListener(v ->
                startActivity(new Intent(this, RegistroUsuarioActivity.class))
        );

        binding.txtOlvidastePass.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RecuperarPasswordActivity.class))
        );

        binding.btnLoginDni.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, DniActivity.class);
            startActivity(intent);
        });
    }

    private void getToken() {

        String email = binding.txtUsername.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa tu correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        Swaply api = RetrofitClient.getApiService();
        AuthRequest authRequest = new AuthRequest(email, password);

        api.obtenerToken(authRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {

                if (!response.isSuccessful()) {
                    if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                AuthResponse auth = response.body();
                if (auth == null) {
                    Toast.makeText(LoginActivity.this, "Respuesta vacía del servidor", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences sp = getSharedPreferences("SP_SWAPLY", MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("tokenJWT", auth.getAccess_token());
                ed.putInt("idUsuario", auth.getId_usuario());
                ed.apply();

                cargarInfoUsuario(auth.getId_usuario());
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarInfoUsuario(int idUsuario) {

        Swaply api = RetrofitClient.getApiService();

        api.obtenerUsuario(idUsuario).enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(LoginActivity.this, "No se pudo cargar información del usuario", Toast.LENGTH_SHORT).show();
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
                    ed.putString("dniUsuario", u.getDni()); // 👈 clave para login con foto
                    ed.apply();
                }

                irAlHome();
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error cargando usuario: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                irAlHome();
            }
        });
    }

    private void irAlHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
