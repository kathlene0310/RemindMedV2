package com.example.capstone1;

import static com.example.capstone1.home_page.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Base64;

public class history_for_measurements extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<measurment_info> myArrayList;
    measurementAdapter myAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    String measurement[];
    long accounttype ;
    FloatingActionButton profileBtn;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    TextView firstname, clear;
    FirebaseAuth rootAuthen;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_for_measurements);
        //firstname = findViewById(R.id.firstnameview);
        rootAuthen = FirebaseAuth.getInstance();
        userId = rootAuthen.getCurrentUser().getUid();
        clear = findViewById(R.id.clearAll_measurements);
        profileBtn = findViewById(R.id.Profile_historyH);


        DocumentReference documentReference = fstore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "listen:error", error);
                    //firstname.setText(" ");
                    return;
                }

                try{
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Base64.Decoder decoder = Base64.getDecoder();
                        byte [] bytes =decoder.decode(value.getString("firstname"));
                        //firstname.setText(new String(bytes));
                    }
                }catch (Exception e)
                {
                    //firstname.setText(" ");
                }

            }
        });
        measurement = getResources().getStringArray(R.array.measurements);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        recyclerView =  findViewById(R.id.recyclerHistory);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        myArrayList = new ArrayList<measurment_info>();


        myAdapter = new measurementAdapter(history_for_measurements.this, myArrayList);
        showMeasurementHistory();
        recyclerView.setAdapter(myAdapter);
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(history_for_measurements.this);
                alert.setTitle("Delete")
                        .setMessage("Are you sure you want to clear your history")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(String i : measurement)
                                {
                                    db.document("users/" + currentFirebaseUser.getUid()).collection("New Health Measurements")
                                            .document(i).collection(i).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    for (QueryDocumentSnapshot snapshot: task.getResult())
                                                    {
                                                        db.document("users/" + currentFirebaseUser.getUid()).collection("New Health Measurements")
                                                                .document(i).collection(i).document(snapshot.getId()).delete();
                                                    }
                                                }
                                            });
                                }
                                myAdapter.notifyDataSetChanged();
                                Intent intent = new Intent(history_for_measurements.this, history_for_measurements.class);
                                startActivity(intent);


                            }
                        });
                alert.show();
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentReference.addSnapshotListener(history_for_measurements.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "listen:error", error);
                            firstname.setText(" ");
                            return;
                        }
                        try {
                            accounttype = value.getLong("accounttype");

                            Log.d("TAG", "tag: " + accounttype);
                            if (accounttype == 1)
                            {
                                Intent intent = new Intent(history_for_measurements.this, user_information.class);
                                startActivity(intent);
                            }
                            else if (accounttype == 2)
                            {
                                Intent intent = new Intent(history_for_measurements.this, guestLogout.class);
                                startActivity(intent);
                            }
                        }catch (Exception e){
                            Intent intent = new Intent(history_for_measurements.this, guestLogout.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });



    }

    private void showMeasurementHistory()
    {
        measurement = getResources().getStringArray(R.array.measurements);
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        for ( String i : measurement) {
            db.document("users/" + currentFirebaseUser.getUid()).collection("New Health Measurements").document(i).collection(i).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("Firestore error", e.getMessage());
                        return;
                    }
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            myArrayList.add(dc.getDocument().toObject(measurment_info.class));
                        }
                        myAdapter.notifyDataSetChanged();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }
            });
        }


    }

    public void HistoryHM_To_Home(View view) {
        Intent intent = new Intent(history_for_measurements.this, home_page.class);
        startActivity(intent);
    }
    public void HistoryHM_To_Today(View view) {
        Intent intent = new Intent(history_for_measurements.this, today_page_recycler.class);
        startActivity(intent);
    }
    public void HistoryHM_To_History(View view) {
        Intent intent = new Intent(history_for_measurements.this, history_page.class);
        startActivity(intent);
    }
    public void HistoryHM_To_User(View view) {
        Intent intent = new Intent(history_for_measurements.this, user_information.class);
        startActivity(intent);
    }
    public void HistoryHM_To_HistoryM(View view) {
        Intent intent = new Intent(history_for_measurements.this, history_page.class);
        startActivity(intent);

    }  public void to_pdf(View view) {
        Intent intent = new Intent(history_for_measurements.this, pdf_form.class);
        startActivity(intent);
    }
    public void HistoryHM_To_PDF(View view) {
        Intent intent = new Intent(history_for_measurements.this, health_measurement_pdf_choices.class);
        startActivity(intent);
    }

}