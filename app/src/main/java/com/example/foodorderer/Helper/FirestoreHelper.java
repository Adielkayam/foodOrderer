package com.example.foodorderer.Helper;

import android.util.Log;

import com.example.foodorderer.Domain.FoodDomain;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue; // Import FieldValue
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query; // For ordering

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";
    private FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void savePurchaseHistory(String userId, ArrayList<FoodDomain> cartItems, double totalAmount, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "User ID is null or empty. Cannot save purchase history.");
            if (failureListener != null) {
                failureListener.onFailure(new IllegalArgumentException("User ID is null or empty"));
            }
            return;
        }
        if (cartItems == null || cartItems.isEmpty()) {
            Log.e(TAG, "Cart items are null or empty. Cannot save purchase history.");
            if (failureListener != null) {
                failureListener.onFailure(new IllegalArgumentException("Cart items are null or empty"));
            }
            return;
        }

        Map<String, Object> purchaseData = new HashMap<>();
        purchaseData.put("userId", userId); // Optional: denormalize if needed directly in the purchase record
        purchaseData.put("items", cartItems); // Firestore can handle lists of custom objects
        purchaseData.put("totalAmount", totalAmount);
        purchaseData.put("timestamp", FieldValue.serverTimestamp()); // Use FieldValue for server-side timestamp

        db.collection("users").document(userId)
          .collection("purchaseHistory")
          .add(purchaseData)
          .addOnSuccessListener(aVoid -> { // Modified to match OnSuccessListener<Void>
              Log.d(TAG, "Purchase history saved successfully for user: " + userId);
              if (successListener != null) {
                  successListener.onSuccess(null); // Call the passed listener
              }
          })
          .addOnFailureListener(e -> {
              Log.w(TAG, "Error adding purchase history for user: " + userId, e);
              if (failureListener != null) {
                  failureListener.onFailure(e); // Call the passed listener
              }
          });
    }

    public void getOrderHistory(String userId, OnCompleteListener<QuerySnapshot> listener) {
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "User ID is null or empty. Cannot fetch purchase history.");
            // Optionally, invoke listener.onComplete with a failed task
            // For simplicity, just returning here. Proper error handling would be better.
            return;
        }
        db.collection("users").document(userId)
          .collection("purchaseHistory")
          .orderBy("timestamp", Query.Direction.DESCENDING) // Order by most recent first
          .get()
          .addOnCompleteListener(listener); // Pass the listener directly
    }
}
