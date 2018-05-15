package com.attraxus.service;

import com.android.badge.BadgeCountManager;
import com.android.badge.BadgeUtil;
import com.android.device.Device;
import com.attraxus.stock.Config;
import com.attraxus.stock.IO;
import com.attraxus.stock.R;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;

	/**
	 * 繼承 Daemon 重啟保護機制
	 * 綁定CoreService為守護服務,如果服務終止則CorehService將負責重新啟動任務
	 * START_STICKY:如果服務在onStartCommand後異常終止,
	 * 則系統重新啟動,重啟後intent為null
	 */

public class PushService extends Daemon {
	private static PushService context = null;
	private Client client = null;
	private Polling pollingUtils;
	//輪詢最大週期週期計算
	private short cycleMaxCount = 0;
	//系統輪詢時間週期
	private final int second = Config.PollingCycle.toInt();
	//輪詢最大週期,達到此數值重新設定系統輪詢
	private final int cycleMax = second;
	
	@Override
	public void onCreate() {
		className = PushService.class.getSimpleName();
		setClassName(className);
		setBindService(CoreService.class);
		super.onCreate();
		context = this;
		init();
	}
	
	/**
	 * 檢查網路連接是否正常,如果正常則繼續執行保持遠端連線動作,
	 * 如果網路連接異常,則待機等待下一次輪巡
	 * 
	 * @param intent 意圖
	 * @param flags
	 * @param startId 調用次數
	 */
	
	@SuppressLint("InlinedApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(Device.networkConnected(context))
		{
			if(intent != null && client != null)
				keepRemoteConnection(intent.getAction());
		}
		else
		{
			IO.LOG(className,"onStartCommand","無網路連線");
		}
		return START_STICKY;
	}

	/*
	 * 檢查client連線狀態
	 * 
	 * @param action 要做的動作
	 */
	
	private void keepRemoteConnection(String action) {
		if (action != null && action.equals(INTENT_ACTION_HEARTBEAT)) 
		{
			short status = client.status();
			connectionStatus(status);
		} 
	}
	
	/*
	 * client目前連線狀態,如果cycleCount大於cycle則重新設定Polling,Client
	 * 
	 * @param status client目前狀態
	 */
	
	private void connectionStatus(short status)
	{
		if (cycleMaxCount < cycleMax) 
		{
			switch(status)
			{
				case Client.SOCKET_NORMAL:
					cycleMaxCount++;
					break;
				case Client.NOT_CHECKEDIN_YET:
				case Client.SOCKET_UNUSUAL:
					initClient();
					break;
			}
		} 
		else 
		{
			init();
		}
	}
	
	private void  init() {
		cycleMaxCount = 0;
		resrtPolling();
		initClient();
	}
	
	void initClient()
	{
		String userId = User.id(this);
		if(userId != null)
		{
			client = null;
			client = new Client(userId);
			IO.LOG(className,"initClient","...");
		}
		else
		{
			IO.LOG(className,"initClient","!!!user not exist!!!");
		}
	}
	
	private void initPolling()
	{
		pollingUtils = new Polling();
		pollingUtils.startPollingService(this,second,PushService.class, PushService.INTENT_ACTION_HEARTBEAT);
	}
	
	private void stopPolling()
	{
		if(pollingUtils != null)
			pollingUtils.stopPollingService(this);
		pollingUtils = null;
	}
	
	private void resrtPolling() {
		IO.LOG(className,"resrtPolling","...");
		stopPolling(); 
		initPolling();  
    }
	
	public static PushService getContext() {
		return context;
	}
	
	/*
	 * 向裝置推送通知
	 */
	
	void pushMsg(String title, String text) {
		BadgeCountManager.addNumber();
	    Notification notification = NotificationUI.sendNotification(title,text,R.drawable.icon);
	    BadgeUtil.setBadgeCount(getContext(),BadgeCountManager.getCount(), R.drawable.icon, notification);
	}
}
