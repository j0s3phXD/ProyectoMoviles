package com.example.proyectomoviles.model;

import java.util.List;

public class RptaProducto {
    private int code;
    private String message;
    private List<ProductoEntry> data;

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

    public List<ProductoEntry> getData() {
        return data;
    }

    public void setData(List<ProductoEntry> data) {
        this.data = data;
    }
}

