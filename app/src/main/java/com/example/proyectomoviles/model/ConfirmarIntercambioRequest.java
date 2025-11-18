package com.example.proyectomoviles.model;

public class ConfirmarIntercambioRequest {

    private int id_intercambio;
    private String estado;

    public ConfirmarIntercambioRequest(int id_intercambio, String estado) {
        this.id_intercambio = id_intercambio;
        this.estado = estado;
    }

    public int getId_intercambio() { return id_intercambio; }
    public String getEstado() { return estado; }
}