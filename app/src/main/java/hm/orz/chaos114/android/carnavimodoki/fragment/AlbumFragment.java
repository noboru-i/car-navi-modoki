package hm.orz.chaos114.android.carnavimodoki.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import hm.orz.chaos114.android.carnavimodoki.App;
import hm.orz.chaos114.android.carnavimodoki.R;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Music;
import hm.orz.chaos114.android.carnavimodoki.model.PlayingModel;
import hm.orz.chaos114.android.carnavimodoki.service.MusicService;

public class AlbumFragment extends Fragment {
    private static final String ARG_ARTIST = "artist";

    @InjectView(R.id.list)
    ListView mListView;
    @InjectView(R.id.artist_name)
    TextView mArtistNameView;

    private PlayingModel mPlayingModel;
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

        mPlayingModel = App.Models().getPlayingModel();

        fetchAlbums();
        mArtistNameView.setText(mArtist);

        return view;
    }
    //endregion

    @OnClick(R.id.play_all)
    public void onClickPlayAll() {
        mPlayingModel.reset();
        for (int i = 0; i < mListView.getCount(); i++) {
            String album = (String) mListView.getItemAtPosition(i);
            mPlayingModel.insertAlbum(album);
        }

        App.Bus().post(MusicService.ControlEvent.PLAY);
    }

    private void fetchAlbums() {

        List<String> albums = Music.fetchAlbums(mArtist);

        AlbumAdapter adapter = new AlbumAdapter(getActivity(), 0, albums);
        mListView.setAdapter(adapter);
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
