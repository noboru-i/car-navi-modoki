package hm.orz.chaos114.android.carnavimodoki.db.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import static hm.orz.chaos114.android.carnavimodoki.util.LogUtil.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class PlayListEntity extends Model {
    @Column(name = "number")
    private int number;
    @Column(name = "music")
    private Music music;
    @Column(name = "movie")
    private Movie movie;

    public static List<PlayListEntity> all() {
        return new Select()
                .from(PlayListEntity.class)
                .orderBy("number ASC")
                .execute();
    }

    public static PlayListEntity findByNumber(int number) {
        return new Select()
                .from(PlayListEntity.class)
                .where("number = ?", number)
                .executeSingle();
    }

    public static void reset() {
        new Delete().from(PlayListEntity.class).execute();
    }

    public void saveNext() {
        PlayListEntity last = new Select().from(PlayListEntity.class).orderBy("number DESC").executeSingle();
        if (last == null) {
            number = 1;
        } else {
            number = last.getNumber() + 1;
        }
        d("next: " + this);

        save();
    }
}
