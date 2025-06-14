package com.example.foodorderer.Domain;

import com.google.firebase.Timestamp; // Correct import
import java.util.ArrayList;
import java.util.Date; // For storing converted Timestamp

public class PurchaseRecord {
    private ArrayList<FoodDomain> items;
    private double totalAmount;
    private Timestamp timestamp; // Store Firestore Timestamp directly

    // Required empty constructor for Firestore deserialization
    public PurchaseRecord() {}

    public PurchaseRecord(ArrayList<FoodDomain> items, double totalAmount, Timestamp timestamp) {
        this.items = items;
        this.totalAmount = totalAmount;
        this.timestamp = timestamp;
    }

    public ArrayList<FoodDomain> getItems() {
        return items;
    }

    public void setItems(ArrayList<FoodDomain> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    // Optional: Helper to get Date object if needed for formatting
    public Date getPurchaseDate() {
        return timestamp != null ? timestamp.toDate() : null;
    }
}
