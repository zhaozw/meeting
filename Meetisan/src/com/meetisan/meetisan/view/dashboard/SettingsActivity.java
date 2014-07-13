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

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.widget.LabelWithIcon;

public class SettingsActivity extends Activity implements OnClickListener{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings);

		initView();
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.settings);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton)findViewById(R.id.btn_title_icon_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);
		
		Button mLogoutBtn = (Button)findViewById(R.id.btn_logout);
		mLogoutBtn.setOnClickListener(this);
		
		LabelWithIcon mLabel = (LabelWithIcon)findViewById(R.id.btn_about);
		mLabel.setOnClickListener(this);
		mLabel = (LabelWithIcon)findViewById(R.id.btn_notify);
		mLabel.setOnClickListener(this);
		mLabel = (LabelWithIcon)findViewById(R.id.btn_push);
		mLabel.setOnClickListener(this);
		mLabel = (LabelWithIcon)findViewById(R.id.btn_privacy);
		mLabel.setOnClickListener(this);
		mLabel = (LabelWithIcon)findViewById(R.id.btn_blocked);
		mLabel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_title_icon_left:
			this.finish();
			break;
		case R.id.btn_notify:
			intent = new Intent(this, SettingsNotifyActivity.class);
			break;
		case R.id.btn_privacy:
			intent = new Intent(this, SettingsPrivacyActivity.class);
			break;
		case R.id.btn_push:
			break;
		case R.id.btn_blocked:
			break;
		case R.id.btn_about:
			intent = new Intent(this, SettingsAboutActivity.class);
			break;
		case R.id.btn_logout:
			
			break;
		default:
			break;
		}
		
		if(intent != null) {
			startActivity(intent);
		}
	}

}
