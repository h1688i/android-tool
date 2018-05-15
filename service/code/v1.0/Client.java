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
 * 用戶對遠端連接服務
 */

public class Client extends Thread {

	private String TAG = Client.class.getSimpleName();
	//遠端主機ip位址
	private String serverIP = Config.BgService.toString();
	//遠端主機port數值
	private int serverPort = Config.BgPort.toInt();
	
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
	
	/**
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 * 啟動一個thread監聽遠端服務訊息
	 */
	@Override
	public void run() 
	{
		startListener();
	}
	
	/**
	 * 初始化對遠端連線
	 * 
	 * @exception 如果連線初始化失敗,重新再試一次
	 */
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
	
	/*
	 * 開始監聽遠端連線,先做遠端登入動作
	 * 登入成功,則進入迴圈等待遠端回應
	 * 登入失敗,執行socketClose動作
	 */
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
	
	/*
	 * 遠端回應相應處理
	 */
	
	private void work(char action,String text)
	{
		switch (action) {
		case signinResponse:	
			checkLogin(text);
			break;
		case heartbetResponse:
			checkConnection();
			break;	
		case realTimeResponse:
			pushMsg(text);
			break;
		}
	}
	
	/**
	 * 遠端回應登入成功後相關動作
	 * 
	 * @param text 回應訊息
	 */
	
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
	
	/*
	 * 遠端連線檢查回應
	 */
	
	private void checkConnection()
	{
		//do nothing
	}
	
	/**
	 * 遠端訊息推顯示
	 * 
	 * @param text 及時推送訊息
	 */
	private void pushMsg(String text)
	{
		// 更新 mjwftjohm 檔案 = 1,更新訊息
		String path = Environment.getDataDirectory()+"/data/com.attraxus.stock/files/mjwftjohm.txt";
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
	
	/**
	 * 向遠端服務器發送訊息
	 * 
	 * @param msg 要發送的訊息
	 * @return 發送狀態 true 成功,false 失敗
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
	
	/*
	 * Socket,BufferedReader,BufferedWriter,物件關閉
	 */
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
	
	/*
	 * client連線狀態
	 */
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
	
	/*
	 * 每3秒重新登入一次
	 */
	public void relogin()
	{
		try {
			Thread.sleep(3000);
			PushService.getContext().initClient();
			IO.LOG(TAG,"relogin","not ready");
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	/*
	 * ntp server 
	 */
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
}

