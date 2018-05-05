package com.attraxus.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class Polling {

	AlarmManager manager;
	PendingIntent pendingIntent;
  
    public void startPollingService(Context context, int seconds , Class<?> cls,String action) {
 
        manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
                                                                                                                                                                                                                                      
  
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                                                                                                                                                                                                      
  
        long triggerAtTime = SystemClock.elapsedRealtime();
                                                                                                                                                                                                               
        //使用AlarmManger的setRepeating(精確)方法设置定期執行時間週期
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,
        		seconds * 1000, pendingIntent);
    }
    
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
    
    public void stopPollingService(Context context) {
        if(manager!=null)
        	manager.cancel(pendingIntent);
        manager = null;
        pendingIntent = null;
    }
}
