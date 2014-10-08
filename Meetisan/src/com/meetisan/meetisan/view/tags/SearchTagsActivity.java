package com.meetisan.meetisan.view.tags;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.model.TagsAdapter;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.ClearEditText;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.Mode;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.OnRefreshListener2;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshListView;

public class SearchTagsActivity extends Activity implements OnClickListener {

	private static final String FORMAT_CREATE_TAG_TIPS = "A '%s' Tag doesn\'t exist. You can search for related Tags or Create this Tag yourself.";

	private PullToRefreshListView mPullTagsListView;
	private ListView mTagsListView;
	private List<TagInfo> mTagsData = new ArrayList<TagInfo>();
	// private SearchPanel mSearchPanel;
	private ClearEditText mSearchView;
	private TextView mEmptyTxt;
	private LinearLayout mEmptyLayout;

	private long mMaxMyTags = 0;
	private TagsAdapter mTagsAdapter;
	private long curUserId = -1;
	private String tagName = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_search_tags);

		curUserId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);

		if (curUserId < 0) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		initView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			onBackPressed();
			break;
		case R.id.btn_create_tag:
			Intent intent = new Intent(this, CreateTagsActivity.class);
			intent.putExtra("TagName", tagName);
			startActivity(intent);
			break;
		}
	}

	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// InputMethodUtils.hideInputMethod(SearchTagsActivity.this);
	// }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);
		mSearchView = (ClearEditText) findViewById(R.id.search_panel);
		mSearchView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				tagName = v.getText().toString();
				getMyTagsFromServer(1, true, true);
				return false;
			}
		});
		// mSearchView.setSearchListener(new SearchListener() {
		//
		// @Override
		// public void onClickSearchResult(String query) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onClear() {
		// // TODO Auto-generated method stub
		// }
		//
		// @Override
		// public void onAutoSuggestion(String query) {
		// // TODO Auto-generated method stub
		// tagName = query;
		// getMyTagsFromServer(1, true, true);
		// }
		// });

		mEmptyTxt = (TextView) findViewById(R.id.txt_empty_tags);
		mEmptyLayout = (LinearLayout) findViewById(R.id.layout_empty);
		((Button) findViewById(R.id.btn_create_tag)).setOnClickListener(this);

		mPullTagsListView = (PullToRefreshListView) findViewById(R.id.list_tags);
		mPullTagsListView.setMode(Mode.BOTH);
		// mPullTagsListView.setEmptyView(mEmptyLayout);
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

		mTagsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putLong("TagID", mTagsData.get(arg2 - 1).getId());
				bundle.putLong("UserID", curUserId);
				intent.setClass(SearchTagsActivity.this, TagProfileActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mPullTagsListView.setVisibility(View.VISIBLE);
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
		if (mTagsData.size() >= mMaxMyTags) {
			mPullTagsListView.setMode(Mode.PULL_FROM_START);
		} else {
			mPullTagsListView.setMode(Mode.BOTH);
		}

		if (!TextUtils.isEmpty(tagName) && mTagsData.size() <= 0) {
			mEmptyTxt.setText(String.format(FORMAT_CREATE_TAG_TIPS, tagName));
			mEmptyLayout.setVisibility(View.VISIBLE);
		} else {
			mEmptyLayout.setVisibility(View.GONE);
		}
	}

	private CustomizedProgressDialog mProgressDialog = null;

	/**
	 * get My Tags from server
	 * 
	 * @param pageIndex
	 *            load page index
	 */
	private void getMyTagsFromServer(int pageIndex, final boolean isRefresh, final boolean isNeedsDialog) {
		if (tagName == null) {
			return;
		}

		if (TextUtils.isEmpty(tagName)) {
			mTagsData.clear();
			updateMyTagsListView();
			return;
		}

		HttpRequest request = new HttpRequest();

		if (isNeedsDialog) {
			if (mProgressDialog == null) {
				mProgressDialog = new CustomizedProgressDialog(this, R.string.searching);
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
						info.setId(json.getLong(ServerKeys.KEY_ID));
						// info.setUserTagId(json.getLong(ServerKeys.KEY_USER_TAG_ID));
						// info.setUserId(json.getLong(ServerKeys.KEY_USER_ID));
						info.setCategroyId(json.getLong("CategroyID"));
						info.setTitle(json.getString(ServerKeys.KEY_TITLE));
						info.setLogoUri(json.getString(ServerKeys.KEY_LOGO));
						// info.setEndorsed(json.getLong(ServerKeys.KEY_ENDORSEMENTS));
						// info.setPeople(json.getLong(ServerKeys.KEY_PEOPLES));
						// info.setMeetings(json.getLong(ServerKeys.KEY_MEETINGS));
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

		request.get(ServerKeys.FULL_URL_SEARCH_TAG + "/?name=" + tagName + "&pageindex=" + pageIndex + "&pagesize="
				+ ServerKeys.PAGE_SIZE, null);
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
