package hm.orz.chaos114.android.carnavimodoki.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import hm.orz.chaos114.android.carnavimodoki.R;

public class DashboardFragment extends Fragment {

    @InjectView(R.id.music_button)
    Button musicButton;
    @InjectView(R.id.movie_button)
    Button movieButton;
    @InjectView(R.id.playlist_button)
    Button playlistButton;

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @OnClick(R.id.music_button)
    void onClickMusic() {
        Fragment fragment = getFragmentManager().findFragmentByTag("artist");
        if (fragment == null) {
            fragment = new ArtistFragment();
        } else {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        getFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, fragment, "artist")
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.movie_button)
    void onClickMovie() {
        Fragment fragment = getFragmentManager().findFragmentByTag("movies");
        if (fragment == null) {
            fragment = new MoviesFragment();
        } else {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        getFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, fragment, "movies")
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.playlist_button)
    void onClickPlaylist() {
        Fragment fragment = getFragmentManager().findFragmentByTag("play_list");
        if (fragment == null) {
            fragment = new PlayListFragment();
        } else {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        getFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, fragment, "play_list")
                .addToBackStack(null)
                .commit();
    }
}
