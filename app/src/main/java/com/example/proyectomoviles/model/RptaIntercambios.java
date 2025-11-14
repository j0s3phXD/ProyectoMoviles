package com.example.proyectomoviles.model;

import java.util.List;

public class RptaIntercambios {
    private int code;
    private String message;
    private List<IntercambioEntry> data;

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

    public List<IntercambioEntry> getData() {
        return data;
    }

    public void setData(List<IntercambioEntry> data) {
        this.data = data;
    }
}
