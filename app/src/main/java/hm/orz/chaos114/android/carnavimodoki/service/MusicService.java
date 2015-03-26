package hm.orz.chaos114.android.carnavimodoki.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import com.squareup.otto.Subscribe;

import java.io.IOException;

import hm.orz.chaos114.android.carnavimodoki.App;

public class MusicService extends Service {
    public enum ControlEvent {
        START,
        PAUSE
    }

    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        App.Bus().register(this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onDestroy() {
        App.Bus().unregister(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Subscribe
    public void subscribeControlEvent(ControlEvent event) {
        switch (event) {
            case START:
                mMediaPlayer = new MediaPlayer();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String mediaId = sp.getString("media_id", null);
                if (mediaId == null) {
                    return;
                }
                try {
                    mMediaPlayer.setDataSource(getApplicationContext(), Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaId));
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case PAUSE:
                break;
        }
    }
}
