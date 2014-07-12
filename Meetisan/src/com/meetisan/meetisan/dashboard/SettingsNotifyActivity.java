package com.meetisan.meetisan.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.SettingsKeeper;
import com.meetisan.meetisan.widget.LabelWithSwitchButton;

public class SettingsNotifyActivity extends Activity implements OnClickListener {

	private LabelWithSwitchButton mJoinBtn, mInviteBtn;
	private boolean isNotifyJoin = true;
	private boolean isNotifyInvite = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings_notify);

		initView();
		setPreferValue();
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.notify);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_icon_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

		mJoinBtn = (LabelWithSwitchButton) findViewById(R.id.switch_join);
		mInviteBtn = (LabelWithSwitchButton) findViewById(R.id.switch_invitate);
	}

	private void setPreferValue() {
		isNotifyJoin = SettingsKeeper.readPreferSettings(this, SettingsKeeper.KEY_NOTIFY_JOIN, isNotifyJoin);
		mJoinBtn.setChecked(isNotifyJoin);
		isNotifyInvite = SettingsKeeper.readPreferSettings(this, SettingsKeeper.KEY_NOTIFY_INVITE, isNotifyInvite);
		mInviteBtn.setChecked(isNotifyInvite);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_icon_left:
			this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		saveSettings();
	}

	private void saveSettings() {
		isNotifyJoin = mJoinBtn.isChecked();
		SettingsKeeper.writePreferSettings(this, SettingsKeeper.KEY_NOTIFY_JOIN, isNotifyJoin);
		isNotifyInvite = mInviteBtn.isChecked();
		SettingsKeeper.writePreferSettings(this, SettingsKeeper.KEY_NOTIFY_INVITE, isNotifyInvite);
	}
}
