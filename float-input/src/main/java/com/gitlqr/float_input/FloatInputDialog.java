package com.gitlqr.float_input;

import android.content.Context;
import android.widget.EditText;

/**
 * 默认的悬浮输入框实现
 *
 * @author LQR
 * @since 2024/7/7
 */
public class FloatInputDialog extends AbsFloatInputDialog {

    private EditText inputView;

    public FloatInputDialog(Context context) {
        super(context);
    }

    public FloatInputDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_float_input;
    }

    @Override
    public EditText getInputView() {
        if (inputView == null && rootView != null) {
            inputView = rootView.findViewById(R.id.etInput);
        }
        return inputView;
    }
}
