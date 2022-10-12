package com.example.capstone1.dependent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.R;

public class user_management extends AppCompatActivity {
    Button loginBtn;
    int role = 0;
    String[] options = {"Dependent", "User"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_dependent_user_management);







    }

    public void Profile_To_Account(View view) {
        Intent intent = new Intent(this, account.class);
        startActivity(intent);
    }

    public void Profile_To_Home(View view) {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }
}
