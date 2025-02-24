package com.example.orleviprojectjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;

public class SettingActivity extends AppCompatActivity {

    private RadioButton radioButtonLight, radioButtonDark;
    private ImageButton ReturnFSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        radioButtonLight = findViewById(R.id.radioButtonLight);
        radioButtonDark = findViewById(R.id.radioButtonDark);
        ReturnFSetting = findViewById(R.id.ReturnFSetting);

        radioButtonLight.setChecked(true);

        radioButtonLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonDark.setChecked(false);
                Intent intent = new Intent();
                intent.putExtra("theme", "light");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        radioButtonDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonLight.setChecked(false);
                Intent intent = new Intent();
                intent.putExtra("theme", "dark");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        ReturnFSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String theme = radioButtonLight.isChecked() ? "light" : "dark";
                Intent intent = new Intent();
                intent.putExtra("theme", theme);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

}