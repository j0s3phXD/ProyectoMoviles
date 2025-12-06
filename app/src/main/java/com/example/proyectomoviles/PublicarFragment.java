package com.example.proyectomoviles;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.databinding.FragmentPublicarBinding;
import com.example.proyectomoviles.model.categoria.CategoriaRequest;
import com.example.proyectomoviles.model.categoria.CategoriaResponse;
import com.example.proyectomoviles.model.producto.ProductoEntry;
import com.example.proyectomoviles.model.producto.PublicarRequest;
import com.example.proyectomoviles.model.auth.GeneralResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicarFragment extends Fragment {

    private FragmentPublicarBinding binding;
    private boolean modoEdicion = false;
    private int idProductoEditar = -1;
    private String condicionSeleccionada = null;
    private List<CategoriaRequest> listaCategorias = new ArrayList<>();
    private int idCategoriaSeleccionada = 0;

    private Uri imagenSeleccionadaUri;

    private ActivityResultLauncher<String> seleccionarImagenLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            imagenSeleccionadaUri = uri;

                            Glide.with(this)
                                    .load(uri)
                                    .centerCrop()
                                    .into(binding.ivFotoProducto);

                            // Ocultar texto
                            binding.textAgregarFoto.setVisibility(View.GONE);
                            binding.ivFotoProducto.setVisibility(View.VISIBLE);
                        }
                    }
            );

    public PublicarFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        binding.btnRegresar.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        cargarCategorias();

        binding.frameAgregarFoto.setOnClickListener(v -> {
            seleccionarImagenLauncher.launch("image/*");
        });

        binding.btnPublicar.setOnClickListener(v -> {
            if (token.isEmpty()) {
                Toast.makeText(getContext(), "No se encontró token de sesión", Toast.LENGTH_SHORT).show();
                return;
            }

            if (modoEdicion) {
                editarProducto(token, idProductoEditar);
            } else {
                publicarObjeto(token);
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

                    ArrayAdapter<CategoriaRequest> adapter = new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_spinner_item,
                            listaCategorias
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerCategoria.setAdapter(adapter);

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

        if (imagenSeleccionadaUri == null) {
            Toast.makeText(getContext(), "Debes seleccionar una foto del producto", Toast.LENGTH_SHORT).show();
            return;
        }

        int idCategoria = categoriaSeleccionada.getId_categoria();

        File archivoImagen = crearArchivoDesdeUri(imagenSeleccionadaUri);
        if (archivoImagen == null) {
            Toast.makeText(getContext(), "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        MultipartBody.Part fotoPart = crearMultipart(archivoImagen);

        RequestBody rbTitulo = RequestBody.create(MediaType.parse("text/plain"), titulo);
        RequestBody rbDescripcion = RequestBody.create(MediaType.parse("text/plain"), descripcion);
        RequestBody rbCondicion = RequestBody.create(MediaType.parse("text/plain"), condicion);
        RequestBody rbCategoria = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idCategoria));
        RequestBody rbIntercambio = RequestBody.create(MediaType.parse("text/plain"), intercambio);

        Swaply api = RetrofitClient.getApiService(token);
        Call<GeneralResponse> call = api.publicarProductoConFoto(
                fotoPart,
                rbTitulo,
                rbDescripcion,
                rbCondicion,
                rbCategoria,
                rbIntercambio
        );

        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                GeneralResponse rpta = response.body();
                if (rpta != null && rpta.getCode() == 1) {
                    Toast.makeText(getActivity(), rpta.getMessage(), Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getActivity(), (rpta != null ? rpta.getMessage() : "Error desconocido"), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
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
        }

        if (producto.getCondicion() != null) {
            condicionSeleccionada = producto.getCondicion();
            Log.d("EDITAR_PRODUCTO", "Condición cargada: " + condicionSeleccionada);
        }

        if (producto.getFoto() != null && !producto.getFoto().isEmpty()) {

            String urlFoto = RetrofitClient.BASE_URL
                    + "uploads/productos/"
                    + producto.getFoto();

            Log.d("EDITAR_PRODUCTO", "Cargando imagen desde: " + urlFoto);

            Glide.with(this)
                    .load(urlFoto)
                    .centerCrop()
                    .into(binding.ivFotoProducto);

            binding.layoutUploadContent.setVisibility(View.GONE);
            binding.ivFotoProducto.setVisibility(View.VISIBLE);
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

        Swaply api = RetrofitClient.getApiService(token);

        if (imagenSeleccionadaUri == null) {
            PublicarRequest request = new PublicarRequest(titulo, descripcion, condicion, idCategoria, intercambio);
            request.setId_producto(idProducto);

            Call<GeneralResponse> call = api.editarProducto(request);

            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getActivity(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GeneralResponse rpta = response.body();
                    if (rpta != null && rpta.getCode() == 1) {
                        Toast.makeText(getActivity(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    } else {
                        Toast.makeText(getActivity(), (rpta != null ? rpta.getMessage() : "Error desconocido"), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            File archivoImagen = crearArchivoDesdeUri(imagenSeleccionadaUri);
            if (archivoImagen == null) {
                Toast.makeText(getContext(), "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            MultipartBody.Part fotoPart = crearMultipart(archivoImagen);

            RequestBody rbIdProducto = RequestBody.create(
                    MediaType.parse("text/plain"), String.valueOf(idProducto));
            RequestBody rbTitulo = RequestBody.create(
                    MediaType.parse("text/plain"), titulo);
            RequestBody rbDescripcion = RequestBody.create(
                    MediaType.parse("text/plain"), descripcion);
            RequestBody rbCondicion = RequestBody.create(
                    MediaType.parse("text/plain"), condicion);
            RequestBody rbCategoria = RequestBody.create(
                    MediaType.parse("text/plain"), String.valueOf(idCategoria));
            RequestBody rbIntercambio = RequestBody.create(
                    MediaType.parse("text/plain"), intercambio);

            Call<GeneralResponse> call = api.editarProductoConFoto(
                    rbIdProducto,
                    rbTitulo,
                    rbDescripcion,
                    rbCondicion,
                    rbCategoria,
                    rbIntercambio,
                    fotoPart
            );

            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getActivity(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GeneralResponse rpta = response.body();
                    if (rpta != null && rpta.getCode() == 1) {
                        Toast.makeText(getActivity(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    } else {
                        Toast.makeText(getActivity(), (rpta != null ? rpta.getMessage() : "Error desconocido"), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private File crearArchivoDesdeUri(Uri uri) {
        File archivoTemp = null;
        try {
            Context context = getContext();
            if (context == null) return null;

            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            String nombreArchivo = "foto_" + System.currentTimeMillis() + ".jpg";
            archivoTemp = new File(context.getCacheDir(), nombreArchivo);

            FileOutputStream outputStream = new FileOutputStream(archivoTemp);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return archivoTemp;
    }

    private MultipartBody.Part crearMultipart(File archivo) {
        RequestBody requestFile = RequestBody.create(
                MediaType.parse("image/*"),
                archivo
        );

        return MultipartBody.Part.createFormData(
                "foto",
                archivo.getName(),
                requestFile
        );
    }

}
