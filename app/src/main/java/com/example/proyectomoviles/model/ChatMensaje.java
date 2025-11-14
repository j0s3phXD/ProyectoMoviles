package com.example.proyectomoviles.model;

public class ChatMensaje {
    private int idUsuario;
    private String texto;
    private long timestamp;

    public ChatMensaje() {} // Requerido por Firebase

    public ChatMensaje(int idUsuario, String texto, long timestamp) {
        this.idUsuario = idUsuario;
        this.texto = texto;
        this.timestamp = timestamp;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
