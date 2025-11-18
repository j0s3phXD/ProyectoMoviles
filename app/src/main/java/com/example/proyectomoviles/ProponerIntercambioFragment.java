package com.example.proyectomoviles;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.model.IniciarIntercambioRequest;
import com.example.proyectomoviles.model.IniciarIntercambioResponse;
import com.example.proyectomoviles.model.ProductoEntry;
import com.example.proyectomoviles.model.RptaProducto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProponerIntercambioFragment extends Fragment {

    // Datos que vienen desde el DetalleProductoFragment
    private int idProductoSolicitado;
    private int idUsuarioDestino;

    // Tu producto elegido
    private int idProductoOfrecidoSeleccionado = -1;

    // Views
    private RecyclerView rvMisProductos;
    private TextView tvNombreProductoDestino;
    private EditText etMensaje;
    private Button btnEnviar;

    public ProponerIntercambioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_proponer_intercambio, container, false);

        tvNombreProductoDestino = view.findViewById(R.id.tvNombreProductoDestino);
        rvMisProductos = view.findViewById(R.id.rvMisProductos);
        etMensaje = view.findViewById(R.id.etMensaje);
        btnEnviar = view.findViewById(R.id.btnEnviarPropuesta);

        rvMisProductos.setLayoutManager(new LinearLayoutManager(getContext()));

        // Recuperamos datos que vienen desde DetalleProductoFragment
        if (getArguments() != null) {
            idProductoSolicitado = getArguments().getInt("id_producto_solicitado", -1);
            idUsuarioDestino = getArguments().getInt("id_usuario_destino", -1);

            ProductoEntry productoDestino =
                    (ProductoEntry) getArguments().getSerializable("producto_destino");

            if (productoDestino != null) {
                tvNombreProductoDestino.setText(productoDestino.getTitulo());
            } else {
                tvNombreProductoDestino.setText("Producto seleccionado");
            }
        }

        // Cargar mis productos desde la API
        cargarMisProductos();

        // Enviar propuesta
        btnEnviar.setOnClickListener(v -> enviarPropuesta());

        return view;
    }

    private void cargarMisProductos() {
        if (getContext() == null) return;

        SharedPreferences prefs =
                getContext().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);

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

        // 👇 nuevo endpoint que usa current_identity.id
        Call<RptaProducto> call = api.misProductos(authHeader);

        call.enqueue(new Callback<RptaProducto>() {
            @Override
            public void onResponse(Call<RptaProducto> call, Response<RptaProducto> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "Error al obtener productos: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                RptaProducto rpta = response.body();

                if (rpta != null && rpta.getCode() == 1) {
                    List<ProductoEntry> listaMisProductos = rpta.getData();

                    if (listaMisProductos == null || listaMisProductos.isEmpty()) {
                        Toast.makeText(getContext(),
                                "No tienes productos disponibles para intercambio",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    MisProductosAdapter adapter = new MisProductosAdapter(
                            listaMisProductos,
                            producto -> idProductoOfrecidoSeleccionado = producto.getId_producto()
                    );

                    rvMisProductos.setAdapter(adapter);

                } else {
                    Toast.makeText(getContext(),
                            rpta != null ? rpta.getMessage() : "Error desconocido",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RptaProducto> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarPropuesta() {
        if (getContext() == null) return;

        if (idProductoOfrecidoSeleccionado == -1) {
            Toast.makeText(getContext(),
                    "Selecciona uno de tus productos para intercambiar",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String mensaje = etMensaje.getText().toString().trim();
        if (mensaje.isEmpty()) {
            Toast.makeText(getContext(),
                    "Escribe un mensaje para el dueño",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs =
                getContext().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);

        String token = prefs.getString("tokenJWT", null);
        if (token == null) {
            Toast.makeText(getContext(), "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "JWT " + token;

        // Tu backend solo recibe 2 campos (por ahora)
        IniciarIntercambioRequest request =
                new IniciarIntercambioRequest(
                        idUsuarioDestino,
                        idProductoSolicitado,
                        idProductoOfrecidoSeleccionado   // ← TU PRODUCTO SELECCIONADO
                );


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://swaply.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Swaply api = retrofit.create(Swaply.class);

        Call<IniciarIntercambioResponse> call = api.iniciarIntercambio(authHeader, request);

        call.enqueue(new Callback<IniciarIntercambioResponse>() {
            @Override
            public void onResponse(Call<IniciarIntercambioResponse> call,
                                   Response<IniciarIntercambioResponse> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "Error al generar intercambio: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                IniciarIntercambioResponse rpta = response.body();

                if (rpta != null) {
                    Toast.makeText(getContext(), rpta.getMessage(), Toast.LENGTH_SHORT).show();
                    // volver atrás
                    if (getView() != null) {
                        Navigation.findNavController(getView()).popBackStack();
                    }
                } else {
                    Toast.makeText(getContext(),
                            "Respuesta vacía del servidor",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<IniciarIntercambioResponse> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
