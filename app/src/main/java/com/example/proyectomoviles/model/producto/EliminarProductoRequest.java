package com.example.proyectomoviles.model.producto;

public class EliminarProductoRequest {
    private int id_producto;

    public EliminarProductoRequest(int id_producto) {
        this.id_producto = id_producto;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }
}
