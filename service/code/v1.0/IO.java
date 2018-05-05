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

public class IO {

	static String logcatPath = Environment.getExternalStorageDirectory()+"/logcat.txt";

	static public void openLogcatFile()
	{
		if(fileIsExists(logcatPath))
		{
			File file = new File( logcatPath ); 
			//file.delete();
		}
	}
	
	static public void LOG(String TAG,String method,String text)
	{
		String msg = method+"->"+text;
		logcatOut(TAG+":"+msg);
		Log.d(TAG,msg);
	}
	
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
	
	static public String readFile(String filePath)
	{
		try
		{
			String str = null;
			File file = new File(filePath);
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
	
	static private boolean fileIsExists(String file){
        File f=new File(file);
        if(!f.exists()){
                return false;
        }
        return true;
    }
	
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
