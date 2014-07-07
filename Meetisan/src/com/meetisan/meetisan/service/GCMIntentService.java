package com.meetisan.meetisan.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.meetisan.meetisan.utils.ToastHelper;

public class GCMIntentService extends GCMBaseIntentService {
	private static final String TAG = GCMIntentService.class.getSimpleName();
	public static final String SENDER_ID = "647678123306";

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		Log.e("GCMIntentService", "Error: " + arg1);
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		Log.d(TAG, "Receive New Message：" + arg1.getStringExtra("mine"));
		final String info = arg1.getStringExtra("mine");
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {

			@Override
			public void run() {
				ToastHelper.showToast("New Message Content：" + info);
			}
		});
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		Log.v(TAG, "Registered Complete");
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.v(TAG, "Unregistered Complete");
	}

}