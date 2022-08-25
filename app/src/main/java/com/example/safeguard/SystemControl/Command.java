package com.example.safeguard.SystemControl;

import android.util.Log;

import com.example.safeguard.service.SocketThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

public class Command {

    private final String TAG = this.getClass().getSimpleName();
    private Runtime runtime;
    private Process su;
    private InputStream inputStream;
    private OutputStream outputStream;
    private InputStream errorStream;
    private final ExecutorService mThreadPool;
    private final Callback callback;

    private Boolean mThreadFlag = true;

    public Command(ExecutorService mThreadPool, Callback callback) {
        this.mThreadPool = mThreadPool;
        this.callback = callback;

        try {
            runtime = Runtime.getRuntime();
            su = runtime.exec("su\n");
            inputStream = su.getInputStream();
            outputStream = su.getOutputStream();
            errorStream = su.getErrorStream();

//            outputStream.write("exit\n".getBytes());
//            outputStream.flush();
//            int i = su.waitFor();
//            if(0==i){
//                su=Runtime.getRuntime().exec("su");
//                Log.i(TAG, "Command: 登录成功");
//            }else {
//                Log.e(TAG, "Command: 登录失败");
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        //两个读取线程
        mThreadPool.execute(() -> {
            while (mThreadFlag) {
                try {
                    byte[] bytes = new byte[1024];
                    int len = inputStream.read(bytes);
                    if (len == -1) {
                        Log.e(TAG, "Command: 断开读取流");
                        exit();
                        return;
                    }
                    String message = new String(bytes, 0, len);
                    if (callback != null) {
                        callback.onMessage(message);
                    }
                    Log.i(TAG, "Command: 接收消息" + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mThreadPool.execute(() -> {
            while (mThreadFlag) {
                try {
                    byte[] bytes = new byte[1024];
                    int len = errorStream.read(bytes);
                    if (len == -1) {
                        Log.e(TAG, "Command: 断开读取流");
                        exit();
                        return;
                    }

                    String message = new String(bytes, 0, len);
                    if (callback != null) {
                        callback.onErrorMessage(message);
                    }
                    Log.e(TAG, "Command: 接收错误消息" + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        if (callback != null) {
            callback.onInit();
        }
    }

    private void exit() {
        mThreadFlag = false;
        if (callback != null) {
            callback.onClose();
        }
    }


    //发送命令
    public void sendCommand(String command) {
        try {
            outputStream.write(command.getBytes());
            outputStream.flush();
//            Process p = runtime.exec(command);


//            InputStream errorStream = p.getErrorStream();
//            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
//            String line1;
//            while ((line1 = errorReader.readLine()) != null) {
//                System.out.println(TAG + "  " + line1);
//            }
//
//            InputStream is = p.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(TAG + "  " + line);
//            }
//            p.waitFor();
//            is.close();
//            reader.close();
//            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //回调类
    public interface Callback {
        default void onInit() {
        }

        default void onMessage(String message) {
        }

        default void onErrorMessage(String message) {
        }

        default void onClose() {
        }
    }

}
