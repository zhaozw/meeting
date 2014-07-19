package com.meetisan.meetisan.view.tags;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.android.segmented.SegmentedGroup;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.TagCategory;
import com.meetisan.meetisan.model.TagCategoryAdapter;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.model.TagsAdapter;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.listview.swipe.SwipeListView;

public class TagsActivity extends Activity {

	private SwipeListView mTagsListView, mTagCategoryListView;
	private List<TagInfo> mTagsData = new ArrayList<TagInfo>();
	private List<TagCategory> mTagCategoryData = new ArrayList<TagCategory>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tags);

		initTagsData();
		initTagCategoryData();
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
				if (checkedId == R.id.radio_my_tags) {
					mTagCategoryListView.setVisibility(View.GONE);
					mTagsListView.setVisibility(View.VISIBLE);
				} else {
					mTagsListView.setVisibility(View.GONE);
					mTagCategoryListView.setVisibility(View.VISIBLE);
				}
			}
		});

		mTagsListView = (SwipeListView) findViewById(R.id.list_my_tags);
		TagsAdapter mTagsAdapter = new TagsAdapter(this, mTagsData);
		mTagsListView.setAdapter(mTagsAdapter);
		mTagsListView.setVisibility(View.VISIBLE);
		mTagsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				ToastHelper.showToast("Click " + arg2 + " Item");
			}
		});

		mTagCategoryListView = (SwipeListView) findViewById(R.id.list_tags_category);
		TagCategoryAdapter mCategoryAdapter = new TagCategoryAdapter(this, mTagCategoryData);
		mTagCategoryListView.setAdapter(mCategoryAdapter);
		mTagCategoryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				ToastHelper.showToast("Click " + arg2 + " Item");
			}
		});
	}

	private void initTagsData() {
		mTagsData.clear();
		// data for test
		for (int i = 0; i < 15; i++) {
			TagInfo mInfo = new TagInfo();
			mInfo.setName("Tags Name " + i);
			mInfo.setEndorsed(i * 5);
			mInfo.setPeople(i * 3);
			mInfo.setMeetings(i * 8);

			mTagsData.add(mInfo);
		}
	}

	private void initTagCategoryData() {
		mTagCategoryData.clear();
		// data for test
		for (int i = 0; i < 15; i++) {
			TagCategory mCategory = new TagCategory();
			mCategory.setName("Tag Category " + i);
			TagInfo mInfo1 = new TagInfo();
			mInfo1.setName("Tags Name " + i);
			TagInfo mInfo2 = new TagInfo();
			mInfo2.setName("Tags Name " + i * 2);
			TagInfo mInfo3 = new TagInfo();
			mInfo3.setName("Tags Name " + i * 3);
			if (i % 2 == 0) {
				mCategory.addTags(mInfo1);
				mCategory.addTags(mInfo2);
			} else if (i % 3 == 0) {
				mCategory.addTags(mInfo1);
				mCategory.addTags(mInfo2);
				mCategory.addTags(mInfo3);
			} else {
				mCategory.addTags(mInfo1);
			}

			mTagCategoryData.add(mCategory);
		}
	}
}
