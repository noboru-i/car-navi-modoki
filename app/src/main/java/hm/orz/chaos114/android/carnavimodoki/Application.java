package hm.orz.chaos114.android.carnavimodoki;

import com.activeandroid.ActiveAndroid;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
