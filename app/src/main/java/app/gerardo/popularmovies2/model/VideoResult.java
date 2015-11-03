package app.gerardo.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gerardo de la Rosa on 11/10/15.
 */
public class VideoResult implements Parcelable {


    private List<Video> results = new ArrayList<Video>();

    protected VideoResult(Parcel in) {
        results = in.createTypedArrayList(Video.CREATOR);
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
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

    public static final Creator<VideoResult> CREATOR = new Creator<VideoResult>() {
        @Override
        public VideoResult createFromParcel(Parcel in) {
            return new VideoResult(in);
        }

        @Override
        public VideoResult[] newArray(int size) {
            return new VideoResult[size];
        }
    };
}
