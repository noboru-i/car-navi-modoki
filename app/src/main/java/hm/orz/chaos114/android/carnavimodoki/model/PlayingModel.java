package hm.orz.chaos114.android.carnavimodoki.model;

import java.util.List;

import hm.orz.chaos114.android.carnavimodoki.db.entity.Movie;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Music;
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

    public void insertAlbum(String album) {
        List<Music> musics = Music.fetchByAlbum(album);
        for (Music music : musics) {
            PlayListEntity entity = new PlayListEntity();
            entity.setMusic(music);
            entity.saveNext();
        }
    }

    public void insertMovie(Movie movie) {
        PlayListEntity entity = new PlayListEntity();
        entity.setMovie(movie);
        entity.saveNext();
    }
}
