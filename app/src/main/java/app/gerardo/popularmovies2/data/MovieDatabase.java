package app.gerardo.popularmovies2.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Gerardo de la Rosa on 19/10/15.
 */

@Database(version = MovieDatabase.VERSION)
public final class MovieDatabase {

    private MovieDatabase(){}

    public static final int VERSION = 1;

    public static class Tables {
        @Table(MovieColumns.class) public static final String MOVIE = "movies";
        @Table(ReviewColumns.class) public static final String REVIEW = "reviews";
        @Table(VideoColumns.class) public static final String VIDEO = "videos";
    }
}
