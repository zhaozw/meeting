package com.meetisan.meetisan;

import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "Jpush " + MyReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "接收Registration Id : " + regId);
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG, "自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
			pushCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

			JPushInterface.reportNotificationOpened(context, bundle.getString(JPushInterface.EXTRA_MSG_ID));
			// 打开自定义的Activity
			if (!MainActivity.isForeground) {
				Intent i = new Intent(context, MainActivity.class);
				i.putExtra(JPushInterface.EXTRA_ALERT, bundle.getString(JPushInterface.EXTRA_ALERT, null));
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
				// pushCustomMessage(context,
				// bundle.getString(JPushInterface.EXTRA_ALERT, null));
			}
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			Log.d(TAG, "用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.e(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
			if (connected) {
				sendRegIdToServer();
			}
		} else {
			Log.d(TAG, "Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	// send msg to MainActivity
	private void pushCustomMessage(Context context, Bundle bundle) {
		if (MainActivity.isForeground) {
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Log.e(TAG, "====Msg: " + message + "; Extras: " + extras);

			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			if (!Util.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (null != extraJson && extraJson.length() > 0) {
						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			context.sendBroadcast(msgIntent);
		}
	}

	// send msg to MainActivity
	private void pushCustomMessage(Context context, String message) {
		Log.e(TAG, "====Msg: " + message);

		Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
		msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
		context.sendBroadcast(msgIntent);
	}

	private void sendRegIdToServer() {
		Context context = MyApplication.getAppContext();
		if (context == null) {
			Log.e(TAG, "===========================================");
		} else {
			Log.d(TAG, "===========================================");
		}
		final String email = UserInfoKeeper.readUserInfo(context, UserInfoKeeper.KEY_USER_EMAIL, "");
		final String pwd = UserInfoKeeper.readUserInfo(context, UserInfoKeeper.KEY_USER_PWD, "");
		HttpRequest request = new HttpRequest();

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				Log.d(TAG, "Send Register ID to Server Result: " + result);
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
			}
		});

		Map<String, String> data = new TreeMap<String, String>();
		data.put(ServerKeys.KEY_EMAIL, email);
		data.put(ServerKeys.KEY_PASSWORD, pwd);
		data.put(ServerKeys.KEY_REG_ID, JPushInterface.getRegistrationID(context));

		request.post(ServerKeys.FULL_URL_LOGIN, data);
	}
}
