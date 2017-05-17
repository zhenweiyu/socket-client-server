package udp;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhenweiyu on 2017/5/17.
 */
public class UDPClient implements UDPEvent{

    private DatagramSocket mSocket;

    private LinkedBlockingQueue<DatagramPacket> mPacketQueue;

    private AtomicBoolean mStop;

    private String ip;

    private int port;

    private SendThread mSendThread;

    public UDPClient(int port){
        try {
            mSocket = new DatagramSocket(port);
            mStop = new AtomicBoolean(false);
            mPacketQueue = new LinkedBlockingQueue<>();
            mSendThread = new SendThread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initDestination(String ip, int port) {
        if(mPacketQueue!=null){
            mPacketQueue.clear();
        }
        if(mSendThread!=null){
            mSendThread.interrupt();
            mSendThread.start();
        }
        this.ip = ip;
        this.port = port;
    }


    @Override
    public void send(byte[] bytes) {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(bytes,bytes.length, InetAddress.getByName(ip),port);
            mPacketQueue.offer(datagramPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        if(mPacketQueue!=null){
            mPacketQueue.clear();
        }
        if(mSendThread!=null){
            mSendThread.interrupt();
        }
        mSendThread = null;
        if(mSocket!=null){
            mSocket.close();
        }
    }

    class SendThread extends Thread{

        public SendThread(){
            super();
        }

        @Override
        public void run() {
            while(!mStop.get()){
               DatagramPacket datagramPacket = mPacketQueue.poll();
               if(datagramPacket==null){
                   continue;
               }
                try {
                    mSocket.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                    mSocket.close();
                }
            }
        }
    }



}
