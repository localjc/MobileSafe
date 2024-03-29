package com.jiangcheng.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {
	private static String path="data/data/com.jiangcheng.mobilesafe/files/address.db";
	                            
	/*
	 * 产一个号码进来，返回一个归属地回去
	 */
	public static String queryNumber(String number){
		String address=number;
		if(number.length() > 11){
			address = "号码过长！！";
		}
		//path  把address.db这个数据库拷贝到data/data/包名/files/address.db
		SQLiteDatabase database=SQLiteDatabase.openDatabase(path, null,SQLiteDatabase.OPEN_READONLY);
		//手机号码 13  14  15 16  18
		//手机号码正则表达式
		if(number.matches("^1[34568]\\d{9}$")){
			//手机号码
			//Cursor cursor=database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{number.substring(0, 7)});
			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id = (select outkey from data1 where id = ?)",
							new String[] { number.substring(0, 7) });
			while(cursor.moveToNext()){
				String location=cursor.getString(0);
		        address=location;
		        System.out.println("address"+address);
			}
			cursor.close();
		}else{
			//其它的电话号码
			switch(number.length()){
			case 3:
				address="匪警号码";
				break;
			case 4:
				address="模拟器";
				break;
				//10086
			case 5:
				address="客服电话";
				break;
			case 7:
				address="本地号码";
				break;
			case 8:
				address="本地号码";
				break;
				
				default:
					//处理长途电话
					if(number.length()>10&&number.startsWith("0")){
						Cursor cursor = database
								.rawQuery(
										"select location from data2 where area=?)",
										new String[] { number.substring(1, 3) });
						while(cursor.moveToNext()){
							String location=cursor.getString(0);
							address=  location.substring(0, location.length()-2);
						}
						cursor.close();
						//0855-353253445
						 cursor = database
								.rawQuery(
										"select location from data2 where area=?)",
										new String[] { number.substring(1, 4) });
						while(cursor.moveToNext()){
							String location=cursor.getString(0);
							address=  location.substring(0, location.length()-2);
						}
					}
					break;
			}
		}
		return address;
	}
}
