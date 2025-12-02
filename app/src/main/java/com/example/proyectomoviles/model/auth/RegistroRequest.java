package com.example.proyectomoviles.model.auth;

public class RegistroRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    // 1. Agregamos la variable para el teléfono
    private String telefono;

    // 2. Actualizamos el constructor para recibir el teléfono
    public RegistroRequest(String nombre, String apellido, String email, String password, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
    }

    // (Opcional) Si usas Getters y Setters en otro lado, agrégalos aquí también.
    // Para enviar los datos con Retrofit, con el constructor y las variables es suficiente.
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}