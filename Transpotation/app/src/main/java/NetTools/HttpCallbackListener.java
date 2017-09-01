package NetTools;

/**
 * Created by moloop on 2017/7/2.
 */

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
