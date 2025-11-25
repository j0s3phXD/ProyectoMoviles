package com.example.proyectomoviles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.FragmentPerfilBinding;
import com.example.proyectomoviles.model.CalificacionEntry;
import com.example.proyectomoviles.model.ConfirmarIntercambioRequest;
import com.example.proyectomoviles.model.IntercambioEntry;
import com.example.proyectomoviles.model.RptaCalificacionPromedio;
import com.example.proyectomoviles.model.RptaCalificaciones;
import com.example.proyectomoviles.model.RptaGeneral;
import com.example.proyectomoviles.model.RptaIntercambios;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private IntercambiosRecibidosAdapter recibidosAdapter;
    private IntercambiosEnviadosAdapter enviadosAdapter;
    private HistorialIntercambiosAdapter historialAdapter;

    private List<IntercambioEntry> listaEnviados = new ArrayList<>();
    private List<IntercambioEntry> listaRecibidos = new ArrayList<>();
    private List<IntercambioEntry> listaHistorial = new ArrayList<>();
    private List<CalificacionEntry> misCalificaciones = new ArrayList<>();

    private int idUsuarioActual;

    private boolean historialCargado = false;
    private boolean calificacionesCargadas = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        idUsuarioActual = prefs.getInt("idUsuario", -1);

        String nombre = prefs.getString("nombreUsuario", "Usuario");
        String apellido = prefs.getString("apellidoUsuario", "");

        binding.txtNombreUsuario.setText(nombre + " " + apellido);

        configurarRecycler();

        cargarPromedioCalificacion(idUsuarioActual);
        cargarIntercambiosEnviados();
        cargarIntercambiosRecibidos();
        cargarHistorialIntercambios();
        cargarMisCalificaciones();

        return binding.getRoot();
    }

    private void configurarRecycler() {

        binding.rvMisIntercambios.setLayoutManager(new LinearLayoutManager(getContext()));
        enviadosAdapter = new IntercambiosEnviadosAdapter(requireContext(), listaEnviados);
        binding.rvMisIntercambios.setAdapter(enviadosAdapter);

        binding.rvIntercambiosRecibidos.setLayoutManager(new LinearLayoutManager(getContext()));
        recibidosAdapter = new IntercambiosRecibidosAdapter(
                requireContext(),
                listaRecibidos,
                new IntercambiosRecibidosAdapter.OnIntercambioClick() {
                    @Override
                    public void onAceptar(IntercambioEntry intercambio) {
                        confirmarIntercambio(intercambio.getId_intercambio(), "aceptado");
                    }

                    @Override
                    public void onRechazar(IntercambioEntry intercambio) {
                        confirmarIntercambio(intercambio.getId_intercambio(), "rechazado");
                    }
                }
        );
        binding.rvIntercambiosRecibidos.setAdapter(recibidosAdapter);

        // SOLO configuramos el Layout, NO creamos todavía el adapter.
        binding.rvHistorialIntercambios.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // ============================================================
    // Cargar promedio
    // ============================================================
    private void cargarPromedioCalificacion(int idUsuario) {
        Swaply api = RetrofitClient.getApiService();

        api.obtenerPromedio(idUsuario).enqueue(new Callback<RptaCalificacionPromedio>() {
            @Override
            public void onResponse(Call<RptaCalificacionPromedio> call, Response<RptaCalificacionPromedio> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                if (response.body().getCode() == 1) {
                    binding.ratingBar.setRating(response.body().getPromedio());
                }
            }

            @Override
            public void onFailure(Call<RptaCalificacionPromedio> call, Throwable t) { }
        });
    }

    // ============================================================
    // Cargar calificaciones hechas por el usuario
    // ============================================================
    private void cargarMisCalificaciones() {

        Swaply api = RetrofitClient.getApiService();

        api.obtenerCalificacionesPorAutor(idUsuarioActual).enqueue(new Callback<RptaCalificaciones>() {
            @Override
            public void onResponse(Call<RptaCalificaciones> call, Response<RptaCalificaciones> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    misCalificaciones.clear();
                    misCalificaciones.addAll(response.body().getData());
                }

                calificacionesCargadas = true;
                intentarCrearAdapterHistorial();
            }

            @Override
            public void onFailure(Call<RptaCalificaciones> call, Throwable t) { }
        });
    }

    private void intentarCrearAdapterHistorial() {

        if (!historialCargado || !calificacionesCargadas) return;

        historialAdapter = new HistorialIntercambiosAdapter(
                requireContext(),
                listaHistorial,
                misCalificaciones,
                intercambio -> {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("id_intercambio", intercambio.getId_intercambio());
                    startActivity(intent);
                }
        );

        binding.rvHistorialIntercambios.setAdapter(historialAdapter);
    }

    // ============================================================
    // Cargar historial de intercambios
    // ============================================================
    private void cargarHistorialIntercambios() {

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        if (token == null) return;

        Swaply api = RetrofitClient.getApiService();
        api.obtenerHistorial("JWT " + token).enqueue(new Callback<RptaIntercambios>() {
            @Override
            public void onResponse(Call<RptaIntercambios> call, Response<RptaIntercambios> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    listaHistorial.clear();

                    listaHistorial.addAll(
                            response.body().getData().stream()
                                    .filter(i -> "aceptado".equalsIgnoreCase(i.getEstado()))
                                    .collect(Collectors.toList())
                    );
                }

                historialCargado = true;
                intentarCrearAdapterHistorial();
            }

            @Override
            public void onFailure(Call<RptaIntercambios> call, Throwable t) { }
        });
    }

    // ============================================================
    // Cargar intercambios enviados / recibidos
    // ============================================================
    private void cargarIntercambiosEnviados() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        if (token == null) return;

        Swaply api = RetrofitClient.getApiService();
        api.obtenerMisIntercambios("JWT " + token).enqueue(new Callback<RptaIntercambios>() {
            @Override
            public void onResponse(Call<RptaIntercambios> call, Response<RptaIntercambios> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    listaEnviados.clear();
                    listaEnviados.addAll(response.body().getData());
                    enviadosAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<RptaIntercambios> call, Throwable t) { }
        });
    }

    private void cargarIntercambiosRecibidos() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        if (token == null) return;

        Swaply api = RetrofitClient.getApiService();
        api.obtenerIntercambiosRecibidos("JWT " + token).enqueue(new Callback<RptaIntercambios>() {
            @Override
            public void onResponse(Call<RptaIntercambios> call, Response<RptaIntercambios> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    listaRecibidos.clear();
                    listaRecibidos.addAll(response.body().getData());
                    recibidosAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<RptaIntercambios> call, Throwable t) { }
        });
    }

    private void confirmarIntercambio(int idIntercambio, String estado) {

        SharedPreferences prefs = getContext().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        if (token == null) return;

        Swaply api = RetrofitClient.getApiService();
        ConfirmarIntercambioRequest request = new ConfirmarIntercambioRequest(idIntercambio, estado);

        api.confirmarIntercambio("JWT " + token, request).enqueue(new Callback<RptaGeneral>() {
            @Override
            public void onResponse(Call<RptaGeneral> call, Response<RptaGeneral> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {

                    Toast.makeText(getContext(), "Intercambio " + estado, Toast.LENGTH_SHORT).show();

                    cargarIntercambiosEnviados();
                    cargarIntercambiosRecibidos();
                    cargarHistorialIntercambios();
                }
            }

            @Override
            public void onFailure(Call<RptaGeneral> call, Throwable t) { }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        // ✔ Recargar al volver de la pantalla de Calificar
        cargarMisCalificaciones();
        cargarHistorialIntercambios();
    }

}
