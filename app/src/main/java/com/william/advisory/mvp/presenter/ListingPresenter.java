package com.william.advisory.mvp.presenter;

import android.app.Activity;
import android.app.Application;

import com.william.advisory.app.utils.PresenterKit;
import com.william.advisory.app.utils.Utils;
import com.william.advisory.mvp.model.UserRepository;
import com.william.advisory.mvp.model.entity.UserProfile;
import com.william.advisory.mvp.model.entity.list.Listing;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.art.di.component.AppComponent;
import me.jessyan.art.mvp.BasePresenter;
import me.jessyan.art.mvp.IView;
import me.jessyan.art.mvp.Message;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import retrofit2.Response;


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
public class ListingPresenter extends BasePresenter<UserRepository> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private Activity activity;

    public ListingPresenter(AppComponent appComponent, Application _mApplication, Activity _activity) {
        super(appComponent.repositoryManager().createRepository(UserRepository.class));
        this.mErrorHandler = appComponent.rxErrorHandler();
        this.mApplication = _mApplication;
        this.activity = _activity;
    }

    public void setupListing(Message msg) {
        UserProfile userProfile = Utils.getUserProfile(mApplication);
        if (null != userProfile) {
            PresenterKit.successObjResult(msg, userProfile, PresenterKit.PROFILE);
        }
    }

    public void callListing(Message msg, UserProfile userProfile) {
        IView view = PresenterKit.viewGetTarget(msg);
        mModel.listing(userProfile.getId(), userProfile.getToken())
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> view.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(view::hideLoading)
                .subscribe(new ErrorHandleSubscriber<Response<Listing>>(mErrorHandler) {
                    @Override
                    public void onNext(Response<Listing> listingResponse) {
                        if (null != listingResponse) {
                            Listing listing = listingResponse.body();
                            if (null != listing) {
                                if (listing.getStatus().getCode() == PresenterKit.SUCCESS) {
                                    showMessage(msg, listing);
                                    PresenterKit.successObjResult(msg, listing, PresenterKit.LISTING);
                                } else {
                                    showMessage(msg, listing);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                    }
                });
    }

    private void showMessage(Message msg, Listing listing) {
        IView view = PresenterKit.viewGetTarget(msg);
        String message = listing.getStatus().getMessage();
        view.showMessage(message);
    }

    public void callUpdate(Message msg, UserProfile userProfile, int _id, String _place, String _distance){
        IView view = PresenterKit.viewGetTarget(msg);
        mModel.update(userProfile.getId(), userProfile.getToken(), _id, _place, Double.valueOf(_distance))
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> view.showLoading())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(view::hideLoading)
                .subscribe(new ErrorHandleSubscriber<Response<UserProfile>>(mErrorHandler) {
                    @Override
                    public void onNext(Response<UserProfile> userProfileResponse) {
                        if (null != userProfileResponse) {
                            UserProfile userProfileResult = userProfileResponse.body();
                            if (null != userProfileResult) {
                                if (userProfileResult.getStatus().getCode() == PresenterKit.SUCCESS) {
                                    showMessage(msg, userProfileResult);
                                    PresenterKit.successResult(msg, PresenterKit.UPDATE);
                                } else {
                                    showMessage(msg, userProfileResult);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                    }
                });
    }

    private void showMessage(Message msg, UserProfile userProfile) {
        IView view = PresenterKit.viewGetTarget(msg);
        String message = userProfile.getStatus().getMessage();
        view.showMessage(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
    }
}