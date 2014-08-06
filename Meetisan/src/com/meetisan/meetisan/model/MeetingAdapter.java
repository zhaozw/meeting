package com.meetisan.meetisan.model;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.HttpBitmap;
import com.meetisan.meetisan.widget.CircleImageView;

public class MeetingAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<MeetingInfo> meetingData;
	private HttpBitmap httpBitmap;

	public MeetingAdapter(Context mContext, List<MeetingInfo> meetingData) {
		inflater = LayoutInflater.from(mContext);
		this.meetingData = meetingData;
		httpBitmap = new HttpBitmap(mContext);
	}

	@Override
	public int getCount() {
		return meetingData.size();
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
			convertView = inflater.inflate(R.layout.item_listview_meet_meetings, parent, false);
			holder = new ViewHolder();
			holder.mCircleImage = (CircleImageView) convertView.findViewById(R.id.iv_portrait);
			holder.mNameTxt = (TextView) convertView.findViewById(R.id.txt_name);
			holder.mDistanceTxt = (TextView) convertView.findViewById(R.id.txt_distance);
			holder.mTagOneTxt = (TextView) convertView.findViewById(R.id.txt_tag_one);
			holder.mTagTwoTxt = (TextView) convertView.findViewById(R.id.txt_tag_two);
			holder.mTagThreeTxt = (TextView) convertView.findViewById(R.id.txt_tag_three);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MeetingInfo mMeetingInfo = meetingData.get(position);

		holder.mCircleImage.setImageResource(R.drawable.portrait_default);
		if (mMeetingInfo.getLogoUri() != null) {
			httpBitmap.displayBitmap(holder.mCircleImage, mMeetingInfo.getLogoUri());
		}
		holder.mNameTxt.setText(mMeetingInfo.getTitle());
		holder.mDistanceTxt.setText(String.format("%.2f", mMeetingInfo.getDistance()) + "km");
		List<TagInfo> tagsList = mMeetingInfo.getTags();
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

		return convertView;
	}

	static class ViewHolder {
		CircleImageView mCircleImage;
		TextView mNameTxt;
		TextView mDistanceTxt;
		TextView mTagOneTxt;
		TextView mTagTwoTxt;
		TextView mTagThreeTxt;
	}

}