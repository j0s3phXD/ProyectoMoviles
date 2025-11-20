package com.example.proyectomoviles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.FragmentCatalogoBinding;
import com.example.proyectomoviles.databinding.FragmentGestionProductosBinding;
import com.example.proyectomoviles.model.ProductoEntry;
import com.example.proyectomoviles.model.ProductoGridItemDecoration;
import com.example.proyectomoviles.model.RptaProducto;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CatalogoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CatalogoFragment extends Fragment {

    private FragmentCatalogoBinding binding;
    private ProductoAdapter adapter;
    private List<ProductoEntry> listaProductos;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int idUsuarioActual = -1;


    public CatalogoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CatalogoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CatalogoFragment newInstance(String param1, String param2) {
        CatalogoFragment fragment = new CatalogoFragment();
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
        try {
            SharedPreferences prefs = requireActivity()
                    .getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);

            String token = prefs.getString("tokenJWT", null);

            if (token != null) {
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    byte[] payloadBytes =
                            android.util.Base64.decode(parts[1],
                                    android.util.Base64.URL_SAFE | android.util.Base64.NO_WRAP);
                    String payloadJson = new String(payloadBytes, "UTF-8");

                    org.json.JSONObject payload = new org.json.JSONObject(payloadJson);

                    // 👇 ESTE ES TU id_usuario
                    idUsuarioActual = payload.getInt("identity");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            idUsuarioActual = -1;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCatalogoBinding.inflate(inflater, container, false);
        cargarProductos();
//        binding.btnRegistrar.setOnClickListener(v -> {
//            NavController navController = Navigation.findNavController(requireView());
//            navController.navigate(R.id.action_productos_to_publicar);
//        });

        return binding.getRoot();
    }

    private void cargarProductos() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://swaply.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Swaply api = retrofit.create(Swaply.class);
        Call<RptaProducto> call = api.listarProductos();

        call.enqueue(new Callback<RptaProducto>() {
            @Override
            public void onResponse(Call<RptaProducto> call, Response<RptaProducto> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Código: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                RptaProducto rpta = response.body();
                if (rpta != null && rpta.getCode() == 1) {

                    List<ProductoEntry> listaProductos = rpta.getData();

                    // ⭐⭐⭐ FILTRAR: SOLO PRODUCTOS DE OTROS USUARIOS ⭐⭐⭐
                    List<ProductoEntry> productosDeOtros = new ArrayList<>();
                    if (listaProductos != null) {
                        for (ProductoEntry p : listaProductos) {
                            if (p.getId_usuario() != idUsuarioActual) {
                                productosDeOtros.add(p);
                            }
                        }
                    }

                    // Configurar RecyclerView en grid
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
                    binding.recyclerViewProductos.setLayoutManager(gridLayoutManager);
                    binding.recyclerViewProductos.setHasFixedSize(true);

                    // Adapter usando SOLO los productos filtrados
                    ProductoHomeAdapter adapter = new ProductoHomeAdapter(productosDeOtros, producto -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("producto", producto);
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_fragmentCatalogo_to_itemProductoPublico, bundle);
                    });


                    binding.recyclerViewProductos.setAdapter(adapter);

                    // Agregar espaciado entre items
                    int largePadding = getResources().getDimensionPixelSize(R.dimen.producto_grid_spacing);
                    int smallPadding = getResources().getDimensionPixelSize(R.dimen.producto_grid_spacing_small);
                    binding.recyclerViewProductos.addItemDecoration(
                            new ProductoGridItemDecoration(largePadding, smallPadding)
                    );

                } else {
                    Toast.makeText(requireContext(), "No hay productos disponibles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RptaProducto> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}