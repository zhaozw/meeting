package com.meetisan.meetisan;

import com.meetisan.meetisan.utils.DebugUtils;

import android.app.Application;
import android.content.Context;
import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {
	private static Context context = null;

	@Override
	public void onCreate() {
		super.onCreate();
		MyApplication.context = getApplicationContext();
		JPushInterface.setDebugMode(DebugUtils.IS_DEBUG);
		JPushInterface.init(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static Context getAppContext() {
		return MyApplication.context;
	}
	
}