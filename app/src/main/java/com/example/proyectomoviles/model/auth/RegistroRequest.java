package com.example.proyectomoviles.model.auth;

public class RegistroRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;

    public RegistroRequest(String nombre, String apellido, String email, String password, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}