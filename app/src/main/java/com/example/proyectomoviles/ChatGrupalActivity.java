package com.example.proyectomoviles;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.model.chat.MensajeGrupal;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatGrupalActivity extends AppCompatActivity {

    private RecyclerView rvMensajes;
    private EditText etMensaje;
    private Button btnEnviar;

    private MensajesGrupalesAdapter mensajesAdapter;
    private List<MensajeGrupal> listaMensajes = new ArrayList<>();
    private int idChatProducto;

    private DatabaseReference chatDatabaseReference;

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

        // Usamos el ID del chat para crear una "sala" de chat única en Firebase
        chatDatabaseReference = FirebaseDatabase.getInstance().getReference("grupales").child(String.valueOf(idChatProducto));

        escucharMensajes();

        btnEnviar.setOnClickListener(v -> enviarMensaje());
    }

    private void escucharMensajes() {
        chatDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MensajeGrupal mensaje = dataSnapshot.getValue(MensajeGrupal.class);
                if (mensaje != null) {
                    listaMensajes.add(mensaje);
                    mensajesAdapter.notifyItemInserted(listaMensajes.size() - 1);
                    rvMensajes.scrollToPosition(listaMensajes.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatGrupalActivity.this, "Error al cargar mensajes.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarMensaje() {
        String mensajeTexto = etMensaje.getText().toString().trim();
        if (mensajeTexto.isEmpty()) {
            return;
        }

        SharedPreferences prefs = getSharedPreferences("SP_SWAPLY", MODE_PRIVATE);
        int idUsuario = prefs.getInt("idUsuario", -1);
        String nombreUsuario = prefs.getString("nombreUsuario", "Tú");

        if (idUsuario == -1) {
            Toast.makeText(this, "Error: Usuario no identificado.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creamos el objeto del mensaje
        MensajeGrupal nuevoMensaje = new MensajeGrupal();
        nuevoMensaje.setIdUsuario(idUsuario);
        nuevoMensaje.setNombreUsuario(nombreUsuario);
        nuevoMensaje.setMensaje(mensajeTexto);
        // Añadimos la fecha de envío
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        nuevoMensaje.setFechaEnvio(fecha);

        // Lo enviamos a Firebase
        chatDatabaseReference.push().setValue(nuevoMensaje)
            .addOnSuccessListener(aVoid -> etMensaje.setText(""))
            .addOnFailureListener(e -> Toast.makeText(ChatGrupalActivity.this, "Error al enviar el mensaje.", Toast.LENGTH_SHORT).show());
    }
}
