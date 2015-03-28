package hm.orz.chaos114.android.carnavimodoki.activity;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hm.orz.chaos114.android.carnavimodoki.R;

public class MoviePlayActivity extends ActionBarActivity {
    public static final String EXTRA_MEDIA_ID = "hm.orz.chaos114.android.carnavimodoki.extra.MEDIA_ID";

    @InjectView(R.id.video)
    VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_play);

        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION  | View.SYSTEM_UI_FLAG_FULLSCREEN);

        ButterKnife.inject(this);

        if (getIntent() != null) {
            String mediaId = getIntent().getStringExtra(EXTRA_MEDIA_ID);
            mVideoView.setVideoURI(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaId));
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.start();
        }
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
