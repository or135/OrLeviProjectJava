package com.example.orleviprojectjava;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> launcher;
    private View activityHome;
    private SharedPreferences sharedPreferences;
    private static final String THEME_PREFERENCES = "ThemePreferences";
    private static final String THEME_KEY = "theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE);
        setContentView(R.layout.activity_home);
        activityHome = findViewById(R.id.activityHome);

        applyTheme(getSavedTheme());

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent intent = result.getData();
                            String theme = intent.getStringExtra("theme");

                            // Save theme to SharedPreferences
                            saveTheme(theme);

                            // Apply theme
                            applyTheme(theme);
                        }
                    }
                }
        );
    }

    private String getSavedTheme() {
        // Default to light theme if no theme is saved
        return sharedPreferences.getString(THEME_KEY, "light");
    }

    private void saveTheme(String theme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(THEME_KEY, theme);
        editor.apply();
    }

    private void applyTheme(String theme) {
        if (theme.equals("dark")) {
            activityHome.setBackgroundColor(Color.BLACK);
        } else {
            activityHome.setBackgroundColor(Color.WHITE);
        }
    }

    public void Setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        // Pass current theme to SettingActivity
        intent.putExtra("current_theme", getSavedTheme());
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