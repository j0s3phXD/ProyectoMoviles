package com.example.proyectomoviles.model;

public class PublicarRequest {
    private int id_producto;
    private String titulo;
    private String descripcion;
    private int id_categoria;
    private String condicion;
    private String intercambio_deseado;
    private String foto;

    public PublicarRequest(String titulo, String descripcion, String condicion, int id_categoria, String intercambio_deseado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.condicion = condicion;
        this.id_categoria = id_categoria;
        this.intercambio_deseado = intercambio_deseado;
        this.foto = null;
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

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getIntercambio_deseado() {
        return intercambio_deseado;
    }

    public void setIntercambio_deseado(String intercambio_deseado) {
        this.intercambio_deseado = intercambio_deseado;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }
}
