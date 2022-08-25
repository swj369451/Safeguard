package com.example.safeguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.safeguard.AlarmManager.MyAlarmManager;
import com.example.safeguard.broadcast.AlarmReceiver;
import com.example.safeguard.service.CommandService;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Permissions permission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //将busybox放到指定文件夹
        InputStream inputStream = getResources().openRawResource(R.raw.busybox);

        Toast.makeText(this, "safeguard,成功启动了！", Toast.LENGTH_LONG).show();
        Log.e("MainActivity", "safeguard,成功启动了！");

        //检查权限
        permission = new Permissions(this, this, this::init);
        permission.requestPermission();



    }

    private void init() {
        Intent intent = new Intent(this, CommandService.class);
        startService(intent);


        MyAlarmManager.alarm(this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}