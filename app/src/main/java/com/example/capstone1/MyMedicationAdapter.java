package com.example.capstone1;

import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyMedicationAdapter extends RecyclerView.Adapter<MyMedicationAdapter.MyViewHolder> {
    Context context;
    ArrayList<medication_info> userArrayList;
    today_page_recycler today = new today_page_recycler();
    int inventory[];

    public MyMedicationAdapter(Context context, ArrayList<medication_info> userArrayList)
    {
        this.context =context;
        this.userArrayList = userArrayList;
    }
    @NonNull
    @Override
    public MyMedicationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.items_today_medication, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMedicationAdapter.MyViewHolder holder, int position) {
        medication_info medication_info = userArrayList.get(position);
        holder.InventoryMeds.setText(medication_info.InventoryMeds);
        holder.Medication.setText(medication_info.Medication);
        holder.StartDate.setText(medication_info.StartDate);
        holder.Time.setText(medication_info.Time);

        if (holder.StartDate.toString().equals(today.selectedDate))
        {
            holder.itemView.setVisibility(View.VISIBLE);

        }
        else{
            holder.itemView.setVisibility(View.GONE);
            today.test.setText(holder.StartDate.toString());

        }
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView Medication, Time, InventoryMeds, StartDate;
        ConstraintLayout constraintLayout, mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Medication =itemView.findViewById(R.id.medNameView);
            StartDate = itemView.findViewById(R.id.dateTV);
            InventoryMeds = itemView.findViewById(R.id.inventoryView);
            Time = itemView.findViewById(R.id.timeView);

        }
    }
}
