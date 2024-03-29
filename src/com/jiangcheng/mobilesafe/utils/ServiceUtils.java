package com.jiangcheng.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {
	/*
	 * 某个服务是否还活着
	 */
	public static boolean isServiceRunning(Context context,String serviceName){
		//校验服务是否还活着
		ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos=am.getRunningServices(100);
		for(RunningServiceInfo info:infos){
			String name=info.service.getClassName();
			if(serviceName.equals(name)){
				return true;
			}
		}
		return false;
	}
}
