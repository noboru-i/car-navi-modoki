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
import lombok.Data;
import lombok.Setter;

public class MusicService extends Service {
    public enum ControlEvent {
        START,
        PAUSE,
        NEXT,
        PREV
    }

    static class PlayList {
        @Setter
        private List<PlayListEntity> list;
        private int currentIndex;

        PlayListEntity getCurrentEntity() {
            return list.get(currentIndex);
        }

        void next() {
            currentIndex++;
        }

        void prev() {
            currentIndex--;
        }
    }

    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;

    private PlayList mPlayList;

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

        mPlayList = new PlayList();
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
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer = new MediaPlayer();

                List<PlayListEntity> playList = PlayListEntity.all();
                if (playList == null) {
                    return;
                }
                mPlayList.setList(playList);
                playCurrent();
                break;
            case PAUSE:
                break;
            case NEXT:
                mPlayList.next();
                playCurrent();
                break;
            case PREV:
                mPlayList.prev();
                playCurrent();
                break;
        }
    }

    private void playCurrent() {
        try {
            String mediaId = mPlayList.getCurrentEntity().getMusic().getMediaId();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getApplicationContext(), Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaId));
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
