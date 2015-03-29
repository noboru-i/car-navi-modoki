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
import hm.orz.chaos114.android.carnavimodoki.App;
import hm.orz.chaos114.android.carnavimodoki.R;
import hm.orz.chaos114.android.carnavimodoki.db.entity.PlayListEntity;
import hm.orz.chaos114.android.carnavimodoki.model.PlayingModel;
import hm.orz.chaos114.android.carnavimodoki.service.MusicService;

public class PlayListFragment extends Fragment {

    @InjectView(R.id.list)
    ListView mListView;

    private OnPlayListItemSelectedListener mListener;
    private PlayingModel mPlayingModel;

    public PlayListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);

        ButterKnife.inject(this, view);

        mPlayingModel = App.Models().getPlayingModel();

        fetchPlayList();
        return view;
    }

    @OnItemClick(R.id.list)
    void onItemClick(int position) {
        PlayListEntity entity = (PlayListEntity) mListView.getItemAtPosition(position);
        mPlayingModel.setCurrentTrackNumber(entity.getNumber());
        mListener.onPlayListItemSelected(entity);

        App.Bus().post(MusicService.ControlEvent.PLAY);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPlayListItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPlayListItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void fetchPlayList() {
        PlayListAdapter adapter = new PlayListAdapter(getActivity(), 0, mPlayingModel.getPlayList());
        mListView.setAdapter(adapter);
    }

    static class PlayListAdapter extends ArrayAdapter<PlayListEntity> {
        private LayoutInflater mInflater;

        public PlayListAdapter(Context context, int resource, List<PlayListEntity> objects) {
            super(context, resource, objects);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row_artist, parent, false);
            }
            PlayListEntity entity = getItem(position);
            TextView nameView = (TextView) convertView.findViewById(R.id.name);
            nameView.setText(entity.getMusic().getTitle());
            return convertView;
        }
    }

    public interface OnPlayListItemSelectedListener {
        void onPlayListItemSelected(PlayListEntity entity);
    }
}
