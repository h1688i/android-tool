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
/*
 * 後臺服務保護
 */
public class Daemon extends Service {
	
	public static final String START = "0";
	public static final String RESTART = "1";
	public static final String HEARTBEAT = "2"; 
	String className = null;
	ServiceBinder binder = null;
	ServiceLink link = null;
	Class<?> objects = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		IO.LOG(className,"onCreate","initialize");
		initialize();
	}
	
	private void  initialize() {
		binder = null;
		link = null;
		binder = new ServiceBinder();
		link = new ServiceLink();
		startBindService();
		ServiceManager.setForegroundDoNotShow(this); 
	}
	
	/*
	 * 設定LOG顯示類別名稱,開發階段測試用
	 */
	void setClassName(String className){
		this.className = className;
	}
	/*
	 * 設定綁定對象
	 */
	void setBindService(Class<?> objects){
		this.objects = objects;
	}
	/*
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
		return binder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
	    return super.onUnbind(intent);
	}

	/*
	 * 與對象綁定,如果綁定的對象失去連結,則將該對象重新啟動
	 */
	class ServiceLink implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
			Daemon.ServiceBinder service = (Daemon.ServiceBinder)iBinder;
			IO.LOG(className,"onServiceConnected",service.getName());
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			IO.LOG(className,"onServiceDisconnected",componentName.getClassName());
			
			Intent intent = new Intent(Daemon.this, objects);
			intent.setAction(PushService.RESTART);
			Daemon.this.startService(intent);
			startBindService();
		}
	}

	/*
	 * 跨進程通信
	 */
	public class ServiceBinder extends Binder {

		public String getName(){
			return className;
		}
		
		public Daemon getService() {  
            return Daemon.this;  
        }
	}
}
