package com.example.proyectomoviles.model.chat;

import com.google.gson.annotations.SerializedName;

public class CrearChatGrupalResponse {
    private int code;
    private String message;
    private Data data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("id_chat_producto")
        private int idChatProducto;

        public int getIdChatProducto() { return idChatProducto; }
    }
}
