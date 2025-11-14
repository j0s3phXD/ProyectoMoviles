package com.example.proyectomoviles;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.FragmentDetalleProductoBinding;
import com.example.proyectomoviles.model.IniciarIntercambioRequest;
import com.example.proyectomoviles.model.IniciarIntercambioResponse;
import com.example.proyectomoviles.model.ProductoEntry;
import com.example.proyectomoviles.model.RptaProductoDetalle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetalleProductoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class    DetalleProductoFragment extends Fragment {

    private FragmentDetalleProductoBinding binding;
    private ProductoEntry productoActual;

    private static final String ARG_ID_PRODUCTO = "id_producto";


    public DetalleProductoFragment() {
        // Required empty public constructor
    }

    public static DetalleProductoFragment newInstance(int idProducto) {
        DetalleProductoFragment fragment = new DetalleProductoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID_PRODUCTO, idProducto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            productoActual = (ProductoEntry) getArguments().getSerializable("producto");

            if (productoActual != null) {
                binding.tvNombreProducto.setText(productoActual.getTitulo());
                binding.tvDescripcionProducto.setText(productoActual.getDescripcion());
                if (productoActual.getCategoria() != null)
                    binding.tvCategoriaProducto.setText(productoActual.getCategoria().getDes_categoria());

                binding.btnContactarVendedor.setOnClickListener(v -> iniciarIntercambio());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalleProductoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void cargarDetalleProducto(int idProducto) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://swaply.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Swaply api = retrofit.create(Swaply.class);
        Call<RptaProductoDetalle> call = api.detalleProducto(idProducto);

        call.enqueue(new Callback<RptaProductoDetalle>() {
            @Override
            public void onResponse(Call<RptaProductoDetalle> call, Response<RptaProductoDetalle> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                RptaProductoDetalle rpta = response.body();
                if (rpta != null && rpta.getCode() == 1) {

                    ProductoEntry producto = rpta.getData();
                    if (producto == null) {
                        Toast.makeText(getContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // --- SET DATA ---
                    binding.tvNombreProducto.setText(producto.getTitulo());
                    binding.tvDescripcionProducto.setText(producto.getDescripcion());

                    // 💥 PREVENCIÓN DE NULL EN CATEGORÍA
                    if (producto.getCategoria() != null) {
                        binding.tvCategoriaProducto.setText(
                                "Categoría: " + producto.getCategoria().getDes_categoria()
                        );
                    } else {
                        binding.tvCategoriaProducto.setText("Categoría: No especificada");
                    }

                    // FOTO
                    // if (producto.getFoto() != null && !producto.getFoto().isEmpty()) {
                    //     Glide.with(requireContext())
                    //          .load(producto.getFoto())
                    //          .into(binding.imgProducto);
                    // }
                }
            }

            @Override
            public void onFailure(Call<RptaProductoDetalle> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void iniciarIntercambio() {

        if (productoActual == null) {
            Toast.makeText(getContext(), "Producto no cargado aún", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener token
        SharedPreferences prefs = getContext().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        if (token == null) {
            Toast.makeText(getContext(), "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "JWT " + token;

        // Crear request con solo destino y producto
        IniciarIntercambioRequest request = new IniciarIntercambioRequest(
                productoActual.getId_usuario(),  // destino
                productoActual.getId_producto()  // producto
        );

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://swaply.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Swaply api = retrofit.create(Swaply.class);
        Call<IniciarIntercambioResponse> call = api.iniciarIntercambio(authHeader, request);

        call.enqueue(new Callback<IniciarIntercambioResponse>() {
            @Override
            public void onResponse(Call<IniciarIntercambioResponse> call, Response<IniciarIntercambioResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                IniciarIntercambioResponse rpta = response.body();
                if (rpta != null) {
                    Toast.makeText(getContext(), rpta.getMessage(), Toast.LENGTH_SHORT).show();

                    Bundle bundle = new Bundle();
                    bundle.putInt("id_usuario_destino", productoActual.getId_usuario()); // dueño del producto
                    Navigation.findNavController(requireView()).navigate(R.id.chatFragment, bundle);

                }
            }

            @Override
            public void onFailure(Call<IniciarIntercambioResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }





}