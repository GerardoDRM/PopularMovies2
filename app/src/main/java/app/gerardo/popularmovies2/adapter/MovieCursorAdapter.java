package app.gerardo.popularmovies2.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.gerardo.popularmovies2.MainFragment;
import app.gerardo.popularmovies2.R;
import app.gerardo.popularmovies2.data.MovieColumns;
import app.gerardo.popularmovies2.data.MovieProvider;

/**
 * Created by Gerardo de la Rosa on 8/11/15.
 * Credit to Sam_chordas https://github.com/schordas/SchematicPlanets/tree/master
 * for all bases
 */
public class MovieCursorAdapter extends CursorRecyclerViewAdapter<MovieCursorAdapter.ViewHolder> {

    private Context mContext;


    public MovieCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.mContext = context;

    }

    @Override
    public void onBindViewHolder(MovieCursorAdapter.ViewHolder viewHolder, final Cursor cursor) {
        DatabaseUtils.dumpCursor(cursor);
        viewHolder.mTextView.setText(cursor.getString(
                cursor.getColumnIndex(MovieColumns.TITLE)
        ));
        // Get image with Picasso
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + cursor.getString(
                cursor.getColumnIndex(MovieColumns.POSTER)
        )).into(viewHolder.moviePoster);
        final int id = cursor.getInt(
                cursor.getColumnIndex(MovieColumns._ID));
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainFragment.CallbackMovie) mContext).onFavoriteSelected(
                        MovieProvider.Movies.withId(id));
            }
        });
    }

    @Override
    public MovieCursorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);
        return new ViewHolder(view);
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
