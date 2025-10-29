package com.example.proyectomoviles.model;

public class CategoriaRequest {
    private int id_categoria;
    private String des_categoria;

    public int getId_categoria() {
        return id_categoria;
    }

    public String getDes_categoria() {
        return des_categoria;
    }

    @Override
    public String toString() {
        return des_categoria;
    }
}
