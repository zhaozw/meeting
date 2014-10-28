package com.meetisan.meetisan.model;

import java.text.ParseException;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
			holder.news = (ImageView) convertView.findViewById(R.id.iv_news);
			holder.name = (TextView) convertView.findViewById(R.id.txt_name);
			holder.time = (TextView) convertView.findViewById(R.id.txt_time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		NotificationInfo info = notificationData.get(position);
		holder.name.setText(info.getTitle());
		try {
			holder.time.setText(Util.convertDateToNotificationTime(info.getCreateDate()));
		} catch (ParseException e) {
			e.printStackTrace();
			holder.time.setText(null);
		}
		holder.news.setVisibility(info.getStatus() == 1 ? View.GONE : View.VISIBLE);
		int type = info.getType();
		switch (type) {
		case NotificationInfo.TYPE_MEETING_INVITATION:
			holder.icon.setImageResource(R.drawable.icon_meeting_invitation);
			break;
		case NotificationInfo.TYPE_TAG_CREATE_SUCCESS:
			holder.icon.setImageResource(R.drawable.icon_create_tag_success);
			break;
		case NotificationInfo.TYPE_TAG_CREATE_FAILED:
			holder.icon.setImageResource(R.drawable.icon_create_tag_failed);
			break;
		case NotificationInfo.TYPE_MEETING_INVITE_JOIN:
			holder.icon.setImageResource(R.drawable.icon_person_join);
			break;
		case NotificationInfo.TYPE_MEETING_INVITE_REFUSE:
			holder.icon.setImageResource(R.drawable.icon_person_join_refuse);
			break;
		case NotificationInfo.TYPE_MEETING_CANCEL:
			holder.icon.setImageResource(R.drawable.icon_meeting_invitation);
			break;
		}

		return convertView;
	}

	static class ViewHolder {
		CircleImageView icon;
		ImageView news;
		TextView name;
		TextView time;
	}

}