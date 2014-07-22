package com.meetisan.meetisan.view.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.widget.CircleImageView;

public class DashboardActivity extends Activity implements OnClickListener {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dashboard);

		initView();
	}

	private void initView() {
		ImageButton mSettingsBtn = (ImageButton) findViewById(R.id.btn_settings);
		mSettingsBtn.setOnClickListener(this);
		CircleImageView mPortraitView = (CircleImageView) findViewById(R.id.iv_portrait);
		mPortraitView.setOnClickListener(this);
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
