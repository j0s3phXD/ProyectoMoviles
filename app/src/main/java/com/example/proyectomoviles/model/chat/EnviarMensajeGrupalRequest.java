package com.example.proyectomoviles.model.chat;

public class EnviarMensajeGrupalRequest {
    private String mensaje;

    public EnviarMensajeGrupalRequest(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
