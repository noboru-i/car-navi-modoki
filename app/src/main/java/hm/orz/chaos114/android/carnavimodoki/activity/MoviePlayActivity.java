package hm.orz.chaos114.android.carnavimodoki.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hm.orz.chaos114.android.carnavimodoki.R;
import hm.orz.chaos114.android.carnavimodoki.pref.entity.PlayingStatus;

public class MoviePlayActivity extends ActionBarActivity {
    public static final String EXTRA_MEDIA_ID = "hm.orz.chaos114.android.carnavimodoki.extra.MEDIA_ID";

    @InjectView(R.id.video)
    VideoView mVideoView;

    public static void startActivity(Activity activity, String mediaId) {
        Intent intent = new Intent(activity, MoviePlayActivity.class);
        intent.putExtra(MoviePlayActivity.EXTRA_MEDIA_ID, mediaId);
        activity.startActivity(intent);
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

        final String mediaId = getIntent().getStringExtra(EXTRA_MEDIA_ID);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mVideoView.setVideoURI(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaId));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mVideoView.setMediaController(new MediaController(MoviePlayActivity.this));
                mVideoView.start();
            }
        }.execute();

        PlayingStatus playingStatus = new PlayingStatus();
        playingStatus.setType(PlayingStatus.Type.MOVIE);
        playingStatus.setMediaId(mediaId);
        playingStatus.save();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
