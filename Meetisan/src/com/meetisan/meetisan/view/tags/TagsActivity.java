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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
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
import com.meetisan.meetisan.widget.DeleteTouchListener;
import com.meetisan.meetisan.widget.DeleteTouchListener.OnDeleteCallback;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.Mode;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.OnRefreshListener2;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshListView;

public class TagsActivity extends Activity {

	// private DropDownListView mTagsListView;
	private PullToRefreshListView mPullTagsListView, mPullCategoryListView;
	private ListView mTagsListView;
	private ListView mCategoryListView;
	private List<TagInfo> mTagsData = new ArrayList<TagInfo>();
	private List<TagCategory> mCategoryData = new ArrayList<TagCategory>();

	private long mUserId = -1;
	private long mMaxMyTags = 0, mMaxAllTags = 0;
	private TagsAdapter mTagsAdapter;
	private DeleteTouchListener mTagsTouchListener;
	private TagCategoryAdapter mCategoryAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tags);

		mUserId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);

		getMyTagsFromServer(1, true, true);
		getAllTagsFromServer(1, true, true);
		initView();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {
		SegmentedGroup mTagsGroup = (SegmentedGroup) findViewById(R.id.group_tags);
		mTagsGroup.setTintColor(getResources().getColor(R.color.segment_group_bg_check),
				getResources().getColor(R.color.segment_group_text_check));
		mTagsGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.radio_my_tags) {
					mPullCategoryListView.setVisibility(View.GONE);
					mPullTagsListView.setVisibility(View.VISIBLE);
				} else {
					mPullTagsListView.setVisibility(View.GONE);
					mPullCategoryListView.setVisibility(View.VISIBLE);
				}
			}
		});

		mPullTagsListView = (PullToRefreshListView) findViewById(R.id.list_my_tags);
		mPullTagsListView.setMode(Mode.BOTH);
		TextView mEmptyTagsView = (TextView) findViewById(R.id.txt_empty_tags);
		mPullTagsListView.setEmptyView(mEmptyTagsView);
		mPullTagsListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(
						"Last Refresh: " + Util.getCurFormatDate());
				getMyTagsFromServer(1, true, false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(
						"Last Loading: " + Util.getCurFormatDate());
				int count = mTagsListView.getCount() - 2;
				if (count < mMaxMyTags) {
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getMyTagsFromServer(pageIndex, false, false);
				} else {
					ToastHelper.showToast("All the data has been loaded ");
					updateMyTagsListView();
				}
			}
		});
		mTagsListView = mPullTagsListView.getRefreshableView();
		registerForContextMenu(mTagsListView);
		mTagsAdapter = new TagsAdapter(this, mTagsData);
		mTagsListView.setAdapter(mTagsAdapter);
		mTagsTouchListener = new DeleteTouchListener(mTagsListView, new OnDeleteCallback() {

			@Override
			public void onDelete(ListView listView, int position) {
				position = position - 1; // ListView Header
				if (position < mTagsListView.getCount()) {
					deleteUserTagFromServer(mTagsData.get(position).getUserTagId(), position, true);
				}
			}
		});
		mTagsListView.setOnTouchListener(mTagsTouchListener);
		mTagsListView.setOnScrollListener(mTagsTouchListener.makeScrollListener());
		mTagsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putLong("TagID", mTagsData.get(arg2 - 1).getId());
				bundle.putLong("UserID", mUserId);
				intent.setClass(TagsActivity.this, TagProfileActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mPullTagsListView.setVisibility(View.VISIBLE);

		mPullCategoryListView = (PullToRefreshListView) findViewById(R.id.list_tags_category);
		TextView mEmptyCategoryView = (TextView) findViewById(R.id.txt_empty_category);
		mPullCategoryListView.setEmptyView(mEmptyCategoryView);
		mPullCategoryListView.setMode(Mode.BOTH);
		mPullCategoryListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(
						"Last Refresh: " + Util.getCurFormatDate());
				getAllTagsFromServer(1, true, false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(
						"Last Loading: " + Util.getCurFormatDate());
				int count = mCategoryListView.getCount() - 2;
				if (count < mMaxAllTags) {
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getAllTagsFromServer(pageIndex, false, false);
				} else {
					ToastHelper.showToast("All the data has been loaded ");
					updateAllTagsListView();
				}
			}
		});
		mCategoryListView = mPullCategoryListView.getRefreshableView();
		registerForContextMenu(mTagsListView);
		mCategoryAdapter = new TagCategoryAdapter(this, mCategoryData);
		mCategoryListView.setAdapter(mCategoryAdapter);
		mCategoryListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putLong("TagCategoryID", mCategoryData.get(arg2 - 1).getId());
				bundle.putString("TagCategoryName", mCategoryData.get(arg2 - 1).getTitle());
				intent.setClass(TagsActivity.this, TagsCategoryActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mPullCategoryListView.setVisibility(View.GONE);
	}

	private void updateDeleteResult(boolean result, int position) {
		if (result && mTagsData.size() > position) {
			mTagsData.remove(position);
		}
		mTagsAdapter.notifyDataSetChanged();
	}

	private void updateMyTagsListView() {
		mTagsAdapter.notifyDataSetChanged();
		mPullTagsListView.onRefreshComplete();
	}

	private void updateAllTagsListView() {
		mCategoryAdapter.notifyDataSetChanged();
		mPullCategoryListView.onRefreshComplete();
	}

	private CustomizedProgressDialog mProgressDialog = null;

	/**
	 * get My Tags from server
	 * 
	 * @param pageIndex
	 *            load page index
	 */
	private void getMyTagsFromServer(int pageIndex, final boolean isRefresh, final boolean isNeedsDialog) {
		if (mTagsTouchListener != null) {
			mTagsTouchListener.hideDeleteLayout();// to hide delete layout
		}

		HttpRequest request = new HttpRequest();

		if (isNeedsDialog) {
			if (mProgressDialog == null) {
				mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);
			} else {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				try {
					if (isRefresh) {
						mTagsData.clear();
					}

					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
					mMaxMyTags = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);
					JSONArray tagArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < tagArray.length(); i++) {
						TagInfo info = new TagInfo();
						JSONObject json = tagArray.getJSONObject(i);
						info.setId(json.getLong(ServerKeys.KEY_TAG_ID));
						info.setUserTagId(json.getLong(ServerKeys.KEY_USER_TAG_ID));
						// info.setUserId(json.getLong(ServerKeys.KEY_USER_ID));
						info.setCategroyId(json.getLong(ServerKeys.KEY_CATEGORY_ID));
						info.setTitle(json.getString(ServerKeys.KEY_TITLE));
						info.setLogo(Util.base64ToBitmap(json.getString(ServerKeys.KEY_LOGO)));
						info.setEndorsed(json.getLong(ServerKeys.KEY_ENDORSEMENTS));
						info.setPeople(json.getLong(ServerKeys.KEY_PEOPLES));
						info.setMeetings(json.getLong(ServerKeys.KEY_MEETINGS));
						mTagsData.add(info);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					// ToastHelper.showToast(R.string.server_response_exception,
					// Toast.LENGTH_LONG);
				} finally {
					updateMyTagsListView();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updateMyTagsListView();
			}
		});

		mUserId = 5;
		request.get(ServerKeys.FULL_URL_GET_USER_TAG + "/" + mUserId + "/?pageindex=" + pageIndex + "&pagesize="
				+ ServerKeys.PAGE_SIZE + "&name=", null);
		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}

	/**
	 * get All Tags from server
	 * 
	 * @param pageIndex
	 *            load page index
	 */
	private void getAllTagsFromServer(int pageIndex, final boolean isRefresh, final boolean isNeedsDialog) {
		HttpRequest request = new HttpRequest();

		if (isNeedsDialog) {
			if (mProgressDialog == null) {
				mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);
			} else {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				try {
					if (isRefresh) {
						mCategoryData.clear();
					}

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

				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.server_response_exception, Toast.LENGTH_LONG);
				} finally {
					updateAllTagsListView();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updateAllTagsListView();
			}
		});

		request.get(
				ServerKeys.FULL_URL_GET_TAG_LIST + "/?pageindex=" + pageIndex + "&pagesize=" + ServerKeys.PAGE_SIZE,
				null);
		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}

	/**
	 * delete user Tag from server
	 * 
	 * @param tagId
	 * @param position
	 * @param isNeedsDialog
	 */
	private void deleteUserTagFromServer(long tagId, final int position, final boolean isNeedsDialog) {

		HttpRequest request = new HttpRequest();

		if (isNeedsDialog) {
			if (mProgressDialog == null) {
				mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);
			} else {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				updateDeleteResult(true, position);
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updateDeleteResult(false, position);
			}
		});

		request.delete(ServerKeys.FULL_URL_DEL_TAG + "/" + tagId);
		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}
}
