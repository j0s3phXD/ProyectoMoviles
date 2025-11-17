package com.example.proyectomoviles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectomoviles.R;
import com.example.proyectomoviles.model.IntercambioEntry;

import java.util.List;

public class IntercambiosEnviadosAdapter extends RecyclerView.Adapter<IntercambiosEnviadosAdapter.ViewHolder> {

    private Context context;
    private List<IntercambioEntry> lista;

    public IntercambiosEnviadosAdapter(Context context, List<IntercambioEntry> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_intercambio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        IntercambioEntry item = lista.get(position);

        holder.txtProductoSolicitado.setText("Solicitaste: " + item.getProducto_solicitado());
        holder.txtProductoOfrecido.setText("Ofreciste: " + item.getProducto_ofrecido());
        holder.txtNombreUsuario.setText("Para: " + item.getNombre_destino());
        holder.txtEstado.setText("Estado: " + item.getEstado());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtProductoSolicitado, txtProductoOfrecido, txtNombreUsuario, txtEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProductoIntercambio);
            txtProductoSolicitado = itemView.findViewById(R.id.txtProductoSolicitado);
            txtProductoOfrecido = itemView.findViewById(R.id.txtProductoOfrecido);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
            txtEstado = itemView.findViewById(R.id.txtEstadoIntercambio);
        }
    }
}

