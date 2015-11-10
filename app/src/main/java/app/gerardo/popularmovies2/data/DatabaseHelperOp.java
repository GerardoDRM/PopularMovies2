package app.gerardo.popularmovies2.data;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.gerardo.popularmovies2.model.Movie;
import app.gerardo.popularmovies2.model.Review;
import app.gerardo.popularmovies2.model.Video;

/**
 * Created by Gerardo de la Rosa on 9/11/15.
 */
public class DatabaseHelperOp {

    private static String LOG_TAG = "DatabaseOp";

    public static void insertMovie(Movie mMovie, Context context) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                MovieProvider.Movies.CONTENT_URI);
        builder.withValue(MovieColumns._ID, mMovie.getId());
        builder.withValue(MovieColumns.TITLE, mMovie.getTitle());
        builder.withValue(MovieColumns.DESCRIPTION, mMovie.getOverview());
        builder.withValue(MovieColumns.BACK_POSTER, mMovie.getBackdropPath());
        builder.withValue(MovieColumns.POSTER, mMovie.getPosterPath());
        builder.withValue(MovieColumns.POPULARITY, mMovie.getPopularity());
        builder.withValue(MovieColumns.VOTE, mMovie.getVoteAverage());
        builder.withValue(MovieColumns.DATE, mMovie.getReleaseDate());

        batchOperations.add(builder.build());


        try {
            context.getContentResolver().applyBatch(MovieProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }

    }

    public static void deleteMovie(long id, Context context) {
        Log.d(LOG_TAG, "delete");
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(
                MovieProvider.Movies.withId(id));

        batchOperations.add(builder.build());

        try {
            context.getContentResolver().applyBatch(MovieProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {

            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
    }

    public static void deleteBulkReviews(long id, Context context) {
        Log.d(LOG_TAG, "delete");
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(
                MovieProvider.Reviews.withId(id));

        batchOperations.add(builder.build());

        try {
            context.getContentResolver().applyBatch(MovieProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {

            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
    }

    public static void deleteBulkTrailers(Long id, Context context) {
        Log.d(LOG_TAG, "delete");
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(
                MovieProvider.Videos.withId(id));

        batchOperations.add(builder.build());

        try {
            context.getContentResolver().applyBatch(MovieProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {

            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
    }

    public static void insertBulkReviews(List<Review> mReviews, Context context, Long id) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();

        for(Review review : mReviews) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                    MovieProvider.Reviews.CONTENT_URI);
            builder.withValue(ReviewColumns._ID, review.getId());
            builder.withValue(ReviewColumns.MOVIE_ID, id);
            builder.withValue(ReviewColumns.AUTHOR, review.getAuthor());
            builder.withValue(ReviewColumns.CONTENT, review.getContent());
            batchOperations.add(builder.build());
        }

        try {
            context.getContentResolver().applyBatch(MovieProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }


    }

    public static void insertBulkVideos(List<Video> mVideos, Context context, Long id) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        for(Video video : mVideos) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                    MovieProvider.Videos.CONTENT_URI);
            builder.withValue(VideoColumns._ID, video.getKey());
            builder.withValue(VideoColumns.MOVIE_ID, id);
            builder.withValue(VideoColumns.NAME, video.getName());
            batchOperations.add(builder.build());
        }

        try {
            context.getContentResolver().applyBatch(MovieProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
    }
}
