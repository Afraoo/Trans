//package com.transport.transpotation;
//
//import android.os.Message;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//
//import java.util.HashMap;
//import java.util.logging.Handler;
//
//import NetTools.HttpCallbackListener;
//import NetTools.HttpUtils;
//
///**
// * Created by moloop on 2017/8/7.
// */
//
//public class toSeperate extends Handler{
//    private boolean seperateState;
//
//    Handler mHandler = new Handler(){//处理从服务器获得的车队编号
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            String result = "";
//            if ("OK".equals(msg.obj.toString())) {
//                result = "success";
//            } else if ("Wrong".equals(msg.obj.toString())) {
//                result = "fail";
//            } else {
//                result = msg.obj.toString();
//            }
////            Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
//            JSONArray json = null;
//            try {
////               json = (JSONArray) new JSONTokener(msg.obj.toString()).nextValue();
//                json = new JSONArray(msg.obj.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        };
//    public void sendSeperate (){
//        String originAddress = "http://192.168.1.203/TServer/GetTeamNum";
//        HashMap<String, String> params = new HashMap<String, String>();
//        try {
//            String compeletedURL = HttpUtils.getURLWithParams(originAddress, params);
//            HttpUtils.sendHttpRequest(compeletedURL, new HttpCallbackListener() {
//                @Override
//                public void onFinish(String response) {
//                    Message message = new Message();
//                    message.obj = response;
//                    mHandler.sendMessage(message);
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    Message message = new Message();
//                    message.obj = e.toString();
//                    mHandler.sendMessage(message);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
