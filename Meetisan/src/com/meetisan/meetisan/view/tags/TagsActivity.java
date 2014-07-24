package com.meetisan.meetisan.view.tags;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.android.segmented.SegmentedGroup;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.TagCategory;
import com.meetisan.meetisan.model.TagCategoryAdapter;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.model.TagsAdapter;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.listview.refresh.DropDownListView;
import com.meetisan.meetisan.widget.listview.refresh.DropDownListView.OnDropDownListener;
import com.meetisan.meetisan.widget.listview.swipe.SwipeListView;
import com.meetisan.meetisan.widget.listview.swipe.SwipeListView.OnItemDeleteListener;

public class TagsActivity extends Activity {

	private SwipeListView mTagsListView;
	private DropDownListView mCategoryListView;
	private List<TagInfo> mTagsData = new ArrayList<TagInfo>();
	private List<TagCategory> mCategoryData = new ArrayList<TagCategory>();

	private long mUserId = -1;
	private long mMaxMyTags = 0, mMaxAllTags = 0;
	private TagsAdapter mTagsAdapter;
	private TagCategoryAdapter mCategoryAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tags);

		mUserId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);

		getMyTagsFromServer(1);
		getAllTagsFromServer(1);
		initView();
	}

	private void initView() {
		SegmentedGroup mTagsGroup = (SegmentedGroup) findViewById(R.id.group_tags);
		mTagsGroup.setTintColor(getResources().getColor(R.color.segment_group_bg_check),
				getResources().getColor(R.color.segment_group_text_check));
		mTagsGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.radio_my_tags) {
					mCategoryListView.setVisibility(View.GONE);
					mTagsListView.setVisibility(View.VISIBLE);
				} else {
					mTagsListView.setVisibility(View.GONE);
					mCategoryListView.setVisibility(View.VISIBLE);
				}
			}
		});

		mTagsListView = (SwipeListView) findViewById(R.id.list_my_tags);
		mTagsAdapter = new TagsAdapter(this, mTagsData);
		mTagsListView.setAdapter(mTagsAdapter);
		mTagsListView.setVisibility(View.VISIBLE);
		mTagsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putLong("TagID", mTagsData.get(arg2).getId());
				bundle.putLong("UserID", mUserId);
				intent.setClass(TagsActivity.this, TagProfileActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mTagsListView.setOnItemDeleteListener(new OnItemDeleteListener() {

			@Override
			public void onSwipeDelete(View view, int position) {
				mTagsData.remove(position);
				mTagsAdapter.notifyDataSetChanged();
			}
		});
		mTagsListView.setOnScrollListener(new MyTagsListViewScrollListener());

		mCategoryListView = (DropDownListView) findViewById(R.id.list_tags_category);
		mCategoryAdapter = new TagCategoryAdapter(this, mCategoryData);
		mCategoryListView.setAdapter(mCategoryAdapter);
		mCategoryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ToastHelper.showToast("Click " + arg2 + " Item");
			}
		});
		mCategoryListView.setOnDropDownListener(new OnDropDownListener() {
			@Override
			public void onDropDown() {
				mCategoryData.clear();
				getAllTagsFromServer(1);
			}
		});
		mCategoryListView.setOnBottomListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int count = mCategoryListView.getCount();
				if (count < mMaxAllTags) {
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getAllTagsFromServer(pageIndex);
				} else {
					mCategoryListView.onBottomComplete();
					ToastHelper.showToast(R.string.loading_complete);
				}
			}
		});
	}

	private void updateMyTagsListView() {
		mTagsAdapter.notifyDataSetChanged();
	}

	private void updateAllTagsListView() {
		mCategoryAdapter.notifyDataSetChanged();
		mCategoryListView.onBottomComplete();
		mCategoryListView.onDropDownComplete();
	}

	private CustomizedProgressDialog mProgressDialog = null;

	/**
	 * get My Tags from server
	 * 
	 * @param pageIndex
	 *            load page index
	 */
	private void getMyTagsFromServer(int pageIndex) {
		HttpRequest request = new HttpRequest();

		if (mProgressDialog == null) {
			mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);
		} else {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				try {
					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
					mMaxMyTags = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);
					JSONArray tagArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < tagArray.length(); i++) {
						TagInfo info = new TagInfo();
						JSONObject json = tagArray.getJSONObject(i);
						info.setId(json.getLong(ServerKeys.KEY_TAG_ID));
						// info.setUserId(json.getLong(ServerKeys.KEY_USER_ID));
						info.setCategroyId(json.getLong(ServerKeys.KEY_CATEGORY_ID));
						info.setTitle(json.getString(ServerKeys.KEY_TITLE));
						info.setLogo(Util.base64ToBitmap(json.getString(ServerKeys.KEY_LOGO)));
						info.setEndorsed(json.getLong(ServerKeys.KEY_ENDORSEMENTS));
						info.setPeople(json.getLong(ServerKeys.KEY_PEOPLES));
						info.setMeetings(json.getLong(ServerKeys.KEY_MEETINGS));
						mTagsData.add(info);
					}
					updateMyTagsListView();
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.server_response_exception, Toast.LENGTH_LONG);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		mUserId = 5; // TODO.. for test
		request.get(ServerKeys.FULL_URL_GET_USER_TAG + "/" + mUserId + "/?pageindex=" + pageIndex + "&pagesize="
				+ ServerKeys.PAGE_SIZE + "&name=", null);
		mProgressDialog.show();
	}

	private class MyTagsListViewScrollListener implements OnScrollListener {

		private int lastVisibleIndex = 0;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			lastVisibleIndex = firstVisibleItem + visibleItemCount;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			int count = mTagsAdapter.getCount();
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastVisibleIndex == count) {
				if (count < mMaxMyTags) {
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getMyTagsFromServer(pageIndex);
					Log.d("MyTagsListViewScrollListener", "---load data: " + pageIndex);
				}
			}
		}
	}

	/**
	 * get All Tags from server
	 * 
	 * @param pageIndex
	 *            load page index
	 */
	private void getAllTagsFromServer(int pageIndex) {
		HttpRequest request = new HttpRequest();

		if (mProgressDialog == null) {
			mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);
		} else {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				try {
					Log.d("TagsActivity", "All Tags: " + result);
					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
					mMaxAllTags = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);

					JSONArray categoryArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < categoryArray.length(); i++) {
						TagCategory tagCategory = new TagCategory();
						JSONObject json = categoryArray.getJSONObject(i);
						tagCategory.setId(json.getLong(ServerKeys.KEY_ID));
						tagCategory.setTitle(json.getString(ServerKeys.KEY_TITLE));
						tagCategory.setLogo(Util.base64ToBitmap(json.getString(ServerKeys.KEY_LOGO)));

						JSONArray tagsArray = json.getJSONArray(ServerKeys.KEY_TAGS);
						for (int j = 0; j < tagsArray.length(); j++) {
							TagInfo tagInfo = new TagInfo();
							JSONObject tagJson = tagsArray.getJSONObject(j);
							tagInfo.setId(tagJson.getLong(ServerKeys.KEY_ID));
							tagInfo.setTitle(tagJson.getString(ServerKeys.KEY_TITLE));
							tagCategory.addTags(tagInfo);
						}

						mCategoryData.add(tagCategory);
					}
					updateAllTagsListView();
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.server_response_exception, Toast.LENGTH_LONG);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		request.get(
				ServerKeys.FULL_URL_GET_TAG_LIST + "/?pageindex=" + pageIndex + "&pagesize=" + ServerKeys.PAGE_SIZE,
				null);
		mProgressDialog.show();
	}
}
