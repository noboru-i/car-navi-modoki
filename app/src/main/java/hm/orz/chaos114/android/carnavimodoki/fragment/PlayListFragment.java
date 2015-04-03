package hm.orz.chaos114.android.carnavimodoki.fragment;

import android.app.Activity;
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
import butterknife.OnItemClick;
import hm.orz.chaos114.android.carnavimodoki.App;
import hm.orz.chaos114.android.carnavimodoki.R;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Movie;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Music;
import hm.orz.chaos114.android.carnavimodoki.db.entity.PlayListEntity;
import hm.orz.chaos114.android.carnavimodoki.model.PlayingModel;
import hm.orz.chaos114.android.carnavimodoki.service.MusicService;

public class PlayListFragment extends Fragment {

    @InjectView(R.id.list)
    ListView mListView;
    @InjectView(R.id.start_stop_button)
    Button mStartStopButton;

    private OnPlayListItemSelectedListener mListener;
    private PlayingModel mPlayingModel;

    public PlayListFragment() {
    }

    //region Fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);

        ButterKnife.inject(this, view);

        mPlayingModel = App.Models().getPlayingModel();

        setStartStopButtonText();
        fetchPlayList();
        return view;
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

    //region ButterKnife
    @OnItemClick(R.id.list)
    void onItemClick(int position) {
        PlayListEntity entity = (PlayListEntity) mListView.getItemAtPosition(position);
        mPlayingModel.setCurrentTrackNumber(entity.getNumber());
        mListener.onPlayListItemSelected(entity);

        App.Bus().post(MusicService.ControlEvent.PLAY);
    }

    @OnClick(R.id.start_stop_button)
    void onClickStartStop() {
        if (App.Models().getPlayingModel().isPlaying()) {
            App.Bus().post(MusicService.ControlEvent.PAUSE);
            return;
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
    //endregion

    //region otto
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
    //endregion

    private void fetchPlayList() {
        PlayListAdapter adapter = new PlayListAdapter(getActivity(), 0, mPlayingModel.getPlayList());
        mListView.setAdapter(adapter);
    }

    private void setStartStopButtonText() {
        if (App.Models().getPlayingModel().isPlaying()) {
            mStartStopButton.setText("停止");
        } else {
            mStartStopButton.setText("再生");
        }
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
            Music music = entity.getMusic();
            Movie movie = entity.getMovie();
            TextView nameView = (TextView) convertView.findViewById(R.id.name);
            if (music != null) {
                nameView.setText(music.getTitle());
            }
            if (movie != null) {
                nameView.setText(movie.getTitle());
            }
            return convertView;
        }
    }

    public interface OnPlayListItemSelectedListener {
        void onPlayListItemSelected(PlayListEntity entity);
    }
}
