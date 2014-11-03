package com.meetisan.meetisan.view.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.widget.wheel.ArrayWheelAdapter;
import com.meetisan.meetisan.widget.wheel.WheelView;

/**
 * SettingsUserBirthdayActivity is a custom WheelView, used to set the birthday,
 * the returned result format is 1991-08-19, through startActivityForResult to
 * call the Activity, and through REQUEST_CODE and RESPONSE_NAME_VALUE to get
 * results.
 * 
 * @author shz
 * 
 */
public class SettingsGenderActivity extends Activity {

	public static final int REQUEST_CODE = 0x1004;
	public static final String RESPONSE_NAME_VALUE = "gender";

	private WheelView mGenderWheel;
	private String[] mGenders = { "Female", "Male" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_set_user_gender);

		initView();
	}

	private void initView() {
		mGenderWheel = (WheelView) findViewById(R.id.wheel_gender);
		mGenderWheel.setAdapter(new ArrayWheelAdapter<String>(mGenders));
		mGenderWheel.setCyclic(false);

		Button mCompleteBtn = (Button) findViewById(R.id.btn_complete);
		mCompleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent();
				mIntent.putExtra(RESPONSE_NAME_VALUE, mGenderWheel.getCurrentItem());
				SettingsGenderActivity.this.setResult(0, mIntent);
				finish();
			}
		});
	}

}