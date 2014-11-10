package com.meetisan.meetisan;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.meetisan.meetisan.database.SettingsKeeper;

public class GuideActivity extends Activity implements OnClickListener, OnPageChangeListener {

	private Button mEnterBtn;
	private ViewPager viewPager;
	private ViewPagerAdapter vAdapter;
	private ArrayList<View> list;
	private static final int[] pics = { R.drawable.pic_guide_1, R.drawable.pic_guide_2, R.drawable.pic_guide_3,
			R.drawable.pic_guide_4, R.drawable.pic_guide_5, R.drawable.pic_guide_6 };
	private ImageView[] points;
	// Record the currently selected location
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);

		SettingsKeeper.writePreferSettings(this, SettingsKeeper.KEY_IS_FIRST_USE_APP, false);

		initViews();
		initData();
	}

	/**
	 * Find views by id
	 */
	private void initViews() {
		mEnterBtn = (Button) findViewById(R.id.btn_enter);
		mEnterBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
				startActivity(intent);
				GuideActivity.this.finish();
			}
		});
		list = new ArrayList<View>();
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		vAdapter = new ViewPagerAdapter(list);
	}

	/**
	 * Init data
	 */
	private void initData() {
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setImageResource(pics[i]);
			iv.setScaleType(ScaleType.CENTER_INSIDE);
			list.add(iv);
		}

		viewPager.setAdapter(vAdapter);
		viewPager.setOnPageChangeListener(this);

		initPoint();
	}

	/**
	 * init point
	 */
	private void initPoint() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_guide);
		points = new ImageView[pics.length];
		for (int i = 0; i < pics.length; i++) {
			points[i] = (ImageView) linearLayout.getChildAt(i);
			points[i].setEnabled(true);
			points[i].setOnClickListener(this);
			points[i].setTag(i);
		}
		currentIndex = 0;
		points[currentIndex].setEnabled(false);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		setCurDot(position);
		if (position == pics.length - 1) {
			mEnterBtn.setVisibility(View.VISIBLE);
		} else {
			mEnterBtn.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
	}

	/**
	 * set position of current view
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {
			return;
		}
		viewPager.setCurrentItem(position);
	}

	/**
	 * set location of point
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
			return;
		}
		points[positon].setEnabled(false);
		points[currentIndex].setEnabled(true);
		currentIndex = positon;
	}

	public class ViewPagerAdapter extends PagerAdapter {

		private ArrayList<View> views;

		public ViewPagerAdapter(ArrayList<View> views) {
			this.views = views;
		}

		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}
			return 0;
		}

		@Override
		public Object instantiateItem(View view, int position) {
			((ViewPager) view).addView(views.get(position), 0);
			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View view, Object arg1) {
			return (view == arg1);
		}

		@Override
		public void destroyItem(View view, int position, Object arg2) {
			((ViewPager) view).removeView(views.get(position));
		}
	}
}