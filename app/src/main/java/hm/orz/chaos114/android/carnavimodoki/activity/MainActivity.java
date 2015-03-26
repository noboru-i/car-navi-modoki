package hm.orz.chaos114.android.carnavimodoki.activity;

import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import hm.orz.chaos114.android.carnavimodoki.R;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Music;
import hm.orz.chaos114.android.carnavimodoki.fragment.AlbumFragment;
import hm.orz.chaos114.android.carnavimodoki.fragment.ArtistFragment;

public class MainActivity extends ActionBarActivity
        implements ArtistFragment.OnArtistSelectedListener {
    private static final String ALBUM_ARTIST = "album_artist";
    private static final String[] COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            ALBUM_ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.TITLE
    };

    //region Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        initContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void initContent() {
        long count = Music.getCount();
        if (count != 0) {
            addArtistFragment();
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ContentResolver cr = getApplicationContext().getContentResolver();
                Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        COLUMNS,
                        MediaStore.Audio.Media.IS_MUSIC + " != 0",
                        null,
                        ALBUM_ARTIST + " ASC, " + MediaStore.Audio.Media.ALBUM_ID + " ASC");
                cursor.moveToFirst();

                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(ALBUM_ARTIST));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    if (artist == null) {
                        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    }
                    Music music = new Music();
                    music.setTitle(title);
                    music.setArtist(artist);
                    music.setAlbum(album);
                    music.save();
                }
                cursor.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                addArtistFragment();
            }
        }.execute();
    }

    private void addArtistFragment() {
        ArtistFragment fragment = new ArtistFragment();
        getFragmentManager().beginTransaction().add(android.R.id.content, fragment, "artist").commit();
    }
}
