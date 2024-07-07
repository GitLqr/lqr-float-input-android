package com.gitlqr.float_input;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.gitlqr.float_input.softinput.OnSoftInputListener;
import com.gitlqr.float_input.softinput.SoftInputObserver;
import com.gitlqr.float_input.utils.SoftInputUtil;

/**
 * 基于 Dialog 实现的悬浮输入框
 *
 * @author LQR
 * @since 2024/7/6
 */
public abstract class AbsFloatInputDialog extends Dialog implements IFloatInput, DialogInterface.OnShowListener, DialogInterface.OnDismissListener, OnSoftInputListener {

    protected View rootView;
    protected SoftInputObserver softInputObserver = new SoftInputObserver();

    public AbsFloatInputDialog(Context context) {
        this(context, 0);
    }

    public AbsFloatInputDialog(Context context, int themeResId) {
        super(context, themeResId);
        setOnShowListener(this);
        setOnDismissListener(this);
    }

    public abstract int getLayoutId();

    public abstract EditText getInputView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate");
        super.onCreate(savedInstanceState);
        rootView = initView();
        setupInputView();
        initWindow();
    }

    @Override
    protected void onStart() {
        log("onStart");
        super.onStart();
        softInputObserver.registerListener(this);
        softInputObserver.onStart(rootView);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        log("onShow");
        softInputObserver.onShow();
        EditText inputView = getInputView();
        if (inputView != null) {
            inputView.postDelayed(() -> {
                // 让输入框获取焦点
                inputView.requestFocus();
                // 显示输入法
                SoftInputUtil.getInstance().show(inputView);
            }, 1000);
        }
    }

    @Override
    protected void onStop() {
        log("onStop");
        super.onStop();
        softInputObserver.onStop();
        softInputObserver.unregisterListener(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        log("onDismiss");
        softInputObserver.destroy();
    }

    protected View initView() {
        // 自定义布局
        View rootView = View.inflate(getContext(), getLayoutId(), null);
        setContentView(rootView);
        // 因为 window 高度设为 MATCH_PARENT，所以，需要对根布局监听点击事件，用于隐藏弹窗
        rootView.setOnClickListener(v -> {
            EditText inputView = getInputView();
            if (inputView != null) {
                SoftInputUtil.getInstance().hide(inputView);
            }
        });
        return rootView;
    }

    protected void setupInputView() {
        // 强制 输入框 横屏时 不要全屏
        EditText inputView = getInputView();
        if (inputView != null) {
            // IME_FLAG_NO_EXTRACT_UI：使软键盘不全屏显示，只占用一部分屏幕
            inputView.setImeOptions(inputView.getImeOptions() | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        }
    }

    /**
     * stateUnspecified：软键盘的状态并没有指定，系统将选择一个合适的状态或依赖于主题的设置
     * stateUnchanged：当这个activity出现时，软键盘将一直保持在上一个activity里的状态，无论是隐藏还是显示
     * stateHidden：用户选择activity时，软键盘总是被隐藏
     * stateAlwaysHidden：当该Activity主窗口获取焦点时，软键盘也总是被隐藏的
     * stateVisible：软键盘通常是可见的
     * stateAlwaysVisible：用户选择activity时，软键盘总是显示的状态
     * <p>
     * adjustUnspecified：默认设置，通常由系统自行决定是隐藏还是显示
     * adjustResize：该Activity总是调整屏幕的大小以便留出软键盘的空间
     * adjustPan：当前窗口的内容将自动移动以便当前焦点从不被键盘覆盖和用户能总是看到输入内容的部分
     */
    protected void initWindow() {
        Window window = getWindow();
        // 输入法显示模式
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // 背景透明
        window.setBackgroundDrawableResource(android.R.color.transparent);
        // 去除黑色半透明遮罩
        window.setDimAmount(0);
        // 清除 Dialog 底部背景模糊和黑暗度
        window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND | WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // 全屏
        // 注意：窗口高度必须是 MATCH_PARENT，否则 ViewTreeObserver 的 OnGlobalLayoutListener 在输入法隐藏时（个别设备上认为窗口尺寸没变），可能不会回调
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        // 屏幕底部显示
        window.setGravity(Gravity.BOTTOM);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onSoftInputVisibleChange(boolean isVisible) {
        if (isVisible) {
            // 输入法显示
        } else {
            // 输入法隐藏
            dismiss();
        }
    }

    private void log(String msg) {
        Log.i("LQR_AbsFID", msg);
    }
}
