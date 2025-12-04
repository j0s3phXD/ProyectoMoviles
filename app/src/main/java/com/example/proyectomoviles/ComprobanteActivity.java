package com.example.proyectomoviles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
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

import com.bumptech.glide.Glide;
import com.example.proyectomoviles.databinding.ActivityComprobanteBinding;
import com.example.proyectomoviles.model.intercambio.IntercambioEntry;

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
        binding.btnBackComprobante.setOnClickListener(v -> finish());

        intercambio = (IntercambioEntry) getIntent().getSerializableExtra("intercambio");

        if (intercambio == null) {
            finish();
            return;
        }

        cargarDatosEnPantalla();
        configurarBotonPDF();
    }
    private void cargarDatosEnPantalla() {

        binding.txtEstado.setText("Estado: " + intercambio.getEstado());
        binding.txtUsuarioOrigen.setText("Solicitante: " + intercambio.getNombre_origen());
        binding.txtUsuarioDestino.setText("Dueño del producto: " + intercambio.getNombre_destino());

        String baseUrlImagenes = com.example.proyectomoviles.Interface.RetrofitClient.BASE_URL
                + "uploads/productos/";

        // Producto solicitado
        binding.txtProductoSolicitado.setText(intercambio.getProducto_solicitado());
        if (intercambio.getImagen_solicitado() != null
                && !intercambio.getImagen_solicitado().isEmpty()) {

            String urlSolicitado = baseUrlImagenes + intercambio.getImagen_solicitado();

            Glide.with(this)
                    .load(urlSolicitado)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.imgSolicitado);
        }

        // Producto ofrecido
        binding.txtProductoOfrecido.setText(intercambio.getProducto_ofrecido());
        if (intercambio.getImagen_ofrecido() != null
                && !intercambio.getImagen_ofrecido().isEmpty()) {

            String urlOfrecido = baseUrlImagenes + intercambio.getImagen_ofrecido();

            Glide.with(this)
                    .load(urlOfrecido)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.imgOfrecido);
        }

        String codigo = "COMP-" + intercambio.getId_intercambio() + "-" + (System.currentTimeMillis() % 100000);
        binding.txtCodigoComprobante.setText("Código: " + codigo);
    }

    private void configurarBotonPDF() {
        binding.btnDescargarPdf.setOnClickListener(v -> generarPDF());
    }

    private void generarPDF() {
        int pageWidth = 595;
        int pageHeight = 842;
        int margin = 40;

        PdfDocument pdf = new PdfDocument();
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // ----- TÍTULO -----
        Paint titlePaint = new Paint();
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(20);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        int y = 60;
        canvas.drawText("COMPROBANTE DE INTERCAMBIO", pageWidth / 2f, y, titlePaint);
        y += 40;

        // ----- TEXTO GENERAL -----
        Paint textPaint = new Paint();
        textPaint.setTextSize(14);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        textPaint.setTextAlign(Paint.Align.LEFT);

        // Código y estado
        canvas.drawText(binding.txtCodigoComprobante.getText().toString(), margin, y, textPaint);
        y += 20;
        canvas.drawText(binding.txtEstado.getText().toString(), margin, y, textPaint);
        y += 30;

        // Línea separadora
        canvas.drawLine(margin, y, pageWidth - margin, y, textPaint);
        y += 25;

        // ----- SOLICITANTE Y DUEÑO -----
        Paint boldPaint = new Paint(textPaint);
        boldPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText("Solicitante: " + intercambio.getNombre_origen(), margin, y, boldPaint);
        y += 20;

        canvas.drawText("Dueño del producto: " + intercambio.getNombre_destino(), margin, y, boldPaint);
        y += 35;

        // ----- PRODUCTO SOLICITADO -----
        canvas.drawText("Producto solicitado:", margin, y, boldPaint);
        y += 20;
        canvas.drawText(intercambio.getProducto_solicitado(), margin, y, textPaint);
        y += 15;

        try {
            Bitmap bmp = ((BitmapDrawable) binding.imgSolicitado.getDrawable()).getBitmap();
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 200, 200, true);
            canvas.drawBitmap(resized, margin, y, null);
        } catch (Exception ignored) {}

        y += 220;

        // ----- PRODUCTO OFRECIDO -----
        canvas.drawText("Producto ofrecido:", margin, y, boldPaint);
        y += 20;
        canvas.drawText(intercambio.getProducto_ofrecido(), margin, y, textPaint);
        y += 15;

        try {
            Bitmap bmp2 = ((BitmapDrawable) binding.imgOfrecido.getDrawable()).getBitmap();
            Bitmap resized2 = Bitmap.createScaledBitmap(bmp2, 200, 200, true);
            canvas.drawBitmap(resized2, margin, y, null);
        } catch (Exception ignored) {}

        pdf.finishPage(page);

        // ===== Guardar archivo (tu mismo código) =====
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
