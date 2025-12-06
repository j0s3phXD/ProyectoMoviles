package com.example.proyectomoviles.model.auth;

public class RegistroRequest {

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;
    private String dni;

    public RegistroRequest(String nombre, String apellido, String email,
                           String password, String telefono, String dni) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.dni = dni;
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

    public String getPassword() {
        return password;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDni() {
        return dni;
    }
}
