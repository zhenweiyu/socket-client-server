package tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/5/16.
 */
public class TCPClient implements TCPEvent {

    private String name;

    private String ip;

    private int port = -1;

    private Socket mSocket;

    private TCPResultListener mTCPResultListener;

    private DataOutputStream mDataOutputStream;

    private DataInputStream mDataInputStream;

    private BlockingDeque<Runnable> mEventBlockDeque;

    private Thread mIOThread;

    private AtomicBoolean mExit;

    private final int SEND_FAIL = -1;

    private final long WAIT_TIME = 500;

    public TCPClient(String name, TCPResultListener mTCPResultListener){
        this.name = name;
        this.mTCPResultListener = mTCPResultListener;
        mEventBlockDeque = new LinkedBlockingDeque<>();
        mExit = new AtomicBoolean(false);
        mIOThread = new IOThread(name);
    }

    private void offerSendEvent(Runnable runnable){
        if(mEventBlockDeque!=null){
            mEventBlockDeque.offer(runnable);
        }
    }


    private boolean isConnected(){
        return mSocket!=null&&mSocket.isConnected()&&!mSocket.isClosed();
    }

    private boolean isParamRight(){
        return mSocket!=null&&ip!=null&&!ip.equals("")&&port!=-1;
    }


    @Override
    public void start() {
        mIOThread.start();
    }

    private void connectActually(){
        try {
            mSocket = new Socket(this.ip,this.port);
            mSocket.setTcpNoDelay(true);
            mSocket.setReuseAddress(true);
            mDataInputStream = new DataInputStream(mSocket.getInputStream());
            mDataOutputStream = new DataOutputStream(mSocket.getOutputStream());
        } catch (IOException e) {
            if(mTCPResultListener !=null){
                mTCPResultListener.onConnectFailed(e.getMessage());
            }

        }
    }


    @Override
    public void send(byte[] bytes) {
        offerSendEvent(new Runnable() {
            @Override
            public void run() {
                    if(!isParamRight()){
                        try {
                            throw new Exception("param error!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    if(!isConnected()){
                        disconnect();
                        connectActually();
                    }
                    try {
                        if(mDataOutputStream!=null) {
                            mDataOutputStream.write(bytes);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        disconnect();
                        if(mTCPResultListener !=null){
                            mTCPResultListener.onSendFailed(bytes,SEND_FAIL);
                        }
                    }
            }
        });
    }

    @Override
    public void connect(String ip, int port) {
        this.ip = ip;
        this.port = port;
        connectActually();
    }

    @Override
    public void disconnect() {
        mEventBlockDeque.offer(new Runnable() {
            @Override
            public void run() {
                if(mSocket!=null){
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(mDataOutputStream!=null){
                    try {
                        mDataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(mDataInputStream!=null){
                    try {
                        mDataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mEventBlockDeque.clear();
                if(mTCPResultListener !=null){
                    mTCPResultListener.onDisconnect();
                }
            }
        });

    }

    @Override
    public void kill() {
       mExit.set(true);
       mIOThread.interrupt();
       disconnect();
        try {
            mIOThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mIOThread = null;
        mEventBlockDeque.clear();
        ip = "";
        port = -1;
    }

    class IOThread extends Thread{

        public IOThread(String name){
            super(name);
        }

        @Override
        public void run() {
            while (!mExit.get()){

                try {
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(mExit.get()){
                    return;
                }
                if(mSocket==null){
                    continue;
                }
                Runnable runnable = mEventBlockDeque.poll();
                if(runnable!=null){
                    runnable.run();
                }
                try {
                    int length = mDataInputStream.available();
                    if(length<=0){
                        continue;
                    }
                    byte[] input = new byte[length];
                    mDataInputStream.readFully(input);
                    if(mTCPResultListener !=null){
                        mTCPResultListener.onResult(input);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }


}
