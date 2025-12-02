package com.example.proyectomoviles.model.calificacion;

public class CalificacionPromedioResponse {

    private int code;       // 1 = OK, 0 = error
    private float promedio;
    private int total;

    public int getCode() {
        return code;
    }
    public float getPromedio() {
        return promedio;
    }
    public int getTotal() {
        return total;
    }

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
