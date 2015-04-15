package hm.orz.chaos114.android.carnavimodoki.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.SurfaceHolder;

import com.squareup.otto.Subscribe;

import java.io.IOException;

import hm.orz.chaos114.android.carnavimodoki.App;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Movie;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Music;
import hm.orz.chaos114.android.carnavimodoki.model.PlayingModel;
import hm.orz.chaos114.android.carnavimodoki.pref.entity.PlayingStatus;
import hm.orz.chaos114.android.carnavimodoki.util.NotificationUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static hm.orz.chaos114.android.carnavimodoki.util.LogUtil.*;

public class MusicService extends Service {
    public enum ControlEvent {
        START,
        PLAY,
        PAUSE,
        NEXT,
        PREV,
    }

    public enum State {
        PLAY,
        PAUSE,
        NEXT,
        PREV
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor(suppressConstructorProperties = true)
    public static class ChangeSizeState {
        private int width;
        private int height;
    }

    public class LocalBinder extends Binder {

        public void setSurfaceHolder(final SurfaceHolder holder) {
            debugMethod();
            mHolder = holder;
        }

        public void releaseSurfaceHolder() {
            mHolder = null;
            mMediaPlayer.setDisplay(null);
        }
    }

    private IBinder mBinder = new LocalBinder();
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mHolder;

    private PlayingModel mPlayingModel;
    private String currentMediaId;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        debugMethod();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        debugMethod();
        mMediaPlayer.setDisplay(null);
        mHolder = null;
        return true;
    }

    @Override
    public void onCreate() {
        App.Bus().register(this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mPlayingModel = App.Models().getPlayingModel();

        mMediaPlayer = new MediaPlayer();

        NotificationUtil.startNotification(this);
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
        debugMethod();
        d("event = " + event);
        switch (event) {
            case START:
                playCurrent(true);
                changeState(State.PLAY);
                break;
            case PLAY:
                playCurrent(false);
                changeState(State.PLAY);
                break;
            case PAUSE:
                pause();
                changeState(State.PAUSE);
                break;
            case NEXT:
                mPlayingModel.next();
                playCurrent(true);
                changeState(State.NEXT);
                break;
            case PREV:
                mPlayingModel.prev();
                playCurrent(true);
                changeState(State.PREV);
                break;
        }
    }

    private void playCurrent(boolean force) {
        Music music = mPlayingModel.getCurrentEntity().getMusic();
        Movie movie = mPlayingModel.getCurrentEntity().getMovie();
        if (music != null) {
            playMusic(music, force);
        } else if (movie != null) {
            playMovie(movie, force);
        }
    }

    private void playMusic(Music music, boolean force) {
        String mediaId = music.getMediaId();
        if (force || !mMediaPlayer.isPlaying()) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(getApplicationContext(), Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaId));
                mMediaPlayer.setLooping(false);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        PlayingStatus playingStatus = new PlayingStatus();
        playingStatus.setType(PlayingStatus.Type.MUSIC);
        playingStatus.setMediaId(mediaId);
        playingStatus.save();

        App.Models().getPlayingModel().setPlaying(true);
    }

    private void playMovie(Movie movie, boolean force) {
        debugMethod();

        Handler handler = new Handler();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                String mediaId = movie.getMediaId();
                if (force || !mediaId.equals(currentMediaId)) {
                    mMediaPlayer.reset();
                    mMediaPlayer = new MediaPlayer();
                    try {
                        mMediaPlayer.setDataSource(getApplicationContext(),
                                Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaId));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (mHolder != null) {
                    mMediaPlayer.setDisplay(mHolder);
                }
                mMediaPlayer.setOnVideoSizeChangedListener((mp, width, height) -> changeSizeState(mp));
                mMediaPlayer.setOnPreparedListener(MusicService::changeSizeState);
                if (force || !mediaId.equals(currentMediaId)) {
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.prepareAsync();
                    mMediaPlayer.setOnPreparedListener(mp -> {
                        PlayingStatus playingStatus = new PlayingStatus();
                        if (playingStatus.getPosition() < mp.getDuration()) {
                            mp.seekTo(playingStatus.getPosition());
                        }
                        mp.start();
                    });
                } else {
                    mMediaPlayer.start();
                    handler.post(() -> changeSizeState(mMediaPlayer));
                }

                currentMediaId = mediaId;

                PlayingStatus playingStatus = new PlayingStatus();
                playingStatus.setType(PlayingStatus.Type.MOVIE);
                playingStatus.setMediaId(mediaId);
                playingStatus.save();

                App.Models().getPlayingModel().setPlaying(true);
                return null;
            }
        }.execute();
    }

    private void pause() {
        mMediaPlayer.pause();
        PlayingStatus playingStatus = new PlayingStatus();
        playingStatus.setPosition(mMediaPlayer.getCurrentPosition());
        playingStatus.save();

        App.Models().getPlayingModel().setPlaying(false);
    }

    private void changeState(State state) {
        App.Bus().post(state);
    }

    private static void changeSizeState(MediaPlayer mp) {
        App.Bus().post(new ChangeSizeState(mp.getVideoWidth(), mp.getVideoHeight()));
    }
}
