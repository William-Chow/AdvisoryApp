package com.william.advisory.mvp.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.william.advisory.R;
import com.william.advisory.app.utils.PrefUtils;
import com.william.advisory.app.utils.PresenterKit;
import com.william.advisory.app.utils.Utils;
import com.william.advisory.app.utils.custom.LinearLayoutManagerWithSmoothScroller;
import com.william.advisory.app.utils.custom.LoadingDialog;
import com.william.advisory.app.utils.custom.PaginationScrollListener;
import com.william.advisory.mvp.model.entity.UserProfile;
import com.william.advisory.mvp.model.entity.list.Listing;
import com.william.advisory.mvp.model.entity.list.ListingItem;
import com.william.advisory.mvp.presenter.ListingPresenter;
import com.william.advisory.mvp.ui.adapter.ListingPaginationAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;
import me.jessyan.art.base.BaseActivity;
import me.jessyan.art.mvp.IView;
import me.jessyan.art.mvp.Message;
import me.jessyan.art.utils.ArtUtils;

import static me.jessyan.art.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArtTemplate on 05/23/2019 17:55
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArt">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
public class ListingActivity extends BaseActivity<ListingPresenter> implements IView {

    @BindView(R.id.rvListing)
    RecyclerView rvListing;

    @BindView(R.id.tvNoRecord)
    TextView tvNoRecord;

    ListingPaginationAdapter listingPaginationAdapter;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 500;

    private int pageSize = 10;
    private String lastTime = "";
    private int pageNumber = 0;

    Listing listing = new Listing();
    UserProfile userProfile = new UserProfile();

    LoadingDialog dialog;

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_listing; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (null != mPresenter) {
            mPresenter.setupListing(PresenterKit.obtainMessage(ListingActivity.this));
        }
    }

    @Override
    @Nullable
    public ListingPresenter obtainPresenter() {
        return new ListingPresenter(ArtUtils.obtainAppComponentFromContext(this), ListingActivity.this.getApplication(), ListingActivity.this);
    }

    @Override
    public void showLoading() {
        dialog = new LoadingDialog(ListingActivity.this, ListingActivity.this.getResources().getString(R.string.pleaseWaitLabel), true);
        dialog.show();
    }

    @Override
    public void hideLoading() {
        if (dialog != null) {
            dialog.close();
        }
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArtUtils.snackbarText(message);
    }

    @Override
    public void handleMessage(@NonNull Message message) {
        checkNotNull(message);
        int recordType = -1;
        switch (message.what) {
            case PresenterKit.PROFILE:
                userProfile = (UserProfile) message.obj;
                if (null != mPresenter && null != userProfile) {
                    mPresenter.callListing(PresenterKit.obtainMessage(ListingActivity.this), userProfile);
                }
                break;
            case PresenterKit.LISTING:
                if (null != mPresenter) {
                    listing = (Listing) message.obj;
                    if (null != listing) {
                        setupAdapter(listing);
                    }
                }
                break;
            case PresenterKit.FIRST_LISTING:
                listing = (Listing) message.obj;
                List<ListingItem> listingItems = listing.getListingItem();
                // this.lastTime = message.str;
                listingPaginationAdapter.addListingItems(listingItems);
                if (listingItems.size() != 0) {
                    rvListing.setVisibility(View.VISIBLE);
                    tvNoRecord.setVisibility(View.GONE);
                    addFooter(recordType);
                }
                noItem();
                break;
            case PresenterKit.NEXT_LISTING:
                List<ListingItem> listingItemList = new ArrayList<>();
                // this.lastTime = message.str;
                // 如果有data回来，也要除去Footer
                if (listingItemList.size() != 0) {
                    removeFooter(recordType);
                }
                isLoading = false;
                listingPaginationAdapter.addListingItems(listingItemList);

                if (listingItemList.size() != 0) {
                    addFooter(recordType);
                }

                // 当没有data回来时，最后除去Footer, 终于没有了
                if (listingItemList.size() == 0) {
                    isLastPage = true;
                    // removeFooter(recordType);
                    listingPaginationAdapter.showRetry(true, ListingActivity.this.getResources().getString(R.string.listingCompleteLabel));
                    noItem();
                }
                break;
            case PresenterKit.UPDATE:
                listingPaginationAdapter.clear();
                if (null != mPresenter && null != userProfile) {
                    mPresenter.callListing(PresenterKit.obtainMessage(ListingActivity.this), userProfile);
                }
                break;
        }
    }

    private void setupAdapter(Listing listing) {
        if(null == listingPaginationAdapter) {
            rvListing.setNestedScrollingEnabled(false);
            listingPaginationAdapter = new ListingPaginationAdapter(ListingActivity.this, -1);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManagerWithSmoothScroller(ListingActivity.this, LinearLayoutManager.VERTICAL, false);
            rvListing.setLayoutManager(linearLayoutManager);
            rvListing.setItemAnimator(new DefaultItemAnimator());
            rvListing.setAdapter(listingPaginationAdapter);

            rvListing.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
                @Override
                protected void loadMoreItems() {
                    isLoading = true;
                    if (null != mPresenter && null != userProfile) {
                        PresenterKit.successResult(PresenterKit.obtainMessage(ListingActivity.this), PresenterKit.NEXT_LISTING);
                    }
                }

                @Override
                public int getTotalPageCount() {
                    return TOTAL_PAGES;
                }

                @Override
                public boolean isLastPage() {
                    return isLastPage;
                }

                @Override
                public boolean isLoading() {
                    return isLoading;
                }
            });

            listingPaginationAdapter.setOnClickListener((_position, _arrayList) -> Toast.makeText(ListingActivity.this, _arrayList.get(_position).getList_name() + " " + _arrayList.get(_position).getDistance(), Toast.LENGTH_SHORT).show());

            listingPaginationAdapter.setOnLongClickListener((_position, _arrayList) -> {
                if (null != userProfile) {
                    customAlertDialog(mPresenter, PresenterKit.obtainMessage(ListingActivity.this), ListingActivity.this, ListingActivity.this.getResources().getString(R.string.updateLabel) + " - " + _arrayList.get(_position).getList_name(), "", ListingActivity.this.getResources().getString(R.string.updateLabel), userProfile, _arrayList.get(_position));
                }
            });

            listingPaginationAdapter.setOnRetryCallBackListener(() -> {
                if (null != mPresenter) {
                    PresenterKit.successResult(PresenterKit.obtainMessage(ListingActivity.this), PresenterKit.NEXT_LISTING);
                }
            });

            if (null != mPresenter) {
                PresenterKit.successObjResult(PresenterKit.obtainMessage(ListingActivity.this), listing, PresenterKit.FIRST_LISTING);
            }
        } else {
            if(null != listing){
                if(listing.getListingItem().size() > 0){
                    listingPaginationAdapter.addListingItems(listing.getListingItem());
                }
            }
        }
    }

    private void addFooter(int _messageType) {
        listingPaginationAdapter.addLoadingFooter(_messageType);
    }

    private void noItem() {
        if (listingPaginationAdapter.getItemCount() == 0) {
            // isLastPage = true;
            tvNoRecord.setVisibility(View.VISIBLE);
            tvNoRecord.setText(ListingActivity.this.getResources().getString(R.string.noRecordLabel));
            // swipeContainer.setVisibility(View.GONE);
            rvListing.setVisibility(View.GONE);
        }
    }

    private void removeFooter(int _messageType) {
        listingPaginationAdapter.removeLoadingFooter(_messageType);
    }

    private void restartPage() {
        isLoading = false;
        isLastPage = false;
        TOTAL_PAGES = 500;
    }

    public static void customAlertDialog(ListingPresenter mPresenter, Message msg, final Activity _activity, String _title, String _message, String _btn, UserProfile userProfile, ListingItem _listingItem) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(_activity);
        final View dialogView = View.inflate(_activity, R.layout.custom_dialog, null);
        alertDialog.setView(dialogView);

        final AlertDialog ad = alertDialog.create();

        final TextView tvDialogMainTitle = dialogView.findViewById(R.id.tvDialogMainTitle);
        tvDialogMainTitle.setText(_title);

        final ImageView ivClose = dialogView.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(view -> ad.dismiss());

        final EditText etPlace = dialogView.findViewById(R.id.etPlace);
        etPlace.setText(_listingItem.getList_name());

        final EditText etDistance = dialogView.findViewById(R.id.etDistance);
        etDistance.setText(String.valueOf(_listingItem.getDistance()));

        final Button btnDialog = dialogView.findViewById(R.id.btnDialog);
        btnDialog.setText(_btn);
        btnDialog.setOnClickListener(v -> {
            if(null != mPresenter && null != userProfile){
                mPresenter.callUpdate(msg, userProfile, _listingItem.getId(), etPlace.getText().toString(), etDistance.getText().toString());
            }
            ad.dismiss();
        });

        ad.setCancelable(false);
        ad.show();
    }

    @Optional
    @OnClick({R.id.tvLogout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvLogout:
                promptAlertDialog(ListingActivity.this, ListingActivity.this.getResources().getString(R.string.noticeLabel), ListingActivity.this.getResources().getString(R.string.logoutMessage), ListingActivity.this.getResources().getString(R.string.yesLabel), ListingActivity.this.getResources().getString(R.string.noLabel));
                break;
            case 0:
                break;
            default:
                break;
        }
    }

    private void promptAlertDialog(Activity _activity, String _title, String _message, String _positiveButton, String _negativeButton) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(_activity);
        alertDialog.setTitle(_title).setMessage(_message).setNegativeButton(_negativeButton, (dialog, which) -> dialog.dismiss()).setPositiveButton(_positiveButton, (dialog, which) -> {
            PrefUtils.getInstance(ListingActivity.this.getApplication()).logout();
            Utils.intentAct(_activity, LoginActivity.class);
            dialog.dismiss();
        }).setCancelable(false).show();
    }
}
