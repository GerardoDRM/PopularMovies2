package app.gerardo.popularmovies2;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import app.gerardo.popularmovies2.API.MoviesApi;
import app.gerardo.popularmovies2.adapter.MovieCursorAdapter;
import app.gerardo.popularmovies2.adapter.MoviesAdapter;
import app.gerardo.popularmovies2.data.MovieProvider;
import app.gerardo.popularmovies2.model.Movie;
import app.gerardo.popularmovies2.model.MovieResult;
import app.gerardo.popularmovies2.utils.GeneralConst;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private String BASE_URL;
    private String APIKEY;
    private Retrofit retrofit;
    private GridView mGrid;
    private RecyclerView recyclerView;
    private MoviesApi apiService;
    private MoviesAdapter adapter;

    private static final int CURSOR_LOADER_ID = 0;
    private MovieCursorAdapter mCursorAdapter;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface CallbackMovie {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Movie movie);
        public void onFavoriteSelected(Uri uri);
    }


    public MainFragment() {
        this.BASE_URL = GeneralConst.BASE_URL;
        this.APIKEY = GeneralConst.APIKEY;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(MoviesApi.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);

        // Get reference of recycler view
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {
        mCursorAdapter = new MovieCursorAdapter(getActivity(), null);
        adapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
        recyclerView.setAdapter(adapter);
        getMoviesSortBy("popularity.desc", null);

    }

    public void getFavorites() {
        recyclerView.setAdapter(mCursorAdapter);
    }

    public void getMoviesSortBy(String sort, Integer count) {
        Call<MovieResult> call = apiService.getMovies(sort, APIKEY, count);
        call.enqueue(new Callback<MovieResult>() {

            @Override
            public void onResponse(Response<MovieResult> response, Retrofit retrofit) {
                int statusCode = response.code();
                MovieResult movieResult = response.body();
                adapter.refill(movieResult.getResults());
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MovieProvider.Movies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Adding action to menu items
        switch (item.getItemId()) {
            case R.id.popularity_radio:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    getMoviesSortBy("popularity.desc", null);
                    recyclerView.setAdapter(adapter);

                }
            case R.id.votes_radio:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    getMoviesSortBy("vote_average.desc", 1000);
                    recyclerView.setAdapter(adapter);

                }
            case R.id.favorites:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    getFavorites();

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
