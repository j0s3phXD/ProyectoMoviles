package com.example.proyectomoviles;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.model.intercambio.IntercambioEntry;

import java.util.List;

public class IntercambiosRecibidosAdapter extends RecyclerView.Adapter<IntercambiosRecibidosAdapter.ViewHolder> {

    public interface OnIntercambioClick {
        void onAceptar(IntercambioEntry intercambio);
        void onRechazar(IntercambioEntry intercambio);
    }

    private Context context;
    private List<IntercambioEntry> lista;
    private OnIntercambioClick listener;

    public IntercambiosRecibidosAdapter(Context context, List<IntercambioEntry> lista, OnIntercambioClick listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_intercambio_recibido, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        IntercambioEntry item = lista.get(position);

        holder.txtProductoSolicitado.setText("Tu producto: " + item.getProducto_solicitado());
        holder.txtProductoOfrecido.setText("Te ofrecen: " + item.getProducto_ofrecido());
        holder.txtNombreUsuario.setText("Solicitante: " + item.getNombre_origen());
        holder.txtEstado.setText("Estado: " + item.getEstado());

        // 🔹 Mostrar comisión (si existe)
        double comision = item.getComision_monto();
        if (comision > 0 && item.getEstado() != null
                && item.getEstado().equalsIgnoreCase("Pendiente")) {

            holder.txtComision.setVisibility(View.VISIBLE);
            // Formato simple, puedes mejorarlo luego con NumberFormat si quieres
            holder.txtComision.setText(
                    "Comisión por aceptar: S/ " + String.format("%.2f", comision)
            );
        } else {
            holder.txtComision.setVisibility(View.GONE);
        }

        // Imagen del producto solicitado
        String urlImagen = item.getImagen_solicitado();
        if (urlImagen != null && !urlImagen.isEmpty()) {

            if (!urlImagen.startsWith("http")) {
                urlImagen = RetrofitClient.BASE_URL + "uploads/productos/" + urlImagen;
            }

            Glide.with(context)
                    .load(urlImagen)
                    .placeholder(R.drawable.logo_registrar)
                    .error(R.drawable.logo_registrar)
                    .into(holder.imgProducto);
        } else {
            holder.imgProducto.setImageResource(R.drawable.logo_registrar);
        }

        // Mostrar u ocultar botones según estado
        if (item.getEstado().equalsIgnoreCase("Pendiente")) {
            holder.btnAceptar.setVisibility(View.VISIBLE);
            holder.btnRechazar.setVisibility(View.VISIBLE);
            holder.btnComprobante.setVisibility(View.GONE);

        } else if (item.getEstado().equalsIgnoreCase("Aceptado")) {
            holder.btnAceptar.setVisibility(View.GONE);
            holder.btnRechazar.setVisibility(View.GONE);
            holder.btnComprobante.setVisibility(View.VISIBLE);

        } else {
            // Rechazado → no mostrar botón comprobante
            holder.btnAceptar.setVisibility(View.GONE);
            holder.btnRechazar.setVisibility(View.GONE);
            holder.btnComprobante.setVisibility(View.GONE);
        }

        // Acciones aceptar y rechazar
        holder.btnAceptar.setOnClickListener(v -> listener.onAceptar(item));
        holder.btnRechazar.setOnClickListener(v -> listener.onRechazar(item));

        // Acción ver comprobante
        holder.btnComprobante.setOnClickListener(v -> {
            Intent intent = new Intent(context, ComprobanteActivity.class);
            intent.putExtra("intercambio", item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtProductoSolicitado, txtProductoOfrecido, txtNombreUsuario, txtEstado;
        TextView txtComision; // 🔹 NUEVO
        Button btnAceptar, btnRechazar, btnComprobante;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProductoIntercambio);
            txtProductoSolicitado = itemView.findViewById(R.id.txtProductoSolicitado);
            txtProductoOfrecido = itemView.findViewById(R.id.txtProductoOfrecido);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
            txtEstado = itemView.findViewById(R.id.txtEstadoIntercambio);

            txtComision = itemView.findViewById(R.id.txtComision); // 🔹 enlace al TextView

            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
            btnComprobante = itemView.findViewById(R.id.btnComprobante);
        }
    }
}
