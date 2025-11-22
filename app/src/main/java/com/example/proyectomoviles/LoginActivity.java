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
import com.example.proyectomoviles.model.AuthRequest;
import com.example.proyectomoviles.model.AuthResponse;
import com.example.proyectomoviles.model.Usuario;
import com.example.proyectomoviles.model.UsuarioResponse;

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
        binding.btnResgistrar.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroUsuarioActivity.class));
        });
    }

    private void getToken() {

        Swaply api = RetrofitClient.getApiService();
        AuthRequest authRequest = new AuthRequest();

        // Tu backend usa "username", pero realmente es el email
        authRequest.setUsername(binding.txtUsername.getText().toString().trim());
        authRequest.setPassword(binding.txtPassword.getText().toString().trim());

        api.obtenerToken(authRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthResponse auth = response.body();
                if (auth == null) {
                    Toast.makeText(LoginActivity.this, "Respuesta vacía del servidor", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Guardamos token e ID
                SharedPreferences sp = getSharedPreferences("SP_SWAPLY", MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();

                ed.putString("tokenJWT", auth.getAccess_token());
                ed.putInt("idUsuario", auth.getId_usuario());
                ed.apply();

                // Cargar info de usuario
                cargarInfoUsuario(auth.getId_usuario());
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    return;
                }

                Usuario u = response.body().getUsuario();
                if (u == null) return;

                // Guardamos los datos del usuario
                SharedPreferences sp = getSharedPreferences("SP_SWAPLY", MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();

                ed.putString("nombreUsuario", u.getNombre());
                ed.putString("apellidoUsuario", u.getApellido());
                ed.putString("emailUsuario", u.getEmail());
                ed.apply();

                // Ir al home
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error cargando usuario: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
