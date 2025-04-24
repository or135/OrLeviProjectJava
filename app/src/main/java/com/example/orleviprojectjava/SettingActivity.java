package com.example.orleviprojectjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class SettingActivity extends ReturnActivity {

    private RadioButton radioButtonLight, radioButtonDark;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        radioButtonLight = findViewById(R.id.radioButtonLight);
        radioButtonDark = findViewById(R.id.radioButtonDark);

        String currentTheme = getIntent().getStringExtra("current_theme");
        if (currentTheme != null) {
            if (currentTheme.equals("dark")) {
                radioButtonDark.setChecked(true);
                radioButtonLight.setChecked(false);
            } else {
                radioButtonLight.setChecked(true);
                radioButtonDark.setChecked(false);
            }
        }

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.US);
            }
        });


        radioButtonLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonDark.setChecked(false);
                Intent intent = new Intent();
                intent.putExtra("theme", "light");
                tts.speak("light mode", TextToSpeech.QUEUE_FLUSH, null, null);
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
                tts.speak("dark mode", TextToSpeech.QUEUE_FLUSH, null, null);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}