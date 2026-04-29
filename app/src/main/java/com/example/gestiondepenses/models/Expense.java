package com.example.gestiondepenses.models;

public class Expense {
    private int id;
    private double amount;
    private String date;
    private String note;
    private int category_id;
    private String category_name;
    private String color;

    // Getters/Setters
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getNote() { return note; }
    public int getCategory_id() { return category_id; }
    public String getCategory_name() { return category_name; }
    public String getColor() { return color; }

    public void setId(int id) { this.id = id; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDate(String date) { this.date = date; }
    public void setNote(String note) { this.note = note; }
    public void setCategory_id(int category_id) { this.category_id = category_id; }
    public void setCategory_name(String category_name) { this.category_name = category_name; }
    public void setColor(String color) { this.color = color; }
}