package app.gerardo.popularmovies2;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import app.gerardo.popularmovies2.API.MoviesApi;
import app.gerardo.popularmovies2.adapter.MoviesAdapter;
import app.gerardo.popularmovies2.data.MovieColumns;
import app.gerardo.popularmovies2.data.MovieProvider;
import app.gerardo.popularmovies2.model.Movie;
import app.gerardo.popularmovies2.model.MovieResult;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    final String APIKEY = "c77c154b48e61f06b2858716425efb7d";
    Retrofit retrofit;
    private GridView mGrid;
    RecyclerView recyclerView;
    MoviesApi apiService;
    MoviesAdapter adapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);

        // Get reference of gridview
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        setupRecyclerView(recyclerView);
        return rootView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        adapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
        recyclerView.setAdapter(adapter);
        getMoviesSortBy("popularity.desc", null);

    }

    public void getFavorites() {
        Cursor c = getActivity().getContentResolver().query(MovieProvider.Movies.CONTENT_URI,
                null, null, null, null);
        if (c.getCount() > 0) {
            Log.d(LOG_TAG, c.getCount() + "");
        } else {
            insertTest();
        }
    }

    private void insertTest() {
        Log.d(LOG_TAG, "insert");
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                MovieProvider.Movies.CONTENT_URI);
        builder.withValue(MovieColumns._ID, 111111);
        builder.withValue(MovieColumns.TITLE, "TEST");
        builder.withValue(MovieColumns.DESCRIPTION, "Test");
        builder.withValue(MovieColumns.BACK_POSTER, "Test");
        builder.withValue(MovieColumns.POSTER, "Test");
        builder.withValue(MovieColumns.POPULARITY, 22.22);
        builder.withValue(MovieColumns.VOTE, 2.3);
        builder.withValue(MovieColumns.DATE, "2015/12/13");

        batchOperations.add(builder.build());


        try {
            getActivity().getContentResolver().applyBatch(MovieProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }

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

                }
            case R.id.votes_radio:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    getMoviesSortBy("vote_average.desc", 1000);

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
