package com.example.proyectomoviles.model;

public class RptaCalificacionPromedio {

    private int code;       // 1 = OK, 0 = error
    private float promedio; // promedio de estrellas
    private int total;      // cuántas calificaciones tiene

    // ====== GETTERS ======

    public int getCode() {
        return code;
    }

    public float getPromedio() {
        return promedio;
    }

    public int getTotal() {
        return total;
    }

    // ====== SETTERS (necesarios para deserializar Retrofit) ======

    public void setCode(int code) {
        this.code = code;
    }

    public void setPromedio(float promedio) {
        this.promedio = promedio;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
