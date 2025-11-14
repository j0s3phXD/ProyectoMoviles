package com.example.proyectomoviles.model;

public class IniciarIntercambioRequest {
    private int id_usuario_destino;
    private int id_producto_solicitado;

    public IniciarIntercambioRequest(int id_usuario_destino, int id_producto_solicitado) {
        this.id_usuario_destino = id_usuario_destino;
        this.id_producto_solicitado = id_producto_solicitado;
    }
}
