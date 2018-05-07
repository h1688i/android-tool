package com.hsu.note;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
 * 這是一個tiny script,適用於小單位的場景描述,簡單易讀易用是他的特性
 * 使用 key=value 方式定義所需存取資料
 * 使用 # 註釋
 */
public class Note {
	
	private Map<String,String> map = new HashMap<>();
	private List<String> keys = new ArrayList<>();
	private String filePath;
	
	/**
	 * @param filePath 檔案絕對路徑
	 */
	public Note(String filePath)
	{
		init(filePath);
	}
	
	/**
	 * 取得資料
	 * @param key 
	 */
	public String getData(String key)
	{
		return get(key);
	}
	
	/**
	 * 更新資料
	 * @param key 要取得的欄位鍵
	 * @param value  
	 */
	public void setData(String key,String value)
	{
		set(key,value);
	}
	
	/**
	 * 讀取腳本並解析腳本
	 * 
	 * @param filePath 檔案絕對路徑
	 */
	private void init(String filePath)
	{
		try
		{
			this.filePath = filePath;
			File file = new File(filePath);
			BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "utf8"));
			
			String text;
			int index = 0;
			while ( (text=read.readLine())!=null)
			{
				if(!text.contains("#"))
				{
					String data[] = text.split("=");
					String key = data[0];
					String value = data[1];
					map.put(key,value);
					keys.add(key);
					//System.out.println("read > "+key+"="+value);
				}
				else
				{
					map.put(index+"#",text);
					keys.add(index+"#");
					index++;
				}
			}
			read.close();
			read = null;
			file = null;		
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 設定資料並存檔
	 * 
	 * @param oldKey 要更新的資料欄位鍵
	 * @param value
	 */
	private void set(String oldKey,String value)
	{
		if(map.containsKey(oldKey))
		{
			map.put(oldKey, value);
			String data = "";
			for(int i = 0 ; i < keys.size() ; i++)
			{
				String newKey = keys.get(i);
				if(!newKey.contains("#"))
				{
					data += newKey + "=" +map.get(newKey) + "\n";	
				}
				else
				{
					data += map.get(newKey) + "\n";	
				}
			}
			//System.out.println("write < "+data);
			save(data);
		}
	}
	
	/**
	 * 設定資料
	 * 
	 * @param key 要取得的欄位鍵
	 */
	private String get(String key)
	{
		update();
		String str = map.get(key);
		if(str==null)
		{
			System.out.println(key+" = null");
		}
		return str;
	}
	
	
	/**
	 * 存檔
	 * 
	 * @param data 要存檔資料
	 */
	private void save(String data)
	{
		try
		{
			File file = new File(filePath);
			BufferedWriter write = new BufferedWriter(new FileWriter(file.getAbsolutePath(),false));
			write.write(data); 
			write.flush();
			write.close();
			write = null;
			file = null;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新資料
	 */
	private void update()
	{
		map.clear();
		keys.clear();
		try
		{
			File file = new File(filePath);
			BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "utf8"));
			
			String text;
			int index = 0;
			while ( (text=read.readLine())!=null)
			{
				if(!text.contains("#"))
				{
					String data[] = text.split("=");
					String key = data[0];
					String value = data[1];
					map.put(key,value);
					keys.add(key);
					//System.out.println("write > "+key+"="+value);
				}
				else
				{
					map.put(index+"#",text);
					keys.add(index+"#");
					index++;
				}
			}
			read.close();
			read = null;
			file = null;		
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
