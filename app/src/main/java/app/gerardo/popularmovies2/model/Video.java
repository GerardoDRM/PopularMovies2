package app.gerardo.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gerardo de la Rosa on 11/10/15.
 */
public class Video implements Parcelable{

    private String key;
    private String name;

    protected Video(Parcel in) {
        key = in.readString();
        name = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
