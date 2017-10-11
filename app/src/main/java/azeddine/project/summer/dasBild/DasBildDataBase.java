package azeddine.project.summer.dasBild;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import azeddine.project.summer.dasBild.objectsUtils.Country;
import azeddine.project.summer.dasBild.objectsUtils.CountryRoomDAO;
import azeddine.project.summer.dasBild.objectsUtils.Photo;
import azeddine.project.summer.dasBild.objectsUtils.PhotoRoomDAO;

/**
 * Created by azeddine on 10/6/17.
 */

@Database(entities = {Country.class, Photo.class},version = 3)
public abstract class DasBildDataBase extends RoomDatabase {
    public abstract CountryRoomDAO countryRoomDAO();
    public abstract PhotoRoomDAO photoRoomDAO();
}
