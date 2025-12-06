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
import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.FragmentPerfilBinding;
import com.example.proyectomoviles.model.calificacion.CalificacionEntry;
import com.example.proyectomoviles.model.intercambio.ConfirmarIntercambioRequest;
import com.example.proyectomoviles.model.intercambio.IntercambioEntry;
import com.example.proyectomoviles.model.calificacion.CalificacionPromedioResponse;
import com.example.proyectomoviles.model.auth.GeneralResponse;
import com.example.proyectomoviles.model.intercambio.IntercambiosResponse;
import com.example.proyectomoviles.model.calificacion.CalificacionesResponse;
import com.example.proyectomoviles.model.producto.ProductoResponse;
import com.example.proyectomoviles.model.intercambio.PagarComisionRequest;

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
    private List<CalificacionEntry> listaCalificaciones = new ArrayList<>();

    private int idUsuarioActual;
    private static final int RC_PAGO_COMISION = 500;
    private IntercambioEntry intercambioSeleccionado;

    private void mostrarAvatar(String nombre, String apellido, String nombreArchivoFoto) {
        String iniciales = obtenerIniciales(nombre, apellido);
        binding.tvInicialesPerfil.setText(iniciales);

        if (nombreArchivoFoto == null || nombreArchivoFoto.trim().isEmpty()) {
            binding.imgPerfil.setImageDrawable(null);
            binding.imgPerfil.setVisibility(View.GONE);
            binding.tvInicialesPerfil.setVisibility(View.VISIBLE);
            return;
        }

        String urlFoto = RetrofitClient.BASE_URL + "uploads/usuarios/" + nombreArchivoFoto;

        Glide.with(this)
                .load(urlFoto)
                .centerCrop()
                .into(binding.imgPerfil);

        binding.imgPerfil.setVisibility(View.VISIBLE);
        binding.tvInicialesPerfil.setVisibility(View.GONE);
    }

    private String obtenerIniciales(String nombre, String apellido) {
        String n = nombre != null ? nombre.trim() : "";
        String a = apellido != null ? apellido.trim() : "";

        StringBuilder sb = new StringBuilder();
        if (!n.isEmpty()) sb.append(n.charAt(0));
        if (!a.isEmpty()) sb.append(a.charAt(0));

        if (sb.length() == 0) return "?";
        return sb.toString().toUpperCase();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        idUsuarioActual = prefs.getInt("idUsuario", -1);
        String nombre = prefs.getString("nombreUsuario", "Usuario");
        String apellido = prefs.getString("apellidoUsuario", "");

        String nombreArchivoFoto = prefs.getString("fotoUsuario", null);

        if (idUsuarioActual <= 0) {
            Toast.makeText(getContext(), "Error obteniendo usuario", Toast.LENGTH_SHORT).show();
            return binding.getRoot();
        }

        binding.txtNombreUsuario.setText(nombre + " " + apellido);

        mostrarAvatar(nombre, apellido, nombreArchivoFoto);

        configurarRecycler();


        cargarPromedioCalificacion(idUsuarioActual);
        cargarIntercambiosEnviados();
        cargarIntercambiosRecibidos();
        cargarHistorialIntercambios();
        cargarCalificacionesHechas(idUsuarioActual);

        cargarContadorProductos();

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

                        double comision = intercambio.getComision_monto();

                        String mensaje;
                        if (comision > 0) {
                            mensaje = "La comisión por aceptar este intercambio es de S/ "
                                    + String.format("%.2f", comision)
                                    + ".\n\n¿Deseas continuar y pagar la comisión para aceptar el intercambio?";
                        } else {
                            mensaje = "¿Seguro que deseas aceptar este intercambio?";
                        }

                        new AlertDialog.Builder(requireContext())
                                .setTitle("Confirmar intercambio")
                                .setMessage(mensaje)
                                .setPositiveButton("Aceptar", (dialog, which) -> {
                                    if (comision > 0) {
                                        intercambioSeleccionado = intercambio;
                                        Intent intent = new Intent(getContext(), PagoActivity.class);
                                        startActivityForResult(intent, RC_PAGO_COMISION);
                                    } else {
                                        confirmarIntercambio(intercambio.getId_intercambio(), "aceptado");
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }


                    @Override
                    public void onRechazar(IntercambioEntry intercambio) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Rechazar intercambio")
                                .setMessage("¿Seguro que quieres rechazar este intercambio?")
                                .setPositiveButton("Sí, rechazar", (dialog, which) ->
                                        confirmarIntercambio(intercambio.getId_intercambio(), "rechazado")
                                )
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }
                }
        );
        binding.rvIntercambiosRecibidos.setAdapter(recibidosAdapter);

        binding.rvHistorialIntercambios.setLayoutManager(new LinearLayoutManager(getContext()));

        historialAdapter = new HistorialIntercambiosAdapter(
                requireContext(),
                listaHistorial,
                listaCalificaciones,
                intercambio -> {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("id_intercambio", intercambio.getId_intercambio());
                    startActivity(intent);
                }
        );

        binding.rvHistorialIntercambios.setAdapter(historialAdapter);
    }

    private void cargarCalificacionesHechas(int idUsuario) {

        Swaply api = RetrofitClient.getApiService();

        api.obtenerCalificacionesPorAutor(idUsuario).enqueue(new Callback<CalificacionesResponse>() {
            @Override
            public void onResponse(Call<CalificacionesResponse> call, Response<CalificacionesResponse> response) {

                if (response.body() != null && response.body().getCode() == 1) {

                    listaCalificaciones.clear();
                    listaCalificaciones.addAll(response.body().getData());

                    Log.d("PERFIL", "Calificaciones cargadas = " + listaCalificaciones.size());

                    historialAdapter.updateCalificaciones(listaCalificaciones);
                }
            }

            @Override
            public void onFailure(Call<CalificacionesResponse> call, Throwable t) {
                Log.e("PERFIL", "Error cargando calificaciones: " + t.getMessage());
            }
        });
    }


    private void cargarPromedioCalificacion(int idUsuario) {

        Swaply api = RetrofitClient.getApiService();

        api.obtenerPromedio(idUsuario).enqueue(new Callback<CalificacionPromedioResponse>() {
            @Override
            public void onResponse(Call<CalificacionPromedioResponse> call,
                                   Response<CalificacionPromedioResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                if (response.body().getCode() == 1) {
                    binding.ratingBar.setRating(response.body().getPromedio());
                }
            }

            @Override
            public void onFailure(Call<CalificacionPromedioResponse> call, Throwable t) { }
        });
    }

    private void cargarIntercambiosEnviados() {

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);

        if (token == null) return;

        Swaply api = RetrofitClient.getApiService(token);
        api.obtenerMisIntercambios().enqueue(new Callback<IntercambiosResponse>() {
            @Override
            public void onResponse(Call<IntercambiosResponse> call, Response<IntercambiosResponse> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    listaEnviados.clear();
                    listaEnviados.addAll(response.body().getData());
                    enviadosAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<IntercambiosResponse> call, Throwable t) { }
        });
    }

    private void cargarIntercambiosRecibidos() {

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);

        if (token == null) return;

        Swaply api = RetrofitClient.getApiService(token);
        api.obtenerIntercambiosRecibidos().enqueue(new Callback<IntercambiosResponse>() {
            @Override
            public void onResponse(Call<IntercambiosResponse> call, Response<IntercambiosResponse> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    listaRecibidos.clear();
                    listaRecibidos.addAll(response.body().getData());
                    recibidosAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<IntercambiosResponse> call, Throwable t) { }
        });
    }

    private void cargarHistorialIntercambios() {

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);

        if (token == null) return;

        Swaply api = RetrofitClient.getApiService(token);
        api.obtenerHistorial().enqueue(new Callback<IntercambiosResponse>() {
            @Override
            public void onResponse(Call<IntercambiosResponse> call, Response<IntercambiosResponse> response) {

                if (response.body() == null) return;

                if (response.body().getCode() == 1) {
                    listaHistorial.clear();

                    List<IntercambioEntry> aceptados = response.body().getData().stream()
                            .filter(i -> "aceptado".equalsIgnoreCase(i.getEstado()))
                            .collect(Collectors.toList());

                    listaHistorial.addAll(aceptados);
                    historialAdapter.notifyDataSetChanged();

                    binding.tvContadorIntercambios.setText(String.valueOf(listaHistorial.size()));
                }
            }

            @Override
            public void onFailure(Call<IntercambiosResponse> call, Throwable t) { }
        });
    }
    private void pagarComisionYConfirmar(IntercambioEntry intercambio, String paymentToken) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);

        if (token == null) {
            Toast.makeText(getContext(), "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir la comisión a céntimos (ej: 4.50 -> 450)
        double comision = intercambio.getComision_monto();
        int montoCentimos = (int) Math.round(comision * 100);

        PagarComisionRequest request = new PagarComisionRequest(
                intercambio.getId_intercambio(),
                paymentToken,
                montoCentimos
        );

        Swaply api = RetrofitClient.getApiService(token);

        api.pagarComision(request).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(),
                            "Error al procesar el pago: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                GeneralResponse rpta = response.body();

                if (rpta.getCode() == 1) {
                    Toast.makeText(getContext(),
                            "Pago de comisión exitoso",
                            Toast.LENGTH_SHORT).show();

                    confirmarIntercambio(intercambio.getId_intercambio(), "aceptado");

                } else {
                    Toast.makeText(getContext(),
                            "No se pudo cobrar la comisión: " + rpta.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error de conexión al pagar comisión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarIntercambio(int idIntercambio, String estado) {

        SharedPreferences prefs = getContext().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);

        if (token == null) return;

        Swaply api = RetrofitClient.getApiService(token);
        ConfirmarIntercambioRequest request = new ConfirmarIntercambioRequest(idIntercambio, estado);

        api.confirmarIntercambio(request).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {

                if (response.body() != null && response.body().getCode() == 1) {

                    Toast.makeText(getContext(), "Intercambio " + estado, Toast.LENGTH_SHORT).show();

                    cargarIntercambiosRecibidos();
                    cargarIntercambiosEnviados();
                    cargarHistorialIntercambios();
                    cargarCalificacionesHechas(idUsuarioActual);
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) { }
        });
    }

    private void cargarContadorProductos() {

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);

        if (token == null) return;

        Swaply api = RetrofitClient.getApiService(token);

        api.misProductos().enqueue(new Callback<ProductoResponse>() {
            @Override
            public void onResponse(Call<ProductoResponse> call, Response<ProductoResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                if (response.body().getCode() == 1 && response.body().getData() != null) {
                    int total = response.body().getData().size();
                    binding.tvContadorProductos.setText(String.valueOf(total));
                }
            }

            @Override
            public void onFailure(Call<ProductoResponse> call, Throwable t) { }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PAGO_COMISION && resultCode == Activity.RESULT_OK && data != null) {

            String paymentToken = data.getStringExtra(PagoActivity.EXTRA_PAYMENT_TOKEN);

            if (paymentToken != null && intercambioSeleccionado != null) {
                pagarComisionYConfirmar(intercambioSeleccionado, paymentToken);
            } else {
                Toast.makeText(getContext(),
                        "No se pudo obtener el token de pago",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        cargarPromedioCalificacion(idUsuarioActual);
        cargarIntercambiosEnviados();
        cargarIntercambiosRecibidos();
        cargarHistorialIntercambios();
        cargarCalificacionesHechas(idUsuarioActual);
        cargarContadorProductos();
    }

}
