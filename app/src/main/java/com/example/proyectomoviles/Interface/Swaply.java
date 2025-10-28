package com.example.proyectomoviles.Interface;

import com.example.proyectomoviles.model.AuthRequest;
import com.example.proyectomoviles.model.AuthResponse;
import com.example.proyectomoviles.model.RegistroRequest;
import com.example.proyectomoviles.model.RegistroResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Swaply {

    @POST("auth")
    Call<AuthResponse> obtenerToken(@Body AuthRequest authRequest);

    @POST("api_registrar_usuario")
    Call<RegistroResponse> registrarUsuario(@Body RegistroRequest registroRequest);

}
