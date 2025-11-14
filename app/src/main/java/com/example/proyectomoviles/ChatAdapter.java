package com.example.proyectomoviles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.model.ChatMensaje;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMensaje> mensajes = new ArrayList<>();
    private int miIdUsuario;

    public ChatAdapter(int miIdUsuario) {
        this.miIdUsuario = miIdUsuario;
    }

    public void setMensajes(List<ChatMensaje> mensajes) {
        this.mensajes = mensajes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_mensaje, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMensaje msg = mensajes.get(position);
        holder.txtMensaje.setText(msg.getTexto());

        if(msg.getIdUsuario() == miIdUsuario){
            holder.txtMensaje.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        } else {
            holder.txtMensaje.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensaje);
        }
    }
}
