package com.example.proyectomoviles.model.chat;

import java.util.List;

public class MensajesGrupalesResponse {
    private int code;
    private String message;
    private List<MensajeGrupal> data;

    // Getters
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public List<MensajeGrupal> getData() { return data; }
}
