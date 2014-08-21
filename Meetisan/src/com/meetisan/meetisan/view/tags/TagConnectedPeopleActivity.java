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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.PeopleAdapter;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.view.dashboard.PersonProfileActivity;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.Mode;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.OnRefreshListener2;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshListView;

public class TagConnectedPeopleActivity extends Activity implements OnClickListener {
	private static final String TAG = TagConnectedPeopleActivity.class.getSimpleName();

	private PullToRefreshListView mPullPeopleView;
	private ListView mPeopleListView;
	private List<PeopleInfo> mPeopleData = new ArrayList<PeopleInfo>();

	private PeopleAdapter mPeopleAdapter;

	private PeopleInfo mUserInfo;
	private long mTagID = -1;
	private long mTotalPeople = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_meet_member);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			mTagID = bundle.getLong("TagID");
		}

		mUserInfo = UserInfoKeeper.readUserInfo(this);

		if (mTagID < 0 || mUserInfo.getId() < 0) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		initView();

		getPeoplesFromServer(1, true, true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {
		((ImageButton) findViewById(R.id.btn_title_icon_left)).setOnClickListener(this);
		((TextView) findViewById(R.id.txt_title)).setText(R.string.people_connected);

		/** -----------Init People ListView-------------- */
		mPullPeopleView = (PullToRefreshListView) findViewById(R.id.list_people);
		TextView mEmptyView = (TextView) findViewById(R.id.txt_empty_meetings);
		mEmptyView.setText("Don\'t connect any People !");
		mPullPeopleView.setEmptyView(mEmptyView);
		mPullPeopleView.setMode(Mode.BOTH);
		mPullPeopleView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(
						"Last Refresh: " + Util.getCurFormatDate());
				getPeoplesFromServer(1, true, false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(
						"Last Loading: " + Util.getCurFormatDate());
				int count = mPeopleListView.getCount() - 2; // reduce header and
															// footer item
				// Log.d(TAG, "-------total = " + mTotalPeople + "; count = " +
				// count);
				if (count < mTotalPeople) {
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getPeoplesFromServer(pageIndex, false, false);
				} else {
					ToastHelper.showToast("All the data has been loaded ");
					updatePeopleListView();
				}
			}
		});
		mPeopleListView = mPullPeopleView.getRefreshableView();
		registerForContextMenu(mPeopleListView);
		mPeopleAdapter = new PeopleAdapter(this, mPeopleData);
		mPeopleListView.setAdapter(mPeopleAdapter);
		mPeopleAdapter.notifyDataSetChanged();
		mPullPeopleView.setVisibility(View.VISIBLE);
		mPeopleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putLong("UserID", mPeopleData.get(arg2 - 1).getId());
				intent.setClass(TagConnectedPeopleActivity.this, PersonProfileActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_icon_left:
			finish();
			break;
		default:
			break;
		}
	}

	private void updatePeopleListView() {
		mPeopleAdapter.notifyDataSetChanged();
		mPullPeopleView.onRefreshComplete();
		if (mPeopleData.size() >= mTotalPeople) {
			mPullPeopleView.setMode(Mode.PULL_FROM_START);
		} else {
			mPullPeopleView.setMode(Mode.BOTH);
		}
	}

	private CustomizedProgressDialog mProgressDialog = null;

	/**
	 * get Peoples from server
	 * 
	 * @param pageIndex
	 *            load page index
	 * @param isRefresh
	 *            is refresh or load more
	 * @param isNeedsDialog
	 *            weather show progress dialog
	 */
	private void getPeoplesFromServer(int pageIndex, final boolean isRefresh, final boolean isNeedsDialog) {
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
						mPeopleData.clear();
					}

					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
					mTotalPeople = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);
					Log.d(TAG, "Total People Count: " + mTotalPeople);

					JSONArray peopleArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < peopleArray.length(); i++) {
						PeopleInfo peopleInfo = new PeopleInfo();
						JSONObject peopleJson = peopleArray.getJSONObject(i);
						JSONObject userJson = peopleJson.getJSONObject(ServerKeys.KEY_USER);

						peopleInfo.setId(userJson.getLong(ServerKeys.KEY_ID));
						// peopleInfo.setEmail(userJson.getString(ServerKeys.KEY_EMAIL));
						if (!userJson.isNull(ServerKeys.KEY_NAME)) {
							peopleInfo.setName(userJson.getString(ServerKeys.KEY_NAME));
						}
						if (!userJson.isNull(ServerKeys.KEY_UNIVERSITY)) {
							peopleInfo.setUniversity(userJson.getString(ServerKeys.KEY_UNIVERSITY));
						}
						if (!userJson.isNull(ServerKeys.KEY_AVATAR)) {
							peopleInfo.setAvatarUri(userJson.getString(ServerKeys.KEY_AVATAR));
						}
						peopleInfo.setDistance(userJson.getDouble(ServerKeys.KEY_DISTANCE));

						JSONArray tagArray = peopleJson.getJSONArray(ServerKeys.KEY_TAGS);
						for (int j = 0; j < tagArray.length(); j++) {
							TagInfo tagInfo = new TagInfo();
							JSONObject tagJson = tagArray.getJSONObject(j);
							tagInfo.setId(tagJson.getLong(ServerKeys.KEY_ID));
							if (!tagJson.isNull(ServerKeys.KEY_TITLE)) {
								tagInfo.setTitle(tagJson.getString(ServerKeys.KEY_TITLE));
							}
							peopleInfo.addTopTag(tagInfo);
						}

						mPeopleData.add(peopleInfo);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					// TODO.. server response exception, if Data is null, return
					// is JSONArray not
					// JSONObject
					// ToastHelper.showToast(R.string.server_response_exception,
					// Toast.LENGTH_LONG);
				} finally {
					updatePeopleListView();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updatePeopleListView();
			}
		});

		request.get(
				ServerKeys.FULL_URL_GET_CONNECTTED_PEOPLE + "/" + mUserInfo.getId() + "/?pageindex=" + pageIndex
						+ "&pagesize=" + ServerKeys.PAGE_SIZE + "&lat=" + mUserInfo.getLatitude() + "&lon="
						+ mUserInfo.getLongitude() + "&tagIDs=" + mTagID, null);

		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}

}
