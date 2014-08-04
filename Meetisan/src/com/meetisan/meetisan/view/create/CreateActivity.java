package com.meetisan.meetisan.view.create;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.TagInfo;

public class CreateActivity extends FragmentActivity implements OnClickListener,
		OnFragmentInteractionListener {
	// private static final String TAG = CreateActivity.class.getSimpleName();
	private ImageButton mLeftButton;
	private TextView mTitleTextView;
	private Button mRightButton;
	private final int totoalSteps = 3;
	/**
	 * step index start from 1
	 */
	private int mCurrentStepIndex = 1;
	private String[] mStepsTitleText;
	private CreateStep1Fragment mCreateStep1Fragment;
	private CreateStep2Fragment mCreateStep2Fragment;
	private CreateStep3Fragment mCreateStep3Fragment;

	private CreateDoneFragment	mCreateDoneFragment;
	
	private long meetPersonID = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		Log.d("CreateActivity", "On Create");
		
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			meetPersonID = bundle.getLong("PersonID", -1L);
			Log.d("CreateActivity", "Meet Person ID: " + meetPersonID);
		}
		
		initTitle();
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
			String string2 = String.format(getString(R.string.format_step_index), stepIndex,
					totoalSteps);
			mTitleTextView.setText(mStepsTitleText[stepIndex - 1] + string2);
			mRightButton.setVisibility(View.VISIBLE);
			mRightButton.setText(R.string.create);
			break;
		case 4:
			String string3 = getString(R.string.create_successful);
			mTitleTextView.setText(string3);
			mRightButton.setVisibility(View.GONE);
			break;
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

	public void showFirstFragment() {
		mCurrentStepIndex = 1;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (mCreateStep1Fragment == null) {
			mCreateStep1Fragment = new CreateStep1Fragment();
			transaction.add(R.id.fl_fragment_container, mCreateStep1Fragment);
		} else {
			transaction.show(mCreateStep1Fragment);
		}
		transaction.commit();
		setTitleViews(mCurrentStepIndex);
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
		case 3:
			transaction.hide(mCreateStep3Fragment);
			break;
		case 4:
			transaction.hide(mCreateDoneFragment);
			break;
		default:
			break;
		}

		switch (id) {
		case R.id.btn_title_left:
			mCurrentStepIndex--;
			break;
		case R.id.btn_title_right:
			if (!checkUserInput(mCurrentStepIndex)) {
				return;
			}
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
		case 3:
			if (mCreateStep3Fragment == null) {
				mCreateStep3Fragment = new CreateStep3Fragment();
				transaction.add(R.id.fl_fragment_container, mCreateStep3Fragment);
			} else {
				transaction.show(mCreateStep3Fragment);
			}
			break;
		case 4:
			if (mCreateDoneFragment == null) {
				mCreateDoneFragment = new CreateDoneFragment();
				transaction.add(R.id.fl_fragment_container, mCreateDoneFragment);
			} else {
				transaction.show(mCreateDoneFragment);
			}
			break;
		default:
			break;
		}
		transaction.commit();
		setTitleViews(mCurrentStepIndex);
	}

	private boolean checkUserInput(int index) {
		switch (index) {
		case 1:
			return mCreateStep1Fragment.checkUserInput();
		case 2:
			return mCreateStep2Fragment.checkUserInput();
		case 3:

		default:
			break;
		}
		return true;
	}

	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
	}

	public Map<String, Object> getData() {
		Map<String, Object> data = new TreeMap<String, Object>();
		if (mCreateStep1Fragment != null) {
			data.putAll(mCreateStep1Fragment.getData());
		}
		
		if (mCreateStep3Fragment != null) {
			data.putAll(mCreateStep3Fragment.getData());
		}
		return data;
	}
	
	public List<TagInfo> getTagInfos() {
		return mCreateStep2Fragment.getData();
	}
}
