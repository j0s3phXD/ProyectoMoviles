package com.example.proyectomoviles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.model.producto.ProductoEntry;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MisProductosAdapter extends RecyclerView.Adapter<MisProductosAdapter.ViewHolder> {

    private final List<ProductoEntry> productos;
    private final OnProductoClick listener;
    private int selected = -1;

    public interface OnProductoClick {
        void onClick(ProductoEntry prod);
    }

    public MisProductosAdapter(List<ProductoEntry> productos, OnProductoClick listener) {
        this.productos = productos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MisProductosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mi_producto, parent, false);
        return new MisProductosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MisProductosAdapter.ViewHolder holder, int position) {
        ProductoEntry p = productos.get(position);

        holder.tvNombre.setText(p.getTitulo());

        // Cargar foto desde servidor
        String urlFoto = RetrofitClient.BASE_URL + "uploads/productos/" + p.getFoto();
        Glide.with(holder.itemView.getContext())
                .load(urlFoto)
                .placeholder(R.drawable.image_placeholder) // opcional
                .error(R.drawable.image_placeholder)
                .centerCrop()
                .into(holder.imgProducto);

        // Selección visual
        holder.cardView.setStrokeWidth(selected == position ? 6 : 0);
        holder.cardView.setStrokeColor(
                holder.itemView.getResources().getColor(R.color.purple_500)
        );

        holder.itemView.setOnClickListener(v -> {
            int prev = selected;
            selected = holder.getAdapterPosition();

            notifyItemChanged(prev);
            notifyItemChanged(selected);

            if (listener != null) listener.onClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return productos != null ? productos.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        ImageView imgProducto;
        TextView tvNombre;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardMiProducto);
            imgProducto = itemView.findViewById(R.id.imgMiProducto);
            tvNombre = itemView.findViewById(R.id.tvNombreMiProducto);
        }
    }
}
