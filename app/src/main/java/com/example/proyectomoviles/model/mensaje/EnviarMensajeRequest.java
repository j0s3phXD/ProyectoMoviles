package com.example.proyectomoviles.model.mensaje;

public class EnviarMensajeRequest {
    private int id_intercambio;
    private String mensaje;

    public EnviarMensajeRequest(int id_intercambio, String mensaje) {
        this.id_intercambio = id_intercambio;
        this.mensaje = mensaje;
    }

    public int getId_intercambio() {
        return id_intercambio;
    }

    public String getMensaje() {
        return mensaje;
    }
}
