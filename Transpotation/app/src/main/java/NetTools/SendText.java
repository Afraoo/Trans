package NetTools;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;


/**
 * Created by moloop on 2017/8/20.
 */

public class SendText extends Activity{

    private String carNumber;
    private String speed;
    private String distance;
    private String angle;
    private String direction;
    private String result;
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    Handler handler = new Handler(){//处理从服务器获得的
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

    public void sendText(String teamNumber, String carNumber, String speed, String distance, String angle, String direction) {
        String originAddress = "http://192.168.3.25:8080/TServer/getText";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("teamNumber", teamNumber);
        params.put("carNumber", carNumber);
        params.put("speed", speed);
        params.put("distance", distance);
        params.put("angle", angle);
        params.put("direction", direction);
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

    private String teamNumber;

    public String getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(String teamNumber) {
        this.teamNumber = teamNumber;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }


}
