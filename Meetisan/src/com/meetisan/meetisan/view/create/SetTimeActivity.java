package com.meetisan.meetisan.view.create;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.meetisan.meetisan.R;

public class SetTimeActivity extends Activity implements OnClickListener {
	private Button mBackButton;
	private TextView mTitleTextView;
	private Button mSetButton;
	private DatePicker mDatePicker;
	private TimePicker mTimePicker;
	private String title;
	private long time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_time);
		Intent intent = getIntent();
		title  = intent.getStringExtra("title");
		time = intent.getLongExtra("time", 0);
		initView();
	}

	private void initView() {
		mBackButton = (Button) findViewById(R.id.btn_title_left);
		mTitleTextView = (TextView) findViewById(R.id.txt_title);
		mSetButton = (Button) findViewById(R.id.btn_title_right);
		
		mBackButton.setOnClickListener(this);
		mTitleTextView.setText(title);
		mSetButton.setOnClickListener(this);
		
		mDatePicker = (DatePicker) findViewById(R.id.datePicker1);
		mTimePicker = (TimePicker) findViewById(R.id.timePicker1);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {
			
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar calendar2 = Calendar.getInstance();
				calendar2.set(Calendar.YEAR, year);
				calendar2.set(Calendar.MONTH, monthOfYear);
				calendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				time = calendar2.getTimeInMillis();
			}
		});
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		mTimePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				Calendar calendar2 = Calendar.getInstance();
				calendar2.set(Calendar.HOUR_OF_DAY, hourOfDay);
				calendar2.set(Calendar.MINUTE, minute);
				time = calendar2.getTimeInMillis();
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_title_left:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.btn_title_right:
			Intent intent = new Intent();
			intent.putExtra("time", time);
			setResult(RESULT_OK, intent);
			finish();
			break;
		default:
			break;
		}
	}

}
