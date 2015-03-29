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
import java.util.List;

import hm.orz.chaos114.android.carnavimodoki.App;
import hm.orz.chaos114.android.carnavimodoki.db.entity.PlayListEntity;
import hm.orz.chaos114.android.carnavimodoki.model.PlayingModel;
import lombok.Data;
import lombok.Setter;

public class MusicService extends Service {
    public enum ControlEvent {
        START,
        PLAY,
        PAUSE,
        NEXT,
        PREV
    }

    public enum State {
        PLAY,
        PAUSE,
        NEXT,
        PREV
    }

    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;

    private PlayingModel mPlayingModel;

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

        mPlayingModel = App.Models().getPlayingModel();

        mMediaPlayer = new MediaPlayer();
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
                playCurrent();
                changeState(State.PLAY);
                break;
            case PLAY:
                playCurrent();
                changeState(State.PLAY);
                break;
            case PAUSE:
                pause();
                changeState(State.PAUSE);
                break;
            case NEXT:
                mPlayingModel.next();
                playCurrent();
                changeState(State.NEXT);
                break;
            case PREV:
                mPlayingModel.prev();
                playCurrent();
                changeState(State.PREV);
                break;
        }
    }

    private void playCurrent() {
        try {
            String mediaId = mPlayingModel.getCurrentEntity().getMusic().getMediaId();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getApplicationContext(), Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaId));
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        App.Models().getPlayingModel().setPlaying(true);
    }

    private void pause() {
        mMediaPlayer.pause();
        App.Models().getPlayingModel().setPlaying(false);
    }

    private void changeState(State state) {
        App.Bus().post(state);
    }
}
