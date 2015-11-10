package app.gerardo.popularmovies2;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
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

import com.squareup.picasso.Picasso;

import java.util.List;

import app.gerardo.popularmovies2.API.MoviesApi;
import app.gerardo.popularmovies2.data.DatabaseHelperOp;
import app.gerardo.popularmovies2.data.MovieColumns;
import app.gerardo.popularmovies2.data.MovieProvider;
import app.gerardo.popularmovies2.data.ReviewColumns;
import app.gerardo.popularmovies2.data.VideoColumns;
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

    private static final String YOUTUBE = "http://www.youtube.com/watch?v=";
    // Fetch and store ShareActionProvider
    private ShareActionProvider mShareActionProvider;
    private String mYoutubeLink;
    private ImageView header, mPoster;
    private TextView mMovieTitle, mPopularityText, mVotesText, mDescriptionText, mDateText;
    private Movie mMovie;
    private List<Review> mReviews;
    private List<Video> mVideos;
    private Uri mUri;
    private static final int DETAIL_LOADER = 0;
    private long mMovieId;
    private FloatingActionButton mFloatingButton;
    LinearLayout reviewsLayout;
    LinearLayout videoLayout;


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

    // Retrofit initialization
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
        View rootView = inflater.inflate(R.layout.fragment_details_movie, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        if(!GeneralConst.mTwoPane) appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Clean Title
        appCompatActivity.getSupportActionBar().setTitle("");

        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_label);
        header = (ImageView) rootView.findViewById(R.id.header_img_poster);
        mPoster = (ImageView) rootView.findViewById(R.id.img_poster);
        mPopularityText = (TextView) rootView.findViewById(R.id.popularity_data);
        mVotesText = (TextView) rootView.findViewById(R.id.votes_data);
        mDescriptionText = (TextView) rootView.findViewById(R.id.overview_data);
        mDateText = (TextView) rootView.findViewById(R.id.release_date_data);


        mFloatingButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        // Favorite functionality
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if movie is stored on our database
                Cursor movie = getActivity().getContentResolver().query(MovieProvider.Movies.withId(mMovieId),
                        null, null, null, null);
                // If there isn't data then it means that this movie can be
                // stored
                if (movie.getCount() == 0) {
                    // Changing color to active
                    mFloatingButton.setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);
                    // Insert data using content provider
                    DatabaseHelperOp.insertMovie(mMovie, getActivity());
                    if (mReviews != null && mReviews.size() > 0) {
                        DatabaseHelperOp.insertBulkReviews(mReviews, getActivity(), mMovieId);
                    }
                    if (mVideos != null && mVideos.size() > 0) {
                        DatabaseHelperOp.insertBulkVideos(mVideos, getActivity(),mMovieId);
                    }
                }
                // Else we delete the movie
                else {
                    // Changing to clean
                    mFloatingButton.setColorFilter(0xffffffff, PorterDuff.Mode.MULTIPLY);
                    // Delete Data using content provider
                    DatabaseHelperOp.deleteBulkTrailers(mMovieId, getActivity());
                    DatabaseHelperOp.deleteBulkReviews(mMovieId, getActivity());
                    DatabaseHelperOp.deleteMovie(mMovieId, getActivity());

                }

                movie.close();

            }
        });

        Intent intent = getActivity().getIntent();
        Bundle arguments = getArguments();
        // Get diferent parameters
        if (intent.getExtras() != null) {
            if (intent.getExtras().containsKey(GeneralConst.MOVIE_KEY)) {
                mMovie = intent.getParcelableExtra(GeneralConst.MOVIE_KEY);
                showMovieInfo();
            }
        } else if (arguments != null) {
            if (arguments.containsKey(GeneralConst.MOVIE_KEY)) {
                mMovie = arguments.getParcelable(GeneralConst.MOVIE_KEY);
                showMovieInfo();
            } else if (arguments.containsKey(GeneralConst.MOVIE_URI)) {
                mUri = arguments.getParcelable(GeneralConst.MOVIE_URI);
            }
        } else if (intent.getData() != null) {
            mUri = intent.getData();
        } else {
            getFirstMovie();
        }
        return rootView;
    }

    // Check if movie is already stored
    public void checkStoredMovie(Long id) {
        Cursor movie = getActivity().getContentResolver().query(MovieProvider.Movies.withId(id),
                null, null, null, null);
        if (movie.getCount() > 0) {
            mFloatingButton.setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);
        }
    }


    public void showMovieInfo() {
        mMovieId = mMovie.getId();
        getGeneralInfo();
        getMoviesTrailers();
        getMovieReviews();
        checkStoredMovie(mMovie.getId());

    }
    // At the begining if tablet is activate
    // then load first movie
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
    // Get Movie Info
    private void getGeneralInfo() {
        String title = mMovie.getTitle();
        String back = mMovie.getBackdropPath();
        String poster = mMovie.getPosterPath();
        double popularity = mMovie.getPopularity();
        double vote = mMovie.getVoteAverage();
        String overview = mMovie.getOverview();
        String date = mMovie.getReleaseDate();

        insertBasicData(title, poster, back, popularity, vote, overview, date);
    }

    private Intent createShareMoviesIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        // This Flag is deprecated but I still use it because the app
        // has min SDK 15
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                YOUTUBE + mYoutubeLink);
        return shareIntent;
    }

    // Get Reviews
    public String getMovieReviews() {
        Call<ReviewResult> call = apiService.getReviwes(mMovie.getId().toString(), APIKEY);
        call.enqueue(new Callback<ReviewResult>() {

            @Override
            public void onResponse(Response<ReviewResult> response, Retrofit retrofit) {
                int statusCode = response.code();
                ReviewResult reviewResult = response.body();
                reviewsLayout = (LinearLayout) getActivity().findViewById(R.id.reviews_layout);
                mReviews = reviewResult.getResults();
                //reviewsLayout.removeAllViews();
                for (final Review r : mReviews) {
                    insertMovieReviews(r.getAuthor(), r.getContent());
                }

            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });

        return null;

    }

    // Get trailers
    public String getMoviesTrailers() {
        Call<VideoResult> call = apiService.getTrailers(mMovie.getId().toString(), APIKEY);
        call.enqueue(new Callback<VideoResult>() {

            @Override
            public void onResponse(Response<VideoResult> response, Retrofit retrofit) {
                int statusCode = response.code();
                VideoResult movieResult = response.body();
                mVideos = movieResult.getResults();
                videoLayout = (LinearLayout) getActivity().findViewById(R.id.trailers_layout);
                //videoLayout.removeAllViews();
                for (final Video v : mVideos) {
                    insertMovieTrailers(v.getName(), v.getKey());
                }
                mYoutubeLink = mVideos.get(0).getKey();
                // Refresh Share Action Provider
                mShareActionProvider.setShareIntent(createShareMoviesIntent());


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
        mShareActionProvider.setShareIntent(createShareMoviesIntent());

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
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
            mFloatingButton.setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);
            mMovieId = data.getInt(data.getColumnIndex(MovieColumns._ID));
            // Get movie data
            String title = data.getString(data.getColumnIndex(MovieColumns.TITLE));
            String back = data.getString(data.getColumnIndex(MovieColumns.BACK_POSTER));
            String poster = data.getString(data.getColumnIndex(MovieColumns.POSTER));
            double popularity = data.getDouble(data.getColumnIndex(MovieColumns.POPULARITY));
            double vote = data.getDouble(data.getColumnIndex(MovieColumns.VOTE));
            String overview = data.getString(data.getColumnIndex(MovieColumns.DESCRIPTION));
            String date = data.getString(data.getColumnIndex(MovieColumns.DATE));

            insertBasicData(title, poster, back, popularity, vote, overview, date);

            // Get reviews data
            Cursor reviews = getActivity().getContentResolver().query(
                    MovieProvider.Reviews.withId(data.getInt(data.getColumnIndex(MovieColumns._ID))),
                    null, null, null, null);
            reviewsLayout = (LinearLayout) getActivity().findViewById(R.id.reviews_layout);
            if (reviews != null) {
                for (int i = 0; i < reviews.getCount(); i++) {
                    reviews.move(1);
                    String author = reviews.getString(reviews.getColumnIndex(ReviewColumns.AUTHOR));
                    String content = reviews.getString(reviews.getColumnIndex(ReviewColumns.CONTENT));
                    insertMovieReviews(author, content);
                }
                reviews.close();
            }

            // Get Trailers data
            Cursor videos = getActivity().getContentResolver().query(
                    MovieProvider.Videos.withId(data.getInt(data.getColumnIndex(MovieColumns._ID))),
                    null, null, null, null);
            videoLayout = (LinearLayout) getActivity().findViewById(R.id.trailers_layout);
            if (videos != null) {
                for (int i = 0; i < videos.getCount(); i++) {
                    videos.move(1);
                    String name = videos.getString(videos.getColumnIndex(VideoColumns.NAME));
                    String key = videos.getString(videos.getColumnIndex(VideoColumns._ID));
                    if(i == 0) {
                        mYoutubeLink = key;
                        // Refresh ShareActionProvider
                        mShareActionProvider.setShareIntent(createShareMoviesIntent());
                    }
                    insertMovieTrailers(name, key);
                }
                videos.close();
            }

        }

    }

    private void insertBasicData(String title, String poster, String back, double popularity,
                                 double vote, String overview, String date) {
        // Adding title
        mMovieTitle.setText(title);
        // Adding image with Picasso and changing color to status bar and collapse
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500//" + back).into(header);
        // Adding image with Picasso and changing color to status bar and collapse
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w342//" + poster).into(mPoster);
        // Adding popularity
        mPopularityText.setText(String.valueOf(popularity));
        // Adding votes
        mVotesText.setText(String.valueOf(vote));
        // Adding Overview
        mDescriptionText.setText(overview);
        // Adding release date
        mDateText.setText(date);
    }


    private void insertMovieReviews(final String author, final String content) {
        String out = "";
        View review = getActivity().getLayoutInflater().inflate(R.layout.items_review, null);
        TextView mAuthor = (TextView) review.findViewById(R.id.author_label);
        mAuthor.setText(author);
        TextView mContent = (TextView) review.findViewById(R.id.content_label);
        if(content.length() > 15) out = content.substring(0,15) + "...";
        else out = content;
        mContent.setText(out);
        reviewsLayout.addView(review);

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(author)
                        .setMessage(content)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void insertMovieTrailers(String name, final String key) {

        View trailer = getActivity().getLayoutInflater().inflate(R.layout.items_video, null);
        TextView mTrailerName = (TextView) trailer.findViewById(R.id.trailer_name);
        mTrailerName.setText(name);
        mTrailerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                    startActivity(intent);
                }catch (ActivityNotFoundException ex){
                    Intent intent=new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v="+key));
                    startActivity(intent);
                }
            }
        });

        videoLayout.addView(trailer);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
