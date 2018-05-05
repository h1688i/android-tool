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
 * 使用 # 來當註釋
 */
public class Note {
	
	private Map<String,String> map = new HashMap<>();
	private List<String> keys = new ArrayList<>();
	private String filePath;
	
	public Note(String filePath)
	{
		init(filePath);
	}
	
	public String getData(String title)
	{
		return get(title);
	}
	
	public void setData(String title,String value)
	{
		set(title,value);
	}
	
	private void init(String filePath)
	{
		try
		{
			this.filePath = filePath;
			File file = new File(filePath);
			BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "utf8"));
			
			String str;
			int i = 0;
			while ( (str=read.readLine())!=null)
			{
				if(!str.contains("#"))
				{
					String s[] = str.split("=");
					String title = s[0];
					String value = s[1];
					map.put(title,value);
					keys.add(title);
					//System.out.println("read > "+title+"="+value);
				}
				else
				{
					map.put(i+"#",str);
					keys.add(i+"#");
					i++;
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
	
	private void set(String title,String value)
	{
		if(map.containsKey(title))
		{
			map.put(title, value);
			String data = "";
			for(int i = 0 ; i < keys.size() ; i++)
			{
				String key = keys.get(i);
				if(!key.contains("#"))
				{
					data += key + "=" +map.get(key) + "\n";	
				}
				else
				{
					data += map.get(key) + "\n";	
				}
			}
			//System.out.println("write < "+data);
			writeFile(data);
		}
	}
	
	private String get(String title)
	{
		writeTxtFile();
		String s = map.get(title);
		if(s==null)
		{
			System.out.println(title+" = null");
		}
		return s;
	}
	
	private void writeFile(String data)
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
	
	private void writeTxtFile()
	{
		map.clear();
		keys.clear();
		try
		{
			File file = new File(filePath);
			BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "utf8"));
			
			String str;
			int i = 0;
			while ( (str=read.readLine())!=null)
			{
				if(!str.contains("#"))
				{
					String s[] = str.split("=");
					String title = s[0];
					String value = s[1];
					map.put(title,value);
					keys.add(title);
					//System.out.println("write > "+title+"="+value);
				}
				else
				{
					map.put(i+"#",str);
					keys.add(i+"#");
					i++;
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
