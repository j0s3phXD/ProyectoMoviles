package com.example.proyectomoviles.model.intercambio;

public class IniciarIntercambioResponse {
    private int code;
    private String message;
    private Data data;

    public class Data {
        private int id_intercambio;

        public int getId_intercambio() {
            return id_intercambio;
        }
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public Data getData() { return data; }
}
