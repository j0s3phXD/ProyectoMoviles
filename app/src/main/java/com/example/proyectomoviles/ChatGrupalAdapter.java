package com.example.proyectomoviles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.model.chat.ChatGrupal;

import java.util.List;

public class ChatGrupalAdapter extends RecyclerView.Adapter<ChatGrupalAdapter.ViewHolder> {

    public interface OnChatGrupalClickListener {
        void onChatGrupalClick(ChatGrupal chat);
    }

    private Context context;
    private List<ChatGrupal> listaChats;
    private OnChatGrupalClickListener listener;

    public ChatGrupalAdapter(Context context, List<ChatGrupal> listaChats, OnChatGrupalClickListener listener) {
        this.context = context;
        this.listaChats = listaChats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_chat_grupal, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatGrupal item = listaChats.get(position);

        holder.txtNombreProducto.setText(item.getNombreProducto());
        holder.txtUltimoMensaje.setText(item.getUltimoMensaje());
        holder.txtCantidadParticipantes.setText("(" + item.getCantidadParticipantes() + ")");

        String urlImagen = item.getImagenProducto();
        if (urlImagen != null && !urlImagen.isEmpty()) {
            if (!urlImagen.startsWith("http")) {
                urlImagen = RetrofitClient.BASE_URL + "uploads/productos/" + urlImagen;
            }
            Glide.with(context)
                    .load(urlImagen)
                    .placeholder(R.drawable.logo_publicar)
                    .error(R.drawable.logo_publicar)
                    .into(holder.imgProducto);
        } else {
            holder.imgProducto.setImageResource(R.drawable.logo_publicar);
        }

        holder.itemView.setOnClickListener(v -> listener.onChatGrupalClick(item));
    }

    @Override
    public int getItemCount() {
        return listaChats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        TextView txtNombreProducto, txtUltimoMensaje, txtCantidadParticipantes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtNombreProducto = itemView.findViewById(R.id.txtNombreProducto);
            txtUltimoMensaje = itemView.findViewById(R.id.txtUltimoMensaje);
            txtCantidadParticipantes = itemView.findViewById(R.id.txtCantidadParticipantes);
        }
    }
}
