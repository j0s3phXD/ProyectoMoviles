package com.example.proyectomoviles;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.FragmentCatalogoBinding;
import com.example.proyectomoviles.model.ProductoEntry;
import com.example.proyectomoviles.model.ProductoGridItemDecoration;
import com.example.proyectomoviles.model.RptaProducto;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CatalogoFragment extends Fragment {

    private FragmentCatalogoBinding binding;

    private ProductoHomeAdapter adapter;

    // Lista completa (productos de otros usuarios)
    private final List<ProductoEntry> listaOriginal = new ArrayList<>();

    // Filtros actuales
    private String currentQuery = "";
    private String categoriaSeleccionada = "Todos";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private int idUsuarioActual = -1;

    public CatalogoFragment() {}

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

        // Obtener idUsuarioActual desde el JWT
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

                    JSONObject payload = new JSONObject(payloadJson);
                    idUsuarioActual = payload.getInt("identity"); // id_usuario
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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerViewProductos.setLayoutManager(gridLayoutManager);
        binding.recyclerViewProductos.setHasFixedSize(true);

        adapter = new ProductoHomeAdapter(new ArrayList<>(), producto -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("producto", producto);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_fragmentCatalogo_to_itemProductoPublico, bundle);
        });

        binding.recyclerViewProductos.setAdapter(adapter);

        int largePadding = getResources().getDimensionPixelSize(R.dimen.producto_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.producto_grid_spacing_small);
        binding.recyclerViewProductos.addItemDecoration(
                new ProductoGridItemDecoration(largePadding, smallPadding)
        );

        configurarBuscador();

        cargarProductos();

        return binding.getRoot();
    }

    private void configurarBuscador() {
        binding.etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString();
                aplicarFiltros();
            }

            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void configurarChipsCategorias() {
        ChipGroup chipGroup = binding.chipGroupCategorias;
        chipGroup.removeAllViews();

        Chip chipTodos = crearChip("Todos");
        chipTodos.setChecked(true);
        chipTodos.setTag("Todos");
        chipGroup.addView(chipTodos);

        // Categorías únicas (en el orden que llegan)
        Set<String> categorias = new LinkedHashSet<>();
        for (ProductoEntry p : listaOriginal) {
            if (p.getDes_categoria() != null && !p.getDes_categoria().trim().isEmpty()) {
                categorias.add(p.getDes_categoria());
            }
        }

        for (String cat : categorias) {
            Chip chip = crearChip(cat);
            chip.setTag(cat);
            chipGroup.addView(chip);
        }

        // Listener de selección
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) {
                // Si se deselecciona todo, volvemos a "Todos"
                categoriaSeleccionada = "Todos";
                chipTodos.setChecked(true);
            } else {
                Chip chipSeleccionado = group.findViewById(checkedId);
                if (chipSeleccionado != null && chipSeleccionado.getTag() != null) {
                    categoriaSeleccionada = chipSeleccionado.getTag().toString();
                } else {
                    categoriaSeleccionada = "Todos";
                }
            }
            aplicarFiltros();
        });
    }

    private Chip crearChip(String texto) {
        Chip chip = new Chip(requireContext());
        chip.setText(texto);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setId(View.generateViewId());
        return chip;
    }

    private void aplicarFiltros() {
        String query = currentQuery.toLowerCase().trim();
        String categoria = categoriaSeleccionada;

        List<ProductoEntry> filtrados = new ArrayList<>();

        for (ProductoEntry p : listaOriginal) {
            boolean coincideTexto =
                    query.isEmpty()
                            || p.getTitulo().toLowerCase().contains(query)
                            || (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(query))
                            || (p.getDes_categoria() != null && p.getDes_categoria().toLowerCase().contains(query));

            boolean coincideCategoria =
                    categoria == null
                            || categoria.equals("Todos")
                            || (p.getDes_categoria() != null
                            && p.getDes_categoria().equalsIgnoreCase(categoria));

            if (coincideTexto && coincideCategoria) {
                filtrados.add(p);
            }
        }

        adapter.updateList(filtrados);
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

                    // Solo productos de otros usuarios
                    List<ProductoEntry> productosDeOtros = new ArrayList<>();
                    if (listaProductos != null) {
                        for (ProductoEntry p : listaProductos) {
                            if (p.getId_usuario() != idUsuarioActual) {
                                productosDeOtros.add(p);
                            }
                        }
                    }

                    listaOriginal.clear();
                    listaOriginal.addAll(productosDeOtros);

                    configurarChipsCategorias();

                    aplicarFiltros();

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
