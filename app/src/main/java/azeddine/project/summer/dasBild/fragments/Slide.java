package azeddine.project.summer.dasBild.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

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
        View view;
        int slideNumber =  args.getInt("INTRO_SLIDE_NUMBER");
        switch (slideNumber){
            case 1:

                view =  inflater.inflate(R.layout.app_intro_travel,container,false);
                break;
            case 2:
                view = inflater.inflate(R.layout.app_intro_save,container,false);
                break;
            case 3:
                 view = inflater.inflate(R.layout.app_intro_discover,container,false);
                 break;
            case 4:
                 view =  inflater.inflate(R.layout.app_intro_start,container,false);
                 break;
            default:
                view = null;
        }


        return view;

    }


}
