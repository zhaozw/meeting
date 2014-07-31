package com.meetisan.meetisan.model;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.CircleImageView;

public class NotificationAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<NotificationInfo> notificationData;

	public NotificationAdapter(Context mContext, List<NotificationInfo> notificationData) {
		inflater = LayoutInflater.from(mContext);
		this.notificationData = notificationData;
	}

	@Override
	public int getCount() {
		return notificationData.size();
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
			convertView = inflater.inflate(R.layout.item_listview_notifications, parent, false);
			holder = new ViewHolder();
			holder.icon = (CircleImageView) convertView.findViewById(R.id.iv_portrait);
			holder.name = (TextView) convertView.findViewById(R.id.txt_name);
			holder.time = (TextView) convertView.findViewById(R.id.txt_time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		NotificationInfo info = notificationData.get(position);
		holder.name.setText(info.getTitle());
		holder.time.setText(Util.convertDateTime(info.getCreateDate()));
		return convertView;
	}

	static class ViewHolder {
		CircleImageView icon;
		TextView name;
		TextView time;
	}

}