package hm.orz.chaos114.android.carnavimodoki;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Music;

public class MainActivity extends ActionBarActivity {
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

    @InjectView(R.id.text)
    TextView mTextView;

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

    private void initContent() {
        long count = Music.getCount();
        List<Music> musics;
        if (count == 0) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    COLUMNS,
                    null,
                    null,
                    ALBUM_ARTIST + " ASC, " + MediaStore.Audio.Media.ALBUM_ID + " ASC");
            cursor.moveToFirst();

            musics = new ArrayList<>();
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
                musics.add(music);
            }
            cursor.close();
        } else {
            musics = Music.all();
        }

        for (Music music : musics) {
            mTextView.append(music.getArtist() + "/" + music.getAlbum() + "/" + music.getTitle() + "\n");
        }
    }
}
