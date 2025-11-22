package com.example.proyectomoviles.model;

public class AuthResponse {
    private String access_token;
    private int id_usuario;
    private String nombre;
    private String apellido;

    public String getAccess_token() {
        return access_token;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }
}

