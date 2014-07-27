package com.meetisan.meetisan.model;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.widget.CircleImageView;

public class TagsAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<TagInfo> tagData;

	private float mDownX, mUpX;
	private RelativeLayout mDeleteLayout;

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
			convertView = inflater.inflate(R.layout.item_listview_tags_tag, parent, false);
			holder = new ViewHolder();
			holder.mRightLayout = (RelativeLayout) convertView.findViewById(R.id.item_right);
			holder.mCircleImage = (CircleImageView) convertView.findViewById(R.id.iv_portrait);
			holder.mNameTxt = (TextView) convertView.findViewById(R.id.txt_name);
			holder.mEndoredTxt = (TextView) convertView.findViewById(R.id.txt_endorsed);
			holder.mPeopleTxt = (TextView) convertView.findViewById(R.id.txt_people);
			holder.mMeetingsTxt = (TextView) convertView.findViewById(R.id.txt_meetings);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// convertView.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View mView, MotionEvent mEvent) {
		// switch (mEvent.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// mDownX = mEvent.getX();
		// break;
		// case MotionEvent.ACTION_UP:
		// mUpX = mEvent.getX();
		// if (mDeleteLayout == null) {
		// // 如果手指按下和抬起的水平距离的绝对值>10,则认为发生了滑动
		// if (Math.abs(mDownX - mUpX) > 10) {
		// mDeleteLayout = holder.mRightLayout;
		// ShowViewAnimation(mDeleteLayout);
		// Log.d("TagsAdapter", "======show=====");
		// return true;
		// } else {
		// return false;
		// }
		// } else {
		// HideViewAnimation(mDeleteLayout);
		// mDeleteLayout = null;
		// Log.d("TagsAdapter", "======hide=====");
		// return true;
		// }
		// // break;
		// }
		// return true;
		// }
		// });
		// holder.mRightLayout.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// HideViewAnimation(holder.mRightLayout);// 点击删除按钮后，影藏按钮
		// tagData.remove(position); // 把数据源里面相应数据删除
		// notifyDataSetChanged(); // 删除数据，加动画
		// }
		// });

		TagInfo mTagInfo = tagData.get(position);
		if (mTagInfo.getLogo() != null) {
			holder.mCircleImage.setImageBitmap(mTagInfo.getLogo());
		}
		holder.mNameTxt.setText(mTagInfo.getTitle());
		holder.mEndoredTxt.setText(String.valueOf(mTagInfo.getEndorsed()));
		holder.mPeopleTxt.setText(String.valueOf(mTagInfo.getPeople()));
		holder.mMeetingsTxt.setText(String.valueOf(mTagInfo.getMeetings()));

		return convertView;
	}

	// // 显示按钮的渐变动画
	// public void ShowViewAnimation(View v) {
	// Animation Ani_Alpha = new AlphaAnimation(0.1f, 1.0f);
	// Ani_Alpha.setDuration(400);
	// v.setAnimation(Ani_Alpha);
	// v.setVisibility(View.VISIBLE);
	// }
	//
	// // 隐藏按钮的渐变动画
	// public void HideViewAnimation(View v) {
	// Animation Ani_Alpha = new AlphaAnimation(1.0f, 0.1f);
	// Ani_Alpha.setDuration(300);
	// v.setAnimation(Ani_Alpha);
	// v.setVisibility(View.GONE);
	// }

	static class ViewHolder {
		RelativeLayout mRightLayout;
		CircleImageView mCircleImage;
		TextView mNameTxt;
		TextView mEndoredTxt;
		TextView mPeopleTxt;
		TextView mMeetingsTxt;
	}

}