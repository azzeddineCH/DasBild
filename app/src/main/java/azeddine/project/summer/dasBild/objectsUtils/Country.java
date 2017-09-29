package azeddine.project.summer.dasBild.objectsUtils;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by azeddine on 28/07/17.
 */

public class Country {


    private String name;
    private String capital;
    private String flagURL;
    private String twoAlphaCode;
    private String threeAlphaCode;
    private ArrayList<Photo> photoArrayList ;



    public Country(String name, String twoAlphaCode, String threeAlphaCode,@Nullable String url){
        this.name = name;
        this.twoAlphaCode = twoAlphaCode.toLowerCase();
        this.flagURL = url ;
        this.threeAlphaCode =threeAlphaCode;
    }
    public Country(String name){
        setName(name);
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

    public ArrayList<Photo> getPhotoArrayList() {
        return photoArrayList;
    }

    public void setPhotoArrayList(ArrayList<Photo> photoArrayList) {
        this.photoArrayList = photoArrayList;
    }

    public String getTwoAlphaCode() {
        return twoAlphaCode;
    }

    public void setTwoAlphaCode(String twoAlphaCode) {
        this.twoAlphaCode = twoAlphaCode;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Country){
            return this.getName().equalsIgnoreCase(((Country) obj).getName());
        }else return false;

    }
}
