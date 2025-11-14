package com.example.proyectomoviles;

public class ChatItem {
    private int idIntercambio;         // ID único del chat/intercambio
    private int idUsuarioOtro;          // ID del otro participante
    private String nombreUsuarioOtro;   // Nombre del otro participante
    private String ultimoMensaje;       // Último mensaje para mostrar en la lista
    private long timestampUltimoMensaje; // Opcional: para ordenar por último mensaje

    public ChatItem(int idIntercambio, int idUsuarioOtro, String nombreUsuarioOtro, String ultimoMensaje, long timestampUltimoMensaje) {
        this.idIntercambio = idIntercambio;
        this.idUsuarioOtro = idUsuarioOtro;
        this.nombreUsuarioOtro = nombreUsuarioOtro;
        this.ultimoMensaje = ultimoMensaje;
        this.timestampUltimoMensaje = timestampUltimoMensaje;
    }

    // Getters y setters
    public int getIdIntercambio() {
        return idIntercambio;
    }

    public void setIdIntercambio(int idIntercambio) {
        this.idIntercambio = idIntercambio;
    }

    public int getIdUsuarioOtro() {
        return idUsuarioOtro;
    }

    public void setIdUsuarioOtro(int idUsuarioOtro) {
        this.idUsuarioOtro = idUsuarioOtro;
    }

    public String getNombreUsuarioOtro() {
        return nombreUsuarioOtro;
    }

    public void setNombreUsuarioOtro(String nombreUsuarioOtro) {
        this.nombreUsuarioOtro = nombreUsuarioOtro;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public long getTimestampUltimoMensaje() {
        return timestampUltimoMensaje;
    }

    public void setTimestampUltimoMensaje(long timestampUltimoMensaje) {
        this.timestampUltimoMensaje = timestampUltimoMensaje;
    }
}
