package com.example.orleviprojectjava;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePictureActivity extends ReturnActivity implements View.OnClickListener {

    private ImageView imageView;
    private EditText editText;
    private TextView textView;
    private final String stringURL = "https://api.openai.com/v1/images/generations";
    private final String stringAPIKey = "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";
    private String stringOutput = " ";
    private Bitmap bitmapOutputImage;
    private DatabaseReference databaseRef;
    private AuthManager authManager;
    private final int INT_TIME_OUT_PERIOD = 60000;
    private Button buttonShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_picture);

        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        buttonShare = findViewById(R.id.buttonShare);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        authManager = new AuthManager();

        buttonShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        CustomDialog customDialog = new CustomDialog(this, v);
        customDialog.show();
    }

    public void buttonGenerateAIImage(View view) {
        String stringInputText = editText.getText().toString().trim();
        textView.setText("In process...");
        if (stringInputText.isEmpty()) {
            textView.setText("Prompt cannot be empty!"); return;
        }

        // JSON פורמט טקסט פשוט לתקשורת בין אפליקציות
        JSONObject jsonObject = new JSONObject(); // פורמט  OpenAI דרוש
        try {
            jsonObject.put("prompt", stringInputText);
            jsonObject.put("n", 1);
            jsonObject.put("size", "1024x1024");
        } catch (JSONException e) {
            textView.setText("Error creating JSON body"); return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, stringURL, jsonObject, //הבקשה עצמה לשרת // סוג הבקשה (POST)
                response -> {
                    try {
                        stringOutput = response.getJSONArray("data").getJSONObject(0).getString("url"); // הוצאת כתובת ה-URL של התמונה
                        textView.setText("Image URL ready");
                    } catch (JSONException e) {
                        textView.setText("Error parsing response");
                    }
                },
                error -> {
                    textView.setText("API request failed: " + error.getMessage());
                }) {
            @Override //getHeaders() מוסיף כותרות לבקשת HTTP // עוטף את המידע
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + stringAPIKey);
                headers.put("Content-Type", "application/json"); //סוג הנתונים שאני שולח לשרת
                return headers;
            }
        };

        RetryPolicy retryPolicy = new DefaultRetryPolicy(INT_TIME_OUT_PERIOD, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy); // מדיניות בקשה
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest); //תור בקשות
    }

    public void buttonShowImage(View view) {
        buttonShare.setClickable(true);
        textView.setText("Downloading image...");

        new Thread(() -> { //להריץ כמה משימות במקביל בלי שהכול ייתקע
            try {
                URL url = new URL(stringOutput);
                bitmapOutputImage = BitmapFactory.decodeStream(url.openStream()); //מוריד את התמונה ומעביר לפורמט מתאים

                runOnUiThread(() -> { // מה שמוחזר לThread הראשי
                    Bitmap bitmapFinalImage = Bitmap.createScaledBitmap(bitmapOutputImage, imageView.getWidth(), imageView.getHeight(), true);
                    imageView.setImageBitmap(bitmapFinalImage);
                    textView.setText("Image generation successful!");
                });
            } catch (Exception e) {
                runOnUiThread(() -> textView.setText("Error downloading image!"));
            }
        }).start();
    }

    public void buttonShareImage(View contextView) {
        buttonShare.setClickable(false);
        if (bitmapOutputImage == null) {
            textView.setText("Please generate and show an image first!"); return;
        }

        String userId = authManager.getCurrentUserId();

        textView.setText("Saving image...");

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //ממיר את התמונה לפורמט שהפיירבייס יכול לשמור
        bitmapOutputImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageData = baos.toByteArray();
        String base64Image = Base64.encodeToString(imageData, Base64.DEFAULT);

        String imageId = UUID.randomUUID().toString();
        Map<String, Object> imageMap = new HashMap<>(); // יוצר את האובייקט של התמונה
        imageMap.put("imageId", imageId);
        imageMap.put("userId", userId);
        imageMap.put("prompt", editText.getText().toString());
        imageMap.put("imageData", base64Image);
        imageMap.put("timestamp", System.currentTimeMillis());
        imageMap.put("likes", 0);

        databaseRef.child("images").child(userId).child(imageId).setValue(imageMap)
                .addOnSuccessListener(aVoid -> {
                    updateUserPhotoCount(userId);
                    textView.setText("Image saved successfully!");
                })
                .addOnFailureListener(e ->
                        textView.setText("Failed to save image: " + e.getMessage())
                );
    }

    private void updateUserPhotoCount(String userId) {
        databaseRef.child("users").child(userId).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        Long currentPhotoCount = dataSnapshot.child("numberOfPhotos").getValue(Long.class);
                        int newPhotoCount = 0;
                        if (currentPhotoCount != null) { newPhotoCount =  currentPhotoCount.intValue() + 1;}
                        dataSnapshot.getRef().child("numberOfPhotos").setValue(newPhotoCount);

                        if (newPhotoCount >= 3) {
                            dataSnapshot.getRef().child("premium").setValue(true);
                            textView.setText("Image saved! You are now a premium user and can comment on photos!");
                        }
                    }})
                .addOnFailureListener(e ->
                        textView.setText("Failed to update photo count: " + e.getMessage())
                );
    }
}