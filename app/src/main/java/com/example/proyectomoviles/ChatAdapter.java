package com.example.proyectomoviles;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.model.intercambio.IntercambioEntry;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(IntercambioEntry intercambio);
    }

    private Context context;
    private List<IntercambioEntry> listaIntercambios;
    private OnChatClickListener listener;

    public ChatAdapter(Context context, List<IntercambioEntry> listaIntercambios, OnChatClickListener listener) {
        this.context = context;
        this.listaIntercambios = listaIntercambios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IntercambioEntry item = listaIntercambios.get(position);

        holder.txtNombreUsuario.setText(item.getNombreOtro());
        holder.txtProducto.setText(item.getProductoOtro());

        String urlImagen = item.getImagenOtro();
        if (urlImagen != null && !urlImagen.isEmpty()) {
            if (!urlImagen.startsWith("http")) {
                urlImagen = RetrofitClient.BASE_URL + "uploads/usuarios/" + urlImagen;
            }
            Glide.with(context)
                    .load(urlImagen)
                    .placeholder(R.drawable.logo_perfil)
                    .error(R.drawable.logo_perfil)
                    .into(holder.imgUsuario);
        } else {
            holder.imgUsuario.setImageResource(R.drawable.logo_perfil);
        }

        holder.itemView.setOnClickListener(v -> listener.onChatClick(item));
    }

    @Override
    public int getItemCount() {
        return listaIntercambios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUsuario;
        TextView txtNombreUsuario, txtProducto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUsuario = itemView.findViewById(R.id.imgUsuario);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
            txtProducto = itemView.findViewById(R.id.txtProducto);
        }
    }
}
