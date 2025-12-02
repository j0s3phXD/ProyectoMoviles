package com.example.proyectomoviles.model.intercambio;

public class IniciarIntercambioRequest {

    private int id_usuario_destino;
    private int id_producto_solicitado;
    private int id_producto_ofrecido;

    public IniciarIntercambioRequest(int id_usuario_destino, int id_producto_solicitado, int id_producto_ofrecido) {
        this.id_usuario_destino = id_usuario_destino;
        this.id_producto_solicitado = id_producto_solicitado;
        this.id_producto_ofrecido = id_producto_ofrecido;
    }

    public int getId_usuario_destino() { return id_usuario_destino; }
    public int getId_producto_solicitado() { return id_producto_solicitado; }
    public int getId_producto_ofrecido() { return id_producto_ofrecido; }
}
