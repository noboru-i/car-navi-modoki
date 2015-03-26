package hm.orz.chaos114.android.carnavimodoki;

import com.activeandroid.ActiveAndroid;
import com.squareup.otto.Bus;

public class App extends android.app.Application {

    private static Bus sBus;

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }

    public static synchronized Bus Bus() {
        if (sBus == null) {
            sBus = new Bus();
        }
        return sBus;
    }
}
