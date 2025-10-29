package com.example.proyectomoviles;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomoviles.R;

public class ProductoViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgProducto;
    public TextView txtTitulo, txtDescripcion, txtCondicion;
    public Button btnEditar, btnEliminar;

    public ProductoViewHolder(View itemView) {
        super(itemView);
        imgProducto = itemView.findViewById(R.id.imgProducto);
        txtTitulo = itemView.findViewById(R.id.txtTitulo);
        txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
        txtCondicion = itemView.findViewById(R.id.txtCondicion);
        btnEditar = itemView.findViewById(R.id.btnEditar);
        btnEliminar = itemView.findViewById(R.id.btnEliminar);
    }
}
