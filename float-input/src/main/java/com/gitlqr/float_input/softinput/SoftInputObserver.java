package com.gitlqr.float_input.softinput;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.HashSet;
import java.util.Set;

/**
 * 输入法观察者
 *
 * @author LQR
 * @since 2024/7/7
 */
public class SoftInputObserver extends Handler implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final int WHAT_ENSURE_GLOBAL_LAYOUT_COMPLETE = 0x0001;
    private static final int WHAT_THROTTLE_GLOBAL_LAYOUT_COMPLETE = 0x0002;

    private View rootView;
    private int preOrientation = 0;
    private int orientation = 0;
    private int verticalOriHeight = 0;
    private int horizontalOriHeight = 0;
    private int lastWindowHeight = 0;
    private boolean isSoftInputShow = false;

    private final Set<OnSoftInputListener> listeners = new HashSet<>();

    public void registerListener(OnSoftInputListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(OnSoftInputListener listener) {
        listeners.remove(listener);
    }

    public void onStart(View rootView) {
        // 取消上一个 视图树 监听
        onStop();
        // 观察最新视图树
        this.rootView = rootView;
        log("observeView addOnGlobalLayoutListener");
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * 尽可能晚，但必须在输入法显示之前调用，才能获取到窗口的原始尺寸
     */
    public void onShow() {
        // 获取当前窗口原始尺寸
        getWindowOrientationAndSize();
    }

    public void onStop() {
        if (rootView != null) {
            log("unObserveView removeOnGlobalLayoutListener");
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

    public void destroy() {
        log("destroy");
        // 取消根视图
        onStop();
        rootView = null;
        // 清除所有监听器
        listeners.clear();
        // 清除所有任务消息
        removeCallbacksAndMessages(null);
        // 重置所有窗口信息
        preOrientation = orientation;
        verticalOriHeight = 0;
        horizontalOriHeight = 0;
        lastWindowHeight = 0;
        isSoftInputShow = false;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case WHAT_ENSURE_GLOBAL_LAYOUT_COMPLETE:
                onGlobalLayout();
                break;
            case WHAT_THROTTLE_GLOBAL_LAYOUT_COMPLETE:
                onGlobalLayoutComplete();
                break;
        }
    }

    @Override
    public void onGlobalLayout() {
        if (rootView == null) return;
        orientation = getWindowOrientation(rootView);
        if (orientation != preOrientation) {
            // TODO: 2024/7/7 窗口旋转
            preOrientation = orientation;
        }

        // 输入法有 显示/隐藏 动画，都会导致窗口高度发生变化
        Rect rect = getWindowDisplayRect(rootView);
        int windowCurrentHeight = rect.height();
        int windowOriHeight = getWindowOriHeight();

        log("onGlobalLayout orientation = " + orientation + ", windowCurrentHeight = " + windowCurrentHeight + ", windowOriHeight = " + windowOriHeight);

        // 屏幕旋转 & 输入法动画 结束
        if (lastWindowHeight == windowCurrentHeight) {
            throttleGlobalLayoutComplete();
            return;
        }
        lastWindowHeight = windowCurrentHeight;
        ensureGlobalLayoutComplete();
    }

    /**
     * 确保 {@link #onGlobalLayoutComplete()} 一定会执行
     */
    private void ensureGlobalLayoutComplete() {
        log("ensureGlobalLayoutComplete");
        removeMessages(WHAT_ENSURE_GLOBAL_LAYOUT_COMPLETE);
        Message msg = Message.obtain(this, WHAT_ENSURE_GLOBAL_LAYOUT_COMPLETE);
        sendMessageDelayed(msg, 100);
    }

    /**
     * 流控 {@link #onGlobalLayoutComplete()} 执行一次
     */
    private void throttleGlobalLayoutComplete() {
        log("throttleGlobalLayoutComplete");
        removeMessages(WHAT_THROTTLE_GLOBAL_LAYOUT_COMPLETE);
        Message msg = Message.obtain(this, WHAT_THROTTLE_GLOBAL_LAYOUT_COMPLETE);
        sendMessageDelayed(msg, 100);
    }

    private void onGlobalLayoutComplete() {
        log("onGlobalLayoutComplete");
        // 此次 GlobalLayout 完成，把 handler 中那些 ensure 和 throttle 消息移除，防止回调两次
        removeMessages(WHAT_ENSURE_GLOBAL_LAYOUT_COMPLETE);
        removeMessages(WHAT_THROTTLE_GLOBAL_LAYOUT_COMPLETE);

        if (rootView == null) return;
        Rect rect = getWindowDisplayRect(rootView);
        int windowCurrentHeight = rect.height();
        int windowOriHeight = getWindowOriHeight();

        if (windowCurrentHeight < windowOriHeight) {
            // 窗口高度变小，说明输入法显示
            if (!isSoftInputShow) {
                onSoftInputShow();
            }
        } else if (windowCurrentHeight == windowOriHeight) {
            // 窗口高度 变回 原大小，不一定是 输入法隐藏，有以下几种情况：
            // 1、输入法还没有出现（隐 -> 显）
            // 2、输入法完全收起来（显 -> 隐）
            // 3、输入法显示动画过程中，点击空白处，触发隐藏输入法
            if (isSoftInputShow) {
                onSoftInputHide();
            }
        }
    }

    private void onSoftInputShow() {
        isSoftInputShow = true;
        log("onSoftInputShow");
        for (OnSoftInputListener listener : listeners) {
            listener.onSoftInputVisibleChange(isSoftInputShow);
        }
    }

    private void onSoftInputHide() {
        isSoftInputShow = false;
        log("onSoftInputHide");
        for (OnSoftInputListener listener : listeners) {
            listener.onSoftInputVisibleChange(isSoftInputShow);
        }
    }

    private void getWindowOrientationAndSize() {
        if (rootView == null) return;
        orientation = getWindowOrientation(rootView);
        preOrientation = orientation;
        log("getScreenOriAndSize, orientation = " + orientation);
        // 获取窗口原始尺寸
        Rect rect = getWindowDisplayRect(rootView);
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT: // 1
                verticalOriHeight = rect.height();
                horizontalOriHeight = rect.width();
                log("current portrait, verticalOriHeight = " + verticalOriHeight + ", horizontalOriHeight = " + horizontalOriHeight);
                break;
            case Configuration.ORIENTATION_LANDSCAPE: // 2
                horizontalOriHeight = rect.height();
                verticalOriHeight = rect.width();
                log("current landscape, verticalOriHeight = " + verticalOriHeight + ", horizontalOriHeight = " + horizontalOriHeight);
                break;
        }
    }

    private Rect getWindowDisplayRect(View rootView) {
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        return rect;
    }

    /**
     * 获取当前窗口当前方向上原始高度
     */
    private int getWindowOriHeight() {
        return orientation == Configuration.ORIENTATION_PORTRAIT ? verticalOriHeight : horizontalOriHeight;
    }

    private int getWindowOrientation(View rootView) {
        return rootView.getResources().getConfiguration().orientation;
    }

    private void log(String msg) {
        Log.i("LQR_SIO", msg);
    }

}
