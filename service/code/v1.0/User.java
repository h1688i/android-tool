package com.attraxus.service;

import java.io.UnsupportedEncodingException;

import com.android.device.AES;
import com.attraxus.stock.IO;

import android.os.Environment;
import android.util.Base64;

public class User {
	
	static String path = Environment.getDataDirectory()+"/data/com.attraxus.stock/files/user_info.txt";
	
	static public String id()
    {
    	String uid = IO.readFile(path);
    	
		try {
			if(uid!=null)
			{
				byte[] TextByte = AES.DecryptAES( 
						AES.IvAES.getBytes("UTF-8"), 
						AES.KeyAES.getBytes("UTF-8"),
						Base64.decode(uid.getBytes("UTF-8"), 
						Base64.DEFAULT)
						);
				uid = new String(TextByte,"UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return uid;
    }
	
	static public void saveId(String id){
		try {
			byte[] TextByte = AES.EncryptAES(
					AES.IvAES.getBytes("UTF-8"), 
					AES.KeyAES.getBytes("UTF-8"), 
					id.getBytes("UTF-8")
					);
			String TEXT = Base64.encodeToString(TextByte, Base64.DEFAULT);
			IO.writerFile(path, TEXT, false);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
