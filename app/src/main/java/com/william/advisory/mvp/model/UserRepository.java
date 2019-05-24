package com.william.advisory.mvp.model;

import com.william.advisory.mvp.model.api.Services;
import com.william.advisory.mvp.model.entity.UserProfile;
import com.william.advisory.mvp.model.entity.list.Listing;

import io.reactivex.Observable;
import me.jessyan.art.mvp.IModel;
import me.jessyan.art.mvp.IRepositoryManager;
import retrofit2.Response;

/**
 * ================================================
 * 必须实现 IModel
 * 可以根据不同的业务逻辑划分多个 Repository 类, 多个业务逻辑相近的页面可以使用同一个 Repository 类
 * 无需每个页面都创建一个独立的 Repository
 * 通过 {@link me.jessyan.art.mvp.IRepositoryManager#createRepository(java.lang.Class)} 获得的 Repository 实例, 为单例对象
 * <p>
 * Created by MVPArtTemplate on 05/23/2019 15:27
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArt">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
public class UserRepository implements IModel {
    private IRepositoryManager mManager;

    public UserRepository(IRepositoryManager manager) {
        this.mManager = manager;
    }

    public Observable<Response<UserProfile>> login(String _email, String _password) {
        return mManager.createRetrofitService(Services.class).login(_email, _password);
    }

    public Observable<Response<Listing>> listing(int _id, String _token) {
        return mManager.createRetrofitService(Services.class).listing(_id, _token);
    }

    public Observable<Response<UserProfile>> update(int _id, String _token, int _listingID, String _listingName, Double _listingDistance) {
        return mManager.createRetrofitService(Services.class).update(_id, _token, _listingID, _listingName, _listingDistance);
    }

    @Override
    public void onDestroy() {

    }
}
