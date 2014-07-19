package com.meetisan.meetisan.view.tags;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.android.segmented.SegmentedGroup;
import com.meetisan.meetisan.R;

public class TagsActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tags);

		initView();
	}

	private void initView() {
		SegmentedGroup mTagsGroup = (SegmentedGroup) findViewById(R.id.group_tags);
		mTagsGroup.setTintColor(getResources().getColor(R.color.segment_group_bg_check),
				getResources().getColor(R.color.segment_group_text_check));
		mTagsGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

			}
		});
	}

}
