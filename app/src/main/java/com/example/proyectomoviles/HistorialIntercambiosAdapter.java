package com.example.proyectomoviles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.model.IntercambioEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistorialIntercambiosAdapter extends RecyclerView.Adapter<HistorialIntercambiosAdapter.ViewHolder> {

    public interface OnHistorialIntercambioClick {
        void onHistorialClick(IntercambioEntry intercambio);
    }

    private Context context;
    private List<IntercambioEntry> lista;
    private OnHistorialIntercambioClick listener;

    public HistorialIntercambiosAdapter(Context context, List<IntercambioEntry> lista, OnHistorialIntercambioClick listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_historial_intercambio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        IntercambioEntry item = lista.get(position);

        holder.txtProductoSolicitado.setText("Tu producto: " + item.getProducto_solicitado());
        holder.txtProductoOfrecido.setText("Producto del otro usuario: " + item.getProducto_ofrecido());
        holder.txtNombreUsuario.setText("Usuario: " + item.getNombre_origen());

        if (item.getImagen_solicitado() != null && !item.getImagen_solicitado().isEmpty()) {
            Picasso.get().load(item.getImagen_solicitado()).into(holder.imgProducto);
        }

        holder.itemView.setOnClickListener(v -> listener.onHistorialClick(item));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtProductoSolicitado, txtProductoOfrecido, txtNombreUsuario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProductoIntercambio);
            txtProductoSolicitado = itemView.findViewById(R.id.txtProductoSolicitado);
            txtProductoOfrecido = itemView.findViewById(R.id.txtProductoOfrecido);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
        }
    }
}
