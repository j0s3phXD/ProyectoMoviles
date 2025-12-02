package com.example.proyectomoviles;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.model.mensaje.EnviarMensajeRequest;
import com.example.proyectomoviles.model.mensaje.Mensaje;
import com.example.proyectomoviles.model.auth.GeneralResponse;
import com.example.proyectomoviles.model.mensaje.MensajesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMensajes;
    private EditText etMensaje;
    private Button btnEnviar;

    private MensajesAdapter adapter;
    private List<Mensaje> listaMensajes = new ArrayList<>();

    private int idIntercambio;
    private int idUsuarioActual;

    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvMensajes = findViewById(R.id.rvMensajes);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        idIntercambio = getIntent().getIntExtra("id_intercambio", -1);

        SharedPreferences prefs = getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        idUsuarioActual = prefs.getInt("idUsuario", -1);

        Log.d("ChatActivity", "ID de usuario cargado de SharedPreferences: " + idUsuarioActual);

        if (idIntercambio == -1 || idUsuarioActual == -1) {
            Toast.makeText(this, "Error: No se pudo obtener la información del usuario o del intercambio", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        configurarRecycler();

        btnEnviar.setOnClickListener(v -> enviarMensaje());

        iniciarPollingMensajes();
    }

    private void configurarRecycler() {
        adapter = new MensajesAdapter(this, listaMensajes, idUsuarioActual);
        rvMensajes.setLayoutManager(new LinearLayoutManager(this));
        rvMensajes.setAdapter(adapter);
    }

    private void iniciarPollingMensajes() {
        runnable = new Runnable() {
            @Override
            public void run() {
                cargarMensajes();
                handler.postDelayed(this, 5000); // Repetir cada 5 segundos
            }
        };
        handler.post(runnable);
    }

    private void cargarMensajes() {
        SharedPreferences prefs = getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        if (token == null) return;

        Swaply api = RetrofitClient.getApiService(token);
        Call<MensajesResponse> call = api.obtenerMensajes(idIntercambio);

        call.enqueue(new Callback<MensajesResponse>() {
            @Override
            public void onResponse(Call<MensajesResponse> call, Response<MensajesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getCode() == 1) {
                        listaMensajes.clear();
                        listaMensajes.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                        if (!listaMensajes.isEmpty()) {
                            rvMensajes.scrollToPosition(listaMensajes.size() - 1);
                        }
                    } else {
                        Toast.makeText(ChatActivity.this, "Error al cargar: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Error de red: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MensajesResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ChatActivity", "onFailure: ", t);
            }
        });
    }

    private void enviarMensaje() {
        String textoMensaje = etMensaje.getText().toString().trim();
        if (textoMensaje.isEmpty()) return;

        SharedPreferences prefs = getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        if (token == null) {
            Toast.makeText(this, "Debes iniciar sesión para enviar mensajes", Toast.LENGTH_SHORT).show();
            return;
        }

        Swaply api = RetrofitClient.getApiService(token);
        EnviarMensajeRequest request = new EnviarMensajeRequest(idIntercambio, textoMensaje);
        Call<GeneralResponse> call = api.enviarMensaje(request);

        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    etMensaje.setText("");
                    new Handler(Looper.getMainLooper()).postDelayed(() -> cargarMensajes(), 500);
                } else {
                    Toast.makeText(ChatActivity.this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Error de envío: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Detener el polling
    }
}
