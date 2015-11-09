package app.gerardo.popularmovies2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailsMovieActivity extends AppCompatActivity {
    private final String DETAILS_FRAGMENT = "MOVIE_FRAGMENT_DETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_movie);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_details, new DetailsMovieActivityFragment(), DETAILS_FRAGMENT)
                    .commit();
        }
    }

}
