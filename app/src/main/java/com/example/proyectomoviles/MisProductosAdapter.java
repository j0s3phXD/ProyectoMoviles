package com.example.proyectomoviles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.model.ProductoEntry;

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
                .inflate(android.R.layout.simple_list_item_activated_1, parent, false);
        return new MisProductosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MisProductosAdapter.ViewHolder holder, int position) {
        if (position == RecyclerView.NO_POSITION) return;

        ProductoEntry p = productos.get(position);
        holder.text1.setText(p.getTitulo());

        holder.itemView.setActivated(selected == position);

        holder.itemView.setOnClickListener(v -> {
            int prev = selected;
            selected = holder.getAdapterPosition();
            if (selected == RecyclerView.NO_POSITION) return;

            if (prev != -1) {
                notifyItemChanged(prev);
            }
            notifyItemChanged(selected);

            if (listener != null) {
                listener.onClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productos != null ? productos.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
        }
    }
}
