package communi.dog.aplicatiion;

import android.app.Application;

public class CommuniDogApp extends Application {
    private static CommuniDogApp instance = null;
    private DB localDdb;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        localDdb = new DB(this);
    }

    public static CommuniDogApp getInstance() {
        return instance;
    }

    public DB getDb() {
        return localDdb;
    }
}
