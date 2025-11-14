package com.example.proyectomoviles.model;

public class IntercambioEntry {
    private int id_intercambio;
    private int id_usuario_origen;
    private int id_usuario_destino;
    private int id_producto_solicitado;
    private int id_producto_ofrecido;
    private String nombre_origen;
    private String nombre_destino;
    private String producto_solicitado;
    private String producto_ofrecido;

    // Getters y setters
    public int getId_intercambio() { return id_intercambio; }
    public void setId_intercambio(int id_intercambio) { this.id_intercambio = id_intercambio; }

    public int getId_usuario_origen() { return id_usuario_origen; }
    public void setId_usuario_origen(int id_usuario_origen) { this.id_usuario_origen = id_usuario_origen; }

    public int getId_usuario_destino() { return id_usuario_destino; }
    public void setId_usuario_destino(int id_usuario_destino) { this.id_usuario_destino = id_usuario_destino; }

    public int getId_producto_solicitado() { return id_producto_solicitado; }
    public void setId_producto_solicitado(int id_producto_solicitado) { this.id_producto_solicitado = id_producto_solicitado; }

    public int getId_producto_ofrecido() { return id_producto_ofrecido; }
    public void setId_producto_ofrecido(int id_producto_ofrecido) { this.id_producto_ofrecido = id_producto_ofrecido; }

    public String getNombre_origen() { return nombre_origen; }
    public void setNombre_origen(String nombre_origen) { this.nombre_origen = nombre_origen; }

    public String getNombre_destino() { return nombre_destino; }
    public void setNombre_destino(String nombre_destino) { this.nombre_destino = nombre_destino; }

    public String getProducto_solicitado() { return producto_solicitado; }
    public void setProducto_solicitado(String producto_solicitado) { this.producto_solicitado = producto_solicitado; }

    public String getProducto_ofrecido() {
        return producto_ofrecido;
    }

    public void setProducto_ofrecido(String producto_ofrecido) {
        this.producto_ofrecido = producto_ofrecido;
    }
}
