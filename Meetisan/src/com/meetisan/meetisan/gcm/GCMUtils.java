//package com.meetisan.meetisan.gcm;
//
//import java.io.IOException;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.google.android.gcm.GCMRegistrar;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.meetisan.meetisan.GCMIntentService;
//
//public class GCMUtils {
//	private static final String TAG = GCMUtils.class.getSimpleName();
//	private static GoogleCloudMessaging mGCM;
//
//	/**
//	 * get GCM Registration ID
//	 */
//	public static String getRegistrationId(Context context) {
//		String regId = null;
//		if (GCMRegistrar.isRegistered(context)) {
//			regId = GCMRegistrar.getRegistrationId(context);
//			if (TextUtils.isEmpty(regId)) {
//				// TODO.. regId = getRegisterIdFromServer();
//				Log.d(TAG,
//						"The device has registered, but RegId is missing, you can get it from local server");
//			}
//		}
//
//		return regId;
//	}
//
//	/**
//	 * register GCM device if it has not registered.
//	 * 
//	 * @param context
//	 */
//	public static void registerGCMDevice(Context context) {
//		GCMRegistrar.checkDevice(context);
//		GCMRegistrar.checkManifest(context);
//
//		if (!GCMRegistrar.isRegistered(context)) {
//			Log.d(TAG, "The device has not registered, register now ...");
//			GCMRegistrar.register(context, GCMIntentService.SENDER_ID);
//		} else {
//			Log.d(TAG, "The device has registered, RegId = " + getRegistrationId(context));
//		}
//	}
//
//	/**
//	 * unregister GCM device
//	 * 
//	 * @param context
//	 */
//	public static void unRegisterGCMDevice(Context context) {
//		if (GCMRegistrar.isRegistered(context)) {
//			Log.d(TAG, "unregistered GCM devices");
//			GCMRegistrar.unregister(context);
//		} else {
//			Log.d(TAG, "The device has not registered, do not need to unregister");
//		}
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public static void sendGCM(final Context context) {
//		new AsyncTask() {
//			@Override
//			protected String doInBackground(Object... params) {
//				if (mGCM == null) {
//					mGCM = GoogleCloudMessaging.getInstance(context);
//				}
//				AtomicInteger msgId = new AtomicInteger();
//				try {
//					Bundle data = new Bundle();
//					data.putString("MsgContent", "Hello World");
//					data.putString("MsgAction", "Test GCM");
//					String id = Integer.toString(msgId.incrementAndGet());
//					mGCM.send(GCMIntentService.SENDER_ID + "@gcm.googleapis.com", id, data);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//
//			@Override
//			protected void onPostExecute(Object msg) {
//			}
//		}.execute(null, null, null);
//	}
//
// }
