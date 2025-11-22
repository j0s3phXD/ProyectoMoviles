package com.example.proyectomoviles;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.model.EnviarMensajeRequest;
import com.example.proyectomoviles.model.Mensaje;
import com.example.proyectomoviles.model.RptaGeneral;
import com.example.proyectomoviles.model.RptaMensajes;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        String authHeader = "JWT " + token;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://swaply.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Swaply api = retrofit.create(Swaply.class);
        Call<RptaMensajes> call = api.obtenerMensajes(authHeader, idIntercambio);

        call.enqueue(new Callback<RptaMensajes>() {
            @Override
            public void onResponse(Call<RptaMensajes> call, Response<RptaMensajes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getCode() == 1) {
                        listaMensajes.clear();
                        listaMensajes.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                        if (!listaMensajes.isEmpty()) {
                            rvMensajes.scrollToPosition(listaMensajes.size() - 1);
                        }
                    } else {
                        // Error lógico del servidor
                        Toast.makeText(ChatActivity.this, "Error al cargar: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Error de red o HTTP
                    Toast.makeText(ChatActivity.this, "Error de red: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RptaMensajes> call, Throwable t) {
                // Error de conexión o conversión
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
        String authHeader = "JWT " + token;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://swaply.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Swaply api = retrofit.create(Swaply.class);
        EnviarMensajeRequest request = new EnviarMensajeRequest(idIntercambio, textoMensaje);
        Call<RptaGeneral> call = api.enviarMensaje(authHeader, request);

        call.enqueue(new Callback<RptaGeneral>() {
            @Override
            public void onResponse(Call<RptaGeneral> call, Response<RptaGeneral> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    etMensaje.setText("");
                    new Handler(Looper.getMainLooper()).postDelayed(() -> cargarMensajes(), 500);
                } else {
                    Toast.makeText(ChatActivity.this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RptaGeneral> call, Throwable t) {
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
