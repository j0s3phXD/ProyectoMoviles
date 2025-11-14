package com.example.proyectomoviles;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyectomoviles.model.ChatMensaje;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private int idIntercambio;
    private int miIdUsuario;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private EditText edtMensaje;
    private Button btnEnviar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Obtener datos del Bundle
        if (getArguments() != null) {
            idIntercambio = getArguments().getInt("id_intercambio");
            miIdUsuario = getArguments().getInt("mi_id_usuario");
        }

        recyclerView = view.findViewById(R.id.recyclerMensajes);
        edtMensaje = view.findViewById(R.id.edtMensaje);
        btnEnviar = view.findViewById(R.id.btnEnviar);

        chatAdapter = new ChatAdapter(miIdUsuario);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        escucharMensajes(idIntercambio); // función para recibir mensajes en tiempo real

        btnEnviar.setOnClickListener(v -> {
            String texto = edtMensaje.getText().toString().trim();
            if(!texto.isEmpty()) {
                enviarMensaje(idIntercambio, miIdUsuario, texto); // <-- Aquí llamas a tu método
                edtMensaje.setText(""); // Limpiar EditText
            }
        });

        return view;
    }

    // Coloca aquí tu método enviarMensaje
    private void enviarMensaje(int idIntercambio, int idUsuario, String mensajeTexto) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference chatRef = db.child("chats").child("chat_" + idIntercambio).child("mensajes");

        long timestamp = System.currentTimeMillis();
        ChatMensaje mensaje = new ChatMensaje(idUsuario, mensajeTexto, timestamp);

        chatRef.push().setValue(mensaje)
                .addOnSuccessListener(aVoid -> {
                    // Actualizar último mensaje en info
                    db.child("chats").child("chat_" + idIntercambio).child("info")
                            .child("ultimoMensaje").setValue(mensajeTexto);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al enviar mensaje: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void escucharMensajes(int idIntercambio) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mensajesRef = db.child("chats").child("chat_" + idIntercambio).child("mensajes");

        mensajesRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChatMensaje> listaMensajes = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ChatMensaje mensaje = ds.getValue(ChatMensaje.class);
                    listaMensajes.add(mensaje);
                }
                // actualizar tu adapter del RecyclerView
                chatAdapter.setMensajes(listaMensajes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al leer mensajes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
