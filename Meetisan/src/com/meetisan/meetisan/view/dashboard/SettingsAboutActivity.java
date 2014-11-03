package com.meetisan.meetisan.view.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.DialogUtils.OnDialogClickListener;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.LabelWithIcon;

public class SettingsAboutActivity extends Activity implements OnClickListener {

	private TextView mVersionTxt;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings_about);

		initView();

		showAppVersion();

	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setText(R.string.settings);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

		mVersionTxt = (TextView) findViewById(R.id.txt_version);

		LabelWithIcon labelBtn = (LabelWithIcon) findViewById(R.id.btn_score);
		labelBtn.setOnClickListener(this);
		labelBtn = (LabelWithIcon) findViewById(R.id.btn_feedbak);
		labelBtn.setOnClickListener(this);

		Button mLogoutBtn = (Button) findViewById(R.id.btn_logout);
		mLogoutBtn.setOnClickListener(this);
	}

	private void showAppVersion() {
		String version = null;
		try {
			version = getVersion();
		} catch (Exception e) {
			version = getResources().getString(R.string.failed_get_version);
			e.printStackTrace();
		}
		mVersionTxt.setText(version);
	}

	private String getVersion() throws Exception {
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		int versionCode = packInfo.versionCode;
		String versionName = packInfo.versionName;
		return versionCode + "." + versionName;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			this.finish();
			break;
		case R.id.btn_score:
			Uri uri = Uri.parse("market://details?id=" + getPackageName());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.btn_feedbak:
			Uri emailUri = Uri.parse("mailto:info@meetisan.com");
			String emailSubject = "Feedback to Meetisan";
			String emailBody = "Enter your feedback content...";
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, emailUri);
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject); // 主题
			emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody); // 正文
			startActivity(Intent.createChooser(emailIntent, "Please select an App to send Email"));
			break;
		case R.id.btn_logout:
			logoutMeetisan();
			break;
		default:
			break;
		}
	}

	private void logoutMeetisan() {
		DialogUtils.showDialog(SettingsAboutActivity.this, R.string.logout, R.string.logout_tips, R.string.positive,
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
				if (UserInfoKeeper.clearUserInfo(SettingsAboutActivity.this)) {
					// Intent intent = new Intent(SettingsAboutActivity.this,
					// LoginActivity.class);
					// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// startActivity(intent);

					Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(
							getBaseContext().getPackageName());
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(i);
					SettingsAboutActivity.this.finish();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		long mUserID = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);
		request.post(ServerKeys.FULL_URL_LOGOUT + "/" + mUserID, null);
		mProgressDialog.show();
	}
}
