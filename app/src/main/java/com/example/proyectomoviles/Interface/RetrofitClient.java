package com.example.proyectomoviles.Interface;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitClient {

    public static final String BASE_URL = "https://swaply.pythonanywhere.com/";
    private static Retrofit retrofitNoAuth = null;

    // --------- CLIENTE SIN TOKEN ---------
    public static Retrofit getClient() {
        if (retrofitNoAuth == null) {
            retrofitNoAuth = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitNoAuth;
    }

    public static Swaply getApiService() {
        return getClient().create(Swaply.class);
    }
    // --------- CLIENTE CON TOKEN ---------
    public static Retrofit getClient(String token) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (token != null && !token.isEmpty()) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("Authorization", "JWT " + token)
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });
        }

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }
    public static Swaply getApiService(String token) {
        return getClient(token).create(Swaply.class);
    }
}
