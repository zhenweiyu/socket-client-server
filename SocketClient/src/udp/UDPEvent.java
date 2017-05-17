package udp;

/**
 * Created by zhenweiyu on 2017/5/17.
 */
public interface UDPEvent {

    void initDestination(String ip,int port);

    void send(byte[] bytes);

    void release();
}
