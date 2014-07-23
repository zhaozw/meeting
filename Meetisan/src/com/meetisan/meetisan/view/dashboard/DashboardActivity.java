package com.meetisan.meetisan.view.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meetisan.meetisan.MyApplication;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.widget.CircleImageView;

public class DashboardActivity extends Activity implements OnClickListener {

	private CircleImageView mPortraitView;
	private TextView mNameTxt;
	private PeopleInfo mUserInfo;
	private Bitmap mUserLogo;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dashboard);

		syncUserInfoFromPerfer();

		initView();

		updateUserInfoUI();
	}

	private void syncUserInfoFromPerfer() {
		mUserInfo = UserInfoKeeper.readUserInfo(this);
		mUserLogo = MyApplication.getmLogoBitmap();
	}

	private void initView() {
		ImageButton mSettingsBtn = (ImageButton) findViewById(R.id.btn_settings);
		mSettingsBtn.setOnClickListener(this);
		mPortraitView = (CircleImageView) findViewById(R.id.iv_portrait);
		mPortraitView.setOnClickListener(this);
		mNameTxt = (TextView) findViewById(R.id.txt_name);
	}

	private void updateUserInfoUI() {
		if (mUserInfo != null) {
			if (mUserInfo.getName() != null) {
				mNameTxt.setText(mUserInfo.getName());
			}
		}

		if (mUserLogo != null) {
			mPortraitView.setImageBitmap(mUserLogo);
		} else {
			mPortraitView.setImageResource(R.drawable.portrait_default);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_settings:
			Intent intent1 = new Intent(this, SettingsActivity.class);
			startActivity(intent1);
			break;
		case R.id.iv_portrait:
			Intent intent2 = new Intent(DashboardActivity.this, PersonProfileActivity.class);
			startActivity(intent2);
			break;

		default:
			break;
		}

	}
}
