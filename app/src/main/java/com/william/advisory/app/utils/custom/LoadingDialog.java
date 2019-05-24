package com.william.advisory.app.utils.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.william.advisory.R;


/**
 * Created by JF on 2018/1/30.
 */

public class LoadingDialog {

    private LVCircularRing mLoadingView;
    private Dialog mLoadingDialog;
    private TextView loadingText;

    public LoadingDialog(Context context, String msg) {
        loadingDialog(context, msg, false);
    }

    public LoadingDialog(Context context, String msg, boolean cancelable) {
        loadingDialog(context, msg, cancelable);
    }

    private void loadingDialog(Context context, String msg, boolean cancelable) {
        // 首先得到整个View
        View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog_view, null);
        // 获取整个布局
        LinearLayout layout = view.findViewById(R.id.dialog_view);
//        layout.getBackground().setAlpha(200);
        // 页面中的LoadingView
        mLoadingView = view.findViewById(R.id.lv_circularring);
        // 页面中显示文本
        loadingText = view.findViewById(R.id.loading_text);
        // 显示文本
        loadingText.setText(msg);
        // 创建自定义样式的Dialog
        mLoadingDialog = new Dialog(context, R.style.loading_dialog);
        // 设置返回键无效
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void show(String msg) {
        if (loadingText != null) {
            loadingText.setText(msg);
        }
        mLoadingDialog.show();
        mLoadingView.startAnim();
    }

    public void show() {
        mLoadingDialog.show();
        mLoadingView.startAnim();
    }

    public void close() {
        if (mLoadingDialog != null) {
            mLoadingView.stopAnim();
            mLoadingDialog.dismiss();
        }
    }

}
