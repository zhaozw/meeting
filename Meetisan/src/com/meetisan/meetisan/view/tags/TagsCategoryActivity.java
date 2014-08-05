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
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.Mode;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.OnRefreshListener2;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshListView;

public class TagsCategoryActivity extends Activity {

	private PullToRefreshListView mPullTagsView;
	private ListView mTagsListView;
	private List<TagInfo> mTagsData = new ArrayList<TagInfo>();

	private long mTagCategoryId = -1;
	private long mUserId = -1;
	private String mTagCategoryName = null;
	private long mMaxTags = 0;
	private TagsAdapter mTagsAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tags_category);

		mUserId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mTagCategoryId = bundle.getLong("TagCategoryID");
			mTagCategoryName = bundle.getString("TagCategoryName");
		}

		if (mTagCategoryId < 0) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		initView();
		getMyTagsFromServer(1, true, true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setText(mTagCategoryName);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				TagsCategoryActivity.this.finish();
			}
		});
		mBackBtn.setVisibility(View.VISIBLE);

		mPullTagsView = (PullToRefreshListView) findViewById(R.id.list_category_tags);
		mPullTagsView.setMode(Mode.BOTH);
		TextView mEmptyView = (TextView) findViewById(R.id.txt_empty_meetings);
		mPullTagsView.setEmptyView(mEmptyView);
		mPullTagsView.setOnRefreshListener(new OnRefreshListener2() {

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
				if (count < mMaxTags) {
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getMyTagsFromServer(pageIndex, false, false);
				} else {
					ToastHelper.showToast("All the data has been loaded ");
					updateTagsListView();
				}
			}
		});
		mTagsListView = mPullTagsView.getRefreshableView();
		registerForContextMenu(mTagsListView);
		mTagsAdapter = new TagsAdapter(this, mTagsData);
		mTagsListView.setAdapter(mTagsAdapter);
		mTagsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putLong("TagID", mTagsData.get(arg2 - 1).getId());
				bundle.putLong("UserID", mUserId);
				intent.setClass(TagsCategoryActivity.this, TagProfileActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void updateTagsListView() {
		mTagsAdapter.notifyDataSetChanged();
		mPullTagsView.onRefreshComplete();
	}

	private CustomizedProgressDialog mProgressDialog = null;

	/**
	 * get My Tags from server
	 * 
	 * @param pageIndex load page index
	 */
	private void getMyTagsFromServer(int pageIndex, final boolean isRefresh,
			final boolean isNeedsDialog) {
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

					JSONObject dataJson = (new JSONObject(result))
							.getJSONObject(ServerKeys.KEY_DATA);
					mMaxTags = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);
					JSONArray tagArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < tagArray.length(); i++) {
						TagInfo info = new TagInfo();
						JSONObject json = tagArray.getJSONObject(i);
						info.setId(json.getLong(ServerKeys.KEY_ID));
						info.setTitle(json.getString(ServerKeys.KEY_TITLE));
						info.setLogoUri(json.getString(ServerKeys.KEY_LOGO));
						info.setDescription(json.getString(ServerKeys.KEY_DESCRIPTION));

						// info.setEndorsed(json.getLong(ServerKeys.KEY_ENDORSEMENTS));
						info.setPeople(json.getLong(ServerKeys.KEY_PEOPLES));
						info.setMeetings(json.getLong(ServerKeys.KEY_MEETINGS));
						mTagsData.add(info);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					// ToastHelper.showToast(R.string.server_response_exception, Toast.LENGTH_LONG);
				} finally {
					updateTagsListView();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updateTagsListView();
			}
		});

		request.get(ServerKeys.FULL_URL_GET_TAGS_BY_CATEGORY + "/" + mTagCategoryId
				+ "/?pageindex=" + pageIndex + "&pagesize=" + ServerKeys.PAGE_SIZE + "&name=", null);
		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}

}
