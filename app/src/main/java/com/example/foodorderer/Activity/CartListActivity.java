package com.example.foodorderer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderer.Adaptor.CartListAdaptor;
import com.example.foodorderer.Domain.FoodDomain;
import com.example.foodorderer.Helper.FirestoreHelper;
import com.example.foodorderer.Helper.ManagementCart;
import com.example.foodorderer.Interface.ChangeNumberItemsListener;
import com.example.foodorderer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class CartListActivity extends BaseActivity {
    private RecyclerView.Adapter adapter;
    private RecyclerView cartList;
    private ManagementCart managementCart;
    private TextView totalFeeTxt, taxTxt, totalDeliveryTxt, totalTxt, emptyTxt;
    private double tax;
    private ScrollView scrollView;

    private FirebaseAuth mAuth;
    private FirestoreHelper firestoreHelper;
    private Button checkoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart_list);

        managementCart = new ManagementCart(this);
        mAuth = FirebaseAuth.getInstance();
        firestoreHelper = new FirestoreHelper();

        initView();
        initList();
        calculateCart();
        setupBottomNavigation();

        checkoutBtn.setOnClickListener(v -> handleCheckout());
    }

    private void initView() {
        cartList = findViewById(R.id.cartList); // Corrected ID
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        taxTxt = findViewById(R.id.totalTaxTxt); // Corrected ID
        totalDeliveryTxt = findViewById(R.id.totalDeliveryTxt); // Corrected ID
        totalTxt = findViewById(R.id.totalSumTxt); // Corrected ID
        scrollView = findViewById(R.id.scrollView); // Corrected ID
        emptyTxt = findViewById(R.id.emptyTxt);
        checkoutBtn = findViewById(R.id.checkoutBtn);
    }

    private void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cartList.setLayoutManager(linearLayoutManager);
        adapter = new CartListAdaptor(managementCart.getListCart(), this, new ChangeNumberItemsListener() {
            @Override
            public void changed() {
                calculateCart();
                checkoutBtn.setEnabled(!managementCart.getListCart().isEmpty());
            }
        });

        cartList.setAdapter(adapter);
        if (managementCart.getListCart().isEmpty()) {
            emptyTxt.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        } else {
            emptyTxt.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
        checkoutBtn.setEnabled(!managementCart.getListCart().isEmpty());
    }

    private void calculateCart() {
        double precetTax = 0.2, deliveryFee = 10; // Using existing tax rate from file for this method
        tax = Math.round(managementCart.getTotalFee() * precetTax * 100) / 100.0; // Ensure double division
        double total = Math.round((managementCart.getTotalFee() + tax + deliveryFee) * 100) / 100.0; // Ensure double division
        double itemTotal = Math.round((managementCart.getTotalFee()) * 100) / 100.0; // Ensure double division
        totalFeeTxt.setText("$" + itemTotal);
        taxTxt.setText("$" + tax);
        totalDeliveryTxt.setText("$" + deliveryFee);
        totalTxt.setText("$" + total);
    }

    private void handleCheckout() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in to checkout", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();
        ArrayList<FoodDomain> cartItems = managementCart.getListCart();

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total amount (itemTotal + tax + deliveryFee)
        double percentTax = 0.2; // Tax rate as specified in the prompt for handleCheckout
        double deliveryFee = 10;
        double itemTotal = managementCart.getTotalFee();
        double tax = Math.round(itemTotal * percentTax * 100.0) / 100.0;
        double totalAmount = Math.round((itemTotal + tax + deliveryFee) * 100.0) / 100.0;

        firestoreHelper.savePurchaseHistory(userId, cartItems, totalAmount,
            aVoid -> { // Success listener for Firestore save
                Toast.makeText(CartListActivity.this, "Checkout successful! Purchase history saved.", Toast.LENGTH_LONG).show();
                managementCart.clearCart(new ChangeNumberItemsListener() {
                    @Override
                    public void changed() {
                        calculateCart();
                        // Update visibility of emptyTxt and scrollView
                        if (managementCart.getListCart().isEmpty()) {
                            emptyTxt.setVisibility(View.VISIBLE);
                            scrollView.setVisibility(View.GONE);
                        } else {
                            emptyTxt.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged(); // Notify adapter after clearing
                        checkoutBtn.setEnabled(!managementCart.getListCart().isEmpty());
                    }
                });
            },
            e -> { // Failure listener for Firestore save
                Toast.makeText(CartListActivity.this, "Checkout failed. Please try again: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        );
    }
}
