package com.example.proyectomoviles;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.model.auth.GeneralResponse;
import com.example.proyectomoviles.model.chat.EnviarMensajeGrupalRequest;
import com.example.proyectomoviles.model.chat.MensajeGrupal;
import com.example.proyectomoviles.model.chat.MensajesGrupalesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatGrupalActivity extends AppCompatActivity {

    private RecyclerView rvMensajes;
    private EditText etMensaje;
    private Button btnEnviar;

    private MensajesGrupalesAdapter mensajesAdapter;
    private List<MensajeGrupal> listaMensajes = new ArrayList<>();
    private int idChatProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_grupal);

        idChatProducto = getIntent().getIntExtra("id_chat_producto", -1);
        if (idChatProducto == -1) {
            Toast.makeText(this, "Error: ID de chat no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvMensajes = findViewById(R.id.rv_mensajes_grupales);
        etMensaje = findViewById(R.id.et_mensaje_grupal);
        btnEnviar = findViewById(R.id.btn_enviar_mensaje_grupal);

        rvMensajes.setLayoutManager(new LinearLayoutManager(this));
        mensajesAdapter = new MensajesGrupalesAdapter(this, listaMensajes);
        rvMensajes.setAdapter(mensajesAdapter);

        cargarMensajes();

        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    private void cargarMensajes() {
        SharedPreferences prefs = getSharedPreferences("SP_SWAPLY", MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);

        if (token == null) return;

        Swaply api = RetrofitClient.getApiService(token);
        api.obtenerMensajesGrupales(idChatProducto).enqueue(new Callback<MensajesGrupalesResponse>() {
            @Override
            public void onResponse(Call<MensajesGrupalesResponse> call, Response<MensajesGrupalesResponse> response) {
                if (response.body() != null && response.body().getCode() == 1) {
                    listaMensajes.clear();
                    listaMensajes.addAll(response.body().getData());
                    mensajesAdapter.notifyDataSetChanged();
                    rvMensajes.scrollToPosition(listaMensajes.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<MensajesGrupalesResponse> call, Throwable t) {
            }
        });
    }

    private void enviarMensaje() {
        String mensaje = etMensaje.getText().toString().trim();
        if (mensaje.isEmpty()) {
            return;
        }

        SharedPreferences prefs = getSharedPreferences("SP_SWAPLY", MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);

        if (token == null) return;

        Swaply api = RetrofitClient.getApiService(token);
        EnviarMensajeGrupalRequest request = new EnviarMensajeGrupalRequest(mensaje);

        api.enviarMensajeGrupal(idChatProducto, request).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.body() != null && response.body().getCode() == 1) {
                    etMensaje.setText("");
                    cargarMensajes();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
            }
        });
    }
}
