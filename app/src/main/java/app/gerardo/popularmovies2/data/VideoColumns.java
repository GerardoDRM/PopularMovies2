package app.gerardo.popularmovies2.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by Gerardo de la Rosa on 25/10/15.
 * Credit to SimonVT https://github.com/SimonVT/schematic
 * This app use schematic library to create the content provider
 * it's a simple solution but it has a lot limitations, specially on
 * database actions
 */
public interface VideoColumns {

    @DataType(DataType.Type.TEXT) @PrimaryKey
    public static final String _ID = "_id";
    @DataType(DataType.Type.INTEGER) @References(table = MovieDatabase.Tables.MOVIE, column = MovieColumns._ID) String MOVIE_ID =
            "movieId";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String NAME = "name";
}
