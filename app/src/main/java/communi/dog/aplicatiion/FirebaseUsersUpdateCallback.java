package communi.dog.aplicatiion;

import java.util.HashMap;

public interface FirebaseUsersUpdateCallback {
    void onCallback(HashMap<String, User> users);
}
