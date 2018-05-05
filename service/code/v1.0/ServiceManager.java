package com.attraxus.service;

import com.android.device.Device;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;

/*
 * 背景服務管理
 * 
 */

public class ServiceManager {
	/*
	 * 啟動系統廣播監聽
	 */
	static public void startSystemBroadcastReceiver(Context context)
	{
		IntentFilter filter = new IntentFilter();
		context.registerReceiver(new SystemBroadcastReceiver(), filter);
	}
	/*
	 * 啟動背景服務,如果該服務尚未執行則執行服務,
	 * 如果服務已執行則不做任何動作
	 */
	static public void start(String tag,Context context, Class<?> serviceClass,String action) {
		Intent intent = new Intent(context,serviceClass);
		intent.setAction(action);
		if (!Device.isServiceRunning(tag,context,serviceClass))
			context.startService(intent);
	}
	
	/*
	 * 停止背景服務,如果該服務尚未執行則不做任何動作,
	 * 如果服務執行中則停止服務
	 */
	static public void stop(String tag,Context context, Class<?> serviceClass) {
		Intent intent = new Intent(context,serviceClass);
		if (Device.isServiceRunning(tag,context,serviceClass))
			context.stopService(intent);
	}
	
	//設定前台不顯示
	static public void setForegroundDoNotShow(Service service)
	{
		if (VERSION.SDK_INT < 18) {  
			short PID = 0;
			service.startForeground(PID, null);  
        } 
	}
}
