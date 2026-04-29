package com.example.gestiondepenses.network;

public class ExpenseRequest {
    private double amount;
    private String date;
    private String note;
    private int category_id;

    public ExpenseRequest(double amount, String date, String note, int category_id) {
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.category_id = category_id;
    }
}