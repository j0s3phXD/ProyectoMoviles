package com.example.proyectomoviles;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.model.chat.MensajeGrupal;

import java.util.List;

public class MensajesGrupalesAdapter extends RecyclerView.Adapter<MensajesGrupalesAdapter.ViewHolder> {

    private static final int VIEW_TYPE_ENVIADO = 1;
    private static final int VIEW_TYPE_RECIBIDO = 2;

    private Context context;
    private List<MensajeGrupal> listaMensajes;
    private int idUsuarioActual;

    public MensajesGrupalesAdapter(Context context, List<MensajeGrupal> listaMensajes) {
        this.context = context;
        this.listaMensajes = listaMensajes;

        SharedPreferences prefs = context.getSharedPreferences("SP_SWAPLY", Context.MODE_PRIVATE);
        this.idUsuarioActual = prefs.getInt("idUsuario", -1);
    }

    @Override
    public int getItemViewType(int position) {
        if (listaMensajes.get(position).getIdUsuario() == idUsuarioActual) {
            return VIEW_TYPE_ENVIADO;
        }
        return VIEW_TYPE_RECIBIDO;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_ENVIADO) {
            view = LayoutInflater.from(context).inflate(R.layout.item_mensaje_grupal_enviado, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_mensaje_grupal_recibido, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MensajeGrupal mensaje = listaMensajes.get(position);

        holder.txtMensaje.setText(mensaje.getMensaje());

        if (getItemViewType(position) == VIEW_TYPE_RECIBIDO) {
            holder.txtNombreUsuario.setText(mensaje.getNombreUsuario());
        }
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public void addMensaje(MensajeGrupal mensaje) {
        listaMensajes.add(mensaje);
        notifyItemInserted(listaMensajes.size() - 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje, txtNombreUsuario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensaje);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
        }
    }
}
