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
import android.widget.ImageButton;
import android.widget.TextView;

import com.meetisan.meetisan.R;
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
		mTitleTxt.setText(R.string.about_meetisan);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

		mVersionTxt = (TextView) findViewById(R.id.txt_version);

		LabelWithIcon labelBtn = (LabelWithIcon) findViewById(R.id.btn_score);
		labelBtn.setOnClickListener(this);
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
		default:
			break;
		}
	}

}
