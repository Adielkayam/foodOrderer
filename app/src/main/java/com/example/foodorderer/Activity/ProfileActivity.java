package com.example.foodorderer.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderer.Adaptor.OrderHistoryAdapter;
import com.example.foodorderer.Domain.PurchaseRecord;
import com.example.foodorderer.Helper.FirestoreHelper;
import com.example.foodorderer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";
    private RecyclerView recyclerViewOrderHistory;
    private OrderHistoryAdapter orderHistoryAdapter;
    private ArrayList<PurchaseRecord> purchaseRecordsList;
    private FirestoreHelper firestoreHelper;
    private FirebaseAuth mAuth;
    private TextView textViewNoOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomNavigation();

        mAuth = FirebaseAuth.getInstance();
        firestoreHelper = new FirestoreHelper();
        purchaseRecordsList = new ArrayList<>();

        recyclerViewOrderHistory = findViewById(R.id.recyclerViewOrderHistory);
        textViewNoOrders = findViewById(R.id.textViewNoOrders);

        recyclerViewOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        orderHistoryAdapter = new OrderHistoryAdapter(purchaseRecordsList);
        recyclerViewOrderHistory.setAdapter(orderHistoryAdapter);

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in to view history.", Toast.LENGTH_SHORT).show();
            textViewNoOrders.setText("Please sign in to view history.");
            textViewNoOrders.setVisibility(View.VISIBLE);
            recyclerViewOrderHistory.setVisibility(View.GONE);
            return;
        }
        String userId = currentUser.getUid();

        firestoreHelper.getOrderHistory(userId, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    purchaseRecordsList.clear();
                    if (task.getResult() != null && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            PurchaseRecord record = document.toObject(PurchaseRecord.class);
                            if (record != null) {
                                purchaseRecordsList.add(record);
                            }
                        }

                        orderHistoryAdapter.notifyDataSetChanged();
                        textViewNoOrders.setVisibility(View.GONE);
                        recyclerViewOrderHistory.setVisibility(View.VISIBLE);
                    } else {
                        Log.d(TAG, "No order history found.");
                        textViewNoOrders.setText("No orders found.");
                        textViewNoOrders.setVisibility(View.VISIBLE);
                        recyclerViewOrderHistory.setVisibility(View.GONE);
                    }
                } else {
                    Log.e(TAG, "Error loading order history: ", task.getException());
                    Toast.makeText(ProfileActivity.this, "Failed to load order history.", Toast.LENGTH_SHORT).show();
                    textViewNoOrders.setText("Failed to load order history.");
                    textViewNoOrders.setVisibility(View.VISIBLE);
                    recyclerViewOrderHistory.setVisibility(View.GONE);
                }
            }
        });
    }
}
