package com.meetisan.meetisan.view.create;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meetisan.meetisan.R;

public class CreateActivity extends FragmentActivity implements OnClickListener,
		OnFragmentInteractionListener {
	// private static final String TAG = CreateActivity.class.getSimpleName();
	private ImageButton mLeftButton;
	private TextView mTitleTextView;
	private Button mRightButton;
	private FrameLayout mContainerLayout;
	private final int totoalSteps = 3;
	/**
	 * step index start from 1
	 */
	private int mCurrentStepIndex = 1;
	private String[] mStepsTitleText;
	private CreateStep1Fragment mCreateStep1Fragment;
	private CreateStep2Fragment mCreateStep2Fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		initTitle();

		mContainerLayout = (FrameLayout) findViewById(R.id.fl_fragment_container);
	}

	private void initTitle() {
		mLeftButton = (ImageButton) findViewById(R.id.btn_title_left);
		mTitleTextView = (TextView) findViewById(R.id.tv_title_text);
		mRightButton = (Button) findViewById(R.id.btn_title_right);

		mLeftButton.setVisibility(View.GONE);
		mLeftButton.setOnClickListener(this);
		mStepsTitleText = getResources().getStringArray(R.array.ActivityCreateTitles);
		String indexString = String.format(getString(R.string.format_step_index),
				mCurrentStepIndex, totoalSteps);
		mTitleTextView.setText(mStepsTitleText[mCurrentStepIndex - 1] + indexString);
		mRightButton.setText(R.string.next);
		mRightButton.setOnClickListener(this);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		mCreateStep1Fragment = new CreateStep1Fragment();
		if (findViewById(R.id.fl_fragment_container) != null) {
			transaction.add(R.id.fl_fragment_container, mCreateStep1Fragment);
		}
		transaction.commit();
	}

	private void setTitleViews(int stepIndex) {
		switch (stepIndex) {
		case 1:
			mLeftButton.setVisibility(View.GONE);
			String indexString = String.format(getString(R.string.format_step_index), stepIndex,
					totoalSteps);
			mTitleTextView.setText(mStepsTitleText[stepIndex - 1] + indexString);
			mRightButton.setText(R.string.next);
			break;
		case 2:
			mLeftButton.setVisibility(View.VISIBLE);
			String string = String.format(getString(R.string.format_step_index), stepIndex,
					totoalSteps);
			mTitleTextView.setText(mStepsTitleText[stepIndex - 1] + string);
			mRightButton.setText(R.string.next);
			break;
		case 3:
			mLeftButton.setVisibility(View.VISIBLE);
			String string2 = String.format(getString(R.string.format_step_index), stepIndex,
					totoalSteps);
			mTitleTextView.setText(mStepsTitleText[stepIndex - 1] + string2);
			mRightButton.setText(R.string.done);
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_title_left:
			// TODO back
		case R.id.btn_title_right:
			// TODO next
			changeFragment(id);
			break;
		default:
			break;
		}
	}

	private void changeFragment(int id) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		switch (mCurrentStepIndex) {
		case 1:
			transaction.hide(mCreateStep1Fragment);
			break;
		case 2:
			transaction.hide(mCreateStep2Fragment);
			break;
		default:
			break;
		}
		switch (id) {
		case R.id.btn_title_left:
			// TODO back
			mCurrentStepIndex--;
			break;
		case R.id.btn_title_right:
			// TODO next
			mCurrentStepIndex++;
			break;
		default:
			break;
		}
		
		switch (mCurrentStepIndex) {
		case 1:
			if (mCreateStep1Fragment == null) {
				mCreateStep1Fragment = new CreateStep1Fragment();
				transaction.add(R.id.fl_fragment_container, mCreateStep1Fragment);
			} else {
				transaction.show(mCreateStep1Fragment);
			}
			break;
		case 2:
			if (mCreateStep2Fragment == null) {
				mCreateStep2Fragment = new CreateStep2Fragment();
				transaction.add(R.id.fl_fragment_container, mCreateStep2Fragment);
			} else {
				transaction.show(mCreateStep2Fragment);
			}
			break;
		default:
			break;
		}
		transaction.commit();
		setTitleViews(mCurrentStepIndex);
	}

	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
	}

	private FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter(
			getSupportFragmentManager()) {

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 1:
				return new CreateStep1Fragment();
			case 2:
				return new CreateStep2Fragment();
			default:
				return new CreateStep1Fragment();
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// 这里Destroy的是Fragment的视图层次，并不是Destroy Fragment对象
			super.destroyItem(container, position, object);
			Log.i("INFO", "Destroy Item...");
		}
	};
}
