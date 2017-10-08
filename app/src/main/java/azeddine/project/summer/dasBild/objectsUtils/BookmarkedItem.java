package azeddine.project.summer.dasBild.objectsUtils;

/**
 * Created by azeddine on 10/6/17.
 */

public class BookmarkedItem{
    private boolean bookmarked = false;

    public void setBookmarked(boolean state){
           bookmarked = state;
    }
    public boolean isBookmarked(){
        return bookmarked;
    }
}
