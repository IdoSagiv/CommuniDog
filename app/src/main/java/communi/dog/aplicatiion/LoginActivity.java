package communi.dog.aplicatiion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private DB db;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.db = CommuniDogApp.getInstance().getDb();

        // find email and password views
        emailEditText = findViewById(R.id.input_email_login);
        passwordEditText = findViewById(R.id.user_password);

        // register button
        TextView to_register_btn = findViewById(R.id.register_now);
        to_register_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        // close keyboard when click outside editText
        findViewById(R.id.loginConstraintLayout).setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            emailEditText.requestFocus();
            imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);
            emailEditText.clearFocus();
        });

        // login button
        loginButton = findViewById(R.id.login_button);
        loginButton.setEnabled(false);
        loginButton.setOnClickListener(v -> {
            FirebaseAuth auth = db.getUsersAuthenticator();
            auth.signInWithEmailAndPassword(emailEditText.getText().toString(),
                    passwordEditText.getText().toString()).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginActivity", "signInWithEmail:success");
                    FirebaseUser user = auth.getCurrentUser();
                    db.setCurrentUser(user);

                    // if all permissions approved, update UI
                    requestPermissionsIfNecessary(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.INTERNET
                    });
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                    Toast.makeText(this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            });

            updateLoginButtonState();
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                updateLoginButtonState();
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                updateLoginButtonState();
            }
        });
    }

    public void updateUI() {
        db.currentUserLiveData.observe(this, user -> {
            if (user.isApproved()) {
                startActivity(new Intent(this, MapScreenActivity.class));
            } else {
                startActivity(new Intent(this, WaitForAccessActivity.class));
            }
            finish();
        });
    }

    private void updateLoginButtonState() {
        String email = emailEditText.getText().toString();
        loginButton.setEnabled(!email.isEmpty() && email.length() <= 254 &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                !passwordEditText.getText().toString().isEmpty());
    }


    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else{
            updateUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "cant work without permissions", Toast.LENGTH_SHORT).show();
                db.logoutUser();
                return;
            }
        }
        updateUI();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("userEmail", emailEditText.getText().toString());
        outState.putString("userPassword", passwordEditText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        emailEditText.setText(savedInstanceState.getString("userEmail"));
        passwordEditText.setText(savedInstanceState.getString("userPassword"));
        updateLoginButtonState();
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: {
                    // finish();
                    finishAffinity();
                    System.exit(0);
                    break;
                }
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Close the app?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}