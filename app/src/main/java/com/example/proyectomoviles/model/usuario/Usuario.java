package com.example.proyectomoviles.model.usuario;

public class Usuario {
    private int id_usuario;
    private String nombre;
    private String apellido;
    private String email;
    private String dni;  // 👈 NUEVO

    public int getId_usuario() {
        return id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getDni() {
        return dni;
    }
}
