package com.example.proyectomoviles.model.chat;

import com.google.gson.annotations.SerializedName;

public class MensajeGrupal {
    @SerializedName("id_mensaje")
    private int idMensaje;

    @SerializedName("id_usuario")
    private int idUsuario;

    private String mensaje;

    @SerializedName("fecha_envio")
    private String fechaEnvio;

    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    // Constructor vacío requerido para Firebase
    public MensajeGrupal() {}

    public int getIdMensaje() { return idMensaje; }
    public int getIdUsuario() { return idUsuario; }
    public String getMensaje() { return mensaje; }
    public String getFechaEnvio() { return fechaEnvio; }
    public String getNombreUsuario() { return nombreUsuario; }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
}
