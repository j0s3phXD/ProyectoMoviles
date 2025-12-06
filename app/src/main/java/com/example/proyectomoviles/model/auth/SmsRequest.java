package com.example.proyectomoviles.model.auth;

public class SmsRequest {
    private String telefono;
    private String tipo;

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