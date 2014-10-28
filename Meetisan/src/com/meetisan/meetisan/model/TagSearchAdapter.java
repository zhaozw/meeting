package com.meetisan.meetisan.model;

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

public class TagSearchAdapter extends BaseAdapter {
	private List<TagInfo> mInfoList = new ArrayList<TagInfo>();
	private Context mContext;
	private LayoutInflater mInflater;
	private List<TagInfo> mSelectList = new ArrayList<TagInfo>();

	public TagSearchAdapter(Context context, List<TagInfo> list) {
		mInfoList = list;
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		initSelectList();
	}

	private void initSelectList() {
		mSelectList.clear();
	}

	@Override
	public int getCount() {
		return mInfoList.size();
	}

	@Override
	public TagInfo getItem(int position) {
		return mInfoList.get(position);
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
			convertView = mInflater.inflate(R.layout.item_listview_popup_tag_search, parent, false);
			holder.nameTxt = (TextView) convertView.findViewById(R.id.tv_tag_name);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_select);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final TagInfo info = getItem(position);

		holder.nameTxt.setText(info.getTitle());
		holder.checkBox.setChecked(mSelectList.contains(info));

		return convertView;
	}

	public void toggleSelect(int position) {
		TagInfo info = mInfoList.get(position);

		if (mSelectList.contains(info)) {
			mSelectList.remove(info);
		} else {
			mSelectList.add(info);
		}

		notifyDataSetChanged();
	}

	public long[] getSelectItems() {
		long[] selectIDs = new long[mSelectList.size()];
		for (int i = 0; i < mSelectList.size(); i++) {
			selectIDs[i] = mSelectList.get(i).getId();
		}
		
		return selectIDs;
	}

	class Holder {
		TextView nameTxt;
		CheckBox checkBox;
	}

}
