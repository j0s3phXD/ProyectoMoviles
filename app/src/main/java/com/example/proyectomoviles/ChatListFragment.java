package com.example.proyectomoviles;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.model.IntercambioEntry;
import com.example.proyectomoviles.model.RptaIntercambios;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatListFragment extends Fragment {

    private RecyclerView recycler;
    private ChatListAdapter adapter;
    private List<ChatItem> chats = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);

        recycler = v.findViewById(R.id.recyclerChats);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ChatListAdapter(chats, item -> {
            Bundle b = new Bundle();
            b.putInt("id_intercambio", item.getIdIntercambio());
            Navigation.findNavController(v).navigate(R.id.action_chatList_to_chat, b);
        });

        recycler.setAdapter(adapter);

        // Obtener id de usuario actual desde SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        int idUsuarioActual = prefs.getInt("id_usuario", -1);
        Log.d("CHAT_LIST", "ID usuario leído desde SharedPreferences: " + idUsuarioActual);

        cargarChatsDesdeAPI(idUsuarioActual);

        return v;
    }

    private void cargarChatsDesdeAPI(int idUsuarioActual) {
        SharedPreferences prefs = getContext().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        if (token == null) {
            Toast.makeText(getContext(), "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }
        String authHeader = "JWT " + token;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://swaply.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Swaply api = retrofit.create(Swaply.class);
        Call<RptaIntercambios> call = api.obtenerHistorial(authHeader);

        call.enqueue(new Callback<RptaIntercambios>() {
            @Override
            public void onResponse(Call<RptaIntercambios> call, Response<RptaIntercambios> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                RptaIntercambios rpta = response.body();
                if (rpta != null && rpta.getData() != null) {
                    Log.d("CHAT_LIST", "Intercambios recibidos: " + rpta.getData().size());
                    for (IntercambioEntry i : rpta.getData()) {
                        Log.d("CHAT_LIST", "Intercambio: " + i.getId_intercambio() + " Origen: " + i.getNombre_origen() + " Destino: " + i.getNombre_destino());
                    }
                    cargarChatsDesdeIntercambios(rpta.getData(), idUsuarioActual);
                }
            }

            @Override
            public void onFailure(Call<RptaIntercambios> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cargarChatsDesdeIntercambios(List<IntercambioEntry> intercambios, int idUsuarioActual) {
        chats.clear();

        // Log del ID de usuario actual
        Log.d("CHAT_LIST", "ID Usuario Actual: " + idUsuarioActual);

        for (IntercambioEntry i : intercambios) {
            // Log de los datos del intercambio recibido
            Log.d("CHAT_LIST", "Intercambio recibido - id_intercambio: " + i.getId_intercambio()
                    + ", id_producto_solicitado: " + i.getId_producto_solicitado()
                    + ", id_producto_ofrecido: " + i.getId_producto_ofrecido()
                    + ", nombre_destino: " + i.getNombre_destino()
                    + ", producto_solicitado: " + i.getProducto_solicitado());

            int idUsuarioOtro = (i.getId_usuario_origen() == idUsuarioActual) ?
                    i.getId_usuario_destino() : i.getId_usuario_origen();

            String nombreOtro = (i.getId_usuario_origen() == idUsuarioActual) ?
                    i.getNombre_destino() : i.getNombre_origen();

            String producto = i.getProducto_solicitado() != null ? i.getProducto_solicitado() : "";

            ChatItem chatItem = new ChatItem(
                    i.getId_intercambio(),
                    idUsuarioOtro,
                    nombreOtro,
                    producto,
                    System.currentTimeMillis() // timestamp inicial
            );
            chats.add(chatItem);

            // Log del ID del otro usuario
            Log.d("CHAT_LIST", "Origen: " + i.getNombre_origen() + " Destino: " + i.getNombre_destino() + " idUsuarioActual: " + idUsuarioActual);

        }

        Log.d("CHAT_LIST", "Total chats cargados: " + chats.size());
        adapter.notifyDataSetChanged();
    }


}
