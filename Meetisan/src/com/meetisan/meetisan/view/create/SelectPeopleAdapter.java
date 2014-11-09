package com.meetisan.meetisan.view.create;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.HttpBitmap;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.CircleImageView;

public class SelectPeopleAdapter extends BaseAdapter {
	private static final String TAG = SelectPeopleAdapter.class.getSimpleName();

	private LayoutInflater inflater;
	private List<PeopleInfo> peopleData = new ArrayList<PeopleInfo>();
	private HttpBitmap httpBitmap;
	private List<Integer> mSelectList = new ArrayList<Integer>();
	private List<Long> mSelectedIDs = new ArrayList<Long>();

	public SelectPeopleAdapter(Context mContext, List<PeopleInfo> peopleData, List<Long> selectedIDs) {
		inflater = LayoutInflater.from(mContext);
		this.peopleData = peopleData;
		this.mSelectedIDs = selectedIDs;
		httpBitmap = new HttpBitmap(mContext);
		initSelectList();
	}

	private void initSelectList() {
		mSelectList.clear();
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
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_listview_select_people, parent, false);

			holder.mCircleImage = (CircleImageView) convertView.findViewById(R.id.iv_portrait);
			holder.mNameTxt = (TextView) convertView.findViewById(R.id.txt_name);
			holder.mCollegeTxt = (TextView) convertView.findViewById(R.id.txt_college);
			holder.mDistanceTxt = (TextView) convertView.findViewById(R.id.txt_distance);
			holder.mTagOneTxt = (TextView) convertView.findViewById(R.id.txt_tag_one);
			holder.mTagTwoTxt = (TextView) convertView.findViewById(R.id.txt_tag_two);
			holder.mTagThreeTxt = (TextView) convertView.findViewById(R.id.txt_tag_three);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_select);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PeopleInfo mPeopleInfo = peopleData.get(position);

		// 防止图片错位，先设置为默认图片
		holder.mCircleImage.setImageResource(R.drawable.portrait_person_default);

		if (mPeopleInfo.getAvatarUri() != null) {
			httpBitmap.displayBitmap(holder.mCircleImage, mPeopleInfo.getAvatarUri());
		}
		holder.mNameTxt.setText(Util.formatOutput(mPeopleInfo.getName()));
		holder.mCollegeTxt.setText(Util.formatOutput(mPeopleInfo.getUniversity()));
		int distance = (int) mPeopleInfo.getDistance();
		if (distance >= 1000) {
			holder.mDistanceTxt.setText((int) distance / 1000 + "km");
		} else if (distance >= 0) {
			holder.mDistanceTxt.setText((int) distance + "m");
		} else {
			holder.mDistanceTxt.setText("");
		}
		List<TagInfo> tagsList = mPeopleInfo.getTopTags();
		int tagsCount = tagsList.size();
		if (tagsCount >= 1) {
			holder.mTagOneTxt.setText(tagsList.get(0).getTitle());
			holder.mTagOneTxt.setVisibility(View.VISIBLE);
		}
		if (tagsCount >= 2) {
			holder.mTagTwoTxt.setText(tagsList.get(1).getTitle());
			holder.mTagTwoTxt.setVisibility(View.VISIBLE);
		}
		if (tagsCount >= 3) {
			holder.mTagThreeTxt.setText(tagsList.get(2).getTitle());
			holder.mTagThreeTxt.setVisibility(View.VISIBLE);
		}
		holder.checkBox.setChecked(mSelectList.contains(position));

		return convertView;
	}

	public void toggleSelect(int position) {
		int index = mSelectList.indexOf(position);
		if (index != -1) {
			mSelectList.remove(index);
			Log.e(TAG, "Remove Item: index=" + position);
		} else {
			mSelectList.add(position);
			Log.e(TAG, "Add Item:  index=" + position);
		}

		notifyDataSetChanged();
	}

	public void notifyDataSetChanged(boolean b) {
		mSelectList.clear();
		for (int i = 0; i < peopleData.size(); i++) {
			long id = peopleData.get(i).getId();
			for (Long l : mSelectedIDs) {
				if (id == l) {
					mSelectList.add(i);
					break;
				}
			}
		}

		notifyDataSetChanged();
	}

	public List<PeopleInfo> getSelectList() {
		List<PeopleInfo> mSelectPeople = new ArrayList<PeopleInfo>();
		for (Integer i : mSelectList) {
			mSelectPeople.add(peopleData.get(i));
		}

		return mSelectPeople;
	}

	static class ViewHolder {
		CircleImageView mCircleImage;
		CheckBox checkBox;
		TextView mNameTxt;
		TextView mCollegeTxt;
		TextView mDistanceTxt;
		TextView mTagOneTxt;
		TextView mTagTwoTxt;
		TextView mTagThreeTxt;
	}

}