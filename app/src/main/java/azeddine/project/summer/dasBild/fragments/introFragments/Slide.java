package azeddine.project.summer.dasBild.fragments.introFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import azeddine.project.summer.dasBild.R;

/**
 * Created by azeddine on 11/4/17.
 */

public class Slide extends Fragment {
    private static final String TAG = "Slide";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        int slideNumber =  args.getInt("INTRO_SLIDE_NUMBER");
        switch (slideNumber){
            case 1:
                return inflater.inflate(R.layout.app_intro_travel_slide,container,false);
            case 2:
                return inflater.inflate(R.layout.app_intro_descover_slide,container,false);
            case 3:
                return inflater.inflate(R.layout.app_intro_save,container,false);
            default:
                return null;
        }

    }
}
