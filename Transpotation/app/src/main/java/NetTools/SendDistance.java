package NetTools;

import android.os.Handler;
import android.os.Message;

import java.util.HashMap;

/**
 * Created by moloop on 2017/8/25.
 */

public class SendDistance {

    private String result="";
    Handler handler = new Handler(){
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("OK".equals(msg.obj.toString())){
                result = "success";
            }else if ("Wrong".equals(msg.obj.toString())){
                result = "fail";
            }else {
                result = msg.obj.toString();
            }

        }
    };
    public void sendDistance(String teamNumber, String speed, String distance){
        String originAddress = "http://192.168.3.25:8080/TServer/getDistance";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("teamNumber", teamNumber);
        params.put("speed", speed);
        params.put("distance", distance);
        try {
            String compeletedURL = HttpUtils.getURLWithParams(originAddress, params);
            HttpUtils.sendHttpRequest(compeletedURL, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Message message = new Message();
                    message.obj = response;
                    handler.sendMessage(message);
                    setResult(message.obj.toString());
                }

                @Override
                public void onError(Exception e) {
                    Message message = new Message();
                    message.obj = e.toString();
                    handler.sendMessage(message);
                    setResult(message.obj.toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
