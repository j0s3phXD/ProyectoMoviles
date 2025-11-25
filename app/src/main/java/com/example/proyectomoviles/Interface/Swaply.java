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
import com.example.proyectomoviles.model.PublicarRequest;
import com.example.proyectomoviles.model.RegistroRequest;
import com.example.proyectomoviles.model.RegistroResponse;
import com.example.proyectomoviles.model.RptaCalificacionPromedio;
import com.example.proyectomoviles.model.RptaCalificaciones;
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
    Call<RptaGeneral> publicarObjeto(@Body PublicarRequest publicarRequest);
    @GET("api_listarcategoria")
    Call<CategoriaResponse> listarCategorias();
    @GET("api_listar_productos")
    Call<RptaProducto> listarProductos();
    @POST("api_eliminar_producto")
    Call<RptaGeneral> eliminarProducto(@Body EliminarProductoRequest eliminarRequest);
    @POST("api_editar_producto")
    Call<RptaGeneral> editarProducto(@Body PublicarRequest editarRequest);
    @GET("api_mis_productos")
    Call<RptaProducto> misProductos();
    @Multipart
    @POST("api_registrar_producto")
    Call<RptaGeneral> publicarProductoConFoto(
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
            @Part("id_producto") RequestBody idProducto,
            @Part("titulo") RequestBody titulo,
            @Part("descripcion") RequestBody descripcion,
            @Part("condicion") RequestBody condicion,
            @Part("id_categoria") RequestBody idCategoria,
            @Part("intercambio_deseado") RequestBody intercambio,
            @Part MultipartBody.Part foto
    );
    @POST("api_iniciar_intercambio")
    Call<IniciarIntercambioResponse> iniciarIntercambio(
            @Body IniciarIntercambioRequest request
    );
    @GET("api_historial_intercambios")
    Call<RptaIntercambios> obtenerHistorial();
    @GET("api_mis_intercambios")
    Call<RptaIntercambios> obtenerMisIntercambios();
    @GET("api_intercambios_recibidos")
    Call<RptaIntercambios> obtenerIntercambiosRecibidos();
    @POST("api_confirmar_intercambio")
    Call<RptaGeneral> confirmarIntercambio(
            @Body ConfirmarIntercambioRequest request
    );
    @POST("api_enviar_mensaje")
    Call<RptaGeneral> enviarMensaje(
            @Body EnviarMensajeRequest request
    );
    @GET("api_mensajes/{id_intercambio}")
    Call<RptaMensajes> obtenerMensajes(
            @Path("id_intercambio") int idIntercambio
    );
    @POST("api/calificar")
    Call<RptaGeneral> enviarCalificacion(@Body CalificacionRequest request);
    @GET("api/calificaciones/promedio/{id_usuario}")
    Call<RptaCalificacionPromedio> obtenerPromedio(@Path("id_usuario") int idUsuario);
    @GET("api_usuario/{id}")
    Call<UsuarioResponse> obtenerUsuario(@Path("id") int idUsuario);

    @GET("api/calificaciones/autor/{id_autor}")
    Call<RptaCalificaciones> obtenerCalificacionesPorAutor(@Path("id_autor") int idAutor);

}
