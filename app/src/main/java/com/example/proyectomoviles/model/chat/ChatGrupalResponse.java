package com.example.proyectomoviles.model.chat;

import java.util.List;

public class ChatGrupalResponse {
    private int code;
    private String message;
    private List<ChatGrupal> data;

    // Getters
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public List<ChatGrupal> getData() { return data; }
}
