package app.gerardo.popularmovies2.API;

import app.gerardo.popularmovies2.model.MovieResult;
import app.gerardo.popularmovies2.model.ReviewResult;
import app.gerardo.popularmovies2.model.VideoResult;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Gerardo de la Rosa on 5/10/15.
 */
public interface MoviesApi {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter
    @GET("discover/movie")
    Call<MovieResult> getMovies(@Query("sort_by") String sortBy,
                                @Query("api_key") String apiKey,
                                @Query("vote_count.gte") Integer count);

    @GET("movie/{id}/videos")
    Call<VideoResult> getTrailers(@Path("id") String id,
                                  @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewResult> getReviwes(@Path("id") String id,
                                  @Query("api_key") String apiKey);

}
