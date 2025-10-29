package com.example.proyectomoviles.model;

import java.util.List;

public class CategoriaResponse {
    private int code;
    private String message;
    private List<CategoriaRequest> data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<CategoriaRequest> getData() {
        return data;
    }
}
