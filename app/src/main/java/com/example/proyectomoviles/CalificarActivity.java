package com.example.proyectomoviles;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectomoviles.Interface.RetrofitClient;
import com.example.proyectomoviles.Interface.Swaply;
import com.example.proyectomoviles.model.calificacion.CalificacionRequest;
import com.example.proyectomoviles.model.auth.GeneralResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalificarActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText txtComentario;
    private Button btnEnviar;

    private int idUsuarioRecibe;   // usuario al que califico
    private int idUsuarioAutor;    // yo (quien envía)
    private int idIntercambio;     // intercambio relacionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);

        ratingBar = findViewById(R.id.ratingBar);
        txtComentario = findViewById(R.id.txtComentario);
        btnEnviar = findViewById(R.id.btnEnviarRating);

        // Obtener datos enviados desde el adaptador
        idUsuarioRecibe = getIntent().getIntExtra("id_usuario_recibe", -1);
        idUsuarioAutor = getIntent().getIntExtra("id_usuario_autor", -1);
        idIntercambio = getIntent().getIntExtra("id_intercambio", -1);

        btnEnviar.setOnClickListener(v -> enviarCalificacion());
    }

    private void enviarCalificacion() {

        int estrellas = (int) ratingBar.getRating();
        String comentario = txtComentario.getText().toString().trim();

        if (estrellas == 0) {
            Toast.makeText(this, "Debes seleccionar entre 1 y 5 estrellas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idUsuarioRecibe == -1 || idUsuarioAutor == -1 || idIntercambio == -1) {
            Toast.makeText(this, "Error: datos incompletos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Desactivar botón mientras se guarda
        btnEnviar.setEnabled(false);

        CalificacionRequest req = new CalificacionRequest(
                idUsuarioRecibe,
                idUsuarioAutor,
                estrellas,
                comentario,
                idIntercambio
        );

        Swaply api = RetrofitClient.getApiService();

        api.enviarCalificacion(req).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {

                btnEnviar.setEnabled(true);

                if (!response.isSuccessful()) {
                    Toast.makeText(CalificarActivity.this,
                            "Error del servidor: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                GeneralResponse rpta = response.body();

                if (rpta != null && rpta.getCode() == 1) {
                    Toast.makeText(CalificarActivity.this,
                            "¡Calificación enviada correctamente!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CalificarActivity.this,
                            rpta != null ? rpta.getMessage() : "Error desconocido",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {

                btnEnviar.setEnabled(true);

                Toast.makeText(CalificarActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
