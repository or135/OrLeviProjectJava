package com.example.orleviprojectjava;



import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
public class CreateUserActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextGmail2, editTextPassword2,editTextUserName2;
    private Button buttonSignUp2;
    private TextView buttonlogin2;
    private FirebaseAuth mAuth;
    private AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        mAuth = FirebaseAuth.getInstance();
        authManager = new AuthManager();

        editTextUserName2 = findViewById(R.id.editTextUserName2);
        editTextGmail2 = findViewById(R.id.editTextGmail2);
        editTextPassword2 = findViewById(R.id.editTextPassword2);
        buttonSignUp2 = findViewById(R.id.buttonSignUp2);
        buttonSignUp2.setOnClickListener(this);
        buttonlogin2 = findViewById(R.id.buttonlogin2);
        buttonlogin2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonlogin2)// Navigate to LoginActivity
            startActivity(new Intent(this, MainActivity.class));
        if (v == buttonSignUp2)
            registerUser();
    }

    private void registerUser() {
        String username = editTextUserName2.getText().toString().trim();
        String email = editTextGmail2.getText().toString().trim();
        String password = editTextPassword2.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            editTextUserName2.setError("Username is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextGmail2.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword2.setError("Password is required");
            return;
        }
        if(password.length()<6){
            editTextPassword2.setError("Password should be at least 6 characters ");
            return;
        }


        authManager.registerUser(email, password, username, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                Toast.makeText(this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}