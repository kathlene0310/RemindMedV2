package com.example.capstone1;

import android.content.Context;
import android.content.Intent;
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
    private  static final int LAYOUT_ONE = 0;
    private static final int LAYOUT_TWO = 1;

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


        String inventory = medication_info.getInventoryMeds();

        int intInv = Integer.parseInt(inventory);
        if (Integer.parseInt(medication_info.getInventoryMeds())  > 1)
        {
            holder.constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
        }
        else if(Integer.parseInt(medication_info.getInventoryMeds())   == 1)
        {
            holder.constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.red1));
        }
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, intake_confirmation.class);
                medication_info medication_info = userArrayList.get(position);
                intent.putExtra("description", medication_info.Medication);
                intent.putExtra("pill", medication_info.InventoryMeds);
                intent.putExtra("startdate", medication_info.StartDate);
                intent.putExtra("time", medication_info.Time);
                intent.putExtra("enddate", medication_info.EndDate);
                intent.putExtra("medication_info", medication_info);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView Medication, Time, InventoryMeds, StartDate, medicationHome, timeHome;
        ConstraintLayout constraintLayout, mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Medication =itemView.findViewById(R.id.medNameView);
            StartDate = itemView.findViewById(R.id.dateTV);
            InventoryMeds = itemView.findViewById(R.id.inventoryView);
            Time = itemView.findViewById(R.id.timeView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);

        }
    }

}