package com.meetisan.meetisan;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.meetisan.meetisan.gcm.GCMKeeper;
import com.meetisan.meetisan.utils.DialogUtils;
import com.meetisan.meetisan.utils.DialogUtils.OnDialogClickListener;

public class LauncherActivity extends Activity {
	private static final String TAG = LauncherActivity.class.getSimpleName();
	/**
	 * LauncherActivity shortest display time
	 */
	private static final int SHORTEST_DISPLAY_TIME = 800;
	/**
	 * GCM Sender ID, obtained from the Google APIs Console
	 * (https://code.google.com/apis/console)
	 * 
	 * @category Account: dev.meetisan@gmail.com
	 * 
	 *           Password: meetisan123
	 * 
	 */
	public static final String SENDER_ID = "647678123306";

	private GoogleCloudMessaging mGCM;
	private RegisterGCMTask mGcmTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_splash);

		if (checkGooglePlayServices()) {
			registerGCMDevice(SENDER_ID);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mGCM != null) {
			mGCM.close();
		}
	}

	private void registerGCMDevice(String senderId) {
		mGcmTask = new RegisterGCMTask(this, senderId);
		mGcmTask.execute();
	}

	// shouldn't call it from the UI thread !!
	// private void unRegisterGCMDevice() {
	// GoogleCloudMessaging mGCM = GoogleCloudMessaging.getInstance(this);
	// try {
	// mGCM.unregister();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	private GoogleCloudMessaging getGCMInstance() {
		if (mGCM == null) {
			mGCM = GoogleCloudMessaging.getInstance(this);
		}

		return mGCM;
	}

	public class RegisterGCMTask extends AsyncTask<Void, Integer, String> {

		private Context context;
		private String senderId;
		private String regId;
		private long starTime = 0, curTime = 0;

		public RegisterGCMTask(Context context, String senderId) {
			this.context = context;
			this.senderId = senderId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			starTime = System.currentTimeMillis();
			regId = GCMKeeper.readGCMRegistrationId(context, null);
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// if regId is exist in SharedPreference
			if (regId == null) {
				try {
					Log.d(TAG, "The device has not registered, register now ...");
					regId = getGCMInstance().register(senderId);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Log.d(TAG, "The device has registered");
			}

			do {
				curTime = System.currentTimeMillis();
				Log.d(TAG, "time = " + (curTime - starTime));
			} while (curTime - starTime < SHORTEST_DISPLAY_TIME);

			return regId;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d(TAG, "Register device over , RegId = " + regId);
			if (regId != null) {
				GCMKeeper.writeGCMRegistrationId(context, regId);
				Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
				startActivity(intent);
				LauncherActivity.this.finish();
			} else {
				DialogUtils.showDialog(LauncherActivity.this, R.string.register_device_to_gcm_failed,
						DialogUtils.RESOURCE_ID_NONE, R.string.exit, new OnDialogClickListener() {

							@Override
							public void onClick(boolean isPositiveBtn) {
								// TODO Auto-generated method stub
								LauncherActivity.this.finish();
							}
						});
			}
		}

	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkGooglePlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				DialogUtils.showDialog(LauncherActivity.this, R.string.google_play_services_missing,
						DialogUtils.RESOURCE_ID_NONE, R.string.exit, new OnDialogClickListener() {

							@Override
							public void onClick(boolean isPositiveBtn) {
								// TODO Auto-generated method stub
								LauncherActivity.this.finish();
							}
						});
			} else {
				Log.e(TAG, "This device is not supported.");
				DialogUtils.showDialog(LauncherActivity.this, R.string.device_not_support_google_play_services,
						DialogUtils.RESOURCE_ID_NONE, R.string.exit, new OnDialogClickListener() {

							@Override
							public void onClick(boolean isPositiveBtn) {
								// TODO Auto-generated method stub
								LauncherActivity.this.finish();
							}
						});
			}

			return false;
		}
		return true;
	}

}
