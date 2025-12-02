package com.example.proyectomoviles.model.intercambio;

import java.io.Serializable;

public class IntercambioEntry implements Serializable {

    private int id_intercambio;
    private int id_usuario_origen;
    private int id_usuario_destino;
    private int id_producto_solicitado;
    private int id_producto_ofrecido;

    private String nombre_origen;        // Quien solicita
    private String nombre_destino;       // Dueño del producto
    private String producto_solicitado;  // Tu producto
    private String producto_ofrecido;    // Producto que te ofrecen
    private String estado;               // Pendiente / Aceptado / Rechazado

    private String imagen_solicitado;    // URL o nombre img (opcional)
    private String imagen_ofrecido;      // URL o nombre img (opcional)

    // ---- Getters y Setters ----
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

    public String getProducto_ofrecido() { return producto_ofrecido; }
    public void setProducto_ofrecido(String producto_ofrecido) { this.producto_ofrecido = producto_ofrecido; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getImagen_solicitado() { return imagen_solicitado; }
    public void setImagen_solicitado(String imagen_solicitado) { this.imagen_solicitado = imagen_solicitado; }

    public String getImagen_ofrecido() { return imagen_ofrecido; }
    public void setImagen_ofrecido(String imagen_ofrecido) { this.imagen_ofrecido = imagen_ofrecido; }
}
