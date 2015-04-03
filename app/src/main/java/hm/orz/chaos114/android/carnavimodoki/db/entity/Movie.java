package hm.orz.chaos114.android.carnavimodoki.db.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Movie extends Model {
    @Column(name = "media_id", unique = true)
    private String mediaId;
    @Column(name = "title")
    private String title;

    public static List<Movie> all() {
        return new Select().from(Movie.class).execute();
    }

    public static Movie findByMediaId(String mediaId) {
        return new Select()
                .from(Movie.class)
                .where("media_id = ?", mediaId)
                .executeSingle();
    }
}