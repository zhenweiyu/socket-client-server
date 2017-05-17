package tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhenweiyu on 2017/5/17.
 */
class SocketListenThread extends Thread{

    private ServerSocket serverSocket;
    private ExecutorService ioExecutorService;
    private AtomicBoolean mExit = new AtomicBoolean(false);
    public SocketListenThread(ServerSocket serverSocket){
        super();
        this.serverSocket = serverSocket;
        this.ioExecutorService = Executors.newFixedThreadPool(20);
    }

    @Override
    public void run() {
        while (!mExit.get()){
            try {
                Socket socket = null;
                System.out.println("waiting.....");
                socket = serverSocket.accept();
                System.out.println(String.format("%s (%s) has connected",socket.getInetAddress().getHostAddress(),socket.toString()));
                IORunnable ioRunnable = new IORunnable(socket,socket.toString());
                ioExecutorService.execute(ioRunnable);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void stopListen(){
        mExit.set(true);
    }

    class IORunnable implements Runnable{

        private Socket socket;
        private DataInputStream ioInput;
        private DataOutputStream ioOutput;
        private String name;
        private AtomicBoolean mStop = new AtomicBoolean(false);

        public IORunnable(Socket socket,String name){
            this.socket = socket;
            this.name = name;
            try {
                this.ioInput = new DataInputStream(socket.getInputStream());
                this.ioOutput = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        @Override
        public void run() {
            String response = String.format("tcp response msg: hello %s!",name);
            try {
                ioOutput.write(response.getBytes("utf-8"));
            } catch (IOException e) {
                stop();
            }
            while (!mStop.get()){
                try {

                    int length = ioInput.available();
                    if (length<=0){
                       continue;
                    }
                    byte [] bytes = new byte[length];
                    ioInput.readFully(bytes);
                    System.out.println(String.format("Server accept msg from %s:%s",name,new String(bytes,"utf-8")));

                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        ioInput.close();
                        ioOutput.close();
                        mStop.set(true);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        }

        public void stop(){
            mStop.set(true);
            try {
                socket.close();
                System.out.println(String.format("%s has disconnected",name));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
