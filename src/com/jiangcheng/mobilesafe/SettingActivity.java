package com.jiangcheng.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.jiangcheng.mobilesafe.R;
import com.jiangcheng.mobilesafe.service.AddressService;
import com.jiangcheng.mobilesafe.service.CallSmsSafeService;
import com.jiangcheng.mobilesafe.service.WatchDogService;
import com.jiangcheng.mobilesafe.utils.ServiceUtils;
import com.jiangcheng.mobilesafe.widget.SettingClickView;
import com.jiangcheng.mobilesafe.widget.SettingItemView;

public class SettingActivity extends Activity {
 
 //设置开启自动更新
 private SettingItemView siv_update;
 private SharedPreferences sp;
 
 
 //设置是否开启显示归属地
 private SettingItemView siv_show_address;
 private Intent showAddress;
 
 //黑名单拦截设置
 private SettingItemView siv_callsms_safe;
 private Intent callSmsSafeIntent; 
 
 //设置归属地显示框背景
 private SettingClickView scv_changebg;
private Editor editor;
 
 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showAddress=new Intent(this,AddressService.class);
	    boolean isServiceRunning=ServiceUtils.isServiceRunning(SettingActivity.this, "com.jiangcheng.mobilesafe.service.AddressService");
	    if(isServiceRunning){
	    	//监听来电服务是否开启的
	    	siv_show_address.setChecked(true);
	    	
	    }else{
	    	siv_show_address.setChecked(false);
	    }
	    
	    
	    
	    boolean iscallSmsServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.jiangcheng.mobilesafe.service.CallSmsSafeService");
		siv_callsms_safe.setChecked(iscallSmsServiceRunning);
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_setting);
	    sp=getSharedPreferences("config", MODE_PRIVATE);
	    editor = sp.edit();
	    initView(); 
	    //设置是否开启自动更新
	    siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
              //判断是否选中
				//已经打开自动升级
				if(siv_update.isChecked()){
					siv_update.setChecked(false);
					editor.putBoolean("update", false);
				}else{
					//没有打开自动升级
					siv_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
	    
	    //设置归属地显示控件   
	    showAddress=new Intent(this,AddressService.class);
	    boolean isServiceRunning=ServiceUtils.isServiceRunning(SettingActivity.this, 
	    		"com.jiangcheng.mobilesafe.service.AddressService");
	    if(isServiceRunning){
	    	//监听来电服务是否开启的
	    	siv_show_address.setChecked(true);
	    	
	    }else{
	    	siv_show_address.setChecked(false);
	    }
	    
	    
	    siv_show_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(siv_show_address.isChecked()){
					//变为非选中状态
					siv_show_address.setChecked(false);				
					stopService(showAddress);
					editor.putBoolean("showAddress", false);
				}else{
					//选中状态
					siv_show_address.setChecked(true);
					startService(showAddress);
					editor.putBoolean("showAddress", true);
				}
				editor.commit();
				
			}
		});
	    
	    /** 黑名单拦截设置  
	  	 */
	    
	    callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
	    siv_callsms_safe.setOnClickListener(new OnClickListener() {
	  					@Override
	  					public void onClick(View v) {
	  						if (siv_callsms_safe.isChecked()) {
	  							// 变为非选中状态
	  							siv_callsms_safe.setChecked(false);
	  							stopService(callSmsSafeIntent);
	  							editor.putBoolean("blankNum", false);
	  						} else {
	  							// 选择状态
	  							siv_callsms_safe.setChecked(true);
	  							startService(callSmsSafeIntent);
	  							editor.putBoolean("blankNum", true);
	  						}
	  						editor.commit();
	  					}
	  				});
	    
	    //设置号码归属地的背景
	    scv_changebg=(SettingClickView) findViewById(R.id.scv_changebg);
	    scv_changebg.setTitle("归属地提示框风格");
	    final String[] items={"半透明","活力橙","卫视蓝","金属灰","苹果绿"};
	    int which=sp.getInt("which",0);
		scv_changebg.setDesc(items[which]);
	    scv_changebg.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int dd=sp.getInt("which",0);
				//弹出一个对话框
				AlertDialog.Builder builder=new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items, dd, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//保存选择参数
						Editor editor=sp.edit();
						editor.putInt("which", which);
						editor.commit();
						scv_changebg.setDesc(items[which]);
						
						
						//取消对话框
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});
	    
	}
	
	/**
	 * 初始化各控件的状态
	 */
	private void initView() {
		 siv_update=(SettingItemView) findViewById(R.id.siv_update);
		 siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
		 siv_show_address=(SettingItemView) findViewById(R.id.siv_show_address);
		 boolean update = sp.getBoolean("update", false);
		 boolean blankNum = sp.getBoolean("blankNum",false);
		 boolean showAddress = sp.getBoolean("showAddress", false);
		 boolean watchDog = sp.getBoolean("watchDog", false);
		 siv_update.setChecked(update);
		 siv_callsms_safe.setChecked(blankNum);
		 siv_show_address.setChecked(showAddress);
	}	
	
}
