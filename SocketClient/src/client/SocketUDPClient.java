package client;

import udp.UDPClient;

import java.io.UnsupportedEncodingException;

/**
 * Created by zhenweiyu on 2017/5/17.
 */
public class SocketUDPClient {

    public static void main(String[]args){

        UDPClient udpClient = new UDPClient(5050);
        udpClient.initDestination("127.0.0.1",8080);
        for(int i =0;i<20;i++){
            String msg = new String("current pack is "+i);
            try {
                udpClient.send(msg.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }





    }

}
