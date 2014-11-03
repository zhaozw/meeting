package com.meetisan.meetisan.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.HttpBitmap;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.CircleImageView;

public class EndorseAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<PeopleInfo> peopleData;
	private List<TagInfo> mSelectTags = new ArrayList<TagInfo>();
	private HttpBitmap httpBitmap;

	public EndorseAdapter(Context mContext, List<PeopleInfo> peopleData) {
		inflater = LayoutInflater.from(mContext);
		this.peopleData = peopleData;
		httpBitmap = new HttpBitmap(mContext);
	}

	@Override
	public int getCount() {
		return peopleData.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_listview_endorse_people, parent, false);
			holder = new ViewHolder();
			holder.mCircleImage = (CircleImageView) convertView.findViewById(R.id.iv_portrait);
			holder.mNameTxt = (TextView) convertView.findViewById(R.id.txt_name);
			// holder.mCollegeTxt = (TextView)
			// convertView.findViewById(R.id.txt_college);
			// holder.mDistanceTxt = (TextView)
			// convertView.findViewById(R.id.txt_distance);
			holder.mOneLayout = (LinearLayout) convertView.findViewById(R.id.layout_tag_one);
			holder.mTwoLayout = (LinearLayout) convertView.findViewById(R.id.layout_tag_two);
			holder.mThreeLayout = (LinearLayout) convertView.findViewById(R.id.layout_tag_three);
			holder.mTagOneTxt = (TextView) convertView.findViewById(R.id.txt_tag_one);
			holder.mTagTwoTxt = (TextView) convertView.findViewById(R.id.txt_tag_two);
			holder.mTagThreeTxt = (TextView) convertView.findViewById(R.id.txt_tag_three);
			holder.mOneCB = (CheckBox) convertView.findViewById(R.id.cb_one);
			holder.mTwoCB = (CheckBox) convertView.findViewById(R.id.cb_two);
			holder.mThreeCB = (CheckBox) convertView.findViewById(R.id.cb_three);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final PeopleInfo mPeopleInfo = peopleData.get(position);

		// 防止图片错位，先设置为默认图片
		holder.mCircleImage.setImageResource(R.drawable.portrait_person_default);

		if (mPeopleInfo.getAvatarUri() != null) {
			httpBitmap.displayBitmap(holder.mCircleImage, mPeopleInfo.getAvatarUri());
		}
		holder.mNameTxt.setText(Util.formatOutput(mPeopleInfo.getName()));
		// holder.mCollegeTxt.setText(Util.formatOutput(mPeopleInfo.getUniversity()));
		// holder.mCollegeTxt.setText("");
		// int distance = (int) mPeopleInfo.getDistance();
		// if (distance >= 1000) {
		// holder.mDistanceTxt.setText((int) distance / 1000 + "km");
		// } else if (distance >= 0) {
		// holder.mDistanceTxt.setText((int) distance + "m");
		// } else {
		// holder.mDistanceTxt.setText("");
		// }
		List<TagInfo> tagsList = mPeopleInfo.getTopTags();
		int tagsCount = tagsList.size();
		if (tagsCount >= 1) {
			holder.mOneCB.setChecked(tagsList.get(0).isEndorsed());
			holder.mTagOneTxt.setText(tagsList.get(0).getTitle());
			holder.mOneLayout.setVisibility(View.VISIBLE);
		}
		if (tagsCount >= 2) {
			holder.mTwoCB.setChecked(tagsList.get(1).isEndorsed());
			holder.mTagTwoTxt.setText(tagsList.get(1).getTitle());
			holder.mTwoLayout.setVisibility(View.VISIBLE);
		}
		if (tagsCount >= 3) {
			holder.mThreeCB.setChecked(tagsList.get(2).isEndorsed());
			holder.mTagThreeTxt.setText(tagsList.get(2).getTitle());
			holder.mThreeLayout.setVisibility(View.VISIBLE);
		}

		holder.mOneCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("----", "----------");
				TagInfo info = mPeopleInfo.getTopTags().get(0);
				if (mSelectTags.contains(info)) {
					mSelectTags.remove(info);
					info.setEndorsed(isChecked);
					Log.d("", "Remove Tags");
				} else {
					info.setEndorsed(isChecked);
					mSelectTags.add(info);
					Log.d("", "Add Tags");
				}
			}
		});
		holder.mTwoCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				TagInfo info = mPeopleInfo.getTopTags().get(1);
				if (mSelectTags.contains(info)) {
					mSelectTags.remove(info);
					info.setEndorsed(isChecked);
				} else {
					info.setEndorsed(isChecked);
					mSelectTags.add(info);
				}
			}
		});
		holder.mThreeCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				TagInfo info = mPeopleInfo.getTopTags().get(2);
				if (mSelectTags.contains(info)) {
					mSelectTags.remove(info);
					info.setEndorsed(isChecked);
				} else {
					info.setEndorsed(isChecked);
					mSelectTags.add(info);
				}
			}
		});

		return convertView;
	}

	static class ViewHolder {
		CircleImageView mCircleImage;
		LinearLayout mOneLayout, mTwoLayout, mThreeLayout;
		TextView mNameTxt;
		// TextView mCollegeTxt;
		TextView mTagOneTxt;
		TextView mTagTwoTxt;
		TextView mTagThreeTxt;

		CheckBox mOneCB, mTwoCB, mThreeCB;
	}

	public List<TagInfo> getSelectTags() {
		return this.mSelectTags;
	}
}