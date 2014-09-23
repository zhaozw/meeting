package com.meetisan.meetisan.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.meetisan.meetisan.R;

public class CustomizePopupView {
	// private static final String TAG = "CusomizedPPopupMenu";

	private String[] items;
	private Context context;
	private PopupWindow mPopupMenu;
	private ListView mListView;

	public CustomizePopupView(Context context, String[] items, OnItemClickListener mClickListener,
			OnDismissListener mDismissListener, int height) {
		this.context = context;
		this.items = items;

		if (items == null) {
			return;
		}

		initPopupMenu(height, mClickListener, mDismissListener);
	}

	private void initPopupMenu(int height, OnItemClickListener mClickListener, OnDismissListener mDismissListener) {
		View view = LayoutInflater.from(context).inflate(R.layout.layout_popup_view, null);

		mListView = (ListView) view.findViewById(R.id.listview_menu);
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(context, R.layout.item_popup_menu_list, items);
		mListView.setAdapter(mAdapter);
		if (mClickListener != null) {
			mListView.setOnItemClickListener(mClickListener);
		}
		mListView.setFocusableInTouchMode(true);
		mListView.setFocusable(true);

		mPopupMenu = new PopupWindow(view, LayoutParams.MATCH_PARENT, height);
		mPopupMenu.setAnimationStyle(R.style.AnimationBottomEnterTopExit);
		if (mDismissListener != null) {
			mPopupMenu.setOnDismissListener(mDismissListener);
		}

		mPopupMenu.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
		mPopupMenu.setOutsideTouchable(true);
		mPopupMenu.setFocusable(true);
	}

	// public void showPopupTop(View parent) {
	// mPopupMenu.showAtLocation(parent, Gravity.BOTTOM |
	// Gravity.CENTER_HORIZONTAL, 0,
	// context.getResources().getDimensionPixelSize(R.dimen.activity_title_height)
	// +
	// context.getResources().getDimensionPixelSize(R.dimen.layout_search_and_sort_panel_height));
	// mPopupMenu.setFocusable(true);
	// mPopupMenu.setOutsideTouchable(true);
	// mPopupMenu.update();
	// }

	public void showPopupDown(View parent) {
		mPopupMenu.showAsDropDown(parent);
		mPopupMenu.setFocusable(true);
		mPopupMenu.setOutsideTouchable(true);
		mPopupMenu.update();
	}

	public void dismiss() {
		if (mPopupMenu != null && mPopupMenu.isShowing()) {
			mPopupMenu.dismiss();
		}
	}

	public boolean isShowing() {
		if (mPopupMenu == null) {
			return false;
		}
		return mPopupMenu.isShowing();
	}

	// private final class PopupMenuAdapter extends BaseAdapter {
	//
	// @Override
	// public int getCount() {
	// return itemList.size();
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// return itemList.get(position);
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return position;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// ViewHolder holder = null;
	// if (convertView == null) {
	// convertView =
	// LayoutInflater.from(context).inflate(R.layout.item_listview_popup, null);
	// holder = new ViewHolder();
	// convertView.setTag(holder);
	//
	// holder.type = (TextView) convertView.findViewById(R.id.menu_type);
	// holder.icon = (ImageView) convertView.findViewById(R.id.menu_icon);
	//
	// } else {
	// holder = (ViewHolder) convertView.getTag();
	// }
	//
	// holder.type.setText(itemList.get(position));
	// if (iconList != null && iconList.length >= position) {
	// holder.icon.setBackgroundResource(iconList[position]);
	// }
	//
	// return convertView;
	// }
	//
	// private final class ViewHolder {
	// ImageView icon;
	// TextView type;
	// }
	// }
}
