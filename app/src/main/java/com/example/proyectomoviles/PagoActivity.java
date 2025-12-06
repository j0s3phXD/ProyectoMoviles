package com.example.proyectomoviles;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.CardParams;
import com.stripe.android.model.Token;

public class PagoActivity extends AppCompatActivity {

    public static final String EXTRA_PAYMENT_TOKEN = "payment_token";

    private Stripe stripe;

    private TextInputEditText edtCardNumber;
    private Spinner spMes;
    private Spinner spAnio;
    private TextInputEditText edtCvc;

    private Button btnPagar;
    private Button btnCancelarPago;
    private ProgressBar progressBar;

    private boolean isUpdatingCard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);

        String publishableKey = "pk_test_51Sa5RsBpilonMhFGXpNWE8oLMtfcxGwXzaNDIGbJ5ezBAyuZ7v4lHZK5U65UHDg3BKhVTK4TGgOgh48ngP2EVcE9009lYC3h6S";

        stripe = new Stripe(
                getApplicationContext(),
                publishableKey
        );

        // Enlazar vistas
        edtCardNumber   = findViewById(R.id.edtCardNumber);
        spMes           = findViewById(R.id.spMes);
        spAnio          = findViewById(R.id.spAnio);
        edtCvc          = findViewById(R.id.edtCvc);
        btnPagar        = findViewById(R.id.btnPagar);
        btnCancelarPago = findViewById(R.id.btnCancelarPago);
        progressBar     = findViewById(R.id.progressBarPago);

        progressBar.setVisibility(View.GONE);

        configurarCardNumberFormatting();
        configurarSpinners();

        btnPagar.setOnClickListener(v -> generarToken());
        btnCancelarPago.setOnClickListener(v -> mostrarDialogoCancelar());
    }

    private void configurarCardNumberFormatting() {
        edtCardNumber.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdatingCard) return;
                isUpdatingCard = true;

                String digitsOnly = s.toString().replace(" ", "");
                if (digitsOnly.length() > 16) {
                    digitsOnly = digitsOnly.substring(0, 16);
                }

                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digitsOnly.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(digitsOnly.charAt(i));
                }

                edtCardNumber.removeTextChangedListener(this);
                edtCardNumber.setText(formatted.toString());
                edtCardNumber.setSelection(formatted.length());
                edtCardNumber.addTextChangedListener(this);

                isUpdatingCard = false;
            }
        });
    }

    private void configurarSpinners() {
        String[] meses = new String[12];
        for (int i = 1; i <= 12; i++) {
            meses[i - 1] = String.format("%02d", i);
        }
        ArrayAdapter<String> mesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                meses
        );
        mesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMes.setAdapter(mesAdapter);

        int startYear = 2026;
        int endYear   = 2040;
        int totalYears = endYear - startYear + 1;
        String[] anios = new String[totalYears];
        for (int i = 0; i < totalYears; i++) {
            anios[i] = String.valueOf(startYear + i);
        }

        ArrayAdapter<String> anioAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                anios
        );
        anioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAnio.setAdapter(anioAdapter);
    }

    private void mostrarDialogoCancelar() {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar proceso")
                .setMessage("¿Estás seguro de cancelar el proceso de pago?")
                .setPositiveButton("Cancelar pago", (dialog, which) -> {
                    setResult(RESULT_CANCELED);
                    finish();
                })
                .setNegativeButton("Continuar con el pago", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void generarToken() {

        String cardNumberFormatted = edtCardNumber.getText().toString().trim();
        String cardNumberDigits = cardNumberFormatted.replace(" ", "");
        String cvc = edtCvc.getText().toString().trim();

        String mesStr  = spMes.getSelectedItem() != null ? spMes.getSelectedItem().toString() : "";
        String anioStr = spAnio.getSelectedItem() != null ? spAnio.getSelectedItem().toString() : "";

        if (TextUtils.isEmpty(cardNumberDigits) ||
                TextUtils.isEmpty(mesStr) ||
                TextUtils.isEmpty(anioStr) ||
                TextUtils.isEmpty(cvc)) {

            Toast.makeText(this, "Completa todos los campos de la tarjeta", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cardNumberDigits.matches("\\d{16}")) {
            Toast.makeText(this, "El número de tarjeta debe tener 16 dígitos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int expMonth;
        int expYear;

        try {
            expMonth = Integer.parseInt(mesStr);
            expYear  = Integer.parseInt(anioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Mes o año inválidos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expMonth < 1 || expMonth > 12) {
            Toast.makeText(this, "El mes debe estar entre 01 y 12.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expYear < 2026 || expYear > 2040) {
            Toast.makeText(this, "Selecciona un año válido (2026-2040).", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cvc.matches("\\d{3}")) {
            Toast.makeText(this, "El CVC debe tener 3 dígitos.", Toast.LENGTH_SHORT).show();
            return;
        }

        CardParams cardParams = new CardParams(
                cardNumberDigits,
                expMonth,
                expYear,
                cvc
        );

        progressBar.setVisibility(View.VISIBLE);
        btnPagar.setEnabled(false);
        btnCancelarPago.setEnabled(false);

        stripe.createCardToken(
                cardParams,
                new ApiResultCallback<Token>() {
                    @Override
                    public void onSuccess(@NonNull Token token) {

                        progressBar.setVisibility(View.GONE);
                        btnPagar.setEnabled(true);
                        btnCancelarPago.setEnabled(true);

                        String paymentToken = token.getId();

                        Intent data = new Intent();
                        data.putExtra(EXTRA_PAYMENT_TOKEN, paymentToken);
                        setResult(RESULT_OK, data);
                        finish();
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        btnPagar.setEnabled(true);
                        btnCancelarPago.setEnabled(true);

                        Toast.makeText(PagoActivity.this,
                                "Error generando token: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        mostrarDialogoCancelar();
    }
}
