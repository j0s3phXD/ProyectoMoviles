package com.example.proyectomoviles.model.calificacion;

public class CalificacionRequest {

    private int id_usuario;
    private int id_autor;
    private int estrellas;
    private String comentario;
    private int id_intercambio;

    public CalificacionRequest(int id_usuario, int id_autor, int estrellas, String comentario, int id_intercambio) {
        this.id_usuario = id_usuario;
        this.id_autor = id_autor;
        this.estrellas = estrellas;
        this.comentario = comentario;
        this.id_intercambio = id_intercambio;
    }

    public int getId_usuario() {
        return id_usuario;
    }
    public int getId_autor() {
        return id_autor;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public String getComentario() {
        return comentario;
    }

    public int getId_intercambio() {
        return id_intercambio;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public void setId_autor(int id_autor) {
        this.id_autor = id_autor;
    }

    public void setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setId_intercambio(int id_intercambio) {
        this.id_intercambio = id_intercambio;
    }
}
