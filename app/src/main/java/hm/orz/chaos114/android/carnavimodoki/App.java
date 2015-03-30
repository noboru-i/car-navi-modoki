package hm.orz.chaos114.android.carnavimodoki;

import com.activeandroid.ActiveAndroid;
import com.os.operando.garum.Configuration;
import com.os.operando.garum.Garum;
import com.squareup.otto.Bus;

import hm.orz.chaos114.android.carnavimodoki.model.PlayingModel;
import hm.orz.chaos114.android.carnavimodoki.pref.entity.PlayingStatus;
import lombok.Getter;

public class App extends android.app.Application {

    private static Bus sBus;
    private static ModelLocator sModelLocator;

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

        initGarum();
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

    @SuppressWarnings("unchecked")
    private void initGarum() {
        Configuration.Builder builder = new Configuration.Builder(getApplicationContext());
        builder.setModelClasses(PlayingStatus.class);
        builder.setTypeSerializers(PlayingStatus.TypeSerializer.class);
        Garum.initialize(builder.create());
    }

    @Getter
    public static class ModelLocator {
        private PlayingModel playingModel;

        private ModelLocator() {
            playingModel = new PlayingModel();
        }
    }
}
