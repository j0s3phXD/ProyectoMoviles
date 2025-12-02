package com.example.proyectomoviles.model.mensaje;

import java.util.List;

public class MensajesResponse {
    private int code;
    private String message;
    private List<Mensaje> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Mensaje> getData() {
        return data;
    }

    public void setData(List<Mensaje> data) {
        this.data = data;
    }
}
