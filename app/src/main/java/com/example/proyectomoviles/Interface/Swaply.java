package com.example.proyectomoviles.Interface;

import com.example.proyectomoviles.model.auth.AuthRequest;
import com.example.proyectomoviles.model.auth.AuthResponse;
import com.example.proyectomoviles.model.calificacion.CalificacionRequest;
import com.example.proyectomoviles.model.categoria.CategoriaResponse;
import com.example.proyectomoviles.model.intercambio.ConfirmarIntercambioRequest;
import com.example.proyectomoviles.model.producto.EliminarProductoRequest;
import com.example.proyectomoviles.model.mensaje.EnviarMensajeRequest;
import com.example.proyectomoviles.model.intercambio.IniciarIntercambioRequest;
import com.example.proyectomoviles.model.intercambio.IniciarIntercambioResponse;
import com.example.proyectomoviles.model.producto.PublicarRequest;
import com.example.proyectomoviles.model.auth.RegistroRequest;
import com.example.proyectomoviles.model.auth.RegistroResponse;
import com.example.proyectomoviles.model.calificacion.CalificacionPromedioResponse;
import com.example.proyectomoviles.model.calificacion.CalificacionesResponse;
import com.example.proyectomoviles.model.GeneralResponse;
import com.example.proyectomoviles.model.intercambio.IntercambiosResponse;
import com.example.proyectomoviles.model.mensaje.MensajesResponse;
import com.example.proyectomoviles.model.producto.ProductoResponse;
import com.example.proyectomoviles.model.usuario.UsuarioResponse;

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
    Call<GeneralResponse> publicarProductoJson(@Body PublicarRequest publicarRequest);
    @GET("api_listarcategoria")
    Call<CategoriaResponse> listarCategorias();
    @GET("api_listar_productos")
    Call<ProductoResponse> listarProductos();
    @POST("api_eliminar_producto")
    Call<GeneralResponse> eliminarProducto(@Body EliminarProductoRequest eliminarRequest);
    @POST("api_editar_producto")
    Call<GeneralResponse> editarProducto(@Body PublicarRequest editarRequest);
    @GET("api_mis_productos")
    Call<ProductoResponse> misProductos();
    @Multipart
    @POST("api_registrar_producto")
    Call<GeneralResponse> publicarProductoConFoto(
            @Part MultipartBody.Part foto,
            @Part("titulo") RequestBody titulo,
            @Part("descripcion") RequestBody descripcion,
            @Part("condicion") RequestBody condicion,
            @Part("id_categoria") RequestBody idCategoria,
            @Part("intercambio_deseado") RequestBody intercambio
    );
    @Multipart
    @POST("api_editar_producto_foto")
    Call<GeneralResponse> editarProductoConFoto(
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
    Call<IntercambiosResponse> obtenerHistorial();
    @GET("api_mis_intercambios")
    Call<IntercambiosResponse> obtenerMisIntercambios();
    @GET("api_intercambios_recibidos")
    Call<IntercambiosResponse> obtenerIntercambiosRecibidos();
    @POST("api_confirmar_intercambio")
    Call<GeneralResponse> confirmarIntercambio(
            @Body ConfirmarIntercambioRequest request
    );
    @POST("api_enviar_mensaje")
    Call<GeneralResponse> enviarMensaje(
            @Body EnviarMensajeRequest request
    );
    @GET("api_mensajes/{id_intercambio}")
    Call<MensajesResponse> obtenerMensajes(
            @Path("id_intercambio") int idIntercambio
    );
    @POST("api/calificar")
    Call<GeneralResponse> enviarCalificacion(@Body CalificacionRequest request);
    @GET("api/calificaciones/promedio/{id_usuario}")
    Call<CalificacionPromedioResponse> obtenerPromedio(@Path("id_usuario") int idUsuario);
    @GET("api_usuario/{id}")
    Call<UsuarioResponse> obtenerUsuario(@Path("id") int idUsuario);
    @GET("api/calificaciones/autor/{id_autor}")
    Call<CalificacionesResponse> obtenerCalificacionesPorAutor(@Path("id_autor") int idAutor);

}
