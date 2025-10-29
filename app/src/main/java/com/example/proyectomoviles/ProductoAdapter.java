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
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private final List<ProductoEntry> listaProductos;
    private final OnItemClickListener listener;

    // Constructor
    public ProductoAdapter(List<ProductoEntry> listaProductos, OnItemClickListener listener) {
        this.listaProductos = listaProductos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        ProductoEntry producto = listaProductos.get(position);
        holder.bind(producto, listener);
    }

    @Override
    public int getItemCount() {
        return listaProductos != null ? listaProductos.size() : 0;
    }

    // Interfaz para manejar clics en editar y eliminar
    public interface OnItemClickListener {
        void onEditarClick(ProductoEntry producto);
        void onEliminarClick(ProductoEntry producto);
    }

    // ViewHolder
    public static class ProductoViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtTitulo, txtDescripcion, txtCondicion;
        MaterialButton btnEditar, btnEliminar;

        @SuppressLint("WrongViewCast")
        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtCondicion = itemView.findViewById(R.id.txtCondicion);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        // Vincula los datos y los clics
        public void bind(final ProductoEntry producto, final OnItemClickListener listener) {
            txtTitulo.setText(producto.getTitulo());
            txtDescripcion.setText(producto.getDescripcion());
            txtCondicion.setText("Condición: " + producto.getCondicion());

            btnEditar.setOnClickListener(v -> listener.onEditarClick(producto));
            btnEliminar.setOnClickListener(v -> listener.onEliminarClick(producto));
        }
    }
}
