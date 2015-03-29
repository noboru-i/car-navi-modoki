package hm.orz.chaos114.android.carnavimodoki.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
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

import butterknife.ButterKnife;
import hm.orz.chaos114.android.carnavimodoki.R;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Movie;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Music;
import hm.orz.chaos114.android.carnavimodoki.db.entity.PlayListEntity;
import hm.orz.chaos114.android.carnavimodoki.fragment.AlbumFragment;
import hm.orz.chaos114.android.carnavimodoki.fragment.ArtistFragment;
import hm.orz.chaos114.android.carnavimodoki.fragment.MoviesFragment;
import hm.orz.chaos114.android.carnavimodoki.fragment.PlayListFragment;
import hm.orz.chaos114.android.carnavimodoki.service.MusicService;

public class MainActivity extends ActionBarActivity
        implements ArtistFragment.OnArtistSelectedListener,
        MoviesFragment.OnMovieSelectedListener,
        PlayListFragment.OnPlayListItemSelectedListener {
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

        scan();
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
            case R.id.action_play_list:
                Fragment fragment = new PlayListFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(android.R.id.content, fragment, "play_list");
                ft.addToBackStack(null);
                ft.commit();
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
        Intent intent = new Intent(this, MoviePlayActivity.class);
        intent.putExtra(MoviePlayActivity.EXTRA_MEDIA_ID, movie.getMediaId());
        startActivity(intent);
    }
    //endregion

    //region PlayListFragment.OnPlayListItemSelectedListener
    @Override
    public void onPlayListItemSelected(PlayListEntity entity) {
        // TODO 現状不要
    }
    //endregion

    private void scan() {
        MediaScannerConnection.scanFile(getApplicationContext(),
                new String[]{Environment.getExternalStorageDirectory().toString()},
                null,
                (path, uri) -> {
                    initContent();
                });
    }

    private void initContent() {
        long count = Music.getCount();
        if (count != 0) {
            addArtistFragment();
//            addMoviesFragment();
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
                addArtistFragment();
//                addMoviesFragment();
            }

            private void initMusic() {
                ContentResolver cr = getApplicationContext().getContentResolver();
                Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        MUSIC_COLUMNS,
                        MediaStore.Audio.Media.IS_MUSIC + " != 0",
                        null,
                        null);
                cursor.moveToFirst();

                do {
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
                } while (cursor.moveToNext());
                cursor.close();
            }

            private void initMovie() {
                ContentResolver cr = getApplicationContext().getContentResolver();
                Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        VIDEO_COLUMNS,
                        null,
                        null,
                        null);
                cursor.moveToFirst();

                do {
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    Movie movie = new Movie();
                    movie.setMediaId(id);
                    movie.setTitle(title);
                    movie.save();
                } while (cursor.moveToNext());
                cursor.close();
            }
        }.execute();
    }

    private void addArtistFragment() {
        ArtistFragment fragment = new ArtistFragment();
        getFragmentManager().beginTransaction().add(android.R.id.content, fragment, "artist").commit();
    }

    private void addMoviesFragment() {
        MoviesFragment fragment = new MoviesFragment();
        getFragmentManager().beginTransaction().add(android.R.id.content, fragment, "movies").commit();
    }
}
