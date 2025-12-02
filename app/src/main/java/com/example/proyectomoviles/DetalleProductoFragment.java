package com.example.proyectomoviles;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.databinding.FragmentDetalleProductoBinding;
import com.example.proyectomoviles.model.producto.ProductoEntry;

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

                    String urlImagen = RetrofitClient.BASE_URL
                            + "uploads/productos/"
                            + productoActual.getFoto();

                    Glide.with(this)
                            .load(urlImagen)
                            .into(binding.imgProducto);

                } else {
                    binding.imgProducto.setImageResource(android.R.color.darker_gray);
                }

                // ------- BOTÓN CONTACTAR -------
                binding.btnContactarVendedor.setOnClickListener(v -> {

                    Bundle bundle = new Bundle();
                    bundle.putInt("id_producto_solicitado", productoActual.getId_producto());
                    bundle.putInt("id_usuario_destino", productoActual.getId_usuario());
                    bundle.putSerializable("producto_destino", productoActual);

                    Navigation.findNavController(v)
                            .navigate(R.id.action_itemProductoPublico_to_proponerIntercambioFragment, bundle);
                });
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalleProductoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}