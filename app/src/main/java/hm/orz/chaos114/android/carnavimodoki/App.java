package hm.orz.chaos114.android.carnavimodoki;

import android.content.Context;

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

    public int hoge;

    public boolean fuga() {
        return false;
    }

    public void piyo(){
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

        initGarum();
        initStetho();
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

    private void initStetho() {
        try {
            // debugビルドの場合のみ成功
            Class<?> clazz = Class.forName("hm.orz.chaos114.android.carnavimodoki.util.StethoUtil");
            clazz.getMethod("initStetho", Context.class).invoke(null, this);
        } catch (Exception e) {
            // ignore
        }
    }

    @Getter
    public static class ModelLocator {
        private PlayingModel playingModel;

        private ModelLocator() {
            playingModel = new PlayingModel();
        }
    }
}
