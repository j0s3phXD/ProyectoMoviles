package com.example.proyectomoviles.model;

import java.util.List;

public class CategoriaSeccion {

    private String nombreCategoria;
    private List<ProductoEntry> productos;

    public CategoriaSeccion(String nombreCategoria, List<ProductoEntry> productos) {
        this.nombreCategoria = nombreCategoria;
        this.productos = productos;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public List<ProductoEntry> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoEntry> productos) {
        this.productos = productos;
    }
}
