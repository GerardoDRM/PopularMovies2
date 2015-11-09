package app.gerardo.popularmovies2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.gerardo.popularmovies2.API.MoviesApi;
import app.gerardo.popularmovies2.data.DatabaseHelperOp;
import app.gerardo.popularmovies2.data.MovieColumns;
import app.gerardo.popularmovies2.data.MovieProvider;
import app.gerardo.popularmovies2.data.ReviewColumns;
import app.gerardo.popularmovies2.model.Movie;
import app.gerardo.popularmovies2.model.MovieResult;
import app.gerardo.popularmovies2.model.Review;
import app.gerardo.popularmovies2.model.ReviewResult;
import app.gerardo.popularmovies2.model.Video;
import app.gerardo.popularmovies2.model.VideoResult;
import app.gerardo.popularmovies2.utils.GeneralConst;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsMovieActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailsMovieActivityFragment.class.getSimpleName();

    private static final String MOVIES_SHARE_HASHTAG = "#Popular Movies App";
    // Fetch and store ShareActionProvider
    private ShareActionProvider mShareActionProvider;
    private String mMovieStr;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView header, mPoster;
    private TextView mMovieTitle, mPopularityText, mVotesText, mDescriptionText, mDateText;
    private Movie mMovie;
    private List<Review> mReviews;
    private List<Video> mVideos;
    private Uri mUri;
    private static final int DETAIL_LOADER = 0;
    private int mMovieId;
    private FloatingActionButton mFloatingButton;

    private AppCompatActivity appCompatActivity;


    private String BASE_URL;
    private String APIKEY;
    private Retrofit retrofit;
    private MoviesApi apiService;

    private static final String[] MOVIE_COLUMNS = {
            MovieColumns._ID,
            MovieColumns.TITLE,
            MovieColumns.DESCRIPTION,
            MovieColumns.POSTER,
            MovieColumns.BACK_POSTER,
            MovieColumns.POPULARITY,
            MovieColumns.VOTE,
            MovieColumns.DATE
    };


    public DetailsMovieActivityFragment() {
        setHasOptionsMenu(true);
        this.BASE_URL = GeneralConst.BASE_URL;
        this.APIKEY = GeneralConst.APIKEY;

        retrofit = new Retrofit.Builder()
                .baseUrl(this.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(MoviesApi.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_details_movie, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Adding title name
        appCompatActivity.getSupportActionBar().setTitle("");
        collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);

        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_label);
        header = (ImageView) rootView.findViewById(R.id.header_img_poster);
        mPoster = (ImageView) rootView.findViewById(R.id.img_poster);
        mPopularityText = (TextView) rootView.findViewById(R.id.popularity_data);
        mVotesText = (TextView) rootView.findViewById(R.id.votes_data);
        mDescriptionText = (TextView) rootView.findViewById(R.id.overview_data);
        mDateText = (TextView) rootView.findViewById(R.id.release_date_data);


        mFloatingButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor movie = getActivity().getContentResolver().query(MovieProvider.Movies.withId(mMovie.getId()),
                        null, null, null, null);
                if(movie.getCount() == 0) {
                    mFloatingButton.setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);
                    DatabaseHelperOp.insertMovie(mMovie, getActivity());
                    if(mReviews != null && mReviews.size() > 0) {
                        DatabaseHelperOp.insertBulkReviews(mReviews, getActivity(), mMovie.getId());
                    }
                    if(mVideos != null && mVideos.size() > 0 ) {
                        DatabaseHelperOp.insertBulkVideos(mVideos, getActivity(), mMovie.getId());
                    }
                }
                else {
                    mFloatingButton.setColorFilter(0xffffffff, PorterDuff.Mode.MULTIPLY);

                }

                movie.close();

            }
        });

        Intent intent = getActivity().getIntent();
        Bundle arguments = getArguments();

        if(intent.getExtras() != null) {
            if(intent.getExtras().containsKey(GeneralConst.MOVIE_KEY)) {
                mMovie = intent.getParcelableExtra(GeneralConst.MOVIE_KEY);
                showMovieInfo();
            }
        }
        else if(arguments != null) {
            if(arguments.containsKey(GeneralConst.MOVIE_KEY)){
                mMovie = arguments.getParcelable(GeneralConst.MOVIE_KEY);
                showMovieInfo();
            }
            else if(arguments.containsKey(GeneralConst.MOVIE_URI)) {
                mUri = arguments.getParcelable(GeneralConst.MOVIE_URI);
            }
        }
        else if(intent.getData() != null) {
            mUri = intent.getData();
        }
        else {
            getFirstMovie();
        }
        return rootView;
    }

    public void checkStoredMovie(Long id) {
        Cursor movie = getActivity().getContentResolver().query(MovieProvider.Movies.withId(id),
                null, null, null, null);
        if(movie.getCount() > 0) {
            mFloatingButton.setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);
        }
    }


    public void showMovieInfo() {
        getGeneralInfo();
        getMoviesTrailers();
        getMovieReviews();
        checkStoredMovie(mMovie.getId());

    }

    public void getFirstMovie() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MoviesApi apiService = retrofit.create(MoviesApi.class);
        Call<MovieResult> call = apiService.getMovies("popularity.desc", APIKEY, null);
        call.enqueue(new Callback<MovieResult>() {

            @Override
            public void onResponse(Response<MovieResult> response, Retrofit retrofit) {
                int statusCode = response.code();
                MovieResult movieResult = response.body();
                mMovie = movieResult.getResults().get(0);
                showMovieInfo();
                checkStoredMovie(mMovie.getId());

            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });
    }

    private void getGeneralInfo() {
        String title = mMovie.getTitle();
        String mImageBack = mMovie.getBackdropPath();
        String mImagePoster = mMovie.getPosterPath();
        String mPopularity = String.valueOf(mMovie.getPopularity());
        String mVotes = String.valueOf(mMovie.getVoteAverage());
        String mOverView = mMovie.getOverview();
        String date = mMovie.getReleaseDate();

        // Adding title
        mMovieTitle.setText(mMovie.getTitle());
        // Adding image with Picasso and changing color to status bar and collapse
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500//" + mImageBack).into(header);
        // Adding image with Picasso and changing color to status bar and collapse
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w342//" + mImagePoster).into(mPoster);
        // Adding popularity
        mPopularityText.setText(mPopularity);
        // Adding votes
        mVotesText.setText(mVotes);
        // Adding Overview
        mDescriptionText.setText(mOverView);
        // Adding release date
        mDateText.setText(date);
    }

    private Intent createShareMoviesIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        // This Flag is deprecated but I still use it because the app
        // has min SDK 15
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mMovieStr + MOVIES_SHARE_HASHTAG);
        return shareIntent;
    }


    public String getMovieReviews() {
        Call<ReviewResult> call = apiService.getReviwes(mMovie.getId().toString(), APIKEY);
        call.enqueue(new Callback<ReviewResult>() {

            @Override
            public void onResponse(Response<ReviewResult> response, Retrofit retrofit) {
                int statusCode = response.code();
                ReviewResult reviewResult = response.body();

                LinearLayout viewGroup = (LinearLayout) getActivity().findViewById(R.id.reviews_layout);

                mReviews = reviewResult.getResults();
                for (final Review r : reviewResult.getResults()) {
                    View review = getActivity().getLayoutInflater().inflate(R.layout.items_review, null);
                    TextView mAuthor = (TextView) review.findViewById(R.id.author_label);
                    mAuthor.setText(r.getAuthor());
                    TextView mContent = (TextView) review.findViewById(R.id.content_label);
                    mContent.setText(r.getContent());
                    viewGroup.addView(review);

                    review.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), r.getContent(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });

        return null;

    }


    public String getMoviesTrailers() {
        Call<VideoResult> call = apiService.getTrailers(mMovie.getId().toString(), APIKEY);
        call.enqueue(new Callback<VideoResult>() {

            @Override
            public void onResponse(Response<VideoResult> response, Retrofit retrofit) {
                int statusCode = response.code();
                VideoResult movieResult = response.body();
                LinearLayout viewGroup = (LinearLayout) getActivity().findViewById(R.id.trailers_layout);

                mVideos = movieResult.getResults();
                for (final Video v : movieResult.getResults()) {
                    View trailer = getActivity().getLayoutInflater().inflate(R.layout.items_video, null);
                    TextView mTrailerName = (TextView) trailer.findViewById(R.id.trailer_name);
                    mTrailerName.setText(v.getName());
                    mTrailerName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getActivity(), v.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });

                    viewGroup.addView(trailer);
                }


            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });

        return null;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details_movie, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (mMovieStr != null) {
            mShareActionProvider.setShareIntent(createShareMoviesIntent());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            Log.d("OncreateLoader", mUri.toString());
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            Log.d("onLoadFinished", data.getString(data.getColumnIndex(MovieColumns.TITLE)));
            mFloatingButton.setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);
            // Adding title
            mMovieTitle.setText(data.getString(data.getColumnIndex(MovieColumns.TITLE)));
            // Adding image with Picasso and changing color to status bar and collapse
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500//" + data.getString(
                    data.getColumnIndex(MovieColumns.BACK_POSTER)
            )).into(header);
            // Adding image with Picasso and changing color to status bar and collapse
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w342//" + data.getString(
                    data.getColumnIndex(MovieColumns.POSTER)
            )).into(mPoster);
            // Adding popularity
            mPopularityText.setText(String.valueOf(data.getDouble(
                    data.getColumnIndex(MovieColumns.POPULARITY))
            ));
            // Adding votes
            mVotesText.setText(String.valueOf(data.getDouble(
                    data.getColumnIndex(MovieColumns.VOTE)
            )));
            // Adding Overview
            mDescriptionText.setText(data.getString(
                    data.getColumnIndex(MovieColumns.DESCRIPTION)
            ));
            // Adding release date
            mDateText.setText(data.getString(
                    data.getColumnIndex(MovieColumns.DATE)
            ));

            Cursor reviews = getActivity().getContentResolver().query(
                    MovieProvider.Reviews.withId(data.getInt(data.getColumnIndex(MovieColumns._ID))),
                    null, null, null, null);
            LinearLayout viewGroup = (LinearLayout) getActivity().findViewById(R.id.reviews_layout);

            if(reviews != null && reviews.moveToFirst()) {
                for(int i=0; i<reviews.getCount(); i++) {
                    View review = getActivity().getLayoutInflater().inflate(R.layout.items_review, null);
                    TextView mAuthor = (TextView) review.findViewById(R.id.author_label);
                    mAuthor.setText(reviews.getString(reviews.getColumnIndex(ReviewColumns.AUTHOR)));
                    TextView mContent = (TextView) review.findViewById(R.id.content_label);
                    mContent.setText(reviews.getString(reviews.getColumnIndex(ReviewColumns.CONTENT)));
                    viewGroup.addView(review);

                    review.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "TEST", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                reviews.close();
            }
            Cursor videos = getActivity().getContentResolver().query(
                    MovieProvider.Videos.withId(data.getInt(data.getColumnIndex(MovieColumns._ID))),
                    null, null, null, null);
            if(videos != null && videos.moveToFirst()) {
                Log.d("Videos", videos.getCount() + "");
                videos.close();
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
