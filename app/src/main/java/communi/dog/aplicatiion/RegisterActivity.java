package communi.dog.aplicatiion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText rePasswordEditText;
    private EditText userNameEditText;
    private Button registerBtn;
    private TextView to_login_btn;
    private DB db;
    enum Length{
        LARGE,
        SHORT,
        VALID
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.input_email_register);
        passwordEditText = findViewById(R.id.input_pass_reg);
        rePasswordEditText = findViewById(R.id.input_repass_reg);
        registerBtn = findViewById(R.id.register_bt);
        to_login_btn = findViewById(R.id.back_to_login);
        userNameEditText = findViewById(R.id.input_user_name_register);

        registerBtn.setEnabled(false);

        this.db = CommuniDogApp.getInstance().getDb();
        this.db.refreshDataUsers();

        registerBtn.setOnClickListener(v -> tryToRegister());
        to_login_btn.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        findViewById(R.id.registerConstraintLayout).setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            emailEditText.requestFocus();
            imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);
            emailEditText.clearFocus();
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                registerBtn.setEnabled(checkButtonRegisterEnable());
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                registerBtn.setEnabled(checkButtonRegisterEnable());
            }
        });

        rePasswordEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                registerBtn.setEnabled(checkButtonRegisterEnable());
            }
        });
    }

    boolean checkButtonRegisterEnable() {
        return !(isEmptyEditText(emailEditText) || isEmptyEditText(passwordEditText) || isEmptyEditText(rePasswordEditText));
    }

    void tryToRegister() {
        boolean valid_input = true;
        if (!isEmail(emailEditText)) {
            emailEditText.setError("invalid email");
            valid_input = false;
        }
        // there is no limit on the password length since it is bad practice
        if (isValidLength(passwordEditText.getText().toString(), 6, -1) != Length.VALID) {
            passwordEditText.setError("password should has least 6 characters");
            valid_input = false;
        } else {
            if (!passwordEditText.getText().toString().equals(rePasswordEditText.getText().toString())) {
                rePasswordEditText.setError("does not match");
                valid_input = false;
            }
        }
        Length nameLen = isValidLength(userNameEditText.getText().toString(), 3, 64);
        if (nameLen != Length.VALID)
        {
            valid_input = false;
            String error = nameLen == Length.LARGE ? "name is too large" : "name is too short";
            userNameEditText.setError(error);
        }

        if (!valid_input) return;

        // DB validation
        if (this.db.idUserExists(emailEditText.getText().toString())) {
            Toast.makeText(this, "user is already register", Toast.LENGTH_SHORT).show();
            valid_input = false;
        }

        if (valid_input) {
            // fireBase authentication
            FirebaseAuth auth = db.getUsersAuthenticator();
            auth.createUserWithEmailAndPassword(emailEditText.getText().toString(),
                    passwordEditText.getText().toString()).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d("RegisterActivity", "createUserWithEmail:success");

                    // add user
                    FirebaseUser user = auth.getCurrentUser();
                    db.addUser(user.getUid(), this.emailEditText.getText().toString(),
                            this.userNameEditText.getText().toString());
                    db.setCurrentUser(user);

                    // update UI
                    startActivity(new Intent(this, WaitForAccessActivity.class));
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("RegisterActivity", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Length isValidLength(String input, int min, int max)
    {
        if (max != -1 && input.length() > max)
        {
            return Length.LARGE;
        }
        if (input.length() < min)
        {
            return Length.SHORT;
        }
        return Length.VALID;
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (isValidLength(email.toString(), 1, 254) == Length.VALID &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmptyEditText(EditText text) {
        return text.getText().toString().isEmpty();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backToLoginIntent = new Intent(this, LoginActivity.class);
        startActivity(backToLoginIntent);
    }
}