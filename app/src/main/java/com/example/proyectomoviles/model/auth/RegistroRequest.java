package com.example.proyectomoviles.model.auth;

public class RegistroRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String password;

    public RegistroRequest(String nombre, String apellido, String email, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
    }
}
