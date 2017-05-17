package tcp;

/**
 * Created by Administrator on 2017/5/16.
 */
public interface TCPResultListener {

    void onResult(byte[] result);

    void onSendFailed(byte[] result,int code);

    void onConnectFailed(String message);

    void onDisconnect();
}
