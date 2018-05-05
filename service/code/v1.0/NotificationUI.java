package com.attraxus.service;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

public class NotificationUI extends Activity{
	
	public static Notification sendNotification(String title,String text,int icon)
	{
		NotificationCompat.Builder builder = new NotificationCompat.Builder(PushService.getContext());
		Intent intentToCoreReceiver = new Intent(PushService.getContext(), SystemBroadcastReceiver.class);
		final int notifyID = 0; 
		// 點擊通知後是否要自動移除掉通知
		final boolean autoCancel = true; 
		 // ONE_SHOT：PendingIntent只使用一次；
		 // CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；
		 // NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；
		 // UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
		final int flags = PendingIntent.FLAG_ONE_SHOT;
		// 通知音效的URI，在這裡使用系統內建的通知音效
		final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); 
		// 取得系統的通知服務
		//final NotificationManager notificationManager = 
		//		(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE); 
		
		Intent intentClick = intentToCoreReceiver;
		intentClick.setAction("clicked");
		intentClick.putExtra(SystemBroadcastReceiver.TYPE, notifyID);
		PendingIntent pendingIntentClick = PendingIntent.getBroadcast(PushService.getContext(), 0, intentClick, flags);
		
		Intent intentCancel = intentToCoreReceiver;
		intentCancel.setAction("cancelled");
		intentCancel.putExtra(SystemBroadcastReceiver.TYPE, notifyID);
		PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(PushService.getContext(), 0, intentCancel,flags);
		
		return  builder.setTicker(text)
				.setSmallIcon(icon)
				.setContentTitle(title)
				.setContentText(text)
				.setSound(soundUri)
				.setContentIntent(pendingIntentClick)
		        .setDeleteIntent(pendingIntentCancel)
				.setAutoCancel(autoCancel)
				.setWhen(System.currentTimeMillis())
				.build(); 
	}
	
}
