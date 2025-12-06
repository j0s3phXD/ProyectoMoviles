package com.example.proyectomoviles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.FragmentDetalleProductoBinding;
import com.example.proyectomoviles.model.chat.CrearChatGrupalResponse;
import com.example.proyectomoviles.model.producto.ProductoEntry;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        binding.btnBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );
        
        if (getArguments() != null) {
            productoActual = (ProductoEntry) getArguments().getSerializable("producto");

            if (productoActual != null) {

                // ------- CAMPOS DE TEXTO -------
                binding.tvNombreProducto.setText(productoActual.getTitulo());
                binding.tvDescripcionProducto.setText(productoActual.getDescripcion());
                binding.tvIntercambioTexto.setText(productoActual.getIntercambio_deseado());

                if (productoActual.getCategoria() != null) {
                    binding.tvCategoriaProducto.setText(productoActual.getCategoria().getDes_categoria());
                }

                // ------- CAMPOS DE OFRECIDO POR -------
                if (productoActual.getNombre_usuario() != null) {
                    binding.tvNombreUsuario.setText(productoActual.getNombre_usuario());
                } else {
                    binding.tvNombreUsuario.setText("Usuario desconocido");
                }

                // ------- IMAGEN DEL PRODUCTO -------
                if (productoActual.getFoto() != null && !productoActual.getFoto().isEmpty()) {

                    String urlImagen = RetrofitClient.BASE_URL + "uploads/productos/" + productoActual.getFoto();
                    Glide.with(this).load(urlImagen).into(binding.imgProducto);

                } else {
                    binding.imgProducto.setImageResource(android.R.color.darker_gray);
                }

                binding.btnContactarVendedor.setOnClickListener(v -> {

                    Bundle bundle = new Bundle();
                    bundle.putInt("id_producto_solicitado", productoActual.getId_producto());
                    bundle.putInt("id_usuario_destino", productoActual.getId_usuario());
                    bundle.putSerializable("producto_destino", productoActual);

                    Navigation.findNavController(v).navigate(R.id.action_itemProductoPublico_to_proponerIntercambioFragment, bundle);
                });
                binding.btnChatGrupal.setOnClickListener(v -> {
                    crearOUnirseAChatGrupal();
                });
            }
        }
    }

    private void crearOUnirseAChatGrupal() {
        if (productoActual == null) return;

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);

        if (token == null) {
            Toast.makeText(getContext(), "Debes iniciar sesión para unirte al chat", Toast.LENGTH_SHORT).show();
            return;
        }

        Swaply api = RetrofitClient.getApiService(token);
        api.crearChatGrupal(productoActual.getId_producto()).enqueue(new Callback<CrearChatGrupalResponse>() {
            @Override
            public void onResponse(Call<CrearChatGrupalResponse> call, Response<CrearChatGrupalResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    int idChatProducto = response.body().getData().getIdChatProducto();
                    Intent intent = new Intent(getActivity(), ChatGrupalActivity.class);
                    intent.putExtra("id_chat_producto", idChatProducto);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Error al unirse al chat grupal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CrearChatGrupalResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalleProductoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}