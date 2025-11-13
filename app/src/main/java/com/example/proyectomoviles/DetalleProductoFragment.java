package com.example.proyectomoviles;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectomoviles.Interface.Swaply;
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

    private ImageView imgProducto;
    private TextView tvNombreProducto, tvDescripcionProducto, tvCategoriaProducto;
    private Button btnContactarVendedor;

    private static final String ARG_ID_PRODUCTO = "id_producto";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetalleProductoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetalleProductoFragment newInstance(String param1, String param2) {
        DetalleProductoFragment fragment = new DetalleProductoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int idProducto = getArguments() != null ? getArguments().getInt("id_producto") : -1;

        if (idProducto != -1) {
            cargarDetalleProducto(idProducto);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_producto, container, false);

        imgProducto = view.findViewById(R.id.imgProducto);
        tvNombreProducto = view.findViewById(R.id.tvNombreProducto);
        tvDescripcionProducto = view.findViewById(R.id.tvDescripcionProducto);
        tvCategoriaProducto = view.findViewById(R.id.tvCategoriaProducto);
        btnContactarVendedor = view.findViewById(R.id.btnContactarVendedor);

        if (getArguments() != null) {
            int idProducto = getArguments().getInt(ARG_ID_PRODUCTO);
            cargarDetalleProducto(idProducto);
        }

        return view;
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

                    tvNombreProducto.setText(producto.getTitulo());
                    tvDescripcionProducto.setText(producto.getDescripcion());
                    tvCategoriaProducto.setText("Categoría: " + producto.getCategoria().getDes_categoria());

                }
            }

            @Override
            public void onFailure(Call<RptaProductoDetalle> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}