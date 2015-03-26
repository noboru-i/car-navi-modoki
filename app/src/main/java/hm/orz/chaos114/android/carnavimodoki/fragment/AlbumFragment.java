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
import hm.orz.chaos114.android.carnavimodoki.R;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Music;

public class AlbumFragment extends Fragment {
    private static final String ARG_ARTIST = "artist";

    @InjectView(R.id.list)
    ListView mListView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        ButterKnife.inject(this, view);

        if (getArguments() != null) {
            mArtist = getArguments().getString(ARG_ARTIST);
        }

        fetchAlbums();
        return view;
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
