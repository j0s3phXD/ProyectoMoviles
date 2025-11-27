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

import com.example.proyectomoviles.model.IntercambioEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.squareup.picasso.Picasso;

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

        // Imagen del producto solicitado
        String urlImagen = item.getImagen_solicitado();
        if (urlImagen != null && !urlImagen.isEmpty()) {

            if (!urlImagen.startsWith("http")) {
                urlImagen = RetrofitClient.BASE_URL + "uploads/productos/" + urlImagen;
            }

            Picasso.get()
                    .load(urlImagen)
                    .placeholder(R.drawable.logo_registrar)
                    .error(R.drawable.logo_registrar)
                    .into(holder.imgProducto);
        } else {
            holder.imgProducto.setImageResource(R.drawable.logo_registrar);
        }


        // Mostrar botón solo si el estado es "Aceptado"
        if (item.getEstado().equalsIgnoreCase("Aceptado")) {
            holder.btnComprobante.setVisibility(View.VISIBLE);
        } else {
            holder.btnComprobante.setVisibility(View.GONE);
        }

        // Acción del comprobante
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtProductoSolicitado, txtProductoOfrecido, txtNombreUsuario, txtEstado;
        Button btnComprobante;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProductoIntercambio);
            txtProductoSolicitado = itemView.findViewById(R.id.txtProductoSolicitado);
            txtProductoOfrecido = itemView.findViewById(R.id.txtProductoOfrecido);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
            txtEstado = itemView.findViewById(R.id.txtEstadoIntercambio);

            btnComprobante = itemView.findViewById(R.id.btnComprobante);
        }
    }
}
