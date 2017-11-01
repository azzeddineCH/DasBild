package azeddine.project.summer.dasBild.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.activities.MainActivity;
import azeddine.project.summer.dasBild.loaders.CountryDetailsLoader;
import azeddine.project.summer.dasBild.objectsUtils.Country;
import azeddine.project.summer.dasBild.objectsUtils.KeysUtil;

/**
 * Created by azeddine on 23/08/17.
 */

public class CountryDetailsDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<String> {
    public static final String TAG = "CountryDetailsDialogFra";

    private TextView mCountryDetailsTextView;
    private String mCountryName;
    private ProgressBar mProgressBar;
    private ScrollView mScrollView;
    private ImageView mBookmarkIconButton;
    private ImageView mCountryFlagImageView;
    private TextView mCountryNameTextView;

    private Country mCountry;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mDialogView = inflater.inflate(R.layout.country_details_fragment, null);

        mCountryFlagImageView = mDialogView.findViewById(R.id.country_flag_imageView);
        mCountryNameTextView = mDialogView.findViewById(R.id.country_name_title);
        mCountryDetailsTextView = mDialogView.findViewById(R.id.country_detail_text);
        mProgressBar = mDialogView.findViewById(R.id.load_small_progress_bar);
        mScrollView = mDialogView.findViewById(R.id.scrollView);
        mBookmarkIconButton = mDialogView.findViewById(R.id.bookmark_button);

        final OnBookmarkButtonClickedTask onBookmarkButtonClickedTask = new OnBookmarkButtonClickedTask();

        mBookmarkIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCountry.isBookmarked()){
                    mBookmarkIconButton.setImageResource(R.drawable.ic_bookmark_border);
                    mCountry.setBookmarked(false);
                }else {
                    mBookmarkIconButton.setImageResource(R.drawable.ic_bookmark);
                    mCountry.setBookmarked(true);
                }
                onBookmarkButtonClickedTask.execute(mCountry);

            }
        });

        mDialogView.findViewById(R.id.show_in_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri googleMapsUrl = Uri.parse("geo:0,0?q=" + Uri.encode(mCountryName));
                Intent googleMapsIntent = new Intent(Intent.ACTION_VIEW, googleMapsUrl);
                googleMapsIntent.setPackage("com.google.android.apps.maps");
                if (googleMapsIntent.resolveActivity(getContext().getPackageManager()) != null)
                    startActivity(googleMapsIntent);
                else Toast.makeText(getContext(), R.string.google_maps_intent_error, Toast.LENGTH_SHORT).show();
            }
        });

        mCountryName = getArguments().getString(KeysUtil.COUNTRY_NAME_KEY);
        new CountrySelectionTask().execute(mCountryName);

        builder.setView(mDialogView)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onShow(DialogInterface dialogInterface) {
                ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            }
        });
        return alertDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.background_shape_radius);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = new Bundle();
        args.putString(KeysUtil.COUNTRY_NAME_KEY, mCountryName);
        getLoaderManager().initLoader(KeysUtil.COUNTRY_DETAILS_LOADER_ID, args, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case KeysUtil.COUNTRY_DETAILS_LOADER_ID:
                return new CountryDetailsLoader(getContext(), args.getString(KeysUtil.COUNTRY_NAME_KEY));
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (loader.getId() == KeysUtil.COUNTRY_DETAILS_LOADER_ID) {
            mProgressBar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            if(data == null){
                mCountryDetailsTextView.setText(R.string.no_details);
                mCountryDetailsTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }else{
                mCountryDetailsTextView.setText(data);
                Animation enterAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_fade_in);
                enterAnimation.setDuration(350);
                enterAnimation.setInterpolator(AnimationUtils.loadInterpolator(
                            getContext(),
                            android.R.interpolator.linear
                    ));
                mCountryDetailsTextView.startAnimation(enterAnimation);

            }
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private class OnBookmarkButtonClickedTask extends AsyncTask<Country, Void, Void> {

        @Override
        protected Void doInBackground(Country... countries) {
            MainActivity.dasBildDataBase.countryRoomDAO().updateCountry(mCountry);
            return null;
        }
    }
    private class CountrySelectionTask extends AsyncTask<String, Void,Country> {


        @Override
        protected Country doInBackground(String... strings) {
            return MainActivity.dasBildDataBase.countryRoomDAO().selectCountry(strings[0]);
        }

        @Override
        protected void onPostExecute(Country country) {
            super.onPostExecute(country);
            mCountry = country;
            String mCountryFlagUrl = null;
            if(country == null){
                mBookmarkIconButton.setVisibility(View.GONE);
            }else {
                if (mCountry.isBookmarked()) mBookmarkIconButton.setImageResource(R.drawable.ic_bookmark);
                mCountryFlagUrl = country.getFlagURL();
            }
            mCountryNameTextView.setText(mCountryName);
            Glide.with(getContext()).load(mCountryFlagUrl  == null ? R.mipmap.plant_earth : mCountryFlagUrl )
                    .apply(new RequestOptions().circleCrop())
                    .into(mCountryFlagImageView);

        }
    }
}
