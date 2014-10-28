package com.meetisan.meetisan.widget;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.model.TagSearchAdapter;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;

public class CustomizeSearchPopupView {
	// private static final String TAG = "CusomizedPPopupMenu";

	private String allItem;
	private Context context;
	private PopupWindow mPopupMenu;
	private ListView mListView;
	private TextView mAllTxt;
	private TagSearchAdapter mAdapter;

	public CustomizeSearchPopupView(Context context, String allItem, OnSearchPopupClickListener mClickListener,
			OnDismissListener mDismissListener, int height) {
		this.context = context;
		this.allItem = allItem;

		initPopupMenu(height, mClickListener, mDismissListener);
	}

	private void initPopupMenu(int height, final OnSearchPopupClickListener mClickListener,
			OnDismissListener mDismissListener) {
		View view = LayoutInflater.from(context).inflate(R.layout.layout_popup_search_view, null);

		mAllTxt = (TextView) view.findViewById(R.id.txt_show_all);
		mAllTxt.setText(allItem);
		mAllTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mClickListener.onSearch(true, null);
				dismiss();
			}
		});
		mListView = (ListView) view.findViewById(R.id.listview_menu);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mAdapter != null) {
					mAdapter.toggleSelect(position);
				}
			}
		});
		mListView.setFocusableInTouchMode(true);
		mListView.setFocusable(true);

		Button mSearchBtn = (Button) view.findViewById(R.id.btn_search);
		mSearchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mAdapter == null) {
					mClickListener.onSearch(true, null);
				} else {
					mClickListener.onSearch(false, mAdapter.getSelectItems());
				}
				dismiss();
			}
		});

		mPopupMenu = new PopupWindow(view, LayoutParams.MATCH_PARENT, height);
		mPopupMenu.setAnimationStyle(R.style.AnimationBottomEnterTopExit);
		if (mDismissListener != null) {
			mPopupMenu.setOnDismissListener(mDismissListener);
		}

		mPopupMenu.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
		mPopupMenu.setOutsideTouchable(true);
		mPopupMenu.setFocusable(true);
	}

	public void showPopupDown(View parent, String allTips) {
		mAllTxt.setText(allTips);
		if (!mPopupMenu.isShowing()) {
			mPopupMenu.showAsDropDown(parent);
		}
		mPopupMenu.setFocusable(true);
		mPopupMenu.setOutsideTouchable(true);
		getMyTagsFromServer();
	}

	private void showTags(List<TagInfo> mTagsData) {
		String items[] = new String[mTagsData.size()];
		for (int i = 0; i < mTagsData.size(); i++) {
			items[i] = mTagsData.get(i).getTitle();
		}

		mAdapter = new TagSearchAdapter(context, mTagsData);
		mListView.setAdapter(mAdapter);
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

	/**
	 * get My Tags from server
	 * 
	 * @param pageIndex
	 *            load page index
	 */
	private void getMyTagsFromServer() {
		final List<TagInfo> mTagsData = new ArrayList<TagInfo>();

		HttpRequest request = new HttpRequest();
		final CustomizedProgressDialog mProgressDialog = new CustomizedProgressDialog(context, R.string.loading);

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				try {
					mTagsData.clear();

					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
					JSONArray tagArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < tagArray.length(); i++) {
						TagInfo info = new TagInfo();
						JSONObject json = tagArray.getJSONObject(i);
						info.setId(json.getLong(ServerKeys.KEY_TAG_ID));
						info.setUserTagId(json.getLong(ServerKeys.KEY_USER_TAG_ID));
						// info.setUserId(json.getLong(ServerKeys.KEY_USER_ID));
						info.setCategroyId(json.getLong(ServerKeys.KEY_CATEGORY_ID));
						info.setTitle(json.getString(ServerKeys.KEY_TITLE));
						info.setLogoUri(json.getString(ServerKeys.KEY_LOGO));
						info.setEndorsed(json.getLong(ServerKeys.KEY_ENDORSEMENTS));
						info.setPeople(json.getLong(ServerKeys.KEY_PEOPLES));
						info.setMeetings(json.getLong(ServerKeys.KEY_MEETINGS));
						mTagsData.add(info);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					mProgressDialog.dismiss();
					showTags(mTagsData);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				showTags(mTagsData);
				mProgressDialog.dismiss();
			}
		});

		long mUserId = UserInfoKeeper.readUserInfo(context, UserInfoKeeper.KEY_USER_ID, -1L);
		request.get(ServerKeys.FULL_URL_GET_USER_TAG + "/" + mUserId + "/?pageindex=" + 1 + "&pagesize=" + 1000
				+ "&name=", null);
		mProgressDialog.show();
	}

	public interface OnSearchPopupClickListener {
		void onSearch(boolean isAll, long[] tagIDs);
	}
}
