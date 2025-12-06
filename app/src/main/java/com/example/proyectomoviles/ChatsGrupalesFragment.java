package com.example.proyectomoviles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.model.chat.ChatGrupal;
import com.example.proyectomoviles.model.chat.ChatGrupalResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsGrupalesFragment extends Fragment {

    private RecyclerView rvChatsGrupales;
    private ChatGrupalAdapter chatGrupalAdapter;
    private List<ChatGrupal> listaChatsGrupales = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats_grupales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvChatsGrupales = view.findViewById(R.id.rv_chats_grupales);
        rvChatsGrupales.setLayoutManager(new LinearLayoutManager(getContext()));

        chatGrupalAdapter = new ChatGrupalAdapter(requireContext(), listaChatsGrupales, chat -> {
            Intent intent = new Intent(getActivity(), ChatGrupalActivity.class);
            intent.putExtra("id_chat_producto", chat.getIdChatProducto());
            startActivity(intent);
        });
        rvChatsGrupales.setAdapter(chatGrupalAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Carga los datos cada vez que el fragmento es visible
        cargarChatsGrupales();
    }

    private void cargarChatsGrupales() {
        if (getContext() == null) return;

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);

        if (token == null) return;

        Swaply api = RetrofitClient.getApiService(token);
        api.obtenerChatsGrupales().enqueue(new Callback<ChatGrupalResponse>() {
            @Override
            public void onResponse(Call<ChatGrupalResponse> call, Response<ChatGrupalResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    listaChatsGrupales.clear();
                    listaChatsGrupales.addAll(response.body().getData());
                    chatGrupalAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ChatGrupalResponse> call, Throwable t) {
            }
        });
    }
}
