package com.example.safeguard.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * 自动检测与维护服务
 * 1.摄像头
 * 1.1. 检测摄像头是否存在，是否存在其他应用占用情况，如果有则关闭其应用
 * 2.
 */
public class AutoDetectionAndMaintenance extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
