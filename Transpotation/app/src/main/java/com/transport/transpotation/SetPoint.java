package com.transport.transpotation;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by moloop on 2017/8/27.
 */

public class SetPoint {

    private static final int DECIMAL_DIGITS = 1;

    public static void setPoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + DECIMAL_DIGITS+1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
