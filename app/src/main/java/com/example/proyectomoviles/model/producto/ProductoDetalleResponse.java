package com.example.proyectomoviles.model.producto;

public class ProductoDetalleResponse {
    private int code;
    private String message;
    private ProductoEntry data; // Aquí va un solo producto

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

    public ProductoEntry getData() {
        return data;
    }

    public void setData(ProductoEntry data) {
        this.data = data;
    }
}
