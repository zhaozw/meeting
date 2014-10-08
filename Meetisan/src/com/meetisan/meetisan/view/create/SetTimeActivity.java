package com.meetisan.meetisan.view.create;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.widget.wheel.MeetisanTimeWheel;
import com.meetisan.meetisan.widget.wheel.MeetisanTimeWheel.OnDateWheelScrollListener;
import com.meetisan.meetisan.widget.wheel.WheelView;

public class SetTimeActivity extends Activity implements OnClickListener, OnDateWheelScrollListener {

	private RelativeLayout mStartLayout, mEndLayout;
	private TextView mStartTxt, mEndTxt;
	private MeetisanTimeWheel mTimeWheel;

	// private Calendar mStartCalendar;
	// private Calendar mEndCalendar;

	private long mStartTime = 0, mEndTime = 0;
	private int setTimeIndex = 0;

	private boolean isSetStartLayout = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_time);

		Intent intent = getIntent();
		setTimeIndex = intent.getIntExtra("SetIndex", 0);
		long mStartTime = intent.getLongExtra("StartTime", -1L);
		long mEndTime = intent.getLongExtra("EndTime", -1L);

		initView(mStartTime, mEndTime);
	}

	private void initView(long startTime, long endTime) {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setText(R.string.set_time);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

		mTimeWheel = (MeetisanTimeWheel) findViewById(R.id.time_wheel);
		mTimeWheel.setOnDateWheelScrollListener(this);
		mStartTxt = (TextView) findViewById(R.id.txt_start_time);
		mEndTxt = (TextView) findViewById(R.id.txt_end_time);

		Calendar calendar = Calendar.getInstance();
		if (startTime != -1) {
			calendar.setTimeInMillis(startTime);
		} else {
			startTime = calendar.getTimeInMillis();
		}
		mStartTime = startTime;
		setTimeTxtValue(mStartTxt, calendar);
		mTimeWheel.setDate(calendar);

		if (endTime == -1) {
			endTime = startTime + 3600 * 1000;
		}
		mEndTime = endTime;
		calendar.setTimeInMillis(endTime);
		setTimeTxtValue(mEndTxt, calendar);

		mStartLayout = (RelativeLayout) findViewById(R.id.layout_start_time);
		mStartLayout.setOnClickListener(this);
		mEndLayout = (RelativeLayout) findViewById(R.id.layout_end_time);
		mEndLayout.setOnClickListener(this);
		isSetStartLayout = true;
		setLayoutBgColor(isSetStartLayout);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_title_left:
			onBackPressed();
			break;
		case R.id.btn_title_right:
			// Intent intent = new Intent();
			// intent.putExtra("time", time);
			// setResult(RESULT_OK, intent);
			// finish();
			break;
		case R.id.layout_start_time:
			isSetStartLayout = true;
			setLayoutBgColor(isSetStartLayout);
			mTimeWheel.setDate(mStartTime);
			break;
		case R.id.layout_end_time:
			isSetStartLayout = false;
			setLayoutBgColor(isSetStartLayout);
			mTimeWheel.setDate(mEndTime);
			break;
		default:
			break;
		}
	}

	private void setLayoutBgColor(boolean isSetStartLayout) {
		if (isSetStartLayout) {
			mStartLayout.setBackgroundColor(getResources().getColor(R.color.bg_create_tag_select_color));
			mEndLayout.setBackgroundColor(Color.TRANSPARENT);
		} else {
			mStartLayout.setBackgroundColor(Color.TRANSPARENT);
			mEndLayout.setBackgroundColor(getResources().getColor(R.color.bg_create_tag_select_color));
		}
	}

	private void setTimeTxtValue(TextView mTextView, Calendar date) {
		if (mTextView == null || date == null) {
			return;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
		String time = formatter.format(date.getTime());
		mTextView.setText(time);
	}

	@Override
	public void onDateScrollFinished(WheelView wheel) {
		int hour = mTimeWheel.getHour();
		int min = mTimeWheel.getMinute();
		Calendar date = mTimeWheel.getDate();

		date.set(Calendar.HOUR_OF_DAY, hour);
		date.set(Calendar.MINUTE, min);
		if (isSetStartLayout) {
			mStartTime = date.getTimeInMillis();
			setTimeTxtValue(mStartTxt, date);
		} else {
			mEndTime = date.getTimeInMillis();
			setTimeTxtValue(mEndTxt, date);
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("StartTime", mStartTime);
		intent.putExtra("EndTime", mEndTime);
		intent.putExtra("SetIndex", setTimeIndex);
		setResult(RESULT_OK, intent);
		finish();
	}
}
