package hm.orz.chaos114.android.carnavimodoki.db.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name = "Musics")
@Data
@EqualsAndHashCode(callSuper = false)
public class Music extends Model {
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
}