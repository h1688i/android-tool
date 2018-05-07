package com.attraxus.stock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
/**
 * 檔案存取操作
 */
public class IO {

	static String logcatPath = Environment.getExternalStorageDirectory()+"/Stock.txt";

	/**
	 * 檔案如果存在要處理的動作
	 */
	static public void openLogcatFile()
	{
		if(fileIsExists(logcatPath))
		{
			File file = new File( logcatPath ); 
			//file.delete();
		}
	}
	
	/**
	 * logcat訊息輸出處理
	 * @param name 類別名稱
	 * @param method 類別方法 
	 * @param text 訊息 
	 */
	static public void LOG(String name,String method,String text)
	{
		String msg = method+"->"+text;
		logcatOut(name+":"+msg);
		Log.d(name,msg);
	}
	
	/**
	 * logcat訊息輸出處理
	 * 
	 * @param text 訊息
	 */
	@SuppressLint("SimpleDateFormat")
	static private void logcatOut(final String text) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date dt = new Date();
				String dts = sdf.format(dt);
				writerFile(logcatPath,dts + " " + text,true);
			}
		});
		t.start();
		t = null;
	}
	
	/**
	 * 寫入檔案
	 * 
	 * @param path 檔案絕對路徑
	 * @param text 訊息
	 * @param append 是否換行
	 */
	static public void writerFile(String path,String text,boolean append)
	{
		FileWriter fw;
		try {
			fw = new FileWriter(path, append);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 讀取檔案
	 * 
	 * @param path 檔案絕對路徑
	 */
	static public String readFile(String path)
	{
		try
		{
			String str = null;
			File file = new File(path);
			if(file.exists())
			{
				BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "utf8"));
				str = read.readLine();
				read.close();
				read = null;
				file = null;
			}
			return str;			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 檢查檔案是否存在
	 * 
	 * @param path 檔案絕對路徑
	 * @return 存在 true,不存在 false
	 */
	static private boolean fileIsExists(String path){
        File file = new File(path);
        if(!file.exists()){
                return false;
        }
        return true;
    }
	
	/**
	 * 取得應用程式名稱
	 * 
	 * @param context
	 */
	public static String getApplicationName(Context context) {  
        PackageManager packageManager = null;  
        ApplicationInfo applicationInfo = null;  
        try {  
            packageManager = context.getApplicationContext().getPackageManager();  
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);  
        } catch (PackageManager.NameNotFoundException e) {  
            applicationInfo = null;  
        }  
        String applicationName =   
        (String) packageManager.getApplicationLabel(applicationInfo);  
        return applicationName;  
    }
}
