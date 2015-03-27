package hm.orz.chaos114.android.carnavimodoki.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import hm.orz.chaos114.android.carnavimodoki.App;
import hm.orz.chaos114.android.carnavimodoki.R;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Music;
import hm.orz.chaos114.android.carnavimodoki.db.entity.PlayListEntity;
import hm.orz.chaos114.android.carnavimodoki.service.MusicService;

public class AlbumFragment extends Fragment {
    private static final String ARG_ARTIST = "artist";

    @InjectView(R.id.list)
    ListView mListView;
    @InjectView(R.id.start_stop_button)
    Button mStartStopButton;

    private String mArtist;

    public static AlbumFragment newInstance(String artist) {
        final AlbumFragment f = new AlbumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTIST, artist);
        f.setArguments(args);

        return f;
    }

    public AlbumFragment() {
    }

    //region Fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        ButterKnife.inject(this, view);

        if (getArguments() != null) {
            mArtist = getArguments().getString(ARG_ARTIST);
        }

        setStartStopButtonText();

        fetchAlbums();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        App.Bus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        App.Bus().unregister(this);
    }
    //endregion

    @OnClick(R.id.start_stop_button)
    void onClickStartStop() {
        if (App.Models().getPlayingModel().isPlaying()) {
            App.Bus().post(MusicService.ControlEvent.PAUSE);
            return;
        }
        PlayListEntity.reset();
        for (int i = 0; i < mListView.getCount(); i++) {
            String album = (String) mListView.getItemAtPosition(i);
            List<Music> musics = Music.fetchByAlbum(album);
            for (Music music : musics) {
                PlayListEntity entity = new PlayListEntity();
                entity.setMusic(music);
                entity.saveNext();
            }
        }

        App.Bus().post(MusicService.ControlEvent.START);
    }

    @OnClick(R.id.prev_button)
    void onClickPrev() {
        App.Bus().post(MusicService.ControlEvent.PREV);
    }

    @OnClick(R.id.next_button)
    void onClickNext() {
        App.Bus().post(MusicService.ControlEvent.NEXT);
    }

    @Subscribe
    public void subscribeMusicState(MusicService.State state) {
        switch (state) {
            case PLAY:
                setStartStopButtonText();
                break;
            case PAUSE:
                setStartStopButtonText();
                break;
        }
    }

    private void fetchAlbums() {

        List<String> albums = Music.fetchAlbums(mArtist);

        AlbumAdapter adapter = new AlbumAdapter(getActivity(), 0, albums);
        mListView.setAdapter(adapter);
    }

    private void setStartStopButtonText() {
        if (App.Models().getPlayingModel().isPlaying()) {
            mStartStopButton.setText("停止");
        } else {
            mStartStopButton.setText("再生");
        }
    }

    static class AlbumAdapter extends ArrayAdapter<String> {
        private LayoutInflater mInflater;

        public AlbumAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row_album, parent, false);
            }
            String album = getItem(position);
            TextView nameView = (TextView) convertView.findViewById(R.id.name);
            nameView.setText(album);
            return convertView;
        }
    }
}
