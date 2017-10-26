package azeddine.project.summer.dasBild.objectsUtils;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by azeddine on 28/07/17.
 */

@Entity(tableName = "bookmarked_photo", indices = {@Index(value = "id",unique = true)})
public class Photo implements Serializable{

    @PrimaryKey
    @NonNull
    private String id;
    private String description;
    private String title;
    private String croppedPhotoUrl;
    private String unCroppedPhotoUrl;
    private String photographerUsername;
    private String photographerImageUrl;
    private String dateString;


    public Photo() {

    }

    public Photo(String ID, String url) {
        this.id = ID;
        this.croppedPhotoUrl = url;
    }

    public Photo(String ID, String description, String url) {
        this.id = ID;
        this.description = description;
        this.croppedPhotoUrl = url;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCroppedPhotoUrl() {
        return croppedPhotoUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnCroppedPhotoUrl(String unCroppedPhotoUrl) {
        this.unCroppedPhotoUrl = unCroppedPhotoUrl;
    }

    public void setPhotographerUsername(String photographerUsername) {
        this.photographerUsername = photographerUsername;
    }

    public void setPhotographerImageUrl(String photographerImageUrl) {
        this.photographerImageUrl = photographerImageUrl;
    }

    public String getUnCroppedPhotoUrl() {
        return unCroppedPhotoUrl;
    }

    public String getPhotographerUsername() {
        return photographerUsername;
    }

    public String getPhotographerImageUrl() {
        return photographerImageUrl;
    }

    public void setCroppedPhotoUrl(String croppedPhotoUrl) {
        this.croppedPhotoUrl = croppedPhotoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  Photo){
            Photo p = (Photo) obj;
            return  p.getId().equals(id);
        }else return  false;

        }
}
