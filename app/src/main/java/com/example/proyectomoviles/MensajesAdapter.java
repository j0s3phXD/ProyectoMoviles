package com.example.proyectomoviles;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.model.mensaje.Mensaje;

import java.util.List;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.ViewHolder> {

    private static final int VIEW_TYPE_ENVIADO = 1;
    private static final int VIEW_TYPE_RECIBIDO = 2;

    private Context context;
    private List<Mensaje> listaMensajes;
    private int idUsuarioActual;

    public MensajesAdapter(Context context, List<Mensaje> listaMensajes, int idUsuarioActual) {
        this.context = context;
        this.listaMensajes = listaMensajes;
        this.idUsuarioActual = idUsuarioActual;
    }

    @Override
    public int getItemViewType(int position) {
        int senderId = listaMensajes.get(position).getId_remitente();
        Log.d("MensajesAdapter", "ID Remitente: " + senderId + ", ID Usuario Actual: " + idUsuarioActual);

        if (senderId == idUsuarioActual) {
            return VIEW_TYPE_ENVIADO;
        } else {
            return VIEW_TYPE_RECIBIDO;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_ENVIADO) {
            view = LayoutInflater.from(context).inflate(R.layout.item_mensaje_enviado, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_mensaje_recibido, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mensaje mensaje = listaMensajes.get(position);
        holder.txtMensaje.setText(mensaje.getMensaje());
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensaje);
        }
    }
}
