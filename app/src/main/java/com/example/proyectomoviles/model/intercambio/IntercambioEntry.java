package com.example.proyectomoviles.model.intercambio;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class IntercambioEntry implements Serializable {

    private int id_intercambio;
    private int id_usuario_origen;
    private int id_usuario_destino;
    private int id_producto_solicitado;
    private int id_producto_ofrecido;

    private String nombre_origen;
    private String nombre_destino;
    private String producto_solicitado;
    private String producto_ofrecido;
    private String estado;

    private String imagen_solicitado;
    private String imagen_ofrecido;

    private double comision_monto;
    private String comision_estado;

    @SerializedName("nombre_otro")
    private String nombreOtro;

    @SerializedName("producto_otro")
    private String productoOtro;

    @SerializedName("imagen_otro")
    private String imagenOtro;

    @SerializedName("producto_mio")
    private String productoMio;

    @SerializedName("imagen_mio")
    private String imagenMio;


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

    public double getComision_monto() { return comision_monto; }
    public void setComision_monto(double comision_monto) { this.comision_monto = comision_monto; }

    public String getComision_estado() { return comision_estado; }
    public void setComision_estado(String comision_estado) { this.comision_estado = comision_estado; }

    public String getNombreOtro() { return nombreOtro; }
    public void setNombreOtro(String nombreOtro) { this.nombreOtro = nombreOtro; }

    public String getProductoOtro() { return productoOtro; }
    public void setProductoOtro(String productoOtro) { this.productoOtro = productoOtro; }

    public String getImagenOtro() { return imagenOtro; }
    public void setImagenOtro(String imagenOtro) { this.imagenOtro = imagenOtro; }

    public String getProductoMio() { return productoMio; }
    public void setProductoMio(String productoMio) { this.productoMio = productoMio; }

    public String getImagenMio() { return imagenMio; }
    public void setImagenMio(String imagenMio) { this.imagenMio = imagenMio; }
}
