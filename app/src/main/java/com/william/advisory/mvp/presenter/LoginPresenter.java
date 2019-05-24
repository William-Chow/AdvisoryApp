package com.william.advisory.mvp.presenter;

import android.app.Activity;
import android.app.Application;

import com.william.advisory.app.utils.PrefUtils;
import com.william.advisory.app.utils.PresenterKit;
import com.william.advisory.mvp.model.UserRepository;
import com.william.advisory.mvp.model.entity.UserProfile;

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
 * Created by MVPArtTemplate on 05/23/2019 15:27
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArt">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
public class LoginPresenter extends BasePresenter<UserRepository> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private Activity activity;

    public LoginPresenter(AppComponent appComponent, Application _mApplication, Activity _activity) {
        super(appComponent.repositoryManager().createRepository(UserRepository.class));
        this.mErrorHandler = appComponent.rxErrorHandler();
        this.mApplication = _mApplication;
        this.activity = _activity;
    }

    public void inputChecking(Message msg, String _email, String _password) {
        IView view = PresenterKit.viewGetTarget(msg);
        mModel.login(_email, _password)
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
                            UserProfile userProfile = userProfileResponse.body();
                            if (null != userProfile) {
                                if (null != userProfile.getStatus()) {
                                    if (userProfile.getStatus().getCode() == PresenterKit.SUCCESS) {
                                        PrefUtils.storeUserProfile(mApplication, userProfile);
                                        showMessage(msg, userProfile);
                                        PresenterKit.successResult(msg, PresenterKit.LOGIN);
                                    } else {
                                        showMessage(msg, userProfile);
                                    }
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

    private void showMessage(Message msg, UserProfile userProfile){
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