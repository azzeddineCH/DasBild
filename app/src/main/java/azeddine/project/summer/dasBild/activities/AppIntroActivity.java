package azeddine.project.summer.dasBild.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.fragments.Slide;

public class AppIntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slide slide = new Slide();
        Bundle args = new Bundle();

        setBarColor(getResources().getColor(R.color.colorPrimary));
        setSeparatorColor(getResources().getColor(R.color.colorPrimary));


        args.putInt("INTRO_SLIDE_NUMBER",1);
        slide.setArguments(args);
        addSlide(slide);

        slide = new Slide();
        args = new Bundle();
        args.putInt("INTRO_SLIDE_NUMBER",2);
        slide.setArguments(args);
        addSlide(slide);

        slide = new Slide();
        args = new Bundle();
        args.putInt("INTRO_SLIDE_NUMBER",3);
        slide.setArguments(args);
        addSlide(slide);

        slide = new Slide();
        args = new Bundle();
        args.putInt("INTRO_SLIDE_NUMBER",4);
        slide.setArguments(args);
        addSlide(slide);


    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        loadMainActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        loadMainActivity();
    }
}
