package tcp;

/**
 * Created by Administrator on 2017/5/16.
 */
public interface TCPEvent {

    void start();

    void send(byte[] bytes);

    void connect(String ip,int port);

    void disconnect();

    void kill();

}
