package com.example.proyectomoviles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.model.CalificacionEntry;
import com.example.proyectomoviles.model.IntercambioEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistorialIntercambiosAdapter extends RecyclerView.Adapter<HistorialIntercambiosAdapter.ViewHolder> {

    public interface OnHistorialIntercambioClick {
        void onHistorialClick(IntercambioEntry intercambio);
    }

    private Context context;
    private List<IntercambioEntry> listaIntercambios;
    private List<CalificacionEntry> listaCalificaciones; // NUEVO
    private OnHistorialIntercambioClick listener;

    public HistorialIntercambiosAdapter(Context context,
                                        List<IntercambioEntry> listaIntercambios,
                                        List<CalificacionEntry> listaCalificaciones,
                                        OnHistorialIntercambioClick listener) {

        this.context = context;
        this.listaIntercambios = listaIntercambios;
        this.listaCalificaciones = listaCalificaciones;
        this.listener = listener;
    }

    // Permite actualizar las calificaciones cargadas luego en PerfilFragment
    public void updateCalificaciones(List<CalificacionEntry> nuevas) {
        this.listaCalificaciones = nuevas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_historial_intercambio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        IntercambioEntry item = listaIntercambios.get(position);

        holder.txtProductoSolicitado.setText("Tu producto: " + item.getProducto_solicitado());
        holder.txtProductoOfrecido.setText("Producto del otro usuario: " + item.getProducto_ofrecido());
        holder.txtNombreUsuario.setText("Usuario: " + item.getNombre_origen());

        if (item.getImagen_solicitado() != null && !item.getImagen_solicitado().isEmpty()) {
            Picasso.get().load(item.getImagen_solicitado()).into(holder.imgProducto);
        }

        // CLICK para abrir chat o vista detalle
        holder.itemView.setOnClickListener(v -> listener.onHistorialClick(item));

        // ======================================================
        //  IDENTIFICAR QUIÉN CALIFICA A QUIÉN
        // ======================================================
        SharedPreferences prefs = context.getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        int idUsuarioActual = prefs.getInt("idUsuario", -1);

        int idAutor;
        int idRecibe;
        if (idUsuarioActual == item.getId_usuario_origen()) {
            idAutor = item.getId_usuario_origen();
            idRecibe = item.getId_usuario_destino();
        } else {
            idAutor = item.getId_usuario_destino();
            idRecibe = item.getId_usuario_origen();
        }

        // ======================================================
        //  VERIFICAR SI YA CALIFICÓ ESTE INTERCAMBIO
        // ======================================================
        boolean yaCalificado = false;

        if (listaCalificaciones != null) {
            for (CalificacionEntry c : listaCalificaciones) {
                if (c.getId_intercambio() == item.getId_intercambio()
                        && c.getId_autor() == idAutor) {

                    yaCalificado = true;
                    break;
                }
            }
        }

        // ======================================================
        //  DESHABILITAR BOTÓN SI YA CALIFICÓ
        // ======================================================
        if (yaCalificado) {
            holder.btnCalificar.setEnabled(false);
            holder.btnCalificar.setText("Ya calificado");
            holder.btnCalificar.setBackgroundColor(0xFFBBBBBB);
        } else {
            holder.btnCalificar.setEnabled(true);
            holder.btnCalificar.setText("Calificar");
        }

        // ======================================================
        //  ACCIÓN DEL BOTÓN CALIFICAR
        // ======================================================
        holder.btnCalificar.setOnClickListener(v -> {

            Intent intent = new Intent(context, CalificarActivity.class);
            intent.putExtra("id_usuario_recibe", idRecibe);
            intent.putExtra("id_usuario_autor", idAutor);
            intent.putExtra("id_intercambio", item.getId_intercambio());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaIntercambios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtProductoSolicitado, txtProductoOfrecido, txtNombreUsuario;
        Button btnCalificar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProductoIntercambio);
            txtProductoSolicitado = itemView.findViewById(R.id.txtProductoSolicitado);
            txtProductoOfrecido = itemView.findViewById(R.id.txtProductoOfrecido);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);

            btnCalificar = itemView.findViewById(R.id.btnCalificar);
        }
    }
}
