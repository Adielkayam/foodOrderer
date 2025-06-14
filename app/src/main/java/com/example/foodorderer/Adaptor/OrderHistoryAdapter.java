package com.example.foodorderer.Adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodorderer.Domain.FoodDomain;
import com.example.foodorderer.Domain.PurchaseRecord;
import com.example.foodorderer.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private ArrayList<PurchaseRecord> purchaseRecords;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());

    public OrderHistoryAdapter(ArrayList<PurchaseRecord> purchaseRecords) {
        this.purchaseRecords = purchaseRecords;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order_history_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PurchaseRecord record = purchaseRecords.get(position);

        if (record.getTimestamp() != null) {
            holder.orderDate.setText("Order Date: " + dateFormat.format(record.getTimestamp().toDate()));
        } else {
            holder.orderDate.setText("Order Date: N/A");
        }
        holder.orderTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", record.getTotalAmount()));

        // Create a summary of items
        StringBuilder itemsSummary = new StringBuilder("Items: ");
        if (record.getItems() != null && !record.getItems().isEmpty()) {
            for (int i = 0; i < record.getItems().size(); i++) {
                itemsSummary.append(record.getItems().get(i).getTitle());
                if (i < record.getItems().size() - 1) {
                    itemsSummary.append(", ");
                }
            }
        } else {
            itemsSummary.append("N/A");
        }
        holder.orderItemsSummary.setText(itemsSummary.toString());
    }

    @Override
    public int getItemCount() {
        return purchaseRecords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderDate, orderTotal, orderItemsSummary;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.textViewOrderDate);
            orderTotal = itemView.findViewById(R.id.textViewOrderTotal);
            orderItemsSummary = itemView.findViewById(R.id.textViewOrderItemsSummary);
        }
    }
}
