package com.example.proyectomoviles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.proyectomoviles.databinding.ActivityComprobanteBinding;
import com.example.proyectomoviles.model.IntercambioEntry;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.OutputStream;

public class ComprobanteActivity extends AppCompatActivity {

    private ActivityComprobanteBinding binding;
    private IntercambioEntry intercambio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComprobanteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intercambio = (IntercambioEntry) getIntent().getSerializableExtra("intercambio");

        if (intercambio == null) {
            finish();
            return;
        }

        cargarDatosEnPantalla();
        configurarBotonPDF();
    }

    private void cargarDatosEnPantalla() {

        // Datos básicos
        binding.txtId.setText("ID: " + intercambio.getId_intercambio());
        binding.txtEstado.setText("Estado: " + intercambio.getEstado());
        binding.txtUsuarioOrigen.setText("Solicitante: " + intercambio.getNombre_origen());
        binding.txtUsuarioDestino.setText("Dueño del producto: " + intercambio.getNombre_destino());

        // Producto solicitado
        binding.txtProductoSolicitado.setText(intercambio.getProducto_solicitado());
        if (intercambio.getImagen_solicitado() != null && !intercambio.getImagen_solicitado().isEmpty()) {
            Picasso.get().load(intercambio.getImagen_solicitado()).into(binding.imgSolicitado);
        }

        // Producto ofrecido
        binding.txtProductoOfrecido.setText(intercambio.getProducto_ofrecido());
        if (intercambio.getImagen_ofrecido() != null && !intercambio.getImagen_ofrecido().isEmpty()) {
            Picasso.get().load(intercambio.getImagen_ofrecido()).into(binding.imgOfrecido);
        }

        // Código único
        String codigo = "COMP-" + intercambio.getId_intercambio() + "-" + (System.currentTimeMillis() % 100000);
        binding.txtCodigoComprobante.setText("Código: " + codigo);
    }

    private void configurarBotonPDF() {
        binding.btnDescargarPdf.setOnClickListener(v -> generarPDF());
    }

    private void generarPDF() {

        PdfDocument pdf = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        // Crear página PDF (A4)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // ======= TÍTULO ==========
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(22);
        canvas.drawText("COMPROBANTE DE INTERCAMBIO", 110, 50, titlePaint);

        int y = 100;
        paint.setTextSize(16);

        // ======= Código, ID, Estado ==========
        canvas.drawText(binding.txtCodigoComprobante.getText().toString(), 40, y, paint);
        y += 25;

        canvas.drawText(binding.txtId.getText().toString(), 40, y, paint);
        y += 25;

        canvas.drawText(binding.txtEstado.getText().toString(), 40, y, paint);
        y += 40;

        // ======= Solicitante ==========
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Solicitante:", 40, y, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText(intercambio.getNombre_origen(), 160, y, paint);
        y += 30;

        // ======= Dueño del producto ==========
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Dueño del producto:", 40, y, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText(intercambio.getNombre_destino(), 240, y, paint);
        y += 45;

        // ======= Producto solicitado ==========
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Producto solicitado:", 40, y, paint);
        y += 30;

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText(intercambio.getProducto_solicitado(), 40, y, paint);
        y += 20;

        // -------- Imagen Producto Solicitado --------
        try {
            Bitmap bmp = ((BitmapDrawable) binding.imgSolicitado.getDrawable()).getBitmap();
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 200, 200, false);
            canvas.drawBitmap(resized, 40, y, paint);
        } catch (Exception ignored) {}

        y += 230;

        // ======= Producto ofrecido ==========
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Producto ofrecido:", 40, y, paint);
        y += 30;

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText(intercambio.getProducto_ofrecido(), 40, y, paint);
        y += 20;

        // -------- Imagen Producto Ofrecido --------
        try {
            Bitmap bmp2 = ((BitmapDrawable) binding.imgOfrecido.getDrawable()).getBitmap();
            Bitmap resized2 = Bitmap.createScaledBitmap(bmp2, 200, 200, false);
            canvas.drawBitmap(resized2, 40, y, paint);
        } catch (Exception ignored) {}

        pdf.finishPage(page);

        // ======= Guardar PDF ==========
        String fileName = "comprobante_intercambio_" + intercambio.getId_intercambio() + ".pdf";
        Uri uri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        } else {
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloads, fileName);
            uri = Uri.fromFile(file);
        }

        try {
            OutputStream out = getContentResolver().openOutputStream(uri);
            if (out != null) {
                pdf.writeTo(out);
                out.close();
            }

            Toast.makeText(this, "PDF guardado en Descargas", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar PDF", Toast.LENGTH_SHORT).show();
        }

        pdf.close();
    }

}
