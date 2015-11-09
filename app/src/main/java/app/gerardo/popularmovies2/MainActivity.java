package app.gerardo.popularmovies2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.gerardo.popularmovies2.model.Movie;
import app.gerardo.popularmovies2.utils.GeneralConst;

public class MainActivity extends AppCompatActivity implements MainFragment.CallbackMovie{
    private final String MAIN_FRAGMENT = "MOVIE_FRAGMENT";
    private final String DETAILS_FRAGMENT = "MOVIE_FRAGMENT_DETAILS";
    private Movie mMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_details) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            GeneralConst.mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_details, new DetailsMovieActivityFragment(), DETAILS_FRAGMENT)
                        .commit();
            }

        } else {
            GeneralConst.mTwoPane = false;
        }

    }

    @Override
    public void onItemSelected(Movie movie) {
        if(GeneralConst.mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(GeneralConst.MOVIE_KEY, movie);


            DetailsMovieActivityFragment fragment = new DetailsMovieActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_details, fragment, DETAILS_FRAGMENT)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailsMovieActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putParcelable(GeneralConst.MOVIE_KEY, movie);
            intent.putExtras(mBundle);
            startActivity(intent);
        }
    }

    @Override
    public void onFavoriteSelected(Uri uri) {
        if(GeneralConst.mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(GeneralConst.MOVIE_URI, uri);


            DetailsMovieActivityFragment fragment = new DetailsMovieActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_details, fragment, DETAILS_FRAGMENT)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailsMovieActivity.class)
                    .setData(uri);
            startActivity(intent);
        }
    }
}
