package hm.orz.chaos114.android.carnavimodoki;

import com.activeandroid.ActiveAndroid;
import com.squareup.otto.Bus;

import hm.orz.chaos114.android.carnavimodoki.model.PlayingModel;
import lombok.Data;
import lombok.Getter;

public class App extends android.app.Application {

    private static Bus sBus;
    private static ModelLocator sModelLocator;

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

    public static synchronized ModelLocator Models() {
        if (sModelLocator == null) {
            sModelLocator = new ModelLocator();
        }
        return sModelLocator;
    }

    @Getter
    public static class ModelLocator {
        private PlayingModel playingModel;
        private ModelLocator() {
            playingModel = new PlayingModel();
        }
    }
}
