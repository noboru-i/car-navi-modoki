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
        PlayListEntity entity = PlayListEntity.findByNumber(currentTrackNumber);
        if (entity == null) {
            currentTrackNumber = 1;
            entity = PlayListEntity.findByNumber(currentTrackNumber);
        }

        return entity;
    }

    public void reset() {
        PlayListEntity.reset();
    }

    public void next() {
        currentTrackNumber++;
    }

    public void prev() {
        currentTrackNumber--;
    }
}
