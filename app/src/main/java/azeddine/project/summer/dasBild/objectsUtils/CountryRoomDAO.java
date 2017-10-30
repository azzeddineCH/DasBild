package azeddine.project.summer.dasBild.objectsUtils;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by azeddine on 10/6/17.
 */

@Dao
public interface CountryRoomDAO{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCountries(List<Country> countries);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCountry(Country country);

    @Query("SELECT * FROM countries WHERE region = :region")
    List<Country> selectRegionCountries(String region);

    @Update
    void updateCountry(Country country);

    @Query("SELECT * FROM countries WHERE name= :name LIMIT 1")
    Country selectCountry(String name);

    @Query("SELECT * FROM countries WHERE bookmarked ORDER BY name")
    List<Country> selectBookmarkedCountry();

    @Query("SELECT * FROM countries WHERE bookmarked LIMIT 1")
    Country selectLatestBookmarkedCountry();

}
