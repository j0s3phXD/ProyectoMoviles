package com.example.proyectomoviles.Interface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "https://swaply.pythonanywhere.com/";
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static com.example.proyectomoviles.Interface.Swaply getApiService() {
        return getClient().create(com.example.proyectomoviles.Interface.Swaply.class);
    }
}
