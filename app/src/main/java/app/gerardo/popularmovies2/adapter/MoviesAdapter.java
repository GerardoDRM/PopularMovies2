package app.gerardo.popularmovies2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.gerardo.popularmovies2.MainFragment;
import app.gerardo.popularmovies2.R;
import app.gerardo.popularmovies2.model.Movie;

/**
 * Created by Gerardo de la Rosa on 21/10/15.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private Context mContext;
    private List<Movie> mMovies;
    public MoviesAdapter(Context context, List<Movie> movies) {
        this.mContext = context;
        this.mMovies = movies;
    }

    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.ViewHolder holder, final int position) {
        holder.mTextView.setText(mMovies.get(position).getTitle());
        // Get image with Picasso
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + mMovies.get(position).getPosterPath()).into(holder.moviePoster);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainFragment.CallbackMovie)mContext).onItemSelected(mMovies.get(position));
            }
        });
    }

    public void refill(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextView;
        public final View mView;
        public final ImageView moviePoster;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTextView = (TextView) itemView.findViewById(R.id.grid_item_title);
            moviePoster = (ImageView) itemView.findViewById(R.id.grid_item_image_poster);

        }
    }
}
