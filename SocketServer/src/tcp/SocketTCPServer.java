package tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Created by zhenweiyu on 2017/5/17.
 */
public class SocketTCPServer {

    public static void main(String []args){
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println(String.format("socket tcp has started(ip:%s,port:%d)",inetAddress.getHostAddress(),8080));
            Thread listenThread = new SocketListenThread(serverSocket);
            listenThread.start();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }







}
