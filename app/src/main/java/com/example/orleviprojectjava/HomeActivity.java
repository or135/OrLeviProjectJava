package com.example.orleviprojectjava;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> launcher;
    private View activityHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        activityHome = findViewById(R.id.activityHome);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent intent = result.getData();
                            String theme = intent.getStringExtra("theme");
                            if (theme.equals("dark")) {
                                activityHome.setBackgroundColor(Color.BLACK);
                            } else {
                                activityHome.setBackgroundColor(Color.WHITE);
                            }
                        }
                    }
                }
        );
    }

    public void Setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        launcher.launch(intent);
        }


    public void Profile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void Create(View view) {
        Intent intent = new Intent(this, CreatePictureActivity.class);
        startActivity(intent);
    }

    public void Gallery(View view) {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }
}