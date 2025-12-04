package com.example.proyectomoviles.model.intercambio;
public class PagarComisionRequest {
    private int id_intercambio;
    private String payment_token;
    private int monto; // en céntimos: 500 = S/5.00
    public PagarComisionRequest(int id_intercambio, String payment_token, int monto) {
        this.id_intercambio = id_intercambio;
        this.payment_token = payment_token;
        this.monto = monto;
    }
    public int getId_intercambio() {
        return id_intercambio;
    }
    public String getPayment_token() {
        return payment_token;
    }
    public int getMonto() {
        return monto;
    }
}
