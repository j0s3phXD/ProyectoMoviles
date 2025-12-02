package com.example.proyectomoviles.model.auth;

public class RestablecerPasswordRequest {
    // ¡OJO AQUÍ! El nombre debe ser exacto para que coincida con Python
    private String nueva_password;
    private String email;
    private String codigo;
    private String telefono;

    public RestablecerPasswordRequest(String email, String codigo, String nueva_password, String telefono) {
        this.email = email;
        this.codigo = codigo;
        this.nueva_password = nueva_password;
        this.telefono = telefono;
    }
}