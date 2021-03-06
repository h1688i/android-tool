package com.attraxus.service;

import java.io.UnsupportedEncodingException;

import com.android.device.AES;
import com.attraxus.stock.IO;
import android.content.Context;
import android.util.Base64;

/*
 * 使用者資訊操作
 */
public class User {
	
	static String path = null;
	/*
	 * 取得使用者id,並解密
	 */
	static public String id(Context context)
    {
		String path = context.getFilesDir()+"/user_info.txt";
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
	
	/*
	 * 儲存使用者id,並加密
	 */
	
	static public void saveId(String id,Context context){
		try {
			if(id != null)
			{
				String path = context.getFilesDir()+"/user_info.txt";
				byte[] TextByte = AES.EncryptAES(
						AES.IvAES.getBytes("UTF-8"), 
						AES.KeyAES.getBytes("UTF-8"), 
						id.getBytes("UTF-8")
						);
				String TEXT = Base64.encodeToString(TextByte, Base64.DEFAULT);
				IO.writerFile(path, TEXT, false);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
