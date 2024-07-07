package com.gitlqr.float_input.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 输入法工具类
 *
 * @author LQR
 * @since 2024/7/6
 */
public class SoftInputUtil {

    private final static class Holder {
        private final static SoftInputUtil INSTANCE = new SoftInputUtil();
    }

    public static SoftInputUtil getInstance() {
        return Holder.INSTANCE;
    }

    private InputMethodManager imm;

    private SoftInputUtil() {
    }

    public void show(EditText editText) {
        show(editText, 0);
    }

    public void show(EditText editText, int flags) {
        getInputMethodManager(editText.getContext()).showSoftInput(editText, flags);
    }

    public void hide(EditText editText) {
        hide(editText, 0);
    }

    public void hide(EditText editText, int flags) {
        getInputMethodManager(editText.getContext()).hideSoftInputFromWindow(editText.getWindowToken(), flags);
    }

    private InputMethodManager getInputMethodManager(Context context) {
        if (imm == null) {
            imm = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        return imm;
    }

}
