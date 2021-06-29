package communi.dog.aplicatiion;

import android.widget.Button;
import android.widget.EditText;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import communi.dog.aplicatiion.Activities.LoginActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class LoginActivityTest extends TestCase {

    @Test
    public void when_all_the_fields_are_empty_theTheLoginButtonIsDisable(){
        LoginActivity loginActivity = Robolectric.buildActivity(LoginActivity.class).create().visible().get();

        Button login_button = loginActivity.findViewById(R.id.login_button);
        EditText emailAddress = loginActivity.findViewById(R.id.input_email_login);
        EditText pass = loginActivity.findViewById(R.id.user_password);

        emailAddress.setText("Test@gmail.com");
        pass.setText("111111111");

        assertTrue(login_button.isEnabled());
    }

    @Test
    public void when_one_field_is_empty_theTheLoginButtonIsDisable(){
        LoginActivity loginActivity = Robolectric.buildActivity(LoginActivity.class).create().visible().get();

        Button login_button = loginActivity.findViewById(R.id.login_button);

        assertFalse(login_button.isEnabled());

        EditText emailAddress = loginActivity.findViewById(R.id.input_email_login);
        EditText userPassword = loginActivity.findViewById(R.id.user_password);

        emailAddress.setText("123456789");
        assertFalse(login_button.isEnabled());

        emailAddress.setText("");
        userPassword.setText("123456789");
        assertFalse(login_button.isEnabled());
    }

    @Test
    public void when_two_fields_are_full_theTheLoginButtonIEnable(){
        LoginActivity loginActivity = Robolectric.buildActivity(LoginActivity.class).create().visible().get();

        Button login_button = loginActivity.findViewById(R.id.login_button);

        assertFalse(login_button.isEnabled());

        EditText emailAddress = loginActivity.findViewById(R.id.input_email_login);
        EditText userPassword = loginActivity.findViewById(R.id.user_password);

        emailAddress.setText("123456789");
        userPassword.setText("123456789");

        assertTrue(login_button.isEnabled());
    }
}