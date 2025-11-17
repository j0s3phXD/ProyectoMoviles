package com.example.proyectomoviles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectomoviles.R;
import com.example.proyectomoviles.model.IntercambioEntry;

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

        holder.txtProductoSolicitado.setText("Te solicitaron: " + item.getProducto_solicitado());
        holder.txtProductoOfrecido.setText("A cambio de: " + item.getProducto_ofrecido());
        holder.txtNombreUsuario.setText("Usuario: " + item.getNombre_origen());
        holder.txtEstado.setText("Estado: " + item.getEstado());

        holder.btnAceptar.setOnClickListener(v -> listener.onAceptar(item));
        holder.btnRechazar.setOnClickListener(v -> listener.onRechazar(item));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtProductoSolicitado, txtProductoOfrecido, txtNombreUsuario, txtEstado;
        Button btnAceptar, btnRechazar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProductoIntercambio);
            txtProductoSolicitado = itemView.findViewById(R.id.txtProductoSolicitado);
            txtProductoOfrecido = itemView.findViewById(R.id.txtProductoOfrecido);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
            txtEstado = itemView.findViewById(R.id.txtEstadoIntercambio);

            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }
}
