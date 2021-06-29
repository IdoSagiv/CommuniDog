package communi.dog.aplicatiion.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import communi.dog.aplicatiion.CommuniDogApp;
import communi.dog.aplicatiion.DB;
import communi.dog.aplicatiion.Mail;
import communi.dog.aplicatiion.R;
import communi.dog.aplicatiion.UnapprovedUsersAdapter;

public class UserApprovalActivity extends AppCompatActivity {
    private Mail mailAccount;
    private boolean success;
    private String mailUserName;

    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_approval);

        mailUserName = "userName";
        mailAccount = new Mail(mailUserName, "pass");

        db = CommuniDogApp.getInstance().getDb();

        UnapprovedUsersAdapter adapter = new UnapprovedUsersAdapter();
        RecyclerView recyclerView = findViewById(R.id.unapprovedUsersRecyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        db.unapprovedUsersLiveData.observe(this, users -> {
            TextView noUsersMsg = findViewById(R.id.noUsersToApproveTextView);
            adapter.setItems(users);
            if (users.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                noUsersMsg.setVisibility(View.VISIBLE);

            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noUsersMsg.setVisibility(View.GONE);
            }
        });

        adapter.onApproveCallback = user -> {
            //Initialize confirmation mail
            String[] sendTo = {user.getEmail()};
            mailAccount.setTo(sendTo);
            mailAccount.setFrom(mailUserName);
            mailAccount.setSubject("Registration to CommuniDog approved");

            Thread thread = new Thread(() -> {
                db.approveUser(user.getId());
                try {
                    success = mailAccount.send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (success) {
                // mail sent
                Toast.makeText(this, "mail sent", Toast.LENGTH_SHORT).show();
            } else {
                // mail failed to send
                Toast.makeText(this, "mail failed to sent", Toast.LENGTH_SHORT).show();
            }
        };

        adapter.onDisapproveCallback = v ->
                Toast.makeText(this, "not supported yet", Toast.LENGTH_SHORT).show();
    }
}