package com.example.matala3.models;

import java.util.List;

public class cart {
    private List<Product> cartItems;

    public cart() {}

    public List<Product> getCartItems() { return cartItems; }
    public void setCartItems(List<Product> cartItems) { this.cartItems = cartItems; }
}

