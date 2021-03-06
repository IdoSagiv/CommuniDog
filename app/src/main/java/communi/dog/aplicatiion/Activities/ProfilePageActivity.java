package communi.dog.aplicatiion.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import communi.dog.aplicatiion.CommuniDogApp;
import communi.dog.aplicatiion.DB;
import communi.dog.aplicatiion.MapState;
import communi.dog.aplicatiion.R;
import communi.dog.aplicatiion.User;

public class ProfilePageActivity extends AppCompatActivity {
    private User currentUser;

    private TextView usernameEditText;
    private EditText dogNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText bioEditText;
    private ImageView btnEditProfile;
    private ImageView btnCancelEdit;
    private boolean isEdit = false;
    private DB appDB;

    private String dogNameBeforeEdit;
    private String emailBeforeEdit;
    private String phoneBeforeEdit;
    private String bioBeforeEdit;
    private KeyListener bioEditTextKeyListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        this.appDB = CommuniDogApp.getInstance().getDb();
        currentUser = this.appDB.getCurrentUser();

        // find views
        usernameEditText = findViewById(R.id.profile_user_name);
        dogNameEditText = findViewById(R.id.profile_dog_name);
        emailEditText = findViewById(R.id.usersEmailMyProfile);
        phoneEditText = findViewById(R.id.usersPhoneMyProfile);
        bioEditText = findViewById(R.id.profile_bio);
        bioEditTextKeyListener = bioEditText.getKeyListener();
        btnCancelEdit = findViewById(R.id.btnCancelEditProfile);
        btnEditProfile = findViewById(R.id.btnEditProfile);


        // initialize screen appearance
        setViewsByState(false);
        setViewsContentByUser(currentUser);

        btnEditProfile.setOnClickListener(v -> {
            if (isEdit) {
                btnCancelEdit.setVisibility(View.GONE);
                this.appDB.updateUser(currentUser.getId(),
                        emailEditText.getText().toString(),
                        currentUser.getUserName(),
                        phoneEditText.getText().toString(),
                        dogNameEditText.getText().toString(),
                        bioEditText.getText().toString(), currentUser.isManager(),
                        currentUser.isApproved());
            } else {
                dogNameBeforeEdit = dogNameEditText.getText().toString();
                emailBeforeEdit = emailEditText.getText().toString();
                phoneBeforeEdit = phoneEditText.getText().toString();
                bioBeforeEdit = bioEditText.getText().toString();
            }
            isEdit = !isEdit;
            setViewsByState(isEdit);
        });

        // my marker button callback
        findViewById(R.id.profile_to_my_marker).setOnClickListener(v -> {
            MapState mapState = MapState.getInstance();
            if (mapState.getMarker(currentUser.getId()) == null) {
                Toast.makeText(this, "no marker found", Toast.LENGTH_SHORT).show();
            } else {
                Intent editMarkerIntent = new Intent(this, AddMarkerActivity.class);
                editMarkerIntent.putExtra("marker_id_to_edit", currentUser.getId());
                startActivity(editMarkerIntent);
            }
        });

        // cancel edit button callback
        btnCancelEdit.setOnClickListener(v -> cancelEditing());

        // back button callback
        findViewById(R.id.backToMapFromProfile).setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });
    }

    /**
     * sets the views content as the given user fields
     */
    private void setViewsContentByUser(User user) {
        usernameEditText.setText(user.getUserName());
        dogNameEditText.setText(user.getUserDogName());
        emailEditText.setText(user.getEmail());
        phoneEditText.setText(user.getPhoneNumber());
        bioEditText.setText(user.getUserDescription());
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
        usernameEditText.setText(savedInstanceState.getString("user_name"));
        dogNameEditText.setText(savedInstanceState.getString("dog_name"));
        emailEditText.setText(savedInstanceState.getString("email"));
        phoneEditText.setText(savedInstanceState.getString("phone"));
        bioEditText.setText(savedInstanceState.getString("bio"));
        setViewsByState(isEdit);
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

    /**
     * sets the views state according to the given editState
     */
    private void setViewsByState(boolean isEditState) {
        if (isEditState) {
            btnCancelEdit.setVisibility(View.VISIBLE);
            bioEditText.setKeyListener(bioEditTextKeyListener); // bio edittext will listen to new keys
        } else {
            btnCancelEdit.setVisibility(View.GONE);
            bioEditText.setKeyListener(null); // bio edittext won't listen to new keys (cant edit it)
        }
        dogNameEditText.setEnabled(isEditState);
        emailEditText.setEnabled(isEditState);
        phoneEditText.setEnabled(isEditState);
        int edit_ic = isEditState ? R.drawable.ic_save_profile : R.drawable.ic_edit_profile;
        btnEditProfile.setImageResource(edit_ic);
    }

    public void logout(View view) {
        // negative is positive and vice versa to allow yes on left and no on right
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?").setCancelable(false);
        builder.setNegativeButton("Yes", (dialogInterface, i) -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            appDB.logoutUser();
            startActivity(intent);
        });
        builder.setPositiveButton("No", (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (isEdit) {
            cancelEditing();
        } else {
            super.onBackPressed();
            finish();
        }
    }
}
