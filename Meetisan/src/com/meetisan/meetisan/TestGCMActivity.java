package com.meetisan.meetisan;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.meetisan.meetisan.utils.GCMUtils;

public class TestGCMActivity extends Activity implements OnClickListener {
	private static final String TAG = TestGCMActivity.class.getSimpleName();
	private static final int PLAY_SERVICES_REQUEST = 9000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_test_gcm);

		initViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		checkGooglePlayServices();
	}

	private void initViews() {
		((Button) findViewById(R.id.btn_register)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_unregister)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_register:
			GCMUtils.registerGCMDevice(this);
			break;
		case R.id.btn_unregister:
			GCMUtils.unRegisterGCMDevice(this);
			// GCMUtils.sendGCM(this);
			break;
		default:
			break;
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
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_REQUEST).show();
			} else {
				Log.e(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}
}
