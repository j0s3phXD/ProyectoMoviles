package com.example.proyectomoviles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.model.categoria.CategoriaSeccion;
import com.example.proyectomoviles.model.producto.ProductoEntry;

import java.util.ArrayList;
import java.util.List;

public class CategoriaSeccionAdapter extends RecyclerView.Adapter<CategoriaSeccionAdapter.SeccionViewHolder> {

    private List<CategoriaSeccion> listaSecciones;
    private final ProductoHorizontalAdapter.OnItemClickListener listener;

    public CategoriaSeccionAdapter(List<CategoriaSeccion> listaSecciones,
                                   ProductoHorizontalAdapter.OnItemClickListener listener) {
        this.listaSecciones = listaSecciones;
        this.listener = listener;
    }

    public void updateList(List<CategoriaSeccion> nuevasSecciones) {
        this.listaSecciones = nuevasSecciones;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SeccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seccion_categoria, parent, false);
        return new SeccionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull SeccionViewHolder holder, int position) {
        CategoriaSeccion seccion = listaSecciones.get(position);
        holder.tvTituloCategoria.setText(seccion.getNombreCategoria());

        LinearLayoutManager lm = new LinearLayoutManager(
                holder.itemView.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        holder.rvProductos.setLayoutManager(lm);
        holder.rvProductos.setHasFixedSize(true);

        ProductoHorizontalAdapter adapterHoriz =
                new ProductoHorizontalAdapter(new ArrayList<ProductoEntry>(seccion.getProductos()), listener);

        holder.rvProductos.setAdapter(adapterHoriz);
    }

    @Override
    public int getItemCount() {
        return listaSecciones != null ? listaSecciones.size() : 0;
    }

    static class SeccionViewHolder extends RecyclerView.ViewHolder {

        TextView tvTituloCategoria;
        RecyclerView rvProductos;

        public SeccionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloCategoria = itemView.findViewById(R.id.tvTituloCategoria);
            rvProductos       = itemView.findViewById(R.id.rvProductosCategoria);
        }
    }
}
