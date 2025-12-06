package com.example.proyectomoviles.model.chat;

import com.google.gson.annotations.SerializedName;

public class ChatGrupal {
    @SerializedName("id_chat_producto")
    private int idChatProducto;

    @SerializedName("nombre_producto")
    private String nombreProducto;

    @SerializedName("imagen_producto")
    private String imagenProducto;

    @SerializedName("ultimo_mensaje")
    private String ultimoMensaje;

    @SerializedName("ultima_fecha")
    private String ultimaFecha;

    @SerializedName("cantidad_participantes")
    private int cantidadParticipantes;

    // Getters
    public int getIdChatProducto() { return idChatProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public String getImagenProducto() { return imagenProducto; }
    public String getUltimoMensaje() { return ultimoMensaje; }
    public String getUltimaFecha() { return ultimaFecha; }
    public int getCantidadParticipantes() { return cantidadParticipantes; }
}
