package com.attraxus.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * 系統輪詢
 */

public class Polling {

	AlarmManager manager;
	PendingIntent pendingIntent;
  
	/**
	 * 重覆性輪詢
	 * 
	 * @param context
	 * @param seconds 輪詢週期(秒)
	 * @param myclass 被輪詢對象
	 * @param action 要做的動作
	 */
    public void startPollingService(Context context, int seconds , Class<?> myclass,String action) {
 
        manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
                                                                                                                                                                                                                                      
  
        Intent intent = new Intent(context, myclass);
        intent.setAction(action);
        pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                                                                                                                                                                                                      
  
        long triggerAtTime = SystemClock.elapsedRealtime();
                                                                                                                                                                                                               
        //使用AlarmManger的setRepeating(精確)方法设置定期執行時間週期
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,
        		seconds * 1000, pendingIntent);
    }
    
        /**
	 * 單一任務輪詢
	 * 
	 * @param context
	 * @param seconds 輪詢週期(秒)
	 * @param myclass 被輪詢對象
	 * @param action 要做的動作
	 */
    public void scheduleAlarms(Context context, int seconds , Class<?> cls,String action) {

    	AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
 
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        
        if(pendingIntent!=null)pendingIntent.cancel();
        	pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                                                                                                                                                                                                      
        long triggerAtTime = SystemClock.elapsedRealtime() + seconds * 1000;                     

        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
    }
    
         /**
	 * 停止輪詢
	 * 
	 * @param context
	 */
    public void stopPollingService(Context context) {
        if(manager!=null)
        	manager.cancel(pendingIntent);
        manager = null;
        pendingIntent = null;
    }
}
