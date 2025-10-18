package com.example.proyectomoviles.Interface;

import com.example.proyectomoviles.model.AuthRequest;
import com.example.proyectomoviles.model.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Swaply {

    @POST("auth")
    Call<AuthResponse> obtenerToken(@Body AuthRequest authRequest);

}
