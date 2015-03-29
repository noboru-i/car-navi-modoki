package hm.orz.chaos114.android.carnavimodoki.model;

import java.util.List;

import hm.orz.chaos114.android.carnavimodoki.db.entity.PlayListEntity;
import lombok.Data;

@Data
public class PlayingModel {
    private boolean playing;
    private int currentTrackNumber;

    public List<PlayListEntity> getPlayList() {
        return PlayListEntity.all();
    }

    public PlayListEntity getCurrentEntity() {
        return PlayListEntity.findByNumber(currentTrackNumber);
    }

    public void next() {
        currentTrackNumber++;
    }

    public void prev() {
        currentTrackNumber--;
    }
}
