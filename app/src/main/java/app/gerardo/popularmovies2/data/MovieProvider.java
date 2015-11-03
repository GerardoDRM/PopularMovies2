package app.gerardo.popularmovies2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.NotifyInsert;
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
//        String VIDEOS = "videos";
//        String REVIEWS = "reviews";
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
        public static Uri withId(long id){
            return buildUri(Path.MOVIES, String.valueOf(id));
        }

        @NotifyInsert(paths = Path.MOVIES) public static Uri[] onInsert(ContentValues values) {
            final long listId = values.getAsLong(MovieColumns._ID);
            final String title = values.getAsString(MovieColumns.TITLE);
            final String poster = values.getAsString(MovieColumns.POSTER);
            final String back_poster = values.getAsString(MovieColumns.BACK_POSTER);
            final Double popularity = values.getAsDouble(MovieColumns.POPULARITY);
            final Double votes = values.getAsDouble(MovieColumns.VOTE);

            return new Uri[] {
                    withId(listId)
            };
        }

        @NotifyDelete(paths = Path.MOVIES + "/#") public static Uri[] onDelete(Context context,
                                                                              Uri uri) {
            final long noteId = Long.valueOf(uri.getPathSegments().get(1));
            Cursor c = context.getContentResolver().query(uri, null, null, null, null);
            c.moveToFirst();
            final long listId = c.getLong(c.getColumnIndex(MovieColumns._ID));
            c.close();

            return new Uri[] {
                    withId(noteId)
            };
        }
    }


//    // Review actions
//    @TableEndpoint(table = MovieDatabase.Tables.REVIEW) public static class Reviews{
//        @ContentUri(
//                path = Path.REVIEWS,
//                type = "vnd.android.cursor.dir/reviews")
//        public static final Uri CONTENT_URI = buildUri(Path.REVIEWS);
//
//        @InexactContentUri(
//                name = "MOVIE_ID",
//                path = Path.REVIEWS + "/#",
//                type = "vnd.android.cursor.item/reviews",
//                whereColumn = ReviewColumns._ID,
//                pathSegment = 1)
//        public static Uri withId(long id){
//            return buildUri(Path.REVIEWS, String.valueOf(id));
//        }
//
//        @NotifyInsert(paths = Path.REVIEWS) public static Uri[] onInsert(ContentValues values) {
//            final long id = values.getAsLong(ReviewColumns._ID);
//            final String movie_id = values.getAsString(ReviewColumns.MOVIE_ID);
//            final String author = values.getAsString(ReviewColumns.AUTHOR);
//            final String content = values.getAsString(ReviewColumns.CONTENT);
//
//
//            return new Uri[] {
//                    withId(id)
//            };
//        }
//
//        @NotifyBulkInsert(paths = Path.REVIEWS)
//        public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] values, long[] ids) {
//            return new Uri[] {
//                    uri,
//            };
//        }
//
//        @NotifyDelete(paths = Path.REVIEWS + "/#") public static Uri[] onDelete(Context context,
//                                                                               Uri uri) {
//            final long noteId = Long.valueOf(uri.getPathSegments().get(1));
//            Cursor c = context.getContentResolver().query(uri, null, null, null, null);
//            c.moveToFirst();
//            final long listId = c.getLong(c.getColumnIndex(MovieColumns._ID));
//            c.close();
//
//            return new Uri[] {
//                    withId(noteId)
//            };
//        }
//    }

    // Trailer actions


}
