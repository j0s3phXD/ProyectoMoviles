package com.example.proyectomoviles.model;

import java.io.Serializable;

public class ProductoEntry implements Serializable {
    private int id_producto;
    private String titulo;
    private String descripcion;
    private String condicion;
    private CategoriaRequest categoria;
    private String intercambio_deseado;

    public ProductoEntry(int id_producto, String titulo, String condicion, String descripcion, CategoriaRequest categoria, String intercambio_deseado) {
        this.id_producto = id_producto;
        this.titulo = titulo;
        this.condicion = condicion;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.intercambio_deseado = intercambio_deseado;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public CategoriaRequest getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaRequest categoria) {
        this.categoria = categoria;
    }

    public String getIntercambio_deseado() {
        return intercambio_deseado;
    }

    public void setIntercambio_deseado(String intercambio_deseado) {
        this.intercambio_deseado = intercambio_deseado;
    }
}

