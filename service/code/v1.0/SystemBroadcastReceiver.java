package com.attraxus.service;

import com.android.badge.BadgeCountManager;
import com.android.device.Device;
import com.attraxus.stock.IO;
import com.attraxus.stock.MainActivity;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
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
			if(User.id() != null)
			{
				ServiceManager.start(TAG,context, CoreService.class, CoreService.INTENT_ACTION_RESTART);
				ServiceManager.start(TAG,context, PushService.class, PushService.INTENT_ACTION_RESTART);
				IO.LOG(TAG,"onReceive",action);
			}
		}
		else if(action.equals(Intent.ACTION_PACKAGE_REMOVED))
		{
			// 當此應用被刪除,註銷廣播
			if (Device.isServiceRunning(TAG,context,SystemBroadcastReceiver.class))
			{
				context.unregisterReceiver(this);
				IO.LOG(TAG,"onReceive",action);
			}
		}
		else if (action.equals("clicked")) {
			// 處理通知對話框點擊事件
			// 點擊後開啟app
			Intent startAppIntent = new Intent(context, MainActivity.class);
			startAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(startAppIntent);
			BadgeCountManager.subtractNumber();
		} 
		else if (action.equals("cancelled")) {
			// 處理通知對話框滑動清除和點擊系統删除按鈕事件
			BadgeCountManager.resetCount(context);
		}
	}
}
