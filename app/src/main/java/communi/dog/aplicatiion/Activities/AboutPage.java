package communi.dog.aplicatiion.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.annotation.Nullable;

import communi.dog.aplicatiion.R;

public class AboutPage extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_section);

        setupHyperLink();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .7), (int) (height * .5));
    }

    private void setupHyperLink() {
        TextView linkToGitView = findViewById(R.id.about_info);
        linkToGitView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}