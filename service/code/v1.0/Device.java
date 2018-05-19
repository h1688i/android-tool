package com.android.device;

import com.attraxus.stock.IO;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

/*
 * 裝置相關操作
 */

public class Device {
	private static String TAG = Device.class.getSimpleName();
	
	public static boolean isPad(Context context) {
	    return (context.getResources().getConfiguration().screenLayout
	        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	 /** 
     * 判斷服務是否運行中 
     * @param context
     * @param serviceClass 
     * @return true 服務存在:false 服務不存在 
     */  
	@SuppressWarnings("deprecation")
	public static boolean isServiceRunning(String tag,Context context, Class<?> serviceClass) {
		boolean run = false;
		int pid = 0;
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {	     
	        	/*
	        	 * 如果pid = 0 代表服務沒有啟動成功,服務雖存在系統列表但尚未配置記憶體
	        	 */
	        	pid = service.pid;
	        	if(pid == 0)	      
	        		run = false;	        	
	        	else	        	
	        		run = true;	        	
	        }
	    }
	    IO.LOG(tag,"isServiceRunning", serviceClass.getName() + " pid " + pid);
		return run;
	}
	
	/*
	 * NetworkInfo 這個工具類,可以獲取網路相關細節,
	 * 如果只是要知道是否有網路可以通訊isConnected()就足夠了,
	 */
	public static boolean networkConnected(Context context)
	{
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
		//如果沒有連線的話，info會等於null
		if( info != null )
		{
			//networkInfoPaser(info);
		    return info.isConnected();
		}
		else
		{
			IO.LOG(TAG,"networkConnected","無網路連線");
			return false;
		}
	}
	
	/**
	 * NetworkInfo 狀態細節解析
	 * 
	 * @param info 網路狀態相關細節訊息
	 */
	public static void networkInfoPaser(NetworkInfo info)
	{
		/*
		 * isConnected
		 * 指示網絡連接是否存在，並且可以建立連接並傳遞數據
		 * 在嘗試執行數據事務之前始終調用它。
		 */
	    String status = "網路是否已連線 " + info.isConnected() + "\n";
	    
	    /*
	     * 網路連線方式名稱(WIFI or mobile)
	     */
	    
	    status += "網路連線方式 " + info.getTypeName() + "\n";
	    /*
	     * 網路連線狀態
	     */
	    
	    status += "網路連線狀態 " + info.getState() + "\n";
	    
	    /*  isAvailable
	     * 	網絡連接是否可行。當持久或半持久狀態阻止連接到該網絡的可能性時，網絡不可用。例子包括
		 *	該設備不在任何此類網絡的覆蓋區域內。
		 *	設備位於家庭網絡以外的其他網絡上（即漫遊），並且數據漫遊已被禁用。
		 *	設備的無線電被關閉，例如，因為啟用了飛行模式。
	     */
	    
	    status += "網路是否可使用 " + info.isAvailable() + "\n";
	    
	    /* isConnectedOrConnecting
	     * 指示網絡連接是否存在或正在建立。
	     * 這對於需要執行與讀取或寫入數據不同的與網絡相關的任何應用程序很有用。
	     * 對於後者，請調用isConnected()，以保證網絡完全可用。
	     */
	    
	    status += "網路是否已連接or連線中 " + info.isConnectedOrConnecting() + "\n";
	    
	    /* isFailover
	     * 指示連接管理器嘗試在與其他網絡斷開連接後故障轉移到此網絡是否導致當前連接到網絡的嘗試。
	     */
	    
	    status += "網路是否故障有問題 " + info.isFailover() + "\n";
	    
	    /*
	     * 網路是否在漫遊模式
	     */
	    status += "網路是否在漫遊模式 " + info.isRoaming();
	    IO.LOG(TAG,status,"");
	}
	
	/*
	 * 使用者自啟動管理設定引導
	 */
	@SuppressLint("DefaultLocale")
	public static void selfStartManagerSettingIntent(Context context){

		Intent intent = new Intent();
        try{
        	String manufacturer = Build.MANUFACTURER;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);         
    		if (manufacturer.equalsIgnoreCase("xiaomi")) {
    			ComponentName componentName = new ComponentName("com.miui.securitycenter","com.miui.permcenter.autostart.AutoStartManagementActivity");
    	        intent.setComponent(componentName);
    		} else if (manufacturer.equalsIgnoreCase("sony")) {

    		} else if (manufacturer.toLowerCase().contains("samsung")) {

    		} else if (manufacturer.toLowerCase().contains("htc")) {

    		} else if (manufacturer.toLowerCase().contains("nova")) {

    		} else if(manufacturer.toLowerCase().contains("lg")){

    		}
            context.startActivity(intent);
        }catch (Exception e){
            intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }

    }
}
