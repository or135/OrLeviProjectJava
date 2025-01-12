package com.example.orleviprojectjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CreatePictureActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText editText;
    private TextView textView;
    private final String stringURL = "https://api.openai.com/v1/images/generations";
    private final String stringAPIKey = "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";
    private String stringOutput = " ";
    private Bitmap bitmapOutputImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_picture);

        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
    }

    public void buttonGenerateAIImage(View view) {
        String stringInputText = editText.getText().toString().trim(); //מסדר את הטקסט
        textView.setText("In process...");
        if (stringInputText.isEmpty()) {
            textView.setText("Prompt cannot be empty!");
            return;
        }

        JSONObject jsonObject = new JSONObject(); //מסדר את הבקשה לAI
        try {
            jsonObject.put("prompt", stringInputText);
            jsonObject.put("n", 1);
            jsonObject.put("size", "1024x1024");
        } catch (JSONException e) {
            textView.setText("Error creating JSON body");
            return;
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, //הבקשה עצמה
                stringURL,
                jsonObject,
                response -> {
                    try {
                        stringOutput = response
                                .getJSONArray("data")
                                .getJSONObject(0)
                                .getString("url");
                        textView.setText("Image URL ready");
                    } catch (JSONException e) {
                        textView.setText("Error parsing response");
                    }
                },
                error -> {
                    textView.setText("API request failed: " + error.getMessage());
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + stringAPIKey);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        int intTimeOutPeriod = 60000; // 60 שניות
        RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeOutPeriod,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy); // מדיניות בקשה

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest); //תור בקשות
    }

    public void buttonShowImage(View view) {
        textView.setText("Downloading image...");

        new Thread(() -> {
            try {
                URL url = new URL(stringOutput);
                bitmapOutputImage = BitmapFactory.decodeStream(url.openStream());

                runOnUiThread(() -> {
                    Bitmap bitmapFinalImage = Bitmap.createScaledBitmap(bitmapOutputImage,
                            imageView.getWidth(),
                            imageView.getHeight(),
                            true);
                    imageView.setImageBitmap(bitmapFinalImage);
                    textView.setText("Image generation successful!");
                });
            } catch (Exception e) {
                runOnUiThread(() -> textView.setText("Error downloading image!"));
            }
        }).start();
    }
}