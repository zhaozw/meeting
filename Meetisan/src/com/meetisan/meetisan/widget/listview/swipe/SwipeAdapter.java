package com.meetisan.meetisan.widget.listview.swipe;

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

public class SwipeAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private List<String> data;

	private onRightItemClickListener mListener = null;

	public SwipeAdapter(Context mContext, List<String> data) {
		inflater = LayoutInflater.from(mContext);
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
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
			convertView = inflater.inflate(R.layout.item_swipe_listview,
					parent, false);
			holder = new ViewHolder();
			holder.mLeftItem = (RelativeLayout) convertView.findViewById(R.id.item_left);
			holder.mRightItem = (RelativeLayout) convertView.findViewById(R.id.item_right);
			holder.mTitleTxt = (TextView) convertView.findViewById(R.id.txt_title);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.mTitleTxt.setText(data.get(position));
		holder.mRightItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				holder.mRightItem.setVisibility(View.GONE); // to ensure that A is hiding
				data.remove(position); // remove list item
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
		TextView mTitleTxt;
	}

	public void setOnRightItemClickListener(onRightItemClickListener listener) {
		mListener = listener;
	}

	public interface onRightItemClickListener {
		void onRightItemClick(View view, int position);
	}
}
