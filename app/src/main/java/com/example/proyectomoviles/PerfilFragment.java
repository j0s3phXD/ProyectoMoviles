package com.example.proyectomoviles;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyectomoviles.databinding.FragmentPerfilBinding;
import com.example.proyectomoviles.model.IntercambioEntry;
import com.example.proyectomoviles.model.RptaIntercambios;
import com.example.proyectomoviles.Interface.Swaply;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private IntercambiosRecibidosAdapter recibidosAdapter;
    private IntercambiosEnviadosAdapter enviadosAdapter;

    private List<IntercambioEntry> listaEnviados = new ArrayList<>();
    private List<IntercambioEntry> listaRecibidos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);

        configurarRecycler();

        cargarIntercambiosEnviados();
        cargarIntercambiosRecibidos();

        return binding.getRoot();
    }

    private void configurarRecycler() {

        // Recycler ENVIADOS
        binding.rvMisIntercambios.setLayoutManager(new LinearLayoutManager(getContext()));
        enviadosAdapter = new IntercambiosEnviadosAdapter(requireContext(), listaEnviados);
        binding.rvMisIntercambios.setAdapter(enviadosAdapter);

        // Recycler RECIBIDOS
        binding.rvIntercambiosRecibidos.setLayoutManager(new LinearLayoutManager(getContext()));
        recibidosAdapter = new IntercambiosRecibidosAdapter(
                requireContext(),
                listaRecibidos,
                new IntercambiosRecibidosAdapter.OnIntercambioClick() {
                    @Override
                    public void onAceptar(IntercambioEntry intercambio) {
                        Toast.makeText(getContext(), "ACEPTADO: " + intercambio.getId_intercambio(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRechazar(IntercambioEntry intercambio) {
                        Toast.makeText(getContext(), "RECHAZADO: " + intercambio.getId_intercambio(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        binding.rvIntercambiosRecibidos.setAdapter(recibidosAdapter);
    }

    private void cargarIntercambiosEnviados() {

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
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
        Call<RptaIntercambios> call = api.obtenerMisIntercambios(authHeader);
        call.enqueue(new Callback<RptaIntercambios>() {
            @Override
            public void onResponse(Call<RptaIntercambios> call, Response<RptaIntercambios> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    listaEnviados.clear();
                    listaEnviados.addAll(response.body().getData());
                    enviadosAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "No se obtuvieron enviados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RptaIntercambios> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarIntercambiosRecibidos() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
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
        Call<RptaIntercambios> call = api.obtenerIntercambiosRecibidos(authHeader);
        call.enqueue(new Callback<RptaIntercambios>() {
            @Override
            public void onResponse(Call<RptaIntercambios> call, Response<RptaIntercambios> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    listaRecibidos.clear();
                    listaRecibidos.addAll(response.body().getData());
                    recibidosAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "No se obtuvieron recibidos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RptaIntercambios> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

