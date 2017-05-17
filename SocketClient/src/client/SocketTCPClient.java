package client;

import tcp.TCPResultListener;
import tcp.TCPClient;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 * Created by zhenweiyu on 2017/5/17.
 */
public class SocketTCPClient {

    public static void main(String[]args){

        TCPClient tcpClient = new TCPClient("myTcpClient", new TCPResultListener() {
            @Override
            public void onResult(byte[] result) {
                try {
                    System.out.println(new String(result,"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSendFailed(byte[] result, int code) {
                try {
                    System.out.println(String.format("content:%s send failed",new String(result,"utf-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnectFailed(String message) {
                  System.out.println(String.format("connect failed:%s",message));
            }

            @Override
            public void onDisconnect() {
                System.out.print("current socket has disconnected");
            }
        });
        tcpClient.start();
        boolean isEnd = false;
        while (!isEnd){
            System.out.println("-----input your choice------");
            System.out.println("1.connect");
            System.out.println("2.send Message");
            System.out.println("3.disconnect");
            System.out.println("4.end");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            switch (choice){
                case 1:
                    System.out.print("input ip:");
                    String ip = scanner.next();
                    System.out.print("input port:");
                    int port = scanner.nextInt();
                    System.out.println("try connecting....");
                    tcpClient.connect(ip,port);
                    break;
                case 2:
                    System.out.print("input msg you want to send:");
                    String msg = scanner.next();
                    try {
                        tcpClient.send(msg.getBytes("utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    tcpClient.disconnect();
                    break;
                case 4:
                    tcpClient.kill();
                    isEnd = true;
                    break;
            }
        }





    }

}
