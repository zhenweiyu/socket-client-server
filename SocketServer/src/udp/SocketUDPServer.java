package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by zhenweiyu on 2017/5/17.
 */
public class SocketUDPServer {

    public static void main(String[]args){

        try {
            DatagramSocket serverSocket = new DatagramSocket(8080);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
                        try {
                            serverSocket.receive(receivePacket);
                            String receiveStr = new String(receivePacket.getData(),"utf-8");
                            System.out.println("receive:"+receiveStr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (SocketException e) {
            e.printStackTrace();
        }




    }

}
