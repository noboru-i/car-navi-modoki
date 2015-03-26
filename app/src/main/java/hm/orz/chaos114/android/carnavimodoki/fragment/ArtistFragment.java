package hm.orz.chaos114.android.carnavimodoki.fragment;

import android.app.Activity;
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
import butterknife.OnItemClick;
import hm.orz.chaos114.android.carnavimodoki.R;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Music;

public class ArtistFragment extends Fragment {

    public interface OnArtistSelectedListener {
        void onArtistSelected(String artist);
    }

    @InjectView(R.id.list)
    ListView mListView;

    private OnArtistSelectedListener mListener;

    public ArtistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        ButterKnife.inject(this, view);

        fetchArtists();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnArtistSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArtistSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnItemClick(R.id.list)
    void onItemClick(int position) {
        String artist = (String) mListView.getItemAtPosition(position);
        mListener.onArtistSelected(artist);
    }

    private void fetchArtists() {

        List<String> artists = Music.fetchArtists();

        ArtistAdapter adapter = new ArtistAdapter(getActivity(), 0, artists);
        mListView.setAdapter(adapter);
    }

    static class ArtistAdapter extends ArrayAdapter<String> {
        private LayoutInflater mInflater;

        public ArtistAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row_artist, parent, false);
            }
            String artist = getItem(position);
            TextView nameView = (TextView) convertView.findViewById(R.id.name);
            nameView.setText(artist);
            return convertView;
        }
    }
}
