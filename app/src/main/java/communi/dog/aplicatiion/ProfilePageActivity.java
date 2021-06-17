package communi.dog.aplicatiion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilePageActivity extends AppCompatActivity {
    User currentUser;

    TextView usernameEditText;
    EditText dogNameEditText;
    EditText emailEditText;
    EditText phoneEditText;
    EditText bioEditText;
    private ImageView btnEditProfile;
    private boolean isEdit = false;
    private DB appDB;

    private String dogNameBeforeEdit;
    private String emailBeforeEdit;
    private String phoneBeforeEdit;
    private String bioBeforeEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        this.appDB = CommuniDogApp.getInstance().getDb();
        currentUser = this.appDB.getCurrentUser();

        usernameEditText = findViewById(R.id.profile_user_name);
        dogNameEditText = findViewById(R.id.profile_dog_name);
        emailEditText = findViewById(R.id.usersEmailMyProfile);
        phoneEditText = findViewById(R.id.usersPhoneMyProfile);
        bioEditText = findViewById(R.id.profile_bio);

        TextView btnMyMarker = findViewById(R.id.profile_to_my_marker);
        ImageButton btnBackToMap = findViewById(R.id.backToMapFromProfile);
        ImageView btnCancelEdit = findViewById(R.id.btnCancelEditProfile);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        dogNameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        phoneEditText.setEnabled(false);
        bioEditText.setEnabled(false);

        usernameEditText.setText(currentUser.getUserName());
        dogNameEditText.setText(currentUser.getUserDogName());
        emailEditText.setText(currentUser.getEmail());
        phoneEditText.setText(currentUser.getPhoneNumber());
        bioEditText.setText(currentUser.getUserDescription());

        btnEditProfile.setOnLongClickListener(v -> {
            if (isEdit) {
                Toast.makeText(this, "click to save changes", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "click to edit", Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        btnEditProfile.setOnClickListener(v -> {
            if (isEdit) {
                btnCancelEdit.setVisibility(View.GONE);
                // solving issue #19
                String userId = currentUser.getId();
                String password = currentUser.getPassword();
                String name = currentUser.getUserName();

                // get from user input
                String email = emailEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String bio = bioEditText.getText().toString();
                String dogName = dogNameEditText.getText().toString();
                this.appDB.updateUser(userId, email, password, name, phone, dogName, bio);
            } else {
                btnCancelEdit.setVisibility(View.VISIBLE);
                dogNameBeforeEdit = dogNameEditText.getText().toString();
                emailBeforeEdit = emailEditText.getText().toString();
                phoneBeforeEdit = phoneEditText.getText().toString();
                bioBeforeEdit = bioEditText.getText().toString();
            }
            isEdit = !isEdit;
            dogNameEditText.setEnabled(isEdit);
            bioEditText.setEnabled(isEdit);
            emailEditText.setEnabled(isEdit);
            phoneEditText.setEnabled(isEdit);
            int edit_ic = isEdit ? R.drawable.ic_save_profile : R.drawable.ic_edit_profile;
            btnEditProfile.setImageResource(edit_ic);
        });

        btnMyMarker.setOnClickListener(v -> {
            MapState mapState = CommuniDogApp.getInstance().getMapState();
            if (mapState.getMarker(currentUser.getId()) == null) {
                Toast.makeText(this, "no marker found", Toast.LENGTH_SHORT).show();
            } else {
                Intent editMarkerIntent = new Intent(this, AddMarkerActivity.class);
                editMarkerIntent.putExtra("marker_id_to_edit", currentUser.getId());
                startActivity(editMarkerIntent);
            }
        });

        btnCancelEdit.setOnClickListener(v -> cancelEditing());

        btnBackToMap.setOnClickListener(v -> backToMap());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_edit", isEdit);
        outState.putString("user_name", usernameEditText.getText().toString());
        outState.putString("dog_name", dogNameEditText.getText().toString());
        outState.putString("email", emailEditText.getText().toString());
        outState.putString("phone", phoneEditText.getText().toString());
        outState.putString("bio", bioEditText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isEdit = savedInstanceState.getBoolean("is_edit");
        int edit_ic = isEdit ? R.drawable.ic_save_profile : R.drawable.ic_edit_profile;
        btnEditProfile.setImageResource(edit_ic);
        usernameEditText.setText(savedInstanceState.getString("user_name"));
        dogNameEditText.setText(savedInstanceState.getString("dog_name"));
        emailEditText.setText(savedInstanceState.getString("email"));
        phoneEditText.setText(savedInstanceState.getString("phone"));
        bioEditText.setText(savedInstanceState.getString("bio"));
    }

    private void cancelEditing() {
        dogNameEditText.setText(dogNameBeforeEdit);
        emailEditText.setText(emailBeforeEdit);
        phoneEditText.setText(phoneBeforeEdit);
        bioEditText.setText(bioBeforeEdit);
        if (isEdit) {
            btnEditProfile.callOnClick();
        }
    }

    public void logout (View view) {
        // negative is positive and vice versa to allow yes on left and no on right
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?").setCancelable(false);
        builder.setNegativeButton("Yes", (dialogInterface, i) -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            appDB.resetUser();
            startActivity(intent);
        });
        builder.setPositiveButton("No", (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void backToMap() {
        Intent backToMapIntent = new Intent(ProfilePageActivity.this, MapScreenActivity.class);
        backToMapIntent.putExtra("center_to_my_location", false);
        startActivity(backToMapIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isEdit) {
            cancelEditing();
        } else {
            super.onBackPressed();
            backToMap();
        }
    }
}