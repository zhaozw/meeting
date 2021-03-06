package com.meetisan.meetisan.view.create;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.HttpBitmap;
import com.meetisan.meetisan.widget.CircleImageView;

public class SelectTagsAdapter extends BaseAdapter {
	private List<TagInfo> mInfos = new ArrayList<TagInfo>();
	private Context mContext;
	private LayoutInflater mInflater;
	private HttpBitmap httpBitmap;
	private OnTagDetailsClickListener listener;

	public SelectTagsAdapter(Context context, List<TagInfo> list) {
		mInfos = list;
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		httpBitmap = new HttpBitmap(mContext);
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
			holder.layout = (RelativeLayout) convertView.findViewById(R.id.item_create_tag_list);
			holder.detailBtn = (ImageView) convertView.findViewById(R.id.cb_create_selected_tag);
			holder.icon = (CircleImageView) convertView.findViewById(R.id.iv_portrait);
			holder.name = (TextView) convertView.findViewById(R.id.tv_create_select_tag_name);
			// holder.description = (TextView)
			// convertView.findViewById(R.id.tv_create_select_tag_description);
			holder.people = (TextView) convertView.findViewById(R.id.tv_create_select_tag_people);
			holder.meetings = (TextView) convertView.findViewById(R.id.tv_create_select_tag_meetings);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final TagInfo info = getItem(position);

		holder.detailBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onClick(info);
				}
			}
		});

		if (info.getState() == 0) {
			holder.layout.setBackgroundColor(Color.WHITE);
		} else {
			holder.layout.setBackgroundColor(mContext.getResources().getColor(R.color.bg_create_tag_select_color));
		}
		holder.icon.setImageResource(R.drawable.portrait_person_default);
		if (info.getLogoUri() != null) {
			httpBitmap.displayBitmap(holder.icon, info.getLogoUri());
		}
		holder.name.setText(info.getTitle());
		// holder.description.setText(info.getDescription());
		holder.people.setText(String.valueOf(info.getPeople()));
		holder.meetings.setText(String.valueOf(info.getMeetings()));
		return convertView;
	}

	public void setOnDetailsBtnClickListener(OnTagDetailsClickListener listener) {
		this.listener = listener;
	}

	class Holder {
		RelativeLayout layout;
		ImageView detailBtn;
		CircleImageView icon;
		TextView name;
		// TextView description;
		TextView people;
		TextView meetings;
	}

	public interface OnTagDetailsClickListener {
		void onClick(TagInfo info);
	}
}
