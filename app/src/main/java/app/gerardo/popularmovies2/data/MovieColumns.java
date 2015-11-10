package app.gerardo.popularmovies2.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Gerardo de la Rosa on 19/10/15.
 * Credit to SimonVT https://github.com/SimonVT/schematic
 * This app use schematic library to create the content provider
 * it's a simple solution but it has a lot limitations, specially on
 * database actions
 */
public interface MovieColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    public static final String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String TITLE = "title";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String DESCRIPTION = "description";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String DATE = "date";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String POSTER = "poster";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String BACK_POSTER = "back_poster";
    @DataType(DataType.Type.REAL) @NotNull
    public static final String POPULARITY = "popularity";
    @DataType(DataType.Type.REAL) @NotNull
    public static final String VOTE = "vote";
}
