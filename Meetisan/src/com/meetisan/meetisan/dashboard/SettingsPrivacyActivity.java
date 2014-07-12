package com.meetisan.meetisan.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.SettingsKeeper;
import com.meetisan.meetisan.widget.LabelWithSwitchButton;

public class SettingsPrivacyActivity extends Activity implements OnClickListener {

	private LabelWithSwitchButton mTagsBtn, mCreateBtn, mAttendBtn;
	private boolean isPublicTags = true;
	private boolean isPublicMeetingsCreated = true;
	private boolean isPublicMeetingsAttended = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings_privacy);

		initView();
		setPreferValue();
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.privacy);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_icon_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

		mTagsBtn = (LabelWithSwitchButton) findViewById(R.id.switch_tags);
		mCreateBtn = (LabelWithSwitchButton) findViewById(R.id.switch_create);
		mAttendBtn = (LabelWithSwitchButton) findViewById(R.id.switch_attend);
	}

	private void setPreferValue() {
		isPublicTags = SettingsKeeper.readPreferSettings(this, SettingsKeeper.KEY_PRIVACY_MY_TAGS, isPublicTags);
		mTagsBtn.setChecked(isPublicTags);
		isPublicMeetingsCreated = SettingsKeeper.readPreferSettings(this, SettingsKeeper.KEY_PRIVACY_MEETINGS_CREATED,
				isPublicMeetingsCreated);
		mCreateBtn.setChecked(isPublicMeetingsCreated);
		isPublicMeetingsAttended = SettingsKeeper.readPreferSettings(this,
				SettingsKeeper.KEY_PRIVACY_MEETINGS_ATTENDED, isPublicMeetingsAttended);
		mAttendBtn.setChecked(isPublicMeetingsAttended);
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
		isPublicTags = mTagsBtn.isChecked();
		SettingsKeeper.writePreferSettings(this, SettingsKeeper.KEY_PRIVACY_MY_TAGS, isPublicTags);
		isPublicMeetingsCreated = mCreateBtn.isChecked();
		SettingsKeeper.writePreferSettings(this, SettingsKeeper.KEY_PRIVACY_MEETINGS_CREATED, isPublicMeetingsCreated);
		isPublicMeetingsAttended = mAttendBtn.isChecked();
		SettingsKeeper
				.writePreferSettings(this, SettingsKeeper.KEY_PRIVACY_MEETINGS_ATTENDED, isPublicMeetingsAttended);
	}
}
