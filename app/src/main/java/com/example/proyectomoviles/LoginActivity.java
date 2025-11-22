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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnLogin.setOnClickListener(view -> {
            getToken();
        });

        binding.btnResgistrar.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegistroUsuarioActivity.class);
            startActivity(intent);
        });
    }

    private void getToken(){
        Swaply swaply = RetrofitClient.getApiService();
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(binding.txtUsername.getText().toString());
        authRequest.setPassword(binding.txtPassword.getText().toString());

        Call<AuthResponse> call = swaply.obtenerToken(authRequest);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Código: " + response.code(), Toast.LENGTH_SHORT).show();
                } else {
                    AuthResponse authResponse = response.body();

                    if (authResponse != null) {
                        SharedPreferences sharedPreferences = getSharedPreferences("SP_SWAPLY", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", binding.txtUsername.getText().toString());
                        editor.putString("tokenJWT", authResponse.getAccess_token());
                        editor.putInt("idUsuario", authResponse.getId_usuario());
                        editor.apply();

                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                        finish(); // Finalizar LoginActivity para que el usuario no pueda volver atrás

                    } else {
                        Toast.makeText(LoginActivity.this, "Respuesta de autenticación inválida", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}