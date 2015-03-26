package hm.orz.chaos114.android.carnavimodoki.db.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Music extends Model {
    @Column(name = "media_id")
    private String mediaId;
    @Column(name = "artist")
    private String artist;
    @Column(name = "album")
    private String album;
    @Column(name = "title")
    private String title;

    public static long getCount() {
        return new Select().from(Music.class).count();
    }

    public static List<Music> all() {
        return new Select().from(Music.class).execute();
    }

    public static List<String> fetchArtists() {
        List<Music> musics = new Select()
                .from(Music.class)
                .groupBy("artist")
                .execute();
        List<String> artists = new ArrayList<>();
        for (Music music : musics) {
            artists.add(music.getArtist());
        }
        return artists;
    }

    public static List<String> fetchAlbums(String artist) {
        List<Music> musics = new Select()
                .from(Music.class)
                .where("artist = ?", artist)
                .groupBy("album")
                .execute();
        List<String> albums = new ArrayList<>();
        for (Music music : musics) {
            albums.add(music.getAlbum());
        }
        return albums;
    }

    public static List<Music> fetchByAlbum(String album) {
        return new Select()
                .from(Music.class)
                .where("album = ?", album)
                .execute();
    }
}