package com.example.proyectomoviles;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.FragmentPublicarBinding;
import com.example.proyectomoviles.model.CategoriaRequest;
import com.example.proyectomoviles.model.CategoriaResponse;
import com.example.proyectomoviles.model.ProductoEntry;
import com.example.proyectomoviles.model.PublicarRequest;
import com.example.proyectomoviles.model.RptaGeneral;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PublicarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PublicarFragment extends Fragment {

    private FragmentPublicarBinding binding;
    private boolean modoEdicion = false;
    private int idProductoEditar = -1;
    private String condicionSeleccionada = null;
    private List<CategoriaRequest> listaCategorias = new ArrayList<>();
    private int idCategoriaSeleccionada = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PublicarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PublicarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PublicarFragment newInstance(String param1, String param2) {
        PublicarFragment fragment = new PublicarFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPublicarBinding.inflate(inflater, container, false);
        if (getArguments() != null && getArguments().getSerializable("producto") != null) {
            ProductoEntry producto = (ProductoEntry) getArguments().getSerializable("producto");
            cargarDatosProducto(producto);

            modoEdicion = true;
            idProductoEditar = producto.getId_producto();

            binding.btnPublicar.setText("Actualizar producto");
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        cargarCategorias();

        // Acción del cuadro de foto
        binding.frameAgregarFoto.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Aquí puedes abrir la galería o cámara", Toast.LENGTH_SHORT).show();
        });

        // Listener del botón publicar
        binding.btnPublicar.setOnClickListener(v -> {
            if (token.isEmpty()) {
                Toast.makeText(getContext(), "No se encontró token de sesión", Toast.LENGTH_SHORT).show();
                return;
            }

            if (modoEdicion) {
                editarProducto("JWT " + token, idProductoEditar);
            } else {
                publicarObjeto("JWT " + token);
            }
        });
        if (modoEdicion && condicionSeleccionada != null) {
            binding.spinnerUso.post(() -> {
                for (int i = 0; i < binding.spinnerUso.getCount(); i++) {
                    String item = binding.spinnerUso.getItemAtPosition(i).toString();
                    if (item.equalsIgnoreCase(condicionSeleccionada)) {
                        binding.spinnerUso.setSelection(i);
                        break;
                    }
                }
            });
        }
    }


    private void cargarCategorias() {
        Swaply swaply = RetrofitClient.getApiService();

        Call<CategoriaResponse> call = swaply.listarCategorias();

        call.enqueue(new Callback<CategoriaResponse>() {
            @Override
            public void onResponse(Call<CategoriaResponse> call, Response<CategoriaResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                CategoriaResponse rpta = response.body();
                if (rpta != null && rpta.getCode() == 1) {
                    listaCategorias = rpta.getData();

                    // 🔹 Creas el adapter con esa lista
                    ArrayAdapter<CategoriaRequest> adapter = new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_spinner_item,
                            listaCategorias
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerCategoria.setAdapter(adapter);

                    // 🔹 Si estás editando, selecciona la categoría del producto
                    if (modoEdicion && idCategoriaSeleccionada != 0) {
                        binding.spinnerCategoria.post(() -> {
                            for (int i = 0; i < listaCategorias.size(); i++) {
                                if (listaCategorias.get(i).getId_categoria() == idCategoriaSeleccionada) {
                                    binding.spinnerCategoria.setSelection(i);
                                    break;
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "Sin categorías disponibles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoriaResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void publicarObjeto(String token) {
        String titulo = binding.editTitulo.getText().toString().trim();
        String descripcion = binding.editDescripcion.getText().toString().trim();
        String intercambio = binding.editIntercambio.getText().toString().trim();
        String condicion = binding.spinnerUso.getSelectedItem().toString();
        CategoriaRequest categoriaSeleccionada = (CategoriaRequest) binding.spinnerCategoria.getSelectedItem();

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(getActivity(), "Completa todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idCategoria = categoriaSeleccionada.getId_categoria();

        PublicarRequest request = new PublicarRequest(titulo, descripcion, condicion, idCategoria, intercambio);
        Swaply api = RetrofitClient.getApiService();
        Call<RptaGeneral> call = api.publicarObjeto(token, request);
        call.enqueue(new Callback<RptaGeneral>() {
            @Override
            public void onResponse(Call<RptaGeneral> call, Response<RptaGeneral> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                RptaGeneral rpta = response.body();
                if (rpta != null && rpta.getCode() == 1) {
                    Toast.makeText(getActivity(), rpta.getMessage(), Toast.LENGTH_LONG).show();

                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getActivity(), (rpta != null ? rpta.getMessage() : "Error desconocido"), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RptaGeneral> call, Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDatosProducto(ProductoEntry producto) {
        binding.editTitulo.setText(producto.getTitulo());
        binding.editDescripcion.setText(producto.getDescripcion());
        binding.editIntercambio.setText(producto.getIntercambio_deseado());

        if (producto.getId_categoria() != 0) {
            idCategoriaSeleccionada = producto.getId_categoria();
            Log.d("EDITAR_PRODUCTO", "Categoría cargada: " + idCategoriaSeleccionada);
        } else {
            Log.d("EDITAR_PRODUCTO", "El producto no tiene categoría asociada");
        }
        if (producto.getCondicion() != null) {
            condicionSeleccionada = producto.getCondicion();
            Log.d("EDITAR_PRODUCTO", "Condición cargada: " + condicionSeleccionada);
        }
    }

    private void editarProducto(String token, int idProducto) {
        String titulo = binding.editTitulo.getText().toString().trim();
        String descripcion = binding.editDescripcion.getText().toString().trim();
        String intercambio = binding.editIntercambio.getText().toString().trim();
        String condicion = binding.spinnerUso.getSelectedItem().toString();
        CategoriaRequest categoriaSeleccionada = (CategoriaRequest) binding.spinnerCategoria.getSelectedItem();

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(getActivity(), "Completa todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idCategoria = categoriaSeleccionada.getId_categoria();
        Log.d("EDITAR_PRODUCTO", "id_categoria=" + idCategoria + ", titulo=" + titulo);

        PublicarRequest request = new PublicarRequest(titulo, descripcion, condicion, idCategoria, intercambio);
        request.setId_producto(idProducto);

        Swaply api = RetrofitClient.getApiService();
        Call<RptaGeneral> call = api.editarProducto(token, request);

        call.enqueue(new Callback<RptaGeneral>() {
            @Override
            public void onResponse(Call<RptaGeneral> call, Response<RptaGeneral> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                RptaGeneral rpta = response.body();
                if (rpta != null && rpta.getCode() == 1) {
                    Toast.makeText(getActivity(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();

                    // Regresar a la lista
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getActivity(), (rpta != null ? rpta.getMessage() : "Error desconocido"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RptaGeneral> call, Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}