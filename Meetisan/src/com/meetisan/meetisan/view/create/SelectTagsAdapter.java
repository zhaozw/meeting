package com.meetisan.meetisan.view.create;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.widget.CircleImageView;

public class SelectTagsAdapter extends BaseAdapter {
	private List<TagInfo> mInfos = new ArrayList<TagInfo>();
	private Context mContext;
	private LayoutInflater mInflater;

	public SelectTagsAdapter(Context context, List<TagInfo> list) {
		mInfos = list;
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mInfos.size();
	}

	@Override
	public TagInfo getItem(int position) {
		return mInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.item_listview_create_select_tag, parent, false);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_create_selected_tag);
			holder.icon = (CircleImageView) convertView.findViewById(R.id.iv_portrait);
			holder.name = (TextView) convertView.findViewById(R.id.tv_create_select_tag_name);
			holder.description = (TextView) convertView.findViewById(R.id.tv_create_select_tag_description);
			holder.people = (TextView) convertView.findViewById(R.id.tv_create_select_tag_people);
			holder.meetings = (TextView) convertView.findViewById(R.id.tv_create_select_tag_meetings);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		TagInfo info = getItem(position);
		if (info.getState() == 0) {
			holder.checkBox.setChecked(false);
		} else {
			holder.checkBox.setChecked(true);
		}
		if (info.getLogo() != null) {
			holder.icon.setImageBitmap(info.getLogo());
		}
		holder.name.setText(info.getTitle());
		holder.description.setText(info.getDescription());
		holder.people.setText(String.valueOf(info.getPeople()));
		holder.meetings.setText(String.valueOf(info.getMeetings()));
		return convertView;
	}

	class Holder {
		CheckBox checkBox;
		CircleImageView icon;
		TextView name;
		TextView description;
		TextView people;
		TextView meetings;
	}
}
