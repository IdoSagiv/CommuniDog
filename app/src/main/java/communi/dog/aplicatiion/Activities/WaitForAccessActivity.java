package communi.dog.aplicatiion.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import communi.dog.aplicatiion.Activities.LoginActivity;
import communi.dog.aplicatiion.Activities.MapScreenActivity;
import communi.dog.aplicatiion.CommuniDogApp;
import communi.dog.aplicatiion.DB;
import communi.dog.aplicatiion.R;

public class WaitForAccessActivity extends AppCompatActivity {
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_access);
        db = CommuniDogApp.getInstance().getDb();

        Button logoutBtn = findViewById(R.id.logoutBtnWaitAccessScreen);
        logoutBtn.setOnClickListener(v -> logout());

        db.currentUserLiveData.observe(this, user -> {
            if (user.isApproved()) {
                startActivity(new Intent(this, MapScreenActivity.class));
                finish();
            }
        });
    }

    public void logout() {
        db.logoutUser();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: {
                    logout();
                    break;
                }
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }
}
