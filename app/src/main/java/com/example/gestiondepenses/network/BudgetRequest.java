package com.example.gestiondepenses.network;

public class BudgetRequest {
    private String month_year;
    private double amount;

    public BudgetRequest(String month_year, double amount) {
        this.month_year = month_year;
        this.amount = amount;
    }
}