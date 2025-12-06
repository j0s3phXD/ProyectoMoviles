package com.example.proyectomoviles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

// ML Kit
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DniActivity extends AppCompatActivity {

    private static final int CODIGO_GALERIA = 200;
    private static final int CODIGO_PERMISO_STORAGE = 300;

    private ImageView imgPreview;
    private Button btnSeleccionarFoto, btnLogin;

    private Uri imagenUri = null;

    private TextRecognizer textRecognizer;

    private String dniGuardado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dni);

        imgPreview = findViewById(R.id.imgPreview);
        btnSeleccionarFoto = findViewById(R.id.btnSeleccionarFoto);
        btnLogin = findViewById(R.id.btnLogin);

        // Inicializar ML Kit
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        // Leemos el DNI guardado ()
        SharedPreferences sp = getSharedPreferences("SP_SWAPLY", MODE_PRIVATE);
        dniGuardado = sp.getString("dniUsuario", null);

        if (dniGuardado == null || dniGuardado.isEmpty()) {
            Toast.makeText(
                    this,
                    "Primero inicia sesión normal para registrar tu DNI.",
                    Toast.LENGTH_LONG
            ).show();
        }

        btnSeleccionarFoto.setOnClickListener(v -> abrirGaleria());
        btnLogin.setOnClickListener(v -> procesarImagenYOcr());
    }

    // Abrimos Galeria
    private void abrirGaleria() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CODIGO_PERMISO_STORAGE
                );
                return;
            }
        }

        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        intent.setType("image/*");
        startActivityForResult(intent, CODIGO_GALERIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_GALERIA && resultCode == RESULT_OK && data != null) {
            imagenUri = data.getData();
            if (imagenUri != null) {
                Glide.with(this)
                        .load(imagenUri)
                        .into(imgPreview);
            }
        }
    }

    // Procesar imagen con ML Kit
    private void procesarImagenYOcr() {
        if (imagenUri == null) {
            Toast.makeText(this, "Primero selecciona la foto de tu DNI", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dniGuardado == null || dniGuardado.isEmpty()) {
            Toast.makeText(
                    this,
                    "No se encontró un DNI guardado. Inicia sesión normal primero.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        InputImage image;
        try {
            image = InputImage.fromFilePath(this, imagenUri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "No se pudo leer la imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        textRecognizer.process(image)
                .addOnSuccessListener(this::manejarTextoReconocido)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error procesando la imagen", Toast.LENGTH_SHORT).show()
                );
    }

    private void manejarTextoReconocido(Text visionText) {
        String textoCompleto = visionText.getText();

        String dniDetectado = extraerDniDesdeTexto(textoCompleto);

        if (dniDetectado == null) {
            Toast.makeText(
                    this,
                    "No se detectó ningún número de DNI en la imagen",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        if (!dniDetectado.equals(dniGuardado)) {
            Toast.makeText(
                    this,
                    "El DNI de la imagen no coincide con el DNI registrado",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        // Si todo OK: OCR correcto y coincide con el DNI guardado
        entrarConDni();
    }

    // Extraer DNI del texto OCR
    private String extraerDniDesdeTexto(String texto) {
        if (texto == null) return null;

        Pattern patronMrz = Pattern.compile("PER(\\d{8})");
        Matcher matcherMrz = patronMrz.matcher(texto);

        if (matcherMrz.find()) {
            return matcherMrz.group(1); // 8 dígitos
        }

        // Buscar cualquier número de 8 dígitos
        Pattern patronGeneral = Pattern.compile("\\b(\\d{8})\\b");
        Matcher matcherGeneral = patronGeneral.matcher(texto);

        if (matcherGeneral.find()) {
            return matcherGeneral.group(1);
        }

        return null;
    }

    // Entrar a la app
    private void entrarConDni() {
        // Aquí usamos el token que ya tengas guardado del login normal.
        SharedPreferences sp = getSharedPreferences("SP_SWAPLY", MODE_PRIVATE);
        String token = sp.getString("tokenJWT", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(
                    this,
                    "No hay sesión previa. Inicia sesión normal al menos una vez.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        Toast.makeText(this, "DNI verificado. Ingresando...", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(DniActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
