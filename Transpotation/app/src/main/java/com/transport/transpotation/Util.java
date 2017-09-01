package com.transport.transpotation;

import android.content.Context;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by moloop on 2017/8/6.
 */

public class Util {
    private static Toast toast;
    private static Message message;

    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    public static String showMessage(Message msg) {
        String result = "";
        if(!msg.obj.toString().equals("") && msg.obj != null){
            if ("single success".equals(msg.obj.toString())){
                result = "单个小车操作成功！";
            }else if ("complex success".equals(msg.obj.toString())){
                result = "复数小车操作成功！";
            }else if("successfully stop".equals(msg.obj.toString())) {
                result = "急停成功！";
            }else {
                result = msg.obj.toString();
            }
        }

        return result;
    }
}
