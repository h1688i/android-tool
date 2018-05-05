package com.attraxus.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;
import com.attraxus.stock.Config;
import com.attraxus.stock.IO;
import android.annotation.SuppressLint;
import android.os.Environment;

/*
 * 用戶端網路連接服務
 */

public class Client extends Thread {

	private String TAG = Client.class.getSimpleName();
	//遠端主機ip位址
	private String serverIP = "";
	//遠端主機port數值
	private int serverPort = "";
	
	private Socket socket = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;
	
	/*
	 * 通訊相關設定
	 */
	private String signin = "A!1!";
	private String heartbet = "C!1!OK";
	private final char signinResponse = 'A';
	private final char heartbetResponse = 'C';
	private final char realTimeResponse = 'S';
	private boolean signInSuceesfully = false;
	//連線尚未準備
	public static final short NOT_CHECKEDIN_YET = 0;
	//Socket正常
	public static final short SOCKET_NORMAL = 1;
	//Socket異常
	public static final short SOCKET_UNUSUAL = 2;
	
	/*
	 * 傳入使用者id,如果使用者id存在,進行遠端連線初始化,
	 * 然後初始化遠端登入命令,最後啟動執行緒做登入動作
	 */
	public Client(String userId) {
		if(userId != null && 
		   initConnection())
		{
			signin += userId;
			start();
		}
	}
	
	@Override
	public void run() 
	{
		startListener();
	}
	
	private boolean initConnection()
	{
		try {
			InetSocketAddress inetSocketAddress = new InetSocketAddress(serverIP,serverPort);
			socket = new Socket();				
			socket.connect(inetSocketAddress);	
			in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			return true;
		} catch (Exception e) {
			IO.LOG(TAG,"initConnection","Exception 連線初始化失敗");	
			relogin();
			return false;
		}
	}
	
	private void startListener()
	{
		try {
			short actionResponse = 0;
			IO.LOG(TAG,"startListener","thread process");
			if(sendMsg(signin))
			{
				while (socketAlive()) 
				{
					String text = in.readLine();
					if (text == null)
						break;
					IO.LOG(TAG,serverIP,"response "+text);
					char action = text.charAt(actionResponse);
					work(action,text);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block			
			IO.LOG(TAG,"startListener","Exception");
			socketClose();
		}
	}
	
	private void work(char action,String text)
	{
		switch (action) {
		case signinResponse:	
			//checkLogin(text);
			break;
		case heartbetResponse:
			//checkConnection();
			break;	
		case realTimeResponse:
			//pushMsg(text);
			break;
		}
	}
	/*
	private void checkLogin(String text)
	{
		//do nothing
		short relust = 2;
		String data[] = text.split("!");
		String state = data[relust];
		
		if(state.equals("OK"))
			signInSuceesfully = true;
		else if(state.equals("E1"))
			signInSuceesfully = false;
	}
	
	private void checkConnection()
	{
		//do nothing
	}
	
	private void pushMsg(String text)
	{
		// 更新 update_ui_info 檔案 = 1,更新訊息
		String path = Environment.getDataDirectory()+"/data/com.attraxus.stock/files/update_ui_info.txt";
		IO.writerFile(path,"1", false);
				
		String data[] = text.split("!");
		String name = data[2];
		String date = data[3];
		String time = data[4];
		String price = data[5];
		String direction = data[6];
		String strategyText = name + " 投資方向 " + date +" "+ time +" "+ direction +" "+ price;
		PushService.getContext().pushMsg("Strategy",strategyText);
	}
	*/
	private boolean sendMsg(String msg) {
		boolean result = false;
		try {
			if(socketAlive())
			{
				IO.LOG(TAG,"sendMsg",msg);
				out.write((msg + "\n"));
				out.flush();
				result = true;
			}
			else
			{
				result = false;
				IO.LOG(TAG,"sendMsg","socket broken");
			}
		} catch (IOException e) {
			IO.LOG(TAG,"sendMsg","Exception "+e.toString());
		}
		return result;
	}
	
	public void socketClose() {
		try {
			socket.close();
			socket = null;
			in.close();
			in = null;
			out.close();
			out = null;
			IO.LOG(TAG,"socketClose","...");
		} catch (Exception e) {
			IO.LOG(TAG,"socketClose","Exception "+e.toString());
		}
	}
	
	public short status() {
		if(signInSuceesfully)
		{
			if(sendMsg(heartbet))
				return SOCKET_NORMAL;
			else
				return SOCKET_UNUSUAL;
		}
		else
		{
			return NOT_CHECKEDIN_YET;
		}
	}
	
	/*
	 * socket連接狀態
	 */
	private boolean socketAlive()
	{
		if (socket != null && 
			socket.isClosed() == false && 
			socket.isConnected() == true)
		{
			return true;
		}
		return false;
	}
	
	public void relogin()
	{
		PushService.getContext().initClient();
		IO.LOG(TAG,"relogin","not ready");
	}
	/*
	@SuppressLint("SimpleDateFormat")
	public String ntpTime()
	{
		try {
			NTPUDPClient timeClient = new NTPUDPClient();
			String timeServerUrl = "time-a.nist.gov";
			InetAddress timeServerAddress = InetAddress.getByName(timeServerUrl);
			TimeInfo timeInfo = timeClient.getTime(timeServerAddress);
			TimeStamp timeStamp = timeInfo.getMessage().getTransmitTimeStamp();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String datetime = dateFormat.format(timeStamp.getDate());
			System.out.println(datetime);
			return datetime;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	*/
}

