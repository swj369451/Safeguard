package com.example.safeguard.broadcast;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.safeguard.AlarmManager.MyAlarmManager;
import com.example.safeguard.service.CommandService;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // For our recurring task, we'll just display a message
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();

//        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
//
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        int interval = 2000;
//        manager.set(AlarmManager.RTC_WAKEUP, interval, pendingIntent);
//        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
//
        MyAlarmManager.alarm(context);
        if (!isServiceRunning(context, "CommandService")) {
            Intent commandServiceIntent = new Intent(context, CommandService.class);
            commandServiceIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            context.startForegroundService(commandServiceIntent);
        }
    }
    /*
     * 判断服务是否启动,context上下文对象 ，className服务的name
     */

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        Log.e("OnlineService：", className);
        for (int i = 0; i < serviceList.size(); i++) {
            Log.e("serviceName：", serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().contains(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}