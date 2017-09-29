package azeddine.project.summer.dasBild.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.objectsUtils.Photo;

/**
 * Created by azeddine on 31/07/17.
 */
public class CountryAlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "CountryAlbumAdapter";
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_EMPTY = -1;

    private Context mContext;
    private ArrayList<Photo> photos = new ArrayList<>();
    private int mAlbumColumnsNumber;

    static private OnLoadMoreListener mLoadMoreListener;
    static private OnPhotoClickedListener mOnPhotoClickedListener;

    private RecyclerView mRecyclerView;
    private boolean mAdapterViewLoadingState;


    public interface OnLoadMoreListener {
        void onLoadMore();
    }
    public interface OnPhotoClickedListener {
        void onPhotoClicked(Photo photo);
    }

    public CountryAlbumAdapter(Context context, RecyclerView recyclerView, int albumColumnsNumber) {
        this.mContext = context;
        this.mRecyclerView = recyclerView;
        mAlbumColumnsNumber = albumColumnsNumber;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManger = (LinearLayoutManager) recyclerView.getLayoutManager();
                int itemsNumber = layoutManger.getItemCount();
                int lastVisibleItemPosition = layoutManger.findLastCompletelyVisibleItemPosition();

                if (dy > 0) {
                    if (itemsNumber <= lastVisibleItemPosition + 6 && !isLoading()) {
                        mLoadMoreListener.onLoadMore();
                        setAdapterLoadingState(true);
                    }
                }
            }
        });

        if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
            ((GridLayoutManager) mRecyclerView.getLayoutManager()).setSpanSizeLookup(
                    new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            switch (getItemViewType(position)) {
                                case VIEW_TYPE_LOADING:
                                    return mAlbumColumnsNumber;
                                case VIEW_TYPE_ITEM:
                                    return 1;
                                default:
                                    return -1;
                            }
                        }
                    }
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(photos.isEmpty()){
            return VIEW_TYPE_EMPTY;
        }else{
            return photos.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_album, parent, false);
            return new PhotoViewHolder(view);
        } else if(viewType == VIEW_TYPE_LOADING){
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_load, parent, false);
            return new LoadingProgressViewHolder(view);
        }else {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_empty, parent, false);
            return new LoadingProgressViewHolder(view);
        }

    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof PhotoViewHolder) {
            Photo photo = photos.get(position);
            PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
            photoViewHolder.mView.setTag(photo);
            Glide.with(mContext)
                    .load(photo.getCroppedPhotoUrl())
                    .apply(new RequestOptions().placeholder(mContext.getResources().getDrawable(R.drawable.ic_image)))
                    .apply(new RequestOptions().error(R.drawable.ic_terrain_))
                    .apply(new RequestOptions().centerCrop())
                    .into(photoViewHolder.mPhoto);
        }

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void updatePhotos(ArrayList<Photo> photosList) {
        if (photosList != null) {
            if (!photos.containsAll(photosList)) {
                photos.addAll(photosList);
            }
        }

    }

    public void notifyPhotosUpdates() {
        int i = getItemCount() - 1;
        notifyItemRangeInserted(i, photos.size() - 1);
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
       mLoadMoreListener = listener;
    }

    public void setOnPhotoClickedListener(OnPhotoClickedListener listener) {
        mOnPhotoClickedListener = listener;
    }

    public boolean isLoading() {
        return mAdapterViewLoadingState;
    }

    public void setAdapterLoadingState(boolean recyclerViewLoadingState) {
        this.mAdapterViewLoadingState = recyclerViewLoadingState;
        Log.d(TAG, "setAdapterLoadingState: ");
        if (mAdapterViewLoadingState) {
            photos.add(null);
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemInserted(photos.size() - 1);
                    notifyDataSetChanged();
                }
            });
        } else {
            photos.remove(photos.size() - 1);
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemRemoved(photos.size());
                    notifyDataSetChanged();
                }
            });
        }
    }

    private static class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mPhoto;
        private View mView;

        private PhotoViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mView = itemView;
            mPhoto = itemView.findViewById(R.id.album_item);
        }

        @Override
        public void onClick(View view) {

            Photo photo = (Photo) view.getTag();
            mPhoto.setColorFilter(0x32000000, PorterDuff.Mode.OVERLAY);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPhoto.clearColorFilter();
                }
            }, 100);
            mOnPhotoClickedListener.onPhotoClicked(photo);

        }
    }

    private static class LoadingProgressViewHolder extends RecyclerView.ViewHolder {
        private LoadingProgressViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class EmptyListItemViewHolder extends  RecyclerView.ViewHolder{


        public EmptyListItemViewHolder(View itemView, Bitmap image) {
            super(itemView);
            itemView.findViewById(R.id.empty_list_image_view);
        }
    }
}
