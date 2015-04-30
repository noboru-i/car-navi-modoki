package hm.orz.chaos114.android.carnavimodoki.util;

import android.content.Context;

import com.facebook.stetho.Stetho;

public class StethoUtil {

    public static void initStetho(Context applicationContext) {
        Stetho.initialize(
                Stetho.newInitializerBuilder(applicationContext)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(applicationContext))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(applicationContext))
                        .build());
    }
}
