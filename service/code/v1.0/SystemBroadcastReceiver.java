package com.attraxus.service;

import com.android.badge.BadgeCountManager;
import com.android.device.Device;
import com.attraxus.stock.IO;
import com.attraxus.stock.MainActivity;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * 系統廣播接收相應處理
 */
public class SystemBroadcastReceiver extends BroadcastReceiver {
	
	private String TAG = SystemBroadcastReceiver.class.getSimpleName(); 
	public static final String TYPE = "type";
    @SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		/* 使用者觸發以下動作:
		 * 
		 * 裝置重新開機
		 * 連接電源
		 * 斷開電源
		 * 使用者解鎖螢幕
		 * 網路連線變化
		 * 
		 * 如果使用者id存在,則檢查服務是否存在,並嘗試重新啟動服務
		 */
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)||
			action.equals(Intent.ACTION_POWER_CONNECTED)||
			action.equals(Intent.ACTION_POWER_DISCONNECTED)||
			action.equals(Intent.ACTION_USER_PRESENT)||
			Device.networkConnected(context)) 
		{   
			if(User.id(context) != null){
				ServiceManager.startService(context);
				IO.LOG(TAG,"onReceive",action);
			}
		}
		else if(action.equals(Intent.ACTION_PACKAGE_REMOVED))
		{
			// 當此應用被刪除,註銷廣播
			if (Device.isServiceRunning(TAG,context,SystemBroadcastReceiver.class)){
				context.unregisterReceiver(this);
				IO.LOG(TAG,"onReceive",action);
			}
		}
	}
}
