package communi.dog.aplicatiion.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseUser;

import communi.dog.aplicatiion.CommuniDogApp;
import communi.dog.aplicatiion.DB;
import communi.dog.aplicatiion.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_screen);
        // disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        DB db = CommuniDogApp.getInstance().getDb();

        FirebaseUser currentUser = db.getUsersAuthenticator().getCurrentUser();

        // update UI when DB finish to load the initial data
        db.firstLoadFlagLiveData.observe(this, isLoad -> {
            if (isLoad) {
                updateUI(currentUser);
            }
        });
    }

    /**
     * if there is a logged in user -> navigate to the map screen, else navigate to the login screen
     *
     * @param firebaseUser - the currently logged in user, null if no user os logged in
     */
    private void updateUI(FirebaseUser firebaseUser) {

        if (firebaseUser != null) {
            DB db = CommuniDogApp.getInstance().getDb();
            db.setCurrentUser(firebaseUser);
            db.currentUserLiveData.observe(this, user -> {
                if (user != null) {
                    startActivity(new Intent(this, MapScreenActivity.class));
                    finish();
                }
            });
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
