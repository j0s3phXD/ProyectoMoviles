package com.example.proyectomoviles.Interface;

import com.example.proyectomoviles.model.AuthRequest;
import com.example.proyectomoviles.model.AuthResponse;
import com.example.proyectomoviles.model.CategoriaResponse;
import com.example.proyectomoviles.model.EliminarProductoRequest;
import com.example.proyectomoviles.model.IniciarIntercambioRequest;
import com.example.proyectomoviles.model.IniciarIntercambioResponse;
import com.example.proyectomoviles.model.ProductoEntry;
import com.example.proyectomoviles.model.PublicarRequest;
import com.example.proyectomoviles.model.RegistroRequest;
import com.example.proyectomoviles.model.RegistroResponse;
import com.example.proyectomoviles.model.RptaGeneral;
import com.example.proyectomoviles.model.RptaIntercambios;
import com.example.proyectomoviles.model.RptaProducto;
import com.example.proyectomoviles.model.RptaProductoDetalle;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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

    @GET("api_detalle_producto/{id_producto}")
    Call<RptaProductoDetalle> detalleProducto(@Path("id_producto") int idProducto);

    @POST("api_iniciar_intercambio")
    Call<IniciarIntercambioResponse> iniciarIntercambio(@Header("Authorization") String authorization,  @Body IniciarIntercambioRequest request
    );

    @GET("api_listar_intercambios")
    Call<RptaIntercambios> listarIntercambios(@Header("Authorization") String authorization);

    @GET("api_historial_intercambios")
    Call<RptaIntercambios> obtenerHistorial(
            @Header("Authorization") String authorization
    );
    @GET("api_productos_usuario/{id_usuario}")
    Call<RptaProducto> productosPorUsuario(
            @Header("Authorization") String authorization,
            @Path("id_usuario") int idUsuario
    );

    @GET("api_mis_productos")
    Call<RptaProducto> misProductos(
            @Header("Authorization") String authorization
    );



}
