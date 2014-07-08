package com.meetisan.meetisan.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = GCMBroadcastReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		GoogleCloudMessaging mGCM = GoogleCloudMessaging.getInstance(context);

		String messageType = mGCM.getMessageType(intent);

		if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
			Log.e(TAG, "Receive Error: " + intent.getExtras().toString());
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
			Log.d(TAG, "Deleted Message: " + intent.getExtras().toString());
		} else {
			Log.d(TAG, "Receive Message: " + intent.getExtras().getString("message"));
		}
	}

}