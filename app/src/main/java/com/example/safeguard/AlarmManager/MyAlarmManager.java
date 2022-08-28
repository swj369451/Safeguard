package com.example.safeguard.AlarmManager;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.safeguard.broadcast.AlarmReceiver;
import com.example.safeguard.service.CommandService;

import java.util.List;

public class MyAlarmManager {
    public static void alarm(Context context) {
        /* Setting the alarm here */
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);




        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = 2000;
        manager.set(AlarmManager.RTC_WAKEUP, interval, pendingIntent);
        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
    }


}
