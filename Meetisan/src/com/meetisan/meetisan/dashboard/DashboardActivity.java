package com.meetisan.meetisan.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.meetisan.meetisan.R;

public class DashboardActivity extends Activity implements OnClickListener{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_dashboard);
		
		initView();
	}

	private void initView() {
		ImageButton mSettingsBtn = (ImageButton)findViewById(R.id.btn_settings);
		mSettingsBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		
	}
}
