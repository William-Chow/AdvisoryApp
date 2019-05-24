package com.william.advisory.mvp.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.william.advisory.R;
import com.william.advisory.app.utils.PresenterKit;
import com.william.advisory.app.utils.Utils;
import com.william.advisory.app.utils.custom.LoadingDialog;
import com.william.advisory.mvp.presenter.LoginPresenter;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
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
 * Created by MVPArtTemplate on 05/23/2019 15:27
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArt">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
public class LoginActivity extends BaseActivity<LoginPresenter> implements IView {

    @BindView(R.id.etEmail)
    EditText etEmail;

    @BindView(R.id.etPassword)
    EditText etPassword;

    LoadingDialog dialog;

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_login; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (null != Utils.getUserProfile(LoginActivity.this.getApplication())) {
            Utils.intentAct(LoginActivity.this, ListingActivity.class);
        }
    }

    @Override
    @Nullable
    public LoginPresenter obtainPresenter() {
        return new LoginPresenter(ArtUtils.obtainAppComponentFromContext(this), LoginActivity.this.getApplication(), LoginActivity.this);
    }

    @Override
    public void showLoading() {
        dialog = new LoadingDialog(LoginActivity.this, LoginActivity.this.getResources().getString(R.string.pleaseWaitLabel), true);
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
        switch (message.what) {
            case PresenterKit.LOGIN:
                Utils.intentAct(LoginActivity.this, ListingActivity.class);
                break;
            case 0:
                break;
        }
    }

    /**
     * OnCheck Listener
     *
     * @param compoundButton The compoundButton
     * @param isChecked      The isChecked
     */
    @OnCheckedChanged(R.id.cbShowPassword)
    public void onChecked(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.cbShowPassword:
                if (!isChecked) {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;
            case 0:
                break;
            default:
                break;
        }
    }

    @Optional
    @OnClick({R.id.llLogin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llLogin:
                if (null != mPresenter) {
                    mPresenter.inputChecking(PresenterKit.obtainMessage(LoginActivity.this), etEmail.getText().toString(), etPassword.getText().toString());
                }
                break;
            case 0:
                break;
            default:
                break;
        }
    }
}
