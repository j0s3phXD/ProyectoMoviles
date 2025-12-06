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
import com.example.proyectomoviles.model.intercambio.IntercambioEntry;
import com.example.proyectomoviles.model.intercambio.IntercambiosResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsIndividualesFragment extends Fragment {

    private RecyclerView rvChatsIndividuales;
    private ChatAdapter chatAdapter;
    private List<IntercambioEntry> listaChatsIndividuales = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats_individuales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvChatsIndividuales = view.findViewById(R.id.rv_chats_individuales);
        rvChatsIndividuales.setLayoutManager(new LinearLayoutManager(getContext()));

        chatAdapter = new ChatAdapter(requireContext(), listaChatsIndividuales, intercambio -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("id_intercambio", intercambio.getId_intercambio());
            startActivity(intent);
        });

        rvChatsIndividuales.setAdapter(chatAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarChatsIndividuales(); // Recarga cada vez que entras al fragment
    }

    private void cargarChatsIndividuales() {
        if (getContext() == null) return;

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);

        if (token == null) return;

        Swaply api = RetrofitClient.getApiService(token);

        api.obtenerHistorial().enqueue(new Callback<IntercambiosResponse>() {
            @Override
            public void onResponse(Call<IntercambiosResponse> call, Response<IntercambiosResponse> response) {

                if (!isAdded()) return; // Evita crasheos

                if (response.body() != null && response.body().getCode() == 1) {

                    listaChatsIndividuales.clear();

                    List<IntercambioEntry> aceptados = response.body().getData().stream()
                            .filter(i -> "aceptado".equalsIgnoreCase(i.getEstado()))
                            .collect(Collectors.toList());

                    listaChatsIndividuales.addAll(aceptados);

                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<IntercambiosResponse> call, Throwable t) {
                // Error opcional
            }
        });
    }
}
