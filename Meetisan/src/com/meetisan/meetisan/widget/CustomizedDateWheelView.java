package com.meetisan.meetisan.widget;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.meetisan.meetisan.R;

public class CustomizedDateWheelView extends LinearLayout {

	private static final String TAG = CustomizedDateWheelView.class.getSimpleName();

	private static final int YEAR_MAX = Calendar.getInstance().get(Calendar.YEAR) + 100;
	private static final int YEAR_BASE = YEAR_MAX - 200;
	private static final int MONTH_BASE = 1;
	private static final int DAY_BASE = 1;
	private WheelView mMonthWheel;
	private WheelView mDayWheel;
	private WheelView mYearWheel;

	private int default_year ;
	private int default_month;
	private int default_day;

	public CustomizedDateWheelView(Context context) {
		super(context);
	}

	public CustomizedDateWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.customized_wheel, this, true);
		initialize();
	}

	private void initialize() {
		
		SharedPreferences shared = getContext().getSharedPreferences("userinfo",Context.MODE_PRIVATE);
		
		  String[] str  = shared.getString("birthday", "1999-12-16").split("-");
		
		  default_year = Integer.parseInt(str[0]);
		  
		  default_month = Integer.parseInt(str[2]);
		  
		  default_day = Integer.parseInt(str[1]);
		
		mMonthWheel = (WheelView) findViewById(R.id.wheel_middle);
		mMonthWheel.setAdapter(new NumericWheelAdapter(MONTH_BASE, 12, null));
		mMonthWheel.setCyclic(false);
		mMonthWheel.setCurrentItem(default_month - MONTH_BASE);
		mMonthWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				onMonthChanged(newValue);
			}
		});
		mDayWheel = (WheelView) findViewById(R.id.wheel_right);
		int maxDay = getMaxDayInMonth(mMonthWheel.getCurrentItem());
		mDayWheel.setAdapter(new NumericWheelAdapter(DAY_BASE, maxDay, null));
		mDayWheel.setCyclic(false);
		mDayWheel.setCurrentItem(default_day - DAY_BASE );

		mYearWheel = (WheelView) findViewById(R.id.wheel_left);
		mYearWheel.setAdapter(new NumericWheelAdapter(YEAR_BASE, YEAR_MAX, null));
		mYearWheel.setCyclic(false);
		mYearWheel.setCurrentItem((default_year - YEAR_BASE ));
	}

	private void onMonthChanged(int month) {
		updateDayWheel(getMaxDayInMonth(month));
	}

	private void updateDayWheel(int maxDay) {
		int curItem = mDayWheel.getCurrentItem();
		mDayWheel.setAdapter(new NumericWheelAdapter(DAY_BASE, maxDay, null));
		Log.d(TAG, "curItem = " + curItem + "  max = " + maxDay);
		int index = (curItem > (maxDay - DAY_BASE)) ? (maxDay - DAY_BASE) : curItem;

		mDayWheel.setCurrentItem(index);
	}

	private int getMaxDayInMonth(int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month);
		return calendar.getActualMaximum(Calendar.DATE);
	}
}
