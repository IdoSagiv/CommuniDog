package communi.dog.aplicatiion.Callbacks;

import java.util.HashMap;

import communi.dog.aplicatiion.User;

public interface FirebaseUsersUpdateCallback {
    void onCallback(HashMap<String, User> users);
}
