package hm.orz.chaos114.android.carnavimodoki.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import hm.orz.chaos114.android.carnavimodoki.App;
import hm.orz.chaos114.android.carnavimodoki.R;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Movie;
import hm.orz.chaos114.android.carnavimodoki.model.PlayingModel;
import hm.orz.chaos114.android.carnavimodoki.service.MusicService;

import static hm.orz.chaos114.android.carnavimodoki.util.LogUtil.*;

public class MoviePlayActivity extends ActionBarActivity implements SurfaceHolder.Callback {
    private static final String EXTRA_MEDIA_ID = "hm.orz.chaos114.android.carnavimodoki.extra.MEDIA_ID";

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            debugMethod();
            if (!name.getClassName().equals(MusicService.class.getName())) {
                throw new RuntimeException("Illegal service connected. " + name);
            }

            mLocalBinder = (MusicService.LocalBinder) service;
            if (mHolder != null) {
                playMovie();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            debugMethod();
        }
    };

    private MusicService.LocalBinder mLocalBinder;

    @InjectView(R.id.video)
    SurfaceView mSurfaceView;

    private SurfaceHolder mHolder;
    private String mMediaId;

    public static void startActivity(Activity activity, String mediaId) {
        activity.startActivity(getIntent(activity, mediaId));
    }

    public static Intent getIntent(Context context, String mediaId) {
        Intent intent = new Intent(context, MoviePlayActivity.class);
        intent.putExtra(MoviePlayActivity.EXTRA_MEDIA_ID, mediaId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_play);

        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);

        ButterKnife.inject(this);

        if (getIntent() == null) {
            throw new RuntimeException("intent is null.");
        }

        mMediaId = getIntent().getStringExtra(EXTRA_MEDIA_ID);
        if (mMediaId == null) {
            throw new NullPointerException("media id is not null.");
        }

        mSurfaceView.getHolder().addCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(MoviePlayActivity.this, MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        App.Bus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mLocalBinder != null) {
            unbindService(mConnection);
            mLocalBinder = null;
        }

        App.Bus().unregister(this);
    }

    //region ButterKnife
    @OnClick(R.id.video)
    public void onClickVideo() {
        debugMethod();
        if (App.Models().getPlayingModel().isPlaying()) {
            App.Bus().post(MusicService.ControlEvent.PAUSE);
        } else {
            App.Bus().post(MusicService.ControlEvent.PLAY);
        }
    }
    //endregion

    //region otto
    @Subscribe
    public void subscribeChangeSize(MusicService.ChangeSizeState changeSizeState) {
        // 横幅に合わせて、高さを設定
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
        lp.width = screenSize.x;
        lp.height = (int) ((float) changeSizeState.getHeight() / changeSizeState.getWidth() * screenSize.x);
        mSurfaceView.setLayoutParams(lp);
    }
    //endregion

    //region SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        debugMethod();
        mHolder = holder;
        if (mLocalBinder != null) {
            playMovie();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mLocalBinder != null) {
            mLocalBinder.releaseSurfaceHolder();
        }
    }
    //endregion

    private void playMovie() {
        Movie movie = Movie.findByMediaId(mMediaId);
        PlayingModel playingModel = App.Models().getPlayingModel();
        playingModel.reset();
        playingModel.insertMovie(movie);

        if (mLocalBinder != null) {
            mLocalBinder.setSurfaceHolder(mHolder);
        }
        App.Bus().post(MusicService.ControlEvent.PLAY);
    }
}
