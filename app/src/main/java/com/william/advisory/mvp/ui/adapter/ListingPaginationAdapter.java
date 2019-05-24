package com.william.advisory.mvp.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.william.advisory.R;
import com.william.advisory.mvp.model.entity.list.ListingItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by William Chow on 2019-05-24.
 */
public class ListingPaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<ListingItem> listingItems;
    private Activity activity;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private String errorMsg;

    private int type;

    public ListingPaginationAdapter(Activity _activity, int _type) {
        this.activity = _activity;
        listingItems = new ArrayList<>();
        this.type = _type;
    }

    private AdapterOnClickListener onClickListener;
    private AdapterOnLongClickListener onLongClickListener;

    public void setOnClickListener(AdapterOnClickListener _onClickListener) {
        this.onClickListener = _onClickListener;
    }

    public void setOnLongClickListener(AdapterOnLongClickListener _onLongClickListener) {
        this.onLongClickListener = _onLongClickListener;
    }

    private PersonalRecordPaginationAdapterCallback mCallback;

    public void setOnRetryCallBackListener(PersonalRecordPaginationAdapterCallback _mCallback) {
        this.mCallback = _mCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                View viewItem;
                if (type == -1) {
                    viewItem = inflater.inflate(R.layout.recycler_view_listing_item, parent, false);
                    viewHolder = new ListingViewHolder(viewItem);
                }
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.recycler_view_item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
        }
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                if (type == -1) {
                    ListingItem listingItem = listingItems.get(position);
                    ListingViewHolder listingViewHolder = (ListingViewHolder) holder;
                    listingViewHolder.tvTitle.setText(activity.getResources().getString(R.string.placeLabel) + " " + listingItem.getList_name());
                    listingViewHolder.tvSubTitle.setText(activity.getResources().getString(R.string.distanceLabel) + " " + listingItem.getDistance());
                }
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;
                if (retryPageLoad) {
                    loadingVH.llLoadMoreErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.pbLoadMore.setVisibility(View.GONE);
                    loadingVH.tvLoadMoreErrorText.setText(errorMsg != null ? errorMsg : activity.getString(R.string.errorLabel));
                } else {
                    loadingVH.llLoadMoreErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.pbLoadMore.setVisibility(View.VISIBLE);
                    loadingVH.tvLoadMoreErrorText.setText(activity.getString(R.string.loadingLabel));
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (type == -1) {
            return listingItems == null ? 0 : listingItems.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (type == -1) {
            return (position == listingItems.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
        }
        return 0;
    }

    private void add(ListingItem r) {
        listingItems.add(r);
        notifyItemInserted(listingItems.size() - 1);
    }

    public void addListingItems(List<ListingItem> moveResults) {
        for (ListingItem result : moveResults) {
            add(result);
        }
    }

    public void addLoadingFooter(int type) {
        if (type == -1) {
            isLoadingAdded = true;
            add(new ListingItem());
        }
    }

    public void removeLoadingFooter(int type) {
        if (type == -1) {
            isLoadingAdded = false;

            int position = listingItems.size() - 1;
            if (position == -1)
                position = 0;

            ListingItem result = null;
            if (listingItems.size() > 0) {
                result = getListingItems(position);
            }

            if (result != null) {
                listingItems.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    private ListingItem getListingItems(int position) {
        return listingItems.get(position);
    }


    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show     show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        if (type == -1) {
            retryPageLoad = show;
            notifyItemChanged(listingItems.size() - 1);

            if (errorMsg != null) this.errorMsg = errorMsg;
        }
    }

    public void clear() {
        if (type == -1) {
            int size = this.listingItems.size();
            this.listingItems.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    protected class ListingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView tvTitle, tvSubTitle;
        private LinearLayout llItem;

        ListingViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvSubTitle = view.findViewById(R.id.tvSubTitle);
            llItem = view.findViewById(R.id.llItem);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClickListener(getAdapterPosition(), listingItems);
        }

        @Override
        public boolean onLongClick(View v) {
            onLongClickListener.onLongClickListener(getAdapterPosition(), listingItems);
            return false;
        }
    }

    public interface AdapterOnClickListener {
        void onClickListener(int _position, List<ListingItem> _arrayList);
    }

    public interface AdapterOnLongClickListener{
        void onLongClickListener(int _position, List<ListingItem> _arrayList);
    }

    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar pbLoadMore;
        private TextView tvLoadMoreErrorText;
        private LinearLayout llLoadMoreErrorLayout;

        LoadingVH(View itemView) {
            super(itemView);

            pbLoadMore = itemView.findViewById(R.id.pbLoadMore);
            tvLoadMoreErrorText = itemView.findViewById(R.id.tvLoadMoreErrorText);
            llLoadMoreErrorLayout = itemView.findViewById(R.id.llLoadMoreErrorLayout);

            llLoadMoreErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.llLoadMoreErrorLayout:
                    showRetry(false, null);
                    mCallback.retryPageLoad();
                    break;
            }
        }
    }

    public interface PersonalRecordPaginationAdapterCallback {
        void retryPageLoad();
    }
}
