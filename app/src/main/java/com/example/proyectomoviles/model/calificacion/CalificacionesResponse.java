package com.example.proyectomoviles.model.calificacion;

import java.util.List;

public class CalificacionesResponse {
    private int code;
    private List<CalificacionEntry> data;

    public int getCode() {
        return code;
    }

    public List<CalificacionEntry> getData() {
        return data;
    }
}
