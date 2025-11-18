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

                if (productoActual.getCategoria() != null) {
                    binding.tvCategoriaProducto.setText(productoActual.getCategoria().getDes_categoria());
                }

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