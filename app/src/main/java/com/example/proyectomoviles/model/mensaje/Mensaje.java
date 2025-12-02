package com.example.proyectomoviles.model.mensaje;

public class Mensaje {
    private int id_mensaje;
    private int id_remitente;
    private String mensaje;
    private String fecha_envio;

    public int getId_mensaje() {
        return id_mensaje;
    }

    public int getId_remitente() {
        return id_remitente;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getFecha_envio() {
        return fecha_envio;
    }
}
