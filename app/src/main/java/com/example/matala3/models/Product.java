package com.example.matala3.models;

public class Product {
    private String barcode;
    private String name;
    private String price;
    private int stock;
    private int quantity;

    public Product() {}

    public Product(String barcode, String name, String price, int stock) {
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.quantity =1;
    }

    public String getBarcode() { return barcode; }
    public void setId(String barcode) { this.barcode = barcode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public int getQuantity() { return quantity; } // ✅ Getter for quantity
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
