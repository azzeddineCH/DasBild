package azeddine.project.summer.dasBild.objectsUtils;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by azeddine on 10/9/17.
 */

@Dao
public interface PhotoRoomDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPhoto(Photo photo);

    @Delete
    void deletePhoto(Photo photo);

    @Query("SELECT * FROM bookmarked_photo WHERE id BETWEEN :a AND :b")
    List<Photo> selectPhotosRange(int a,int b);

    @Query("SELECT * FROM bookmarked_photo")
    List<Photo> selectAllPhotos();

    @Query("SELECT * FROM bookmarked_photo WHERE id = :id LIMIT 1")
    Photo selectPhotoById(String id);


}
