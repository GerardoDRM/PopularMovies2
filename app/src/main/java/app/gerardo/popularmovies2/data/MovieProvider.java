package app.gerardo.popularmovies2.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Gerardo de la Rosa on 19/10/15.
 */

@ContentProvider(authority = MovieProvider.AUTHORITY, database = MovieDatabase.class)
public final class MovieProvider {
    public static final String AUTHORITY = "app.gerardo.popularmovies2";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String MOVIES = "movies";
        String VIDEOS = "videos";
        String REVIEWS = "reviews";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }



    @TableEndpoint(table = MovieDatabase.Tables.MOVIE) public static class Movies{
        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/movies",
                defaultSort = MovieColumns.POPULARITY + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.item/movies",
                whereColumn = MovieColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.MOVIES, String.valueOf(id));
        }

    }


    // Review actions
    @TableEndpoint(table = MovieDatabase.Tables.REVIEW) public static class Reviews{
        @ContentUri(
                path = Path.REVIEWS,
                type = "vnd.android.cursor.dir/reviews")
        public static final Uri CONTENT_URI = buildUri(Path.REVIEWS);

        @InexactContentUri(
                name = "REVIEW_ID",
                path = Path.REVIEWS + "/#",
                type = "vnd.android.cursor.item/reviews",
                whereColumn = ReviewColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id){
            return buildUri(Path.REVIEWS, String.valueOf(id));
        }
    }

    // Trailer actions
    @TableEndpoint(table = MovieDatabase.Tables.VIDEO) public static class Videos{
        @ContentUri(
                path = Path.VIDEOS,
                type = "vnd.android.cursor.dir/videos")
        public static final Uri CONTENT_URI = buildUri(Path.VIDEOS);

        @InexactContentUri(
                name = "VIDEO_ID",
                path = Path.VIDEOS + "/#",
                type = "vnd.android.cursor.item/videos",
                whereColumn = ReviewColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id){
            return buildUri(Path.VIDEOS, String.valueOf(id));
        }
    }


}
