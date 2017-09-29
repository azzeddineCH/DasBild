package azeddine.project.summer.dasBild.costumComponents;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by azeddine on 14/08/17.
 */

public class WrapContentGridLayoutManager extends GridLayoutManager{
    public WrapContentGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public WrapContentGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public WrapContentGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
       } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
       }
    }
}
