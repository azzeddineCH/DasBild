package azeddine.project.summer.dasBild;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import azeddine.project.summer.dasBild.objectsUtils.Country;
import azeddine.project.summer.dasBild.objectsUtils.CountryRoomDAO;

/**
 * Created by azeddine on 10/6/17.
 */

@Database(entities = {Country.class},version = 2)
public abstract class DasBildDataBase extends RoomDatabase {
    public abstract CountryRoomDAO countryRoomDAO();
}
