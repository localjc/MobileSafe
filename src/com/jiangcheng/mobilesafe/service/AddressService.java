package com.jiangcheng.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.jiangcheng.mobilesafe.R;
import com.jiangcheng.mobilesafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {
protected static final String TAG = "AddressService";
	/*
 * 窗体管理者
 */
	private WindowManager wm;
	View view;
	/**
	 * 电话服务 
	 */
	private TelephonyManager tm;
	private MyListenerPhone listenerPhone;
	
	private SharedPreferences sp;
	
	private OutCallReceiver receiver;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//相当于服务里面的内部类
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			//这就是我们拿到的拨出去的电话号码
			String phone=getResultData();
			//查询数据库
			String address=NumberAddressQueryUtils.queryNumber(phone);
			
			//Toast.makeText(context, address, 1).show();
			myToast(address);
			
		}

	}
	
	
	
	
	private class MyListenerPhone extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
		//state:状态，incomingNumber:来电号码
			super.onCallStateChanged(state, incomingNumber);
            switch(state){
            case TelephonyManager.CALL_STATE_RINGING:   //来电铃声想起
            	//查询数据库的操作
            	String address=NumberAddressQueryUtils.queryNumber(incomingNumber);
            	//Toast.makeText(getApplicationContext(), address, 1).show();
            	myToast(address);
            	break;
            case TelephonyManager.CALL_STATE_IDLE: //电话的空闲状态
            	//把这个View移出
            	if(view != null){
            		wm.removeView(view);
            	}
            	
            	
            	break;
            	
            	default:
            		break;
            }
		}
		
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		tm=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		//监听来电
		listenerPhone=new MyListenerPhone();
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);
	    
		
		//用代码注册广播接收者
		receiver=new OutCallReceiver();
		//意图匹配器
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		
		//实例化窗体
		wm=(WindowManager) getSystemService(WINDOW_SERVICE);
	}
	
	 WindowManager.LayoutParams params;
	 long[] mHits=new long[2];
	 
	/*
	 * 自定义吐司
	 */
	public void myToast(String address) {
		// TODO Auto-generated method stub
		//items={"半透明","活力橙","卫视蓝","金属灰","苹果绿"};  
		view=view.inflate(this, R.layout.address_show, null);
		TextView textview=(TextView) view.findViewById(R.id.tv_address);
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
 
				
				 System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
			      mHits[mHits.length-1] = SystemClock.uptimeMillis();
			      if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
			    
			    	  //双击居中了。。。。
			    	  params.x=wm.getDefaultDisplay().getWidth()/2-view.getWidth()/2;
			    	  wm.updateViewLayout(view, params);
			    	  Editor editor=sp.edit();
						editor.putInt("lastx", params.x);
						editor.commit();
			      }
			}
		});
		
		//给view对象设置一个触摸的监听器
		view.setOnTouchListener(new OnTouchListener() {
			//定义手指的初始位置
			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:   //手指按下屏幕
					startX=(int) event.getRawX();
					startY=(int) event.getRawY();
                    Log.i(TAG,"手指摸到控件："+startX+"   "+startY);					
					break;
				case MotionEvent.ACTION_MOVE:   //手指在屏幕上移动
					int newX=(int) event.getRawX();
					int newY=(int) event.getRawY();
                    				
					
                    int dx=newX-startX;
                    int dy=newY-startY;
                    params.x+=dx;
                    params.y+=dy;
                    //考虑边界问题
                    if(params.x<0){
                    	params.x=0;
                    }
                    if(params.y<0){
                    	params.y=0;
                    }
                    if(params.x>(wm.getDefaultDisplay().getWidth()-view.getWidth())){
                    	params.x=wm.getDefaultDisplay().getWidth()-view.getWidth();
                    }
                    if(params.y>(wm.getDefaultDisplay().getHeight()-view.getHeight())){
                    	params.y=wm.getDefaultDisplay().getHeight()-view.getHeight();
                    }
                    Log.i(TAG,"手指在控件上移动："+newX+"   "+newY);	
                    wm.updateViewLayout(view, params);
					
                    startX=(int) event.getRawX();
					startY=(int) event.getRawY();
                    
                    break;
					
				case MotionEvent.ACTION_UP:     //手指一开屏幕一瞬间
					//记录控件距离屏幕左上角的坐标
					Editor editor=sp.edit();
					editor.putInt("lastx", params.x);
					editor.putInt("lasty", params.y);
					editor.commit();
					Log.i(TAG,"手指离开控件："+"   ");	
					break;
				
				}
				
				//return true;   //事件处理完毕了，不要让父控件父布局响应相应事件
			     return false;
			}
		});
		
		
		int[] ids={R.drawable.call_locate_white,R.drawable.call_locate_orange
				,R.drawable.call_locate_blue,R.drawable.call_locate_gray
				,R.drawable.call_locate_green};
		
		  sp=getSharedPreferences("config", MODE_PRIVATE);
		int t=sp.getInt("which",0);
		view.setBackgroundResource(ids[sp.getInt("which",0)]);
		
		
		
		 
		  //view.setBackgroundResource(R.drawable.call_locate_gray);
		  
		  textview.setText(address);
		  
		  //窗体的参数就设置好了
		  params = new WindowManager.LayoutParams();
		 
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity=Gravity.TOP+Gravity.LEFT;
        //指定窗体距离左边和上边距离
        params.x=sp.getInt("lastx", 0);
        params.y=sp.getInt("lasty", 0);
        
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //android系统中具有电话优先级的一种窗体类型，记得添加权限
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		
		wm.addView(view, params);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//取消监听来电
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		listenerPhone=null;
		//在服务死掉的时候，用代码取消注册广播的接收者
		unregisterReceiver(receiver);
		receiver=null;
		
	}
	 
}
