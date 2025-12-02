package com.example.proyectomoviles.model.auth;

public class SmsRequest {
    private String telefono;
    private String tipo; // "registro" o "recuperacion"

    public SmsRequest(String telefono, String tipo) {
        this.telefono = telefono;
        this.tipo = tipo;
    }

    // Getters y Setters (Opcional, Retrofit suele usar los campos directamente,
    // pero es buena práctica tenerlos si necesitas acceder a los datos después)
    public String getTelefono() {
        return telefono;
    }

    public String getTipo() {
        return tipo;
    }
}