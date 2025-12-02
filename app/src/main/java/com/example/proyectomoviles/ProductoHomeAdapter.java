package com.example.proyectomoviles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.model.producto.ProductoEntry;
import java.util.List;

public class ProductoHomeAdapter
        extends RecyclerView.Adapter<ProductoHomeAdapter.ProductoViewHolder> {

    private List<ProductoEntry> listaProductos;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ProductoEntry producto);
    }

    public ProductoHomeAdapter(List<ProductoEntry> listaProductos,
                               OnItemClickListener listener) {
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
                .inflate(R.layout.item_producto_publico, parent, false);
        return new ProductoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        ProductoEntry p = listaProductos.get(position);

        holder.txtTitulo.setText(p.getTitulo());
        holder.txtDescripcion.setText(p.getDescripcion());
        holder.txtCondicion.setText("Condición: " + p.getCondicion());

        holder.txtCategoria.setText(p.getDes_categoria());

        holder.txtUsuario.setText("Publicado por: " + p.getNombre_usuario());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(p));
    }

    @Override
    public int getItemCount() {
        return listaProductos != null ? listaProductos.size() : 0;
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtTitulo, txtDescripcion, txtCondicion, txtCategoria, txtUsuario;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtCondicion = itemView.findViewById(R.id.txtCondicion);

            txtCategoria = itemView.findViewById(R.id.txtCategoria);

            txtUsuario = itemView.findViewById(R.id.txtUsuario);
        }
    }
}

