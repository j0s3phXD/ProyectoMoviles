package com.example.proyectomoviles.Interface;

import com.example.proyectomoviles.model.AuthRequest;
import com.example.proyectomoviles.model.AuthResponse;
import com.example.proyectomoviles.model.CalificacionRequest;
import com.example.proyectomoviles.model.CategoriaResponse;
import com.example.proyectomoviles.model.ConfirmarIntercambioRequest;
import com.example.proyectomoviles.model.EliminarProductoRequest;
import com.example.proyectomoviles.model.EnviarMensajeRequest;
import com.example.proyectomoviles.model.IniciarIntercambioRequest;
import com.example.proyectomoviles.model.IniciarIntercambioResponse;
import com.example.proyectomoviles.model.ProductoEntry;
import com.example.proyectomoviles.model.PublicarRequest;
import com.example.proyectomoviles.model.RegistroRequest;
import com.example.proyectomoviles.model.RegistroResponse;
import com.example.proyectomoviles.model.RptaCalificacionPromedio;
import com.example.proyectomoviles.model.RptaGeneral;
import com.example.proyectomoviles.model.RptaIntercambios;
import com.example.proyectomoviles.model.RptaMensajes;
import com.example.proyectomoviles.model.RptaProducto;
import com.example.proyectomoviles.model.RptaProductoDetalle;
import com.example.proyectomoviles.model.UsuarioResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Swaply {

    @POST("auth")
    Call<AuthResponse> obtenerToken(@Body AuthRequest authRequest);
    @POST("api_registrar_usuario")
    Call<RegistroResponse> registrarUsuario(@Body RegistroRequest registroRequest);
    @POST("api_registrar_producto")
    Call<RptaGeneral> publicarObjeto(@Header("Authorization") String authorization, @Body PublicarRequest publicarRequest);
    @GET("api_listarcategoria")
    Call<CategoriaResponse> listarCategorias();
    @GET("api_listar_productos")
    Call<RptaProducto> listarProductos();
    @POST("api_eliminar_producto")
    Call<RptaGeneral> eliminarProducto(@Header("Authorization") String authorization, @Body EliminarProductoRequest eliminarRequest);
    @POST("api_editar_producto")
    Call<RptaGeneral> editarProducto(@Header("Authorization") String authorization, @Body PublicarRequest editarRequest);
    @POST("api_iniciar_intercambio")
    Call<IniciarIntercambioResponse> iniciarIntercambio(@Header("Authorization") String authorization,  @Body IniciarIntercambioRequest request
    );
    @GET("api_historial_intercambios")
    Call<RptaIntercambios> obtenerHistorial(
            @Header("Authorization") String authorization
    );
    @GET("api_mis_productos")
    Call<RptaProducto> misProductos(
            @Header("Authorization") String authorization
    );
    @GET("api_mis_intercambios")
    Call<RptaIntercambios> obtenerMisIntercambios(
            @Header("Authorization") String token
    );
    @GET("api_intercambios_recibidos")
    Call<RptaIntercambios> obtenerIntercambiosRecibidos(
            @Header("Authorization") String token
    );
    @POST("api_confirmar_intercambio")
    Call<RptaGeneral> confirmarIntercambio(
            @Header("Authorization") String token,
            @Body ConfirmarIntercambioRequest request
    );
    @POST("api_enviar_mensaje")
    Call<RptaGeneral> enviarMensaje(
            @Header("Authorization") String authorization,
            @Body EnviarMensajeRequest request
    );
    @GET("api_mensajes/{id_intercambio}")
    Call<RptaMensajes> obtenerMensajes(
            @Header("Authorization") String authorization,
            @Path("id_intercambio") int idIntercambio
    );
    @POST("api/calificar")
    Call<RptaGeneral> enviarCalificacion(@Body CalificacionRequest request);
    @GET("api/calificaciones/promedio/{id_usuario}")
    Call<RptaCalificacionPromedio> obtenerPromedio(@Path("id_usuario") int idUsuario);
    @GET("api_usuario/{id}")
    Call<UsuarioResponse> obtenerUsuario(@Path("id") int idUsuario);
    @Multipart
    @POST("/api_registrar_producto")
    Call<RptaGeneral> publicarProductoConFoto(
            @Header("Authorization") String token,
            @Part MultipartBody.Part foto,
            @Part("titulo") RequestBody titulo,
            @Part("descripcion") RequestBody descripcion,
            @Part("condicion") RequestBody condicion,
            @Part("id_categoria") RequestBody idCategoria,
            @Part("intercambio_deseado") RequestBody intercambio
    );
    @Multipart
    @POST("api_editar_producto_foto")
    Call<RptaGeneral> editarProductoConFoto(
            @Header("Authorization") String token,
            @Part("id_producto") RequestBody idProducto,
            @Part("titulo") RequestBody titulo,
            @Part("descripcion") RequestBody descripcion,
            @Part("condicion") RequestBody condicion,
            @Part("id_categoria") RequestBody idCategoria,
            @Part("intercambio_deseado") RequestBody intercambio,
            @Part MultipartBody.Part foto
    );

}
