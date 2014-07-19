package com.meetisan.meetisan.model;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.widget.CircleImageView;

public class TagsAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<TagInfo> tagData;

	private onRightItemClickListener mListener = null;

	public TagsAdapter(Context mContext, List<TagInfo> tagData) {
		inflater = LayoutInflater.from(mContext);
		this.tagData = tagData;
	}

	@Override
	public int getCount() {
		return tagData.size();
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
			convertView = inflater.inflate(R.layout.item_listview_tags, parent, false);
			holder = new ViewHolder();
			holder.mLeftItem = (RelativeLayout) convertView.findViewById(R.id.item_left);
			holder.mRightItem = (RelativeLayout) convertView.findViewById(R.id.item_right);
			holder.mCircleImage = (CircleImageView) convertView.findViewById(R.id.iv_portrait);
			holder.mNameTxt = (TextView) convertView.findViewById(R.id.txt_name);
			holder.mEndoredTxt = (TextView) convertView.findViewById(R.id.txt_endorsed);
			holder.mPeopleTxt = (TextView) convertView.findViewById(R.id.txt_people);
			holder.mMeetingsTxt = (TextView) convertView.findViewById(R.id.txt_meetings);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		TagInfo mTagInfo = tagData.get(position);

		holder.mCircleImage.setImageResource(R.drawable.portrait);
		holder.mNameTxt.setText(mTagInfo.getName());
		holder.mEndoredTxt.setText(String.valueOf(mTagInfo.getEndorsed()));
		holder.mPeopleTxt.setText(String.valueOf(mTagInfo.getPeople()));
		holder.mMeetingsTxt.setText(String.valueOf(mTagInfo.getMeetings()));

		holder.mRightItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				holder.mRightItem.setVisibility(View.GONE);
				tagData.remove(position); // remove list item
				if (mListener != null) {
					mListener.onRightItemClick(holder.mRightItem, position);
				}
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	static class ViewHolder {
		RelativeLayout mLeftItem;
		RelativeLayout mRightItem;
		CircleImageView mCircleImage;
		TextView mNameTxt;
		TextView mEndoredTxt;
		TextView mPeopleTxt;
		TextView mMeetingsTxt;
	}

	public void setOnRightItemClickListener(onRightItemClickListener listener) {
		mListener = listener;
	}

	public interface onRightItemClickListener {
		void onRightItemClick(View view, int position);
	}
}