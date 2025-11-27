package com.example.proyectomoviles;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500; // 2.5 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ocultar la barra de acción para una experiencia inmersiva
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Cargar vistas
        ImageView logo = findViewById(R.id.imgLogoSplash);
        TextView welcomeText = findViewById(R.id.txtWelcome);

        // Cargar animaciones
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Aplicar animaciones
        logo.startAnimation(fadeIn);
        welcomeText.startAnimation(slideUp);

        // Handler para la transición a la siguiente actividad
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // CORRECCIÓN: Ir a LoginActivity en lugar de RegistroUsuarioActivity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Cierra esta actividad para que el usuario no pueda volver a ella
        }, SPLASH_DELAY);
    }
}
