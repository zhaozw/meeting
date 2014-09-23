package com.meetisan.meetisan.view.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.LoginActivity;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.utils.DialogUtils;
import com.meetisan.meetisan.utils.DialogUtils.OnDialogClickListener;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.LabelWithIcon;

public class SettingsActivity extends Activity implements OnClickListener {

	private long mUserID = -1L;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings);

		mUserID = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);

		initView();
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setText(R.string.settings);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

		Button mLogoutBtn = (Button) findViewById(R.id.btn_logout);
		mLogoutBtn.setOnClickListener(this);

		LabelWithIcon mLabel = (LabelWithIcon) findViewById(R.id.btn_about);
		mLabel.setOnClickListener(this);
		mLabel = (LabelWithIcon) findViewById(R.id.btn_notify);
		mLabel.setOnClickListener(this);
		mLabel = (LabelWithIcon) findViewById(R.id.btn_push);
		mLabel.setOnClickListener(this);
		mLabel = (LabelWithIcon) findViewById(R.id.btn_privacy);
		mLabel.setOnClickListener(this);
		mLabel = (LabelWithIcon) findViewById(R.id.btn_blocked);
		mLabel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_title_left:
			this.finish();
			break;
		case R.id.btn_notify:
			intent = new Intent(this, SettingsNotifyActivity.class);
			break;
		case R.id.btn_privacy:
			intent = new Intent(this, SettingsPrivacyActivity.class);
			break;
		case R.id.btn_push:
			intent = new Intent(this, SettingsPushActivity.class);
			break;
		case R.id.btn_blocked:
			break;
		case R.id.btn_about:
			intent = new Intent(this, SettingsAboutActivity.class);
			break;
		case R.id.btn_logout:
			logoutMeetisan();
			break;
		default:
			break;
		}

		if (intent != null) {
			startActivity(intent);
		}
	}

	private void logoutMeetisan() {
		DialogUtils.showDialog(SettingsActivity.this, R.string.logout, R.string.logout_tips, R.string.positive,
				R.string.cancel, new OnDialogClickListener() {

					@Override
					public void onClick(boolean isPositiveBtn) {
						if (isPositiveBtn) {
							doLogoutMeetisan();
						}
					}
				});
	}

	private CustomizedProgressDialog mProgressDialog = null;

	private void doLogoutMeetisan() {

		HttpRequest request = new HttpRequest();

		if (mProgressDialog == null) {
			mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);
		} else {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				if (UserInfoKeeper.clearUserInfo(SettingsActivity.this)) {
					Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);
					SettingsActivity.this.finish();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		request.post(ServerKeys.FULL_URL_LOGOUT + "/" + mUserID, null);
		mProgressDialog.show();
	}
}
