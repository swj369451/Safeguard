package com.example.safeguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.safeguard.AlarmManager.MyAlarmManager;
import com.example.safeguard.broadcast.AlarmReceiver;
import com.example.safeguard.service.CommandService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Permissions permission;

    private Button button;
    private Intent commandServiceIntent;


    private void init() {
                MyAlarmManager.alarm(this);

        Toast.makeText(this, "safeguard,成功启动了！", Toast.LENGTH_LONG).show();
        Log.e("MainActivity", "safeguard,成功启动了！");

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(commandServiceIntent);
            }
        });


        //保存busybox
        InputStream inputStream = getResources().openRawResource(R.raw.busybox);
        String destinationPath = Environment.getExternalStorageDirectory() + File.separator + "busybox";
        storageFile(inputStream, destinationPath);


        commandServiceIntent = new Intent(this, CommandService.class);
        startService(commandServiceIntent);
    }
    /**
     * 将流的输出，存储到指定文件中
     *
     * @param inputStream
     * @param destinationPath
     */
    private void storageFile(InputStream inputStream, String destinationPath) {
        OutputStream output = null;
        try {
            //判断是否已经保存
            File file = new File(destinationPath);
            if (file.exists()) {
                return;
            }

            //读取数据流，存储到文件中
            output = new FileOutputStream(destinationPath);
            byte[] bytes = new byte[1024];
            int n;
            while ((n = inputStream.read(bytes)) != -1) {
                output.write(bytes, 0, n);
            }

            //释放对象
            output.flush();
            if (output != null) {
                output.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //检查权限
        permission = new Permissions(this, this, this::init);
        permission.requestPermission();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}