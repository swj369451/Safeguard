package com.example.safeguard.service;


import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;


public class SocketThread extends Thread {

    boolean runFlag = true;
    private Socket socket;
    private Callback callback;
    private InputStream ip;
    private OutputStream op;
    private String charsetName;
    private String alias;
    private String TAG = this.getClass().getSimpleName();

    /**
     * 构造函数
     *
     * @param socket   socket
     * @param callback 回调函数
     */
    public SocketThread(Socket socket, Callback callback) {
        this(socket, "utf-8", callback);
    }

    public SocketThread(Socket socket, String charsetName, Callback callback) {
        if (null == socket) {
            runFlag = false;
            return;
        }
        this.alias = socket.getPort() + "";
        this.socket = socket;
        this.callback = callback;
        this.charsetName = charsetName;
    }

    /**
     * 线程运行
     */
    @Override
    public void run() {
        if (null == socket) {
            Log.e(TAG, "run: socket is null");
            runFlag = false;
            return;
        }
        try {
            ip = socket.getInputStream();
            op = socket.getOutputStream();

            if (callback != null) {
                callback.onInit(this);
            }
            Log.i(TAG, "run: " + String.format("socket【port=%d】初始化完成", socket.getPort()));
            while (runFlag) {
                if (socket.isClosed()) {
                    exit();
                    return;
//                    continue;
                }
                try {
                    byte[] bytes = new byte[1024];
                    int len;
                    len = ip.read(bytes);
                    if (len == -1) {
                        exit();
                        return;
                    }
                    String message = new String(bytes, 0, len, charsetName);

                    Log.i(TAG, "run: socket receive data" + message);
                    if (callback != null && len > 0) {
                        callback.onMessage(message, this);
                    }

                } catch (SocketException e) {
                    Log.e(TAG, "run: " + e.getMessage());
                    exit();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送socket消息
     *
     * @param message 消息
     */
    public boolean sendMessage(String message) {

        Log.i(TAG, "sendMessage: " + "socket发送消息" + message);
        if (null != socket && runFlag) {
            try {
                op.write(message.getBytes(charsetName));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "sendMessage: " + e.getMessage());
            }
        }
        return true;
    }

    /**
     * 断开socket连接
     */
    public void exit() {


        runFlag = false;
        try {
            ip.close();
            op.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (callback != null) {
            callback.onClose(alias, this);
        }
        Log.w(TAG, "exit: " + String.format("socket【port=%d】已断开连接", socket.getPort()));
    }

    /**
     * 判断socket是否保持连接
     *
     * @param socket
     * @return
     */
    public Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    //回调类
    public interface Callback {
        default void onInit(SocketThread socketThread) {
        }

        default void onMessage(String message, SocketThread socketThread) {
        }

        default void onClose(String alias, SocketThread socketThread) {
        }
    }
}