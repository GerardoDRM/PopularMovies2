package app.gerardo.popularmovies2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
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

import app.gerardo.popularmovies2.API.MoviesApi;
import app.gerardo.popularmovies2.model.Movie;
import app.gerardo.popularmovies2.model.Review;
import app.gerardo.popularmovies2.model.ReviewResult;
import app.gerardo.popularmovies2.model.Video;
import app.gerardo.popularmovies2.model.VideoResult;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsMovieActivityFragment extends Fragment {
    private static final String MOVIES_SHARE_HASHTAG = "#Popular Movies App";
    // Fetch and store ShareActionProvider
    ShareActionProvider mShareActionProvider;
    private String mMovieStr;
    CollapsingToolbarLayout collapsingToolbar;
    ImageView header, mPoster;
    TextView mPopularityText, mVotesText, mDescriptionText, mDateText;
    Movie movie;


    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    final String APIKEY = "c77c154b48e61f06b2858716425efb7d";
    Retrofit retrofit;
    MoviesApi apiService;



    public DetailsMovieActivityFragment() {
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

        Intent intent = getActivity().getIntent();
        movie = intent.getParcelableExtra("MOVIE");

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_details_movie, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);

        header = (ImageView) rootView.findViewById(R.id.header_img_poster);
        mPoster = (ImageView) rootView.findViewById(R.id.img_poster);
        mPopularityText = (TextView) rootView.findViewById(R.id.popularity_data);
        mVotesText = (TextView) rootView.findViewById(R.id.votes_data);
        mDescriptionText = (TextView) rootView.findViewById(R.id.overview_data);
        mDateText = (TextView) rootView.findViewById(R.id.release_date_data);


        getGeneralInfo(appCompatActivity);
        getMoviesTrailers();
        getMovieReviews();
        return rootView;
    }

    private void getGeneralInfo(AppCompatActivity appCompatActivity) {
        String title = movie.getTitle();
        String mImageBack = movie.getBackdropPath();
        String mImagePoster = movie.getPosterPath();
        String mPopularity = String.valueOf(movie.getPopularity());
        String mVotes = String.valueOf(movie.getVoteAverage());
        String mOverView = movie.getOverview();
        String date = movie.getReleaseDate();

        // Adding title name
        appCompatActivity.getSupportActionBar().setTitle(movie.getTitle());
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
        Call<ReviewResult> call = apiService.getReviwes(movie.getId().toString(),APIKEY);
        call.enqueue(new Callback<ReviewResult>() {

            @Override
            public void onResponse(Response<ReviewResult> response, Retrofit retrofit) {
                int statusCode = response.code();
                ReviewResult reviewResult = response.body();

                LinearLayout viewGroup = (LinearLayout) getActivity().findViewById(R.id.reviews_layout);

                for(final Review r : reviewResult.getResults()) {
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
        Call<VideoResult> call = apiService.getTrailers(movie.getId().toString(), APIKEY);
        call.enqueue(new Callback<VideoResult>() {

            @Override
            public void onResponse(Response<VideoResult> response, Retrofit retrofit) {
                int statusCode = response.code();
                VideoResult movieResult = response.body();
                LinearLayout viewGroup = (LinearLayout) getActivity().findViewById(R.id.trailers_layout);

                for(final Video v : movieResult.getResults()) {
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

}
