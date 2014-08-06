package com.meetisan.meetisan.view.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.SettingsKeeper;
import com.meetisan.meetisan.widget.LabelWithIcon;
import com.meetisan.meetisan.widget.LabelWithSwitchButton;

public class SettingsPushActivity extends Activity implements OnClickListener {

	private LabelWithIcon mReminder1Label, mReminder2Label;
	private LabelWithSwitchButton mJoinBtn, mInvitationBtn;
	private boolean isPushSomeoneJoin = true;
	private boolean isPushInviteMeeting = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings_push);

		initView();
		setPreferValue();
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setText(R.string.push_notifications);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

		mReminder1Label = (LabelWithIcon) findViewById(R.id.btn_reminders_1);
		mReminder2Label = (LabelWithIcon) findViewById(R.id.btn_reminders_2);

		mJoinBtn = (LabelWithSwitchButton) findViewById(R.id.switch_joins);
		mInvitationBtn = (LabelWithSwitchButton) findViewById(R.id.switch_invitation);
	}

	private void setPreferValue() {
		isPushSomeoneJoin = SettingsKeeper.readPreferSettings(this, SettingsKeeper.KEY_PUSH_MEETINGS_JOINS,
				isPushSomeoneJoin);
		mJoinBtn.setChecked(isPushSomeoneJoin);
		isPushInviteMeeting = SettingsKeeper.readPreferSettings(this, SettingsKeeper.KEY_PUSH_MEETINGS_INVITATION,
				isPushInviteMeeting);
		mInvitationBtn.setChecked(isPushInviteMeeting);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
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
		isPushSomeoneJoin = mJoinBtn.isChecked();
		SettingsKeeper.writePreferSettings(this, SettingsKeeper.KEY_PUSH_MEETINGS_JOINS, isPushSomeoneJoin);
		isPushInviteMeeting = mInvitationBtn.isChecked();
		SettingsKeeper.writePreferSettings(this, SettingsKeeper.KEY_PUSH_MEETINGS_INVITATION, isPushInviteMeeting);
	}
}
