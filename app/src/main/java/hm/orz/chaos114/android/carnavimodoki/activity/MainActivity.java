package hm.orz.chaos114.android.carnavimodoki.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.query.Delete;

import butterknife.ButterKnife;
import hm.orz.chaos114.android.carnavimodoki.App;
import hm.orz.chaos114.android.carnavimodoki.R;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Movie;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Music;
import hm.orz.chaos114.android.carnavimodoki.db.entity.PlayListEntity;
import hm.orz.chaos114.android.carnavimodoki.fragment.AlbumFragment;
import hm.orz.chaos114.android.carnavimodoki.fragment.ArtistFragment;
import hm.orz.chaos114.android.carnavimodoki.fragment.MoviesFragment;
import hm.orz.chaos114.android.carnavimodoki.fragment.PlayListFragment;
import hm.orz.chaos114.android.carnavimodoki.pref.entity.PlayingStatus;
import hm.orz.chaos114.android.carnavimodoki.receiver.DeviceAdminReceiver;
import hm.orz.chaos114.android.carnavimodoki.service.MusicService;

public class MainActivity extends ActionBarActivity
        implements ArtistFragment.OnArtistSelectedListener,
        MoviesFragment.OnMovieSelectedListener,
        PlayListFragment.OnPlayListItemSelectedListener {
    private static final int REQUEST_ADMIN = 100;

    private static final String ALBUM_ARTIST = "album_artist";
    private static final String[] MUSIC_COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            ALBUM_ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.TITLE
    };

    private static final String[] VIDEO_COLUMNS = new String[]{
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.ARTIST,
            MediaStore.Video.Media.ALBUM,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.TITLE
    };

    //region Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, MusicService.class));

        ButterKnife.inject(this);

        ComponentName cn = new ComponentName(this, DeviceAdminReceiver.class);
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (!dpm.isAdminActive(cn)) {
            new AlertDialog.Builder(this).setMessage("管理者権限が必要です。")
                    .setPositiveButton("OK", (dialog, which) -> {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                "バッテリー駆動になった時に、画面を消すために必要です。");
                        startActivityForResult(intent, REQUEST_ADMIN);
                    }).setCancelable(false).show();
        }

        scan(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_reload:
                App.Models().getPlayingModel().reset();
                new PlayingStatus().clear();
                scan(true);
                return true;
            case R.id.action_play_list:
                addPlayListFragment();
                return true;
            case R.id.action_music:
                addArtistFragment();
                return true;
            case R.id.action_movie:
                addMoviesFragment();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADMIN:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "認証成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "認証失敗", Toast.LENGTH_LONG).show();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //endregion

    //region ArtistFragment.OnArtistSelectedListener
    @Override
    public void onArtistSelected(String artist) {
        AlbumFragment fragment = AlbumFragment.newInstance(artist);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, fragment, "album");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
    //endregion

    //region MoviesFragment.OnMovieSelectedListener
    @Override
    public void onMovieSelected(Movie movie) {
        MoviePlayActivity.startActivity(this, movie.getMediaId());
    }
    //endregion

    //region PlayListFragment.OnPlayListItemSelectedListener
    @Override
    public void onPlayListItemSelected(PlayListEntity entity) {
        // TODO 現状不要
    }
    //endregion

    private void scan(final boolean force) {
        MediaScannerConnection.scanFile(getApplicationContext(),
                new String[]{Environment.getExternalStorageDirectory().toString()},
                null,
                (path, uri) -> {
                    runOnUiThread(() -> initContent(force));
                });
    }

    private void initContent(boolean force) {
        long count = Music.getCount();
        if (!force && count != 0) {
            // データ更新が必要無い場合
            initFragments();
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                initMusic();
                initMovie();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                initFragments();
            }

            private void initMusic() {
                new Delete().from(Music.class).execute();
                ContentResolver cr = getApplicationContext().getContentResolver();
                Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        MUSIC_COLUMNS,
                        MediaStore.Audio.Media.IS_MUSIC + " != 0",
                        null,
                        null);

                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(ALBUM_ARTIST));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    if (artist == null) {
                        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    }
                    Music music = new Music();
                    music.setMediaId(id);
                    music.setTitle(title);
                    music.setArtist(artist);
                    music.setAlbum(album);
                    music.save();
                }
                cursor.close();
            }

            private void initMovie() {
                new Delete().from(Movie.class).execute();
                ContentResolver cr = getApplicationContext().getContentResolver();
                Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        VIDEO_COLUMNS,
                        null,
                        null,
                        null);

                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    Movie movie = new Movie();
                    movie.setMediaId(id);
                    movie.setTitle(title);
                    movie.save();
                }
                cursor.close();
            }
        }.execute();
    }

    private void initFragments() {
        PlayingStatus playingStatus = new PlayingStatus();
        if (playingStatus.getType() == PlayingStatus.Type.MOVIE) {
            addMoviesFragment();
            if (playingStatus.getMediaId() != null) {
                MoviePlayActivity.startActivity(this, playingStatus.getMediaId());
            }
            return;
        } else if (playingStatus.getType() == PlayingStatus.Type.MUSIC) {
            addArtistFragment();
            addPlayListFragment();

            App.Bus().post(MusicService.ControlEvent.PLAY);
            return;
        }

        // TODO ダッシュボードっぽい何かを表示する？
        addArtistFragment();
    }

    private void addArtistFragment() {
        Fragment fragment = getFragmentManager().findFragmentByTag("artist");
        if (fragment == null) {
            fragment = new ArtistFragment();
        } else {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        getFragmentManager().beginTransaction().add(android.R.id.content, fragment, "artist").commit();
    }

    private void addMoviesFragment() {
        Fragment fragment = getFragmentManager().findFragmentByTag("movies");
        if (fragment == null) {
            fragment = new MoviesFragment();
        } else {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        getFragmentManager().beginTransaction().add(android.R.id.content, fragment, "movies").commit();
    }

    private void addPlayListFragment() {
        Fragment fragment = getFragmentManager().findFragmentByTag("play_list");
        if (fragment == null) {
            fragment = new PlayListFragment();
        } else {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, fragment, "play_list");
        ft.addToBackStack(null);
        ft.commit();
    }
}
