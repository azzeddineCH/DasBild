package azeddine.project.summer.dasBild.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.objectsUtils.Country;

/**
 * Created by azeddine on 28/07/17.
 */

public class CountriesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CountriesListAdapter";

    private static final int VIEW_TYPE_COUNTRY = 0;
    private static final int VIEW_TYPE_LOAD = 1;
    private static final int VIEW_TYPE_EMPTY = -1;

    private Context mContext;
    private Country mFocusedCountry;
    private List<Country> mCountries = new ArrayList<>();
    static private OnCountryItemClickedListener mOnCountryItemClickedListener;
    static private OnCountryItemLongClickedListener mOnCountryItemLongClickedListener;
    private boolean mRecyclerViewLoadingState;


    public interface OnCountryItemClickedListener {
        void onCountryClicked(String countryName);
    }

    public interface OnCountryItemLongClickedListener {
        void onCountryLongClicked(Country country, ImageView sharedImageView);
    }

    public CountriesListAdapter(Context context) {
        mContext = context;
        if (context instanceof OnCountryItemClickedListener) {
            mOnCountryItemClickedListener = (OnCountryItemClickedListener) context;
        }
        if (context instanceof OnCountryItemLongClickedListener) {
            mOnCountryItemLongClickedListener = (OnCountryItemLongClickedListener) context;
        }
        setRecyclerViewLoadingState(true);
    }


    @Override
    public int getItemViewType(int position) {
        Country country = mCountries.get(position);
        if (country == null) return VIEW_TYPE_LOAD;
        else return VIEW_TYPE_COUNTRY;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_COUNTRY:
                view = LayoutInflater.from(mContext).inflate(R.layout.list_item_country, parent, false);
                return new CountryViewHolder(view);
            case VIEW_TYPE_LOAD:
                view = LayoutInflater.from(mContext).inflate(R.layout.list_item_load, parent, false);
                return new LoadingProgressViewHolder(view);
            default:
                return null;

        }

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CountryViewHolder) {
            CountryViewHolder countryViewHolder = (CountryViewHolder) holder;
            Country country = getCountryByIndex(position);

            if (mFocusedCountry.equals(country)) {
                countryViewHolder.mFlagImageView.setBackgroundResource(R.drawable.shape_border_focus);
                countryViewHolder.mNameTextView.setTextColor(mContext.getResources().getColor(R.color.blue_light));
            }

            countryViewHolder.mNameTextView.setTag(country);
            countryViewHolder.mNameTextView.setText(country.getThreeAlphaCode());
            Glide.with(mContext)
                    .load(country.getThreeAlphaCode().equals("ALL") ? R.mipmap.plant_earth : country.getFlagURL())
                    .apply(new RequestOptions().optionalCircleCrop())
                    .into(countryViewHolder.mFlagImageView);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof CountryViewHolder) {
            CountryViewHolder countryViewHolder = (CountryViewHolder) holder;
            countryViewHolder.mFlagImageView.setBackgroundResource(R.drawable.shape_border_free);
            countryViewHolder.mNameTextView.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
        }
    }

    @Override
    public int getItemCount() {
        return mCountries.size();
    }

    private Country getCountryByIndex(int index) {
        return mCountries.get(index);
    }

    public String getFocusedCountryName() {
        return (mFocusedCountry != null) ? mFocusedCountry.getName() : null;
    }

    public void setCountriesList(List<Country> countriesList, String focusedCountryName) {
        int position;
        setRecyclerViewLoadingState(false);
        if (countriesList != null) {
            if (focusedCountryName != null)
                position = countriesList.indexOf(new Country(focusedCountryName));
            else
                position = 0;
            mCountries.clear();
            mCountries.addAll(countriesList);
            mFocusedCountry = countriesList.get(position);
            notifyDataSetChanged();
        }
    }

    public void setOnCountryItemClickedListener(OnCountryItemClickedListener listener) {
        mOnCountryItemClickedListener = listener;
    }

    public void setOnCountryItemLongClickedListener(OnCountryItemLongClickedListener listener) {
        mOnCountryItemLongClickedListener = listener;
    }

    public void setRecyclerViewLoadingState(boolean recyclerViewLoadingState) {
        mRecyclerViewLoadingState = recyclerViewLoadingState;
        if (mRecyclerViewLoadingState) {
            mCountries.add(null);
            notifyItemInserted(mCountries.size() - 1);
        } else {
            mCountries.remove(mCountries.size() - 1);
            notifyItemRemoved(mCountries.size());
        }
    }

    public class CountryViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener,
            RecyclerView.OnLongClickListener, RecyclerView.OnTouchListener {
        private ImageView mFlagImageView;
        private TextView mNameTextView;

        private boolean isLongPressed = false;

        public CountryViewHolder(View itemView) {
            super(itemView);
            mFlagImageView = itemView.findViewById(R.id.flag);
            mNameTextView = itemView.findViewById(R.id.country_name);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnTouchListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: ");
            final int unfocusedCountryPosition = mCountries.indexOf(mFocusedCountry);
            mFocusedCountry = (Country) mNameTextView.getTag();

            startScaleInAnimation(mFlagImageView, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startScaleOutAnimation(mFlagImageView, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            notifyItemChanged(unfocusedCountryPosition);
                            notifyItemChanged(mCountries.indexOf(mFocusedCountry));
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mOnCountryItemClickedListener.onCountryClicked(mFocusedCountry.getName());
        }

        @Override
        public boolean onLongClick(View view) {
            startScaleInAnimation(mFlagImageView, null);
            isLongPressed = true;
            mOnCountryItemLongClickedListener.onCountryLongClicked(((Country) mNameTextView.getTag()), mFlagImageView);
            return true;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
                if (isLongPressed) startScaleOutAnimation(mFlagImageView, null);
            }
            return false;
        }

        void startScaleInAnimation(View view, Animation.AnimationListener listener) {
            Animation scale = new ScaleAnimation(1, 0.8f, 1, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scale.setInterpolator(AnimationUtils.loadInterpolator(
                    mContext,
                    android.R.interpolator.linear_out_slow_in
            ));
            scale.setDuration(100);
            scale.setFillAfter(true);
            scale.setAnimationListener(listener);
            view.startAnimation(scale);
        }

        void startScaleOutAnimation(View view, Animation.AnimationListener listener) {
            Animation scale = new ScaleAnimation(0.8f, 1, 0.8f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scale.setInterpolator(AnimationUtils.loadInterpolator(
                    mContext,
                    android.R.interpolator.linear_out_slow_in
            ));
            scale.setDuration(100);
            scale.setFillAfter(true);
            scale.setAnimationListener(listener);
            view.startAnimation(scale);
        }
    }

    private static class LoadingProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;

        private LoadingProgressViewHolder(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.load_small_progress_bar);
        }
    }
}
