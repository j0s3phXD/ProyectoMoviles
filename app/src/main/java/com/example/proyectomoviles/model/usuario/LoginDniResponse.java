package com.example.proyectomoviles.model.usuario;

import com.google.gson.annotations.SerializedName;

public class LoginDniResponse {

    private int code;
    private String message;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("foto_url")
    private String fotoUrl;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public String getAccessToken() { return accessToken; }
    public int getIdUsuario() { return idUsuario; }
    public String getFotoUrl() { return fotoUrl; }
}
