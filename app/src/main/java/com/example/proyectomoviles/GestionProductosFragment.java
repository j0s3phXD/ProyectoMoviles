package com.example.proyectomoviles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.FragmentGestionProductosBinding;
import com.example.proyectomoviles.model.EliminarProductoRequest;
import com.example.proyectomoviles.model.ProductoEntry;
import com.example.proyectomoviles.model.ProductoGridItemDecoration;
import com.example.proyectomoviles.model.PublicarRequest;
import com.example.proyectomoviles.model.RptaGeneral;
import com.example.proyectomoviles.model.RptaProducto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GestionProductosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GestionProductosFragment extends Fragment {

    private FragmentGestionProductosBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GestionProductosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GestionProductosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GestionProductosFragment newInstance(String param1, String param2) {
        GestionProductosFragment fragment = new GestionProductosFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGestionProductosBinding.inflate(inflater, container, false);
        cargarProductos();
        binding.btnRegistrar.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_productos_to_publicar);
        });

        return binding.getRoot();
    }

    private void cargarProductos() {

        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        if (token.isEmpty()) {
            Toast.makeText(getActivity(), "No se encontró token de sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        Swaply api = RetrofitClient.getApiService();

        Call<RptaProducto> call = api.misProductos("JWT " + token);

        call.enqueue(new Callback<RptaProducto>() {
            @Override
            public void onResponse(Call<RptaProducto> call, Response<RptaProducto> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Código: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                RptaProducto rpta = response.body();
                if (rpta != null && rpta.getCode() == 1) {
                    List<ProductoEntry> listaProductos = rpta.getData();

                    // Configurar RecyclerView
                    binding.recyclerProductos.setHasFixedSize(true);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                    binding.recyclerProductos.setLayoutManager(gridLayoutManager);
                    binding.recyclerProductos.setClipToPadding(false);
                    binding.recyclerProductos.setPadding(8, 8, 8, 8);

                    // Usa tu ProductoAdapter como ya lo tenías
                    ProductoAdapter adapter = new ProductoAdapter(listaProductos, new ProductoAdapter.OnItemClickListener() {
                        @Override
                        public void onEditarClick(ProductoEntry producto) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("producto", producto);

                            NavController navController = NavHostFragment.findNavController(GestionProductosFragment.this);
                            navController.navigate(R.id.action_productos_to_publicar, bundle);
                        }

                        @Override
                        public void onEliminarClick(ProductoEntry producto) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Eliminar producto")
                                    .setMessage("¿Seguro que deseas eliminar " + producto.getTitulo() + "?")
                                    .setPositiveButton("Sí", (dialog, which) -> eliminarProducto(producto.getId_producto()))
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                        }
                    });

                    binding.recyclerProductos.setAdapter(adapter);

                    int largePadding = getResources().getDimensionPixelSize(R.dimen.producto_grid_spacing);
                    int smallPadding = getResources().getDimensionPixelSize(R.dimen.producto_grid_spacing_small);
                    binding.recyclerProductos.addItemDecoration(
                            new ProductoGridItemDecoration(largePadding, smallPadding)
                    );

                } else {
                    Toast.makeText(getActivity(), "No tienes productos registrados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RptaProducto> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarProducto(int idProducto) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        Swaply api = RetrofitClient.getApiService();
        EliminarProductoRequest request = new EliminarProductoRequest(idProducto);

        Call<RptaGeneral> call = api.eliminarProducto("JWT " + token, request);

        call.enqueue(new Callback<RptaGeneral>() {
            @Override
            public void onResponse(Call<RptaGeneral> call, Response<RptaGeneral> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                RptaGeneral rpta = response.body();
                if (rpta != null && rpta.getCode() == 1) {
                    Toast.makeText(getActivity(), "Producto eliminado", Toast.LENGTH_SHORT).show();
                    cargarProductos(); // recargar la lista
                } else {
                    Toast.makeText(getActivity(), rpta != null ? rpta.getMessage() : "Error desconocido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RptaGeneral> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}