package com.example.proyectomoviles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.model.IntercambioEntry;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(ChatItem chat);
    }

    private final List<ChatItem> items;
    private final OnChatClickListener listener;

    public ChatListAdapter(List<ChatItem> items, OnChatClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatItem item = items.get(position);

        holder.txtNombreChat.setText(item.getNombreUsuarioOtro());
        holder.txtUltimoMensaje.setText(item.getUltimoMensaje());

        holder.itemView.setOnClickListener(v -> listener.onChatClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreChat, txtUltimoMensaje;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreChat = itemView.findViewById(R.id.txtNombreDestinatario);
            txtUltimoMensaje = itemView.findViewById(R.id.txtProductoSolicitado);
        }
    }
}


