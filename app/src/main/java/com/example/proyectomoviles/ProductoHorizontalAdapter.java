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

import java.util.List;

public class ProductoHorizontalAdapter extends RecyclerView.Adapter<ProductoHorizontalAdapter.ProductoViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ProductoEntry producto);
    }

    private List<ProductoEntry> listaProductos;
    private final OnItemClickListener listener;

    public ProductoHorizontalAdapter(List<ProductoEntry> listaProductos, OnItemClickListener listener) {
        this.listaProductos = listaProductos;
        this.listener = listener;
    }

    public void updateList(List<ProductoEntry> nuevaLista) {
        this.listaProductos = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_horizontal, parent, false);
        return new ProductoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        ProductoEntry producto = listaProductos.get(position);

        holder.txtTitulo.setText(producto.getTitulo());
        holder.txtDescripcion.setText(producto.getDescripcion());
        holder.txtCondicion.setText("Condición: " + producto.getCondicion());

        String categoria = producto.getDes_categoria();
        if (categoria != null && !categoria.trim().isEmpty()) {
            holder.txtCategoria.setText(categoria);
            holder.txtCategoria.setVisibility(View.VISIBLE);
        } else {
            holder.txtCategoria.setVisibility(View.GONE);
        }

        String nombreFoto = producto.getFoto();
        if (nombreFoto != null && !nombreFoto.isEmpty()) {
            String urlFoto = RetrofitClient.BASE_URL
                    + "uploads/productos/"
                    + nombreFoto;

            Glide.with(holder.itemView.getContext())
                    .load(urlFoto)
                    .centerCrop()
                    .into(holder.imgProducto);
        } else {
            holder.imgProducto.setImageResource(0);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(producto));
    }

    @Override
    public int getItemCount() {
        return listaProductos != null ? listaProductos.size() : 0;
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtTitulo, txtDescripcion, txtCondicion, txtCategoria;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProducto    = itemView.findViewById(R.id.imgProducto);
            txtTitulo      = itemView.findViewById(R.id.txtTitulo);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtCondicion   = itemView.findViewById(R.id.txtCondicion);
            txtCategoria   = itemView.findViewById(R.id.txtCategoria);
        }
    }
}
