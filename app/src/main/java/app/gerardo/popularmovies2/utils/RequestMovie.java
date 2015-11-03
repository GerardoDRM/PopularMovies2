package app.gerardo.popularmovies2.utils;

import app.gerardo.popularmovies2.API.MoviesApi;
import app.gerardo.popularmovies2.model.Review;
import app.gerardo.popularmovies2.model.ReviewResult;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Gerardo de la Rosa on 20/10/15.
 */
public class RequestMovie {
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    final String APIKEY = "c77c154b48e61f06b2858716425efb7d";

    public static RequestMovie mInstance = null;

    static Retrofit retrofit;
    static MoviesApi apiService = null;

    public static RequestMovie getInstance() {
        if(mInstance == null) {
            mInstance = new RequestMovie();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(MoviesApi.class);

        }
        return mInstance;
    }


    public String getMovieReviews() {
        Call<ReviewResult> call = apiService.getReviwes("49026",APIKEY);
        call.enqueue(new Callback<ReviewResult>() {

            @Override
            public void onResponse(Response<ReviewResult> response, Retrofit retrofit) {
                int statusCode = response.code();
                String out = "";
                ReviewResult q = response.body();
                for(Review m : q.getResults()) {
                    out += m.getAuthor() + " ";
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
            }
        });

        return null;

    }
}
