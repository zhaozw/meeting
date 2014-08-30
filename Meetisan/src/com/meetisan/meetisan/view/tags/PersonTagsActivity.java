package com.meetisan.meetisan.view.tags;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.DeleteTouchListener;
import com.meetisan.meetisan.widget.DeleteTouchListener.OnDeleteCallback;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.Mode;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.OnRefreshListener2;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshListView;

public class PersonTagsActivity extends Activity implements OnClickListener {

	private PullToRefreshListView mPullTagsListView;
	private ListView mTagsListView;
	private List<TagInfo> mTagsData = new ArrayList<TagInfo>();

	private long mMaxMyTags = 0;
	private TagsAdapter mTagsAdapter;
	private DeleteTouchListener mTagsTouchListener;
	private long userId = -1;
	private long curUserId = -1;
	private String userName = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_person_tags);

		curUserId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			userId = bundle.getLong("UserID");
			userName = bundle.getString("PersonName");
		}

		if (userId < 0 || curUserId < 0) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		initView();
		getMyTagsFromServer(1, true, true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			this.finish();
			break;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setText(userName + "\'s Tags");
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

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
		if (userId == curUserId) {
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
		}

		mTagsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putLong("TagID", mTagsData.get(arg2 - 1).getId());
				bundle.putLong("UserID", userId);
				intent.setClass(PersonTagsActivity.this, TagProfileActivity.class);
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
						info.setLogoUri(json.getString(ServerKeys.KEY_LOGO));
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

		request.get(ServerKeys.FULL_URL_GET_USER_TAG + "/" + userId + "/?pageindex=" + pageIndex + "&pagesize="
				+ ServerKeys.PAGE_SIZE + "&name=", null);
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
