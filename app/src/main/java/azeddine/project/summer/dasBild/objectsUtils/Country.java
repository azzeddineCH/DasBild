package azeddine.project.summer.dasBild.objectsUtils;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by azeddine on 28/07/17.
 */

@Entity(tableName = "countries",indices = {@Index(value = "name",unique = true)})
public class Country extends BookmarkedItem{



    @PrimaryKey
    @NonNull
    private String name;

    @Ignore
    private String capital;

    private String flagURL;

    private String twoAlphaCode;


    private String threeAlphaCode;

    private String region;



    public Country(String name, String twoAlphaCode, String threeAlphaCode, @Nullable String flagURL,String region) {
        this.name = name;
        this.twoAlphaCode = twoAlphaCode.toLowerCase();
        this.flagURL = flagURL;
        this.threeAlphaCode = threeAlphaCode;
        this.region = region;
    }

    @Ignore
    public Country(String name) {
        setName(name);
    }

    @Ignore
    public Country(String name,boolean bookmarked){
        this.name = name;
        setBookmarked(bookmarked);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital;
    }
    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getFlagURL() {
        return flagURL;
    }
    public void setFlagURL(String flagURL) {
        this.flagURL = flagURL;
    }

    public String getThreeAlphaCode() {
        return threeAlphaCode;
    }
    public void setThreeAlphaCode(String threeAlphaCode) {
        this.threeAlphaCode = threeAlphaCode;
    }

    public String getTwoAlphaCode() {
        return twoAlphaCode;
    }
    public void setTwoAlphaCode(String twoAlphaCode) {
        this.twoAlphaCode = twoAlphaCode;
    }

    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Country) {
            return this.getName().equalsIgnoreCase(((Country) obj).getName());
        } else return false;

    }
}
