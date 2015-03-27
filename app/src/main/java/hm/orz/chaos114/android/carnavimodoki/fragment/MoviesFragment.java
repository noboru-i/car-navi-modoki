package hm.orz.chaos114.android.carnavimodoki.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
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
import hm.orz.chaos114.android.carnavimodoki.db.entity.Movie;
import hm.orz.chaos114.android.carnavimodoki.db.entity.Music;

public class MoviesFragment extends Fragment {

    public interface OnMovieSelectedListener {
        void onMovieSelected(String movie);
    }

    @InjectView(R.id.list)
    ListView mListView;

    private OnMovieSelectedListener mListener;

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.inject(this, view);

        fetchMovies();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnMovieSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnItemClick(R.id.list)
    void onItemClick(int position) {
        String movie = (String) mListView.getItemAtPosition(position);
        mListener.onMovieSelected(movie);
    }

    private void fetchMovies() {

        List<Movie> movies = Movie.all();

        MovieAdapter adapter = new MovieAdapter(getActivity(), 0, movies);
        mListView.setAdapter(adapter);
    }

    static class MovieAdapter extends ArrayAdapter<Movie> {
        private LayoutInflater mInflater;

        public MovieAdapter(Context context, int resource, List<Movie> objects) {
            super(context, resource, objects);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row_movie, parent, false);
            }
            Movie movie = getItem(position);
            TextView nameView = (TextView) convertView.findViewById(R.id.name);
            nameView.setText(movie.getTitle());
            return convertView;
        }
    }
}
