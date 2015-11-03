package app.gerardo.popularmovies2;

import android.app.Application;

import app.gerardo.popularmovies2.utils.RequestMovie;

/**
 * Created by Gerardo de la Rosa on 21/10/15.
 */
public class PopularMoviesApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RequestMovie.getInstance();
    }
}
