package com.attraxus.service;

import com.attraxus.stock.IO;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
/**
 * 後臺服務保護
 */
public class Daemon extends Service {
	/*
	 * 第一次啟動
	 * */
	public static final String INTENT_ACTION_START = "0";
	/*
	 * 重新啟動
	 **/
	public static final String INTENT_ACTION_RESTART = "1";
	/*
	 * 心跳
	 **/
	public static final String INTENT_ACTION_HEARTBEAT = "2"; 
	
	String className = null;
	IPC ipc = null;
	ServiceLink link = null;
	/**
	 * 要綁定的對象類別
	 */
	Class<?> objects = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		IO.LOG(className,"onCreate","initialize");
		initialize();
	}
	
	/**
	 * 初使化動作
	 */
	private void  initialize() {
		ipc = null;
		link = null;
		ipc = new IPC();
		link = new ServiceLink();
		startBindService();
		ServiceManager.setForegroundDoNotShow(this); 
	}
	
	/**
	 * 設定LOG顯示類別名稱,開發階段測試用
	 * 
	 * @param className 類別名稱
	 */
	void setClassName(String className){
		this.className = className;
	}
	/**
	 * 設定綁定對象
	 * 
	 * @param objects 綁定對象
	 */
	void setBindService(Class<?> objects){
		this.objects = objects;
	}
	/**
	 * 啟動綁定
	 */
	@SuppressLint("InlinedApi")
	void startBindService(){
		bindService(new Intent(this, objects), link, Context.BIND_IMPORTANT);
	}

	@Override
	public void onDestroy() {
		if(link != null)
			unbindService(link);
		IO.LOG(className,"onDestroy","***** 服務終止 *****");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return ipc;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
	    return super.onUnbind(intent);
	}

	/**
	 * 與對像綁定,如果綁定的對像失去連結,則將該對像重新啟動
	 */
	class ServiceLink implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
			ServiceIPC service = IPC.asInterface(iBinder);
			try {
				IO.LOG(className,"onServiceConnected",service.getName());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			IO.LOG(className,"onServiceDisconnected",componentName.getClassName());
			
			Intent intent = new Intent(Daemon.this, objects);
			intent.setAction(PushService.INTENT_ACTION_RESTART);
			Daemon.this.startService(intent);
		}
	}
	
	/**
	 * 跨進程通信
	 */
	public class IPC extends ServiceIPC.Stub {
		@Override
		public String getName() throws RemoteException {
			// TODO Auto-generated method stub
			return className;
		}
	}
}
