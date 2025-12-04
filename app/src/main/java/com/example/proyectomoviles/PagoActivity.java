package com.example.proyectomoviles;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.CardParams;
import com.stripe.android.model.Token;

public class PagoActivity extends AppCompatActivity {

    public static final String EXTRA_PAYMENT_TOKEN = "payment_token";

    private Stripe stripe;
    private EditText edtCardNumber;
    private EditText edtExpMonth;
    private EditText edtExpYear;
    private EditText edtCvc;
    private Button btnPagar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);

        String publishableKey = "pk_test_51Sa5RsBpilonMhFGXpNWE8oLMtfcxGwXzaNDIGbJ5ezBAyuZ7v4lHZK5U65UHDg3BKhVTK4TGgOgh48ngP2EVcE9009lYC3h6S";

        stripe = new Stripe(
                getApplicationContext(),
                publishableKey
        );

        edtCardNumber = findViewById(R.id.edtCardNumber);
        edtExpMonth   = findViewById(R.id.edtExpMonth);
        edtExpYear    = findViewById(R.id.edtExpYear);
        edtCvc        = findViewById(R.id.edtCvc);
        btnPagar      = findViewById(R.id.btnPagar);
        progressBar   = findViewById(R.id.progressBarPago);

        progressBar.setVisibility(View.GONE);

        btnPagar.setOnClickListener(v -> generarToken());
    }

    private void generarToken() {

        String cardNumber = edtCardNumber.getText().toString().trim();
        String expMonthStr = edtExpMonth.getText().toString().trim();
        String expYearStr  = edtExpYear.getText().toString().trim();
        String cvc         = edtCvc.getText().toString().trim();

        if (TextUtils.isEmpty(cardNumber) ||
                TextUtils.isEmpty(expMonthStr) ||
                TextUtils.isEmpty(expYearStr) ||
                TextUtils.isEmpty(cvc)) {

            Toast.makeText(this, "Completa todos los campos de la tarjeta", Toast.LENGTH_SHORT).show();
            return;
        }

        int expMonth;
        int expYear;

        try {
            expMonth = Integer.parseInt(expMonthStr);
            expYear  = Integer.parseInt(expYearStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Mes o año inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        CardParams cardParams = new CardParams(
                cardNumber,
                expMonth,
                expYear,
                cvc
        );

        progressBar.setVisibility(View.VISIBLE);
        btnPagar.setEnabled(false);

        stripe.createCardToken(
                cardParams,
                new ApiResultCallback<Token>() {
                    @Override
                    public void onSuccess(@NonNull Token token) {

                        progressBar.setVisibility(View.GONE);
                        btnPagar.setEnabled(true);

                        // ESTE ID es el que va al backend (ej: "tok_1SaF24Hf...")
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

                        Toast.makeText(PagoActivity.this,
                                "Error generando token: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
