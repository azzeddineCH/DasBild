package azeddine.project.summer.dasBild.costumComponents;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by azeddine on 14/08/17.
 */

public class GridItem extends AppCompatImageView{
    public GridItem(Context context) {
        super(context);
    }

    public GridItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
