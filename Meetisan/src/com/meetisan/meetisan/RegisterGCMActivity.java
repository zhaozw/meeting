package com.meetisan.meetisan;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.meetisan.meetisan.gcm.GCMKeeper;
import com.meetisan.meetisan.utils.ToastHelper;

public class RegisterGCMActivity extends Activity {
	private static final String TAG = RegisterGCMActivity.class.getSimpleName();
	private static final int PLAY_SERVICES_REQUEST = 9000;
	/**
	 * GCM Sender ID, obtained from the Google APIs Console (https://code.google.com/apis/console)
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
		setContentView(R.layout.activity_test_gcm);

		initViews();
		if (checkGooglePlayServices()) {
			registerGCMDevice(SENDER_ID);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		checkGooglePlayServices();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mGCM != null) {
			mGCM.close();
		}
	}

	private void initViews() {
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
		private ProgressDialog mDialog;
		private String senderId;
		private String regId;

		public RegisterGCMTask(Context context, String senderId) {
			this.context = context;
			this.senderId = senderId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog = ProgressDialog.show(context,
					context.getResources().getString(R.string.title_register_gcm_device), context
							.getResources().getString(R.string.content_register_gcm_device), true,
					false);
			regId = GCMKeeper.readGCMRegistrationId(context, null);
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// if regId is exist in SharedPreference
			if (regId != null) {
				Log.d(TAG, "The device has registered");
				return regId;
			}

			try {
				Log.d(TAG, "The device has not registered, register now ...");
				regId = getGCMInstance().register(senderId);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return regId;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d(TAG, "Register device over , RegId = " + regId);
			mDialog.dismiss();
			if (regId != null) {
				GCMKeeper.writeGCMRegistrationId(context, regId);
				ToastHelper.showToast("Register device success: id = " + regId);
			} else {
				ToastHelper.showToast("Register device failure");
			}
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it doesn't, display a
	 * dialog that allows users to download the APK from the Google Play Store or enable it in the
	 * device's system settings.
	 */
	private boolean checkGooglePlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_REQUEST)
						.show();
			} else {
				Log.e(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

}
