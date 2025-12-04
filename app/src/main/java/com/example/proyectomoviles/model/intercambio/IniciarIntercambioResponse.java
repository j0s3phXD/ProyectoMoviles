package com.example.proyectomoviles.model.intercambio;

public class IniciarIntercambioResponse {
    private int code;
    private String message;
    private Data data;

    public static class Data {
        private int id_intercambio;
        private double comision_monto;       // Nuevo
        private String comision_estado;      // Nuevo

        public int getId_intercambio() {
            return id_intercambio;
        }

        public double getComision_monto() {
            return comision_monto;
        }

        public String getComision_estado() {
            return comision_estado;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }
}
