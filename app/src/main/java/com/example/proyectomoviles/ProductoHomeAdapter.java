package com.example.proyectomoviles;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.model.ProductoEntry;
import java.util.List;

public class ProductoHomeAdapter extends RecyclerView.Adapter<ProductoHomeAdapter.ProductoViewHolder> {

    private final List<ProductoEntry> listaProductos;

    public ProductoHomeAdapter(List<ProductoEntry> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_publico, parent, false);
        return new ProductoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        ProductoEntry producto = listaProductos.get(position);

        holder.txtTitulo.setText(producto.getTitulo());
        holder.txtDescripcion.setText(producto.getDescripcion());
        holder.txtCondicion.setText("Condición: " + producto.getCondicion());

        // Si tienes imagen, puedes cargarla con Glide (opcional)
        // Glide.with(holder.itemView.getContext())
        //      .load(producto.getUrlImagen())
        //      .placeholder(R.drawable.placeholder)
        //      .into(holder.imgProducto);
    }

    @Override
    public int getItemCount() {
        return listaProductos != null ? listaProductos.size() : 0;
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        TextView txtTitulo, txtDescripcion, txtCondicion;

        @SuppressLint("WrongViewCast")
        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtCondicion = itemView.findViewById(R.id.txtCondicion);
        }
    }
}
