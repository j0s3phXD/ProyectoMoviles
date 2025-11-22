package com.example.proyectomoviles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.ActivityRegistroUsuarioBinding;
import com.example.proyectomoviles.model.RegistroRequest;
import com.example.proyectomoviles.model.RegistroResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private ActivityRegistroUsuarioBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityRegistroUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombre = binding.txtNombre.getText().toString().trim();
        String apellido = binding.txtApellidos.getText().toString().trim();
        String email = binding.txtEmail.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        RegistroRequest registroRequest = new RegistroRequest(nombre, apellido, email, password);

        Swaply api = RetrofitClient.getApiService();

        Call<RegistroResponse> call = api.registrarUsuario(registroRequest);
        call.enqueue(new Callback<RegistroResponse>() {
            @Override
            public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(RegistroUsuarioActivity.this, "Error: Código " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                RegistroResponse res = response.body();
                if (res != null && res.getMensaje() != null) {
                    Toast.makeText(RegistroUsuarioActivity.this, res.getMensaje(), Toast.LENGTH_LONG).show();

                    // Si fue exitoso, volver al login
                    if (res.getMensaje().toLowerCase().contains("éxito")) {
                        Intent intent = new Intent(RegistroUsuarioActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else if (res != null && res.getError() != null) {
                    Toast.makeText(RegistroUsuarioActivity.this, "Error: " + res.getError(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegistroUsuarioActivity.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegistroResponse> call, Throwable t) {
                Toast.makeText(RegistroUsuarioActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}