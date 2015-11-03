package app.gerardo.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gerardo de la Rosa on 11/10/15.
 */
public class ReviewResult implements Parcelable {



    private List<Review> results = new ArrayList<Review>();

    protected ReviewResult(Parcel in) {
        results = in.createTypedArrayList(Review.CREATOR);

    }
    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(results);
    }

    public static final Creator<ReviewResult> CREATOR = new Creator<ReviewResult>() {
        @Override
        public ReviewResult createFromParcel(Parcel in) {
            return new ReviewResult(in);
        }

        @Override
        public ReviewResult[] newArray(int size) {
            return new ReviewResult[size];
        }
    };
}
