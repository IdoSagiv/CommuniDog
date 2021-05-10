package communi.dog.aplicatiion;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class RegisterActivityTest extends TestCase {

    @Test
    public void when_all_the_fields_are_empty_theTheRegisterIsDisable(){
        RegisterActivity registerActivity = Robolectric.buildActivity(RegisterActivity.class).create().visible().get();

        Button registerButton = registerActivity.findViewById(R.id.register_bt);
        assertFalse(registerButton.isEnabled());
    }

    @Test
    public void when_all_field_is_full_theTheRegisterIsEnable(){
        RegisterActivity registerActivity = Robolectric.buildActivity(RegisterActivity.class).create().visible().get();

        Button registerButton = registerActivity.findViewById(R.id.register_bt);

        assertFalse(registerButton.isEnabled());
    }
}