package com.meetisan.meetisan;

import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import cn.jpush.android.api.JPushInterface;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.meetisan.meetisan.database.SettingsKeeper;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.utils.DialogUtils;
import com.meetisan.meetisan.utils.DialogUtils.OnDialogClickListener;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;

public class LauncherActivity extends Activity {
	private static final String TAG = LauncherActivity.class.getSimpleName();

	/**
	 * LauncherActivity shortest display time
	 */
	private static final int SHORTEST_DISPLAY_TIME = 800;
	private long mStartTime = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_splash);

		mStartTime = System.currentTimeMillis();

		checkLoginState();

	}

	@Override
	public void onResume() {
		JPushInterface.onResume(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		JPushInterface.onPause(this);
		super.onPause();
	}

	private void checkLoginState() {
		// SplashTimeTask timerTask = new SplashTimeTask();
		// timerTask.execute();

		boolean isFirstUse = SettingsKeeper.readPreferSettings(LauncherActivity.this,
				SettingsKeeper.KEY_IS_FIRST_USE_APP, true);
		if (isFirstUse) {
			Intent intent = new Intent(LauncherActivity.this, GuideActivity.class);
			startActivity(intent);
			LauncherActivity.this.finish();
		} else {
			PeopleInfo mUserInfo = UserInfoKeeper.readUserInfo(this);
			if (mUserInfo.getEmail() != null && mUserInfo.getPwd() != null) {
				doLogin(mUserInfo.getEmail(), mUserInfo.getPwd());
			} else {
				Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
				startActivity(intent);
				LauncherActivity.this.finish();
			}
		}
	}

	private void doLogin(final String email, final String pwd) {
		HttpRequest request = new HttpRequest();

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				JSONObject json;
				try {
					PeopleInfo mUserInfo = new PeopleInfo();
					json = new JSONObject(result);
					JSONObject data = json.getJSONObject(ServerKeys.KEY_DATA);
					mUserInfo.setId(data.getLong(ServerKeys.KEY_ID));
					if (!data.isNull(ServerKeys.KEY_NAME)) {
						mUserInfo.setName(data.getString(ServerKeys.KEY_NAME));
					}
					if (!data.isNull(ServerKeys.KEY_AVATAR)) {
						mUserInfo.setAvatarUri(data.getString(ServerKeys.KEY_AVATAR));
					}
					mUserInfo.setEmail(email);
					mUserInfo.setPwd(pwd);
					mUserInfo.setRegId(JPushInterface.getRegistrationID(getApplicationContext()));

					if (UserInfoKeeper.writeUserInfo(LauncherActivity.this, mUserInfo)) {
						Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
						startActivity(intent);
						LauncherActivity.this.finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.app_occurred_exception);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
				startActivity(intent);
				LauncherActivity.this.finish();
			}
		});

		Map<String, String> data = new TreeMap<String, String>();
		data.put(ServerKeys.KEY_EMAIL, email);
		data.put(ServerKeys.KEY_PASSWORD, pwd);
		String registrationID = JPushInterface.getRegistrationID(getApplicationContext());
		Log.e(TAG, "==Do Login, RegID: " + JPushInterface.getRegistrationID(getApplicationContext()));
		if (registrationID != null) {
			data.put(ServerKeys.KEY_REG_ID, registrationID);
		}
		request.post(ServerKeys.FULL_URL_LOGIN, data);
	}

	public class SplashTimeTask extends AsyncTask<Void, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			long mCurTime = System.currentTimeMillis();
			try {
				while (mCurTime - mStartTime < SHORTEST_DISPLAY_TIME) {
					Thread.sleep(100);
					mCurTime = System.currentTimeMillis();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			boolean isFirstUse = SettingsKeeper.readPreferSettings(LauncherActivity.this,
					SettingsKeeper.KEY_IS_FIRST_USE_APP, true);
			Intent intent = null;
			if (isFirstUse) {
				intent = new Intent(LauncherActivity.this, GuideActivity.class);
			} else {
				long mUserID = UserInfoKeeper.readUserInfo(LauncherActivity.this, UserInfoKeeper.KEY_USER_ID, -1L);
				if (mUserID < 0) {
					intent = new Intent(LauncherActivity.this, LoginActivity.class);
				} else {
					intent = new Intent(LauncherActivity.this, MainActivity.class);
				}
			}
			startActivity(intent);
			finish();

		}

	}

	// private void syncUserInfoFromServer() {
	// String email = mUserInfo.getEmail();
	// String pwd = mUserInfo.getPwd();
	// if (email == null || pwd == null) {
	// gotoLoginActivity();
	// return;
	// }
	// getUserInfoFromServer(email, pwd);
	// }
	//

	//
	// private void getUserInfoFromServer(String email, String pwd) {
	// HttpRequest request = new HttpRequest();
	//
	// if (mProgressDialog == null) {
	// mProgressDialog = new CustomizedProgressDialog(this,
	// R.string.loading_userinfo);
	// } else {
	// if (mProgressDialog.isShowing()) {
	// mProgressDialog.dismiss();
	// }
	// }
	//
	// request.setOnHttpRequestListener(new OnHttpRequestListener() {
	//
	// @Override
	// public void onSuccess(String url, String result) {
	// mProgressDialog.dismiss();
	// JSONObject json;
	// try {
	// json = new JSONObject(result);
	// JSONObject data = json.getJSONObject(ServerKeys.KEY_DATA);
	// mUserInfo.setId(data.getLong(ServerKeys.KEY_ID));
	// mUserInfo.setEmail(data.getString(ServerKeys.KEY_EMAIL));
	// mUserInfo.setPwd(data.getString(ServerKeys.KEY_PASSWORD));
	// if (!data.isNull(ServerKeys.KEY_NAME)) {
	// mUserInfo.setName(data.getString(ServerKeys.KEY_NAME));
	// }
	// if (!data.isNull(ServerKeys.KEY_AVATAR)) {
	// mUserInfo.setAvatarUri(data.getString(ServerKeys.KEY_AVATAR));
	// }
	// UserInfoKeeper.writeUserInfo(MainActivity.this, mUserInfo);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// ToastHelper.showToast(R.string.app_occurred_exception);
	// }
	// }
	//
	// @Override
	// public void onFailure(String url, int errorNo, String errorMsg) {
	// mProgressDialog.dismiss();
	// ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
	// reLogin();
	// }
	// });
	//
	// Map<String, String> data = new TreeMap<String, String>();
	// data.put(ServerKeys.KEY_EMAIL, email);
	// data.put(ServerKeys.KEY_PASSWORD, pwd);
	// String registrationID =
	// JPushInterface.getRegistrationID(getApplicationContext());
	// if (registrationID != null) {
	// data.put(ServerKeys.KEY_REG_ID, registrationID);
	// }
	// request.post(ServerKeys.FULL_URL_LOGIN, data);
	// mProgressDialog.show();
	// }

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkGooglePlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				DialogUtils.showDialog(LauncherActivity.this, -1, R.string.google_play_services_missing,
						DialogUtils.RESOURCE_ID_NONE, R.string.exit, new OnDialogClickListener() {

							@Override
							public void onClick(boolean isPositiveBtn) {
								// TODO Auto-generated method stub
								LauncherActivity.this.finish();
							}
						});
			} else {
				Log.e(TAG, "This device is not supported.");
				DialogUtils.showDialog(LauncherActivity.this, -1, R.string.device_not_support_google_play_services,
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
