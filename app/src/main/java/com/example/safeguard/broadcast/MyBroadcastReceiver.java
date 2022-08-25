package com.example.safeguard.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import com.example.safeguard.AlarmManager.MyAlarmManager;
import com.example.safeguard.MainActivity;
import com.example.safeguard.service.CommandService;


public class MyBroadcastReceiver extends BroadcastReceiver {
    private String TAG = this.getClass().getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //此处及是重启的之后，打开我们app的方法
        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            Log.e(TAG, "自重启");
            Toast.makeText(context, "自重启", Toast.LENGTH_SHORT).show();
            intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //自启动APP（Activity）
            context.startActivity(intent);

            //自启命令服务
            Intent CommandService = new Intent(context, CommandService.class);
            context.startService(CommandService);

//            /* Setting the alarm here */
//            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            int interval = 5000;
//
//            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
//
//            manager.set(AlarmManager.RTC_WAKEUP, interval, pendingIntent);
//            Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
            MyAlarmManager.alarm(context);

        }
    }
}
