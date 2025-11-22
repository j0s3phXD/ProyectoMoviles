package com.example.proyectomoviles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.proyectomoviles.databinding.ActivityComprobanteBinding;
import com.example.proyectomoviles.model.IntercambioEntry;
import com.squareup.picasso.Picasso;

public class ComprobanteActivity extends AppCompatActivity {

    private ActivityComprobanteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComprobanteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IntercambioEntry intercambio = (IntercambioEntry) getIntent().getSerializableExtra("intercambio");

        if (intercambio == null) {
            finish();
            return;
        }

        // Datos básicos
        binding.txtId.setText("ID: " + intercambio.getId_intercambio());
        binding.txtEstado.setText("Estado: " + intercambio.getEstado());
        binding.txtUsuarioOrigen.setText("Solicitante: " + intercambio.getNombre_origen());
        binding.txtUsuarioDestino.setText("Dueño del producto: " + intercambio.getNombre_destino());

        // Producto solicitado (lo que tú querías)
        binding.txtProductoSolicitado.setText(intercambio.getProducto_solicitado());
        if (intercambio.getImagen_solicitado() != null && !intercambio.getImagen_solicitado().isEmpty()) {
            Picasso.get().load(intercambio.getImagen_solicitado()).into(binding.imgSolicitado);
        }

        // Producto ofrecido (lo que tú ofreciste)
        binding.txtProductoOfrecido.setText(intercambio.getProducto_ofrecido());
        if (intercambio.getImagen_ofrecido() != null && !intercambio.getImagen_ofrecido().isEmpty()) {
            Picasso.get().load(intercambio.getImagen_ofrecido()).into(binding.imgOfrecido);
        }

        // Código único del comprobante
        String codigo = "COMP-" + intercambio.getId_intercambio() + "-" +
                (System.currentTimeMillis() % 100000);
        binding.txtCodigoComprobante.setText("Código: " + codigo);
    }
}
