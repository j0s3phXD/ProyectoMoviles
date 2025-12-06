package com.example.proyectomoviles.model.auth;

public class VerificationRequest {
    private String telefono;
    private String codigo;
    private String tipo;

    public VerificationRequest(String telefono, String codigo, String tipo) {
        this.telefono = telefono;
        this.codigo = codigo;
        this.tipo = tipo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTipo() {
        return tipo;
    }
}