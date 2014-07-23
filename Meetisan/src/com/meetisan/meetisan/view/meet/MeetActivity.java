package com.meetisan.meetisan.view.meet;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.android.segmented.SegmentedGroup;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.MeetingAdapter;
import com.meetisan.meetisan.model.MeetingInfo;
import com.meetisan.meetisan.model.PeopleAdapter;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.model.TagInfo;

public class MeetActivity extends Activity {
	private static final String TAG = MeetActivity.class.getSimpleName();

	private ListView mPeopleListView, mMeetingsListView;
	private List<PeopleInfo> mPeopleData = new ArrayList<PeopleInfo>();
	private List<MeetingInfo> mMeetingData = new ArrayList<MeetingInfo>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_meet);

		initPeopleData();
		initMeetingData();
		initView();
	}

	private void initView() {
		SegmentedGroup mTagsGroup = (SegmentedGroup) findViewById(R.id.group_meet);
		mTagsGroup.setTintColor(getResources().getColor(R.color.segment_group_bg_check),
				getResources().getColor(R.color.segment_group_text_check));
		mTagsGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.radio_people) {
					mMeetingsListView.setVisibility(View.GONE);
					mPeopleListView.setVisibility(View.VISIBLE);
				} else {
					mPeopleListView.setVisibility(View.GONE);
					mMeetingsListView.setVisibility(View.VISIBLE);
				}
			}
		});

		mPeopleListView = (ListView) findViewById(R.id.list_people);
		PeopleAdapter mPeopleAdapter = new PeopleAdapter(this, mPeopleData);
		mPeopleListView.setAdapter(mPeopleAdapter);
		mPeopleAdapter.notifyDataSetChanged();
		mPeopleListView.setVisibility(View.VISIBLE);

		mMeetingsListView = (ListView) findViewById(R.id.list_meetings);
		MeetingAdapter mMeetingAdapter = new MeetingAdapter(this, mMeetingData);
		mMeetingsListView.setAdapter(mMeetingAdapter);
		mMeetingAdapter.notifyDataSetChanged();
	}

	private void initPeopleData() {
		mPeopleData.clear();
		// data for test
		for (int i = 0; i < 15; i++) {
			PeopleInfo mPeople = new PeopleInfo();

			mPeople.setName("Jacky " + i);
			mPeople.setUniversity("UNSW");
			mPeople.setDistance(150);

			TagInfo mInfo1 = new TagInfo();
			mInfo1.setName("Tags Name " + i);
			TagInfo mInfo2 = new TagInfo();
			mInfo2.setName("Tags Name " + i * 2);
			TagInfo mInfo3 = new TagInfo();
			mInfo3.setName("Tags Name " + i * 3);
			if (i % 2 == 0) {
				mPeople.addTopTag(mInfo1);
				mPeople.addTopTag(mInfo2);
			} else if (i % 3 == 0) {
				mPeople.addTopTag(mInfo1);
				mPeople.addTopTag(mInfo2);
				mPeople.addTopTag(mInfo3);
			} else {
				mPeople.addTopTag(mInfo1);
			}

			mPeopleData.add(mPeople);
		}
	}

	private void initMeetingData() {
		mMeetingData.clear();
		// data for test
		for (int i = 0; i < 15; i++) {
			MeetingInfo mMeeting = new MeetingInfo();

			mMeeting.setName("Party " + i);
			mMeeting.setDistance(150);

			TagInfo mInfo1 = new TagInfo();
			mInfo1.setName("Tags" + i);
			TagInfo mInfo2 = new TagInfo();
			mInfo2.setName("Tags" + i * 2);
			TagInfo mInfo3 = new TagInfo();
			mInfo3.setName("Tags" + i * 3);
			if (i % 2 == 0) {
				mMeeting.addTags(mInfo1);
				mMeeting.addTags(mInfo2);
			} else if (i % 3 == 0) {
				mMeeting.addTags(mInfo1);
				mMeeting.addTags(mInfo2);
				mMeeting.addTags(mInfo3);
			} else {
				mMeeting.addTags(mInfo1);
			}

			mMeetingData.add(mMeeting);
		}
	}
}
