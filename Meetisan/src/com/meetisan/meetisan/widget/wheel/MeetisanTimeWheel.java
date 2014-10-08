package com.meetisan.meetisan.widget.wheel;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.Util;

public class MeetisanTimeWheel extends LinearLayout {

	private static final String TAG = MeetisanTimeWheel.class.getSimpleName();

	private static final int SHOW_DAY_COUNT = 100;
	private ArrayList<Calendar> mDateList = new ArrayList<Calendar>();

	private WheelView mHourWheel;
	private WheelView mMinWheel;
	private WheelView mDateWheel;
	private OnDateWheelScrollListener mListener;

	public MeetisanTimeWheel(Context context) {
		super(context);
	}

	public MeetisanTimeWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.meetisan_time_wheel, this, true);
		initialize();
	}

	private void initialize() {
		String date[] = new String[SHOW_DAY_COUNT];
		Calendar calendar = Calendar.getInstance();
		int curHour = calendar.get(Calendar.HOUR_OF_DAY);
		int curMin = calendar.get(Calendar.MINUTE);

		for (int i = 0; i < SHOW_DAY_COUNT; i++) {
			Calendar tmpCalendar = Calendar.getInstance();
			tmpCalendar.setTimeInMillis(calendar.getTimeInMillis());
			date[i] = getFormatDate(tmpCalendar, i);
			mDateList.add(tmpCalendar);

			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		mHourWheel = (WheelView) findViewById(R.id.wheel_hour);
		mHourWheel.setAdapter(new NumericWheelAdapter(0, 23, null));
		mHourWheel.setCyclic(false);
		mHourWheel.setCurrentItem(curHour);
		mHourWheel.addScrollingListener(new DateWheelScrollListener());

		mMinWheel = (WheelView) findViewById(R.id.wheel_min);
		mMinWheel.setAdapter(new NumericWheelAdapter(0, 59, null));
		mMinWheel.setCyclic(false);
		mMinWheel.setCurrentItem(curMin);
		mMinWheel.addScrollingListener(new DateWheelScrollListener());

		mDateWheel = (WheelView) findViewById(R.id.wheel_date);
		mDateWheel.setAdapter(new ArrayWheelAdapter<String>(date, SHOW_DAY_COUNT));
		mDateWheel.setCyclic(false);
		mDateWheel.setCurrentItem(0);
		mDateWheel.addScrollingListener(new DateWheelScrollListener());
	}

	private String getFormatDate(Calendar calendar, int i) {
		String date = null;

		if (i == 0) {
			date = "Today";
		} else {
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int week = calendar.get(Calendar.DAY_OF_WEEK);

			date = Util.WEEK_ABBREVIATION[week - 1] + "  " + day + "  " + Util.MONTH_ABBREVIATION[month];
		}

		return date;
	}

	public void setHour(int hour) {
		mHourWheel.setCurrentItem(hour);
	}

	public void setMinute(int min) {
		mMinWheel.setCurrentItem(min);
	}

	public void setDate(long time) {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(time);
		setDate(date);
	}

	public void setDate(Calendar date) {
		int hour = date.get(Calendar.HOUR_OF_DAY);
		int min = date.get(Calendar.MINUTE);
		setHour(hour);
		setMinute(min);
		for (int i = 0; i < mDateList.size(); i++) {
			Calendar tmp = mDateList.get(i);
			if (date.get(Calendar.YEAR) == tmp.get(Calendar.YEAR)
					&& date.get(Calendar.MONTH) == tmp.get(Calendar.MONTH)
					&& date.get(Calendar.DAY_OF_MONTH) == tmp.get(Calendar.DAY_OF_MONTH)) {
				mDateWheel.setCurrentItem(i);
				break;
			}
		}
	}

	public int getHour() {
		return mHourWheel.getCurrentItem();
	}

	public int getMinute() {
		return mMinWheel.getCurrentItem();
	}

	public Calendar getDate() {
		int index = mDateWheel.getCurrentItem();
		Log.d(TAG, "Date Index: " + index);
		return mDateList.get(index);
	}

	public void setOnDateWheelScrollListener(OnDateWheelScrollListener mListener) {
		this.mListener = mListener;
	}

	public class DateWheelScrollListener implements OnWheelScrollListener {

		@Override
		public void onScrollingStarted(WheelView wheel) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			// TODO Auto-generated method stub
			mListener.onDateScrollFinished(wheel);
		}

	}

	public interface OnDateWheelScrollListener {
		public void onDateScrollFinished(WheelView wheel);
	}
}
