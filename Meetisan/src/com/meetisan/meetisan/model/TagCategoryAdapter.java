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

public class TagCategoryAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<TagCategory> categoryData;

	private onRightItemClickListener mListener = null;

	public TagCategoryAdapter(Context mContext, List<TagCategory> categoryData) {
		inflater = LayoutInflater.from(mContext);
		this.categoryData = categoryData;
	}

	@Override
	public int getCount() {
		return categoryData.size();
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
			convertView = inflater.inflate(R.layout.item_listview_tags_category, parent, false);
			holder = new ViewHolder();
			holder.mLeftItem = (RelativeLayout) convertView.findViewById(R.id.item_left);
			holder.mRightItem = (RelativeLayout) convertView.findViewById(R.id.item_right);
			holder.mNameTxt = (TextView) convertView.findViewById(R.id.txt_name);
			holder.mOneTxt = (TextView) convertView.findViewById(R.id.txt_tag_one);
			holder.mTwoTxt = (TextView) convertView.findViewById(R.id.txt_tag_two);
			holder.mThreeTxt = (TextView) convertView.findViewById(R.id.txt_tag_three);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		TagCategory mTagCategory = categoryData.get(position);

		holder.mNameTxt.setText(mTagCategory.getTitle());

		List<TagInfo> tagsList = mTagCategory.getTags();
		int tagsCount = tagsList.size();
		if (tagsCount >= 1) {
			holder.mOneTxt.setText(tagsList.get(0).getTitle());
			holder.mOneTxt.setVisibility(View.VISIBLE);
		}
		if (tagsCount >= 2) {
			holder.mTwoTxt.setText(tagsList.get(1).getTitle());
			holder.mTwoTxt.setVisibility(View.VISIBLE);
		}
		if (tagsCount >= 3) {
			holder.mThreeTxt.setText(tagsList.get(2).getTitle());
			holder.mThreeTxt.setVisibility(View.VISIBLE);
		}

		holder.mRightItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				holder.mRightItem.setVisibility(View.GONE);
				categoryData.remove(position); // remove list item
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
		TextView mNameTxt;
		TextView mOneTxt;
		TextView mTwoTxt;
		TextView mThreeTxt;
	}

	public void setOnRightItemClickListener(onRightItemClickListener listener) {
		mListener = listener;
	}

	public interface onRightItemClickListener {
		void onRightItemClick(View view, int position);
	}
}