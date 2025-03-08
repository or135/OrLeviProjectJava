package com.example.orleviprojectjava;

import android.content.Intent;

import android.view.View;


import androidx.appcompat.app.AppCompatActivity;



public class Activity extends AppCompatActivity {


    public void ReturnTH(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}