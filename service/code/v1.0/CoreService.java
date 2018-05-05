package com.attraxus.service;

import android.annotation.SuppressLint;
import android.content.Intent;

/**
 * 繼承 Daemon 保護機制
 * 綁定PushService為守護服務,如果服務終止則PushService將負責重新啟動任務
 * START_STICKY:如果服務在onStartCommand後異常終止,
 * 則系統重新啟動,重啟後intent為null
 */
public class CoreService extends Daemon {

	@Override
	public void onCreate() {
		className = CoreService.class.getSimpleName();
		setClassName(className);
		setBindService(PushService.class);
		super.onCreate();
	}

	@SuppressLint("InlinedApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
}
