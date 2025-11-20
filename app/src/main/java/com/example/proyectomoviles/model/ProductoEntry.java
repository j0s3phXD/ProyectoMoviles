package com.example.proyectomoviles.model;

import java.io.Serializable;

public class ProductoEntry implements Serializable {
    private int id_producto;
    private String titulo;
    private String descripcion;
    private String condicion;
    private CategoriaRequest categoria;
    private String intercambio_deseado;
    private int id_usuario;

    private String des_categoria;
    private int id_categoria;

    private String nombre_usuario;
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

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getNombre_usuario() { return nombre_usuario; }
    public void setNombre_usuario(String nombre_usuario) { this.nombre_usuario = nombre_usuario; }

    public String getDes_categoria() { return des_categoria; }
    public void setDes_categoria(String des_categoria) { this.des_categoria = des_categoria; }

    public int getId_categoria() { return id_categoria; }
    public void setId_categoria(int id_categoria) { this.id_categoria = id_categoria; }

}

