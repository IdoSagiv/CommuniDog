package communi.dog.aplicatiion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_screen);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //updateUI(CommuniDogApp.getInstance().getDb().getUsersAuthenticator().getCurrentUser());
        new Handler().postDelayed(() -> {
            updateUI(CommuniDogApp.getInstance().getDb().getUsersAuthenticator().getCurrentUser());
        }, 3000);
    }

    private void updateUI(FirebaseUser user) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        //todo: need to fix synchronization problems in the remember logged in users
//        if (user != null) {
//            DB db = CommuniDogApp.getInstance().getDb();
//            db.setCurrentUser(user);
//            db.currentUSerLiveData.observe(this, user1 -> {
//                startActivity(new Intent(this, MapScreenActivity.class));
//                finish();
//            });
//
//        } else {
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//        }
    }
}
