package com.example.proyectomoviles.model.auth;

public class SmsRequest {
    private String telefono;
    private String tipo; // "registro" o "recuperacion"

    public SmsRequest(String telefono, String tipo) {
        this.telefono = telefono;
        this.tipo = tipo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getTipo() {
        return tipo;
    }
}