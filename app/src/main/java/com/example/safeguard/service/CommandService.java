package com.example.safeguard.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.Nullable;

public class CommandService extends Service {


    private final String TAG = this.getClass().getSimpleName();
    //获取主线程
    private Handler handler = new Handler(Looper.getMainLooper());

    // 线程池
    private ExecutorService mThreadPool = Executors.newCachedThreadPool();

    //socket配置
    private static SocketThread socketThread;
    private Socket clientSocket;
    private Boolean isRestart = true;
    private int mSocketConnectTimeOut = 5000;

//    private String mSocketServerAddress = "gcc.ppamatrix.com";
//    private int mSocketServerPort = 18085;
    private Command command;
    private String mSocketServerAddress = "192.168.8.104";
    private int mSocketServerPort = 12344;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        if(socketThread==null){
            init();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        toastMsg("CommandService启动成功");
        Log.i(TAG, "onStartCommand: CommandService启动成功");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void init() {
        if(command==null){
            command = new Command(mThreadPool, new Command.Callback() {
                @Override
                public void onMessage(String message) {
                    socketThread.sendMessage(message);
                }

                @Override
                public void onErrorMessage(String message) {
                    socketThread.sendMessage(message);
                }
            });
        }

        mThreadPool.execute(() -> {
            try {
                clientSocket = new Socket();
                clientSocket.connect(new InetSocketAddress(mSocketServerAddress, mSocketServerPort), mSocketConnectTimeOut);
                socketThread = new SocketThread(clientSocket, new SocketThread.Callback() {
                    @Override
                    public void onInit(SocketThread socketThread) {
                        System.out.println();
                    }

                    @Override
                    public void onMessage(String message, SocketThread socketThread) {
//                        if (message.startsWith("command:")) {
//                            String commandStr = message.substring(8);
//                            command.sendCommand(commandStr+"\n");
//                        }
                        command.sendCommand(message + "\n");
                    }

                    @Override
                    public void onClose(String alias, SocketThread socketThread) {
                        releaseSocket();
                        restartSocket();
                    }
                });
                socketThread.start();
            } catch (IOException e) {
                e.printStackTrace();
                if (e instanceof SocketTimeoutException) {
                    Log.i(TAG, "socket连接超时，正在重连");
                    releaseSocket();
                    restartSocket();
                    return;
                } else if (e instanceof NoRouteToHostException) {
                    Log.e("MainActivity", "该地址不存在，请检查");
                } else if (e instanceof ConnectException) {
                    Log.e("MainActivity", "连接异常或被拒绝，请检查");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    releaseSocket();
                    restartSocket();
                    return;
                }
            }
        });
    }

    /**
     * 重连socket
     */
    private void restartSocket() {
        toastMsg("socket重连");
        if (isRestart) {
            init();
        }
    }

    /**
     * 释放socket资源
     */
    private void releaseSocket() {
        //socket
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientSocket = null;
        }
        //接收线程
        if (socketThread != null) {
            socketThread = null;
        }
    }

    /*因为Toast是要运行在主线程的   所以需要到主线程哪里去显示toast*/
    private void toastMsg(String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
