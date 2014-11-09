package com.meetisan.meetisan.view.create;

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
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.model.TagInfo;
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

public class SelectPeopleActivity extends Activity {
	private static final String TAG = SelectPeopleActivity.class.getSimpleName();

	private PullToRefreshListView mPullPeopleView;
	private ListView mPeopleListView;
	private List<PeopleInfo> mPeopleData = new ArrayList<PeopleInfo>();
	private List<Long> mSelectedIDs = new ArrayList<Long>();

	private SelectPeopleAdapter mPeopleAdapter;

	private long mTotalPeople = 0;
	private long mUserId = -1;
	private boolean mIsMulitSelect = false;
	private String mTagIDs = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_select_person);

		mUserId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);
		mIsMulitSelect = getIntent().getBooleanExtra("isMulitSelect", false);
		mTagIDs = getIntent().getStringExtra("TagIDs");
		String mPeopleIDs = getIntent().getStringExtra("PeopleIDs");
		if (mPeopleIDs != null) {
			try {
				JSONArray mIDArray = new JSONArray(mPeopleIDs);
				for (int i = 0; i < mIDArray.length(); i++) {
					mSelectedIDs.add(mIDArray.getLong(i));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		getPeoplesFromServer(1, true, true);

		initView();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setText(R.string.by_meeting_tags);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		mBackBtn.setVisibility(View.VISIBLE);

		/** -----------Init People ListView-------------- */
		mPullPeopleView = (PullToRefreshListView) findViewById(R.id.list_connections);
		TextView mPeopleEmptyView = (TextView) findViewById(R.id.txt_empty_connections);
		mPullPeopleView.setEmptyView(mPeopleEmptyView);
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
		mPeopleAdapter = new SelectPeopleAdapter(this, mPeopleData, mSelectedIDs);
		mPeopleListView.setAdapter(mPeopleAdapter);
		mPeopleAdapter.notifyDataSetChanged();
		mPullPeopleView.setVisibility(View.VISIBLE);
		mPeopleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Log.d(TAG, "----------On Item Click---" + arg2);
				if (!mIsMulitSelect) {
					JSONArray nameArray = new JSONArray();
					JSONArray idArray = new JSONArray();
					nameArray.put(mPeopleData.get(arg2 - 1).getName());
					idArray.put(mPeopleData.get(arg2 - 1).getId());

					Intent inviteIntent = new Intent();
					inviteIntent.putExtra("inviteName", nameArray.toString());
					inviteIntent.putExtra("inviteID", idArray.toString());
					setResult(RESULT_OK, inviteIntent);
					SelectPeopleActivity.this.finish();
					return;
				} else {
					if (mPeopleAdapter != null) {
						mPeopleAdapter.toggleSelect(arg2 - 1);
					}
				}
			}
		});

	}

	@Override
	public void onBackPressed() {
		if (mPeopleAdapter != null) {
			List<PeopleInfo> mSelectList = mPeopleAdapter.getSelectList();
			JSONArray nameArray = new JSONArray();
			JSONArray idArray = new JSONArray();
			for (PeopleInfo info : mSelectList) {
				nameArray.put(info.getName());
				idArray.put(info.getId());
			}

			Intent inviteIntent = new Intent();
			inviteIntent.putExtra("inviteName", nameArray.toString());
			inviteIntent.putExtra("inviteID", idArray.toString());
			setResult(RESULT_OK, inviteIntent);
			Log.d(TAG, "Name = " + nameArray.toString());
			Log.d(TAG, "IDs = " + idArray.toString());
			finish();
		} else {
			super.onBackPressed();
		}
	}

	private void updatePeopleListView() {
		mPeopleAdapter.notifyDataSetChanged(true);
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
	 * @param mLat
	 *            location
	 * @param mLon
	 *            location
	 * @param isRefresh
	 *            is refresh or load more
	 * @param isNeedsDialog
	 *            weather show progress dialog
	 */
	// private void getPeoplesFromServer(int pageIndex, final boolean isRefresh,
	// final boolean isNeedsDialog) {
	// HttpRequest request = new HttpRequest();
	//
	// if (isNeedsDialog) {
	// if (mProgressDialog == null) {
	// mProgressDialog = new CustomizedProgressDialog(this,
	// R.string.please_waiting);
	// } else {
	// if (mProgressDialog.isShowing()) {
	// mProgressDialog.dismiss();
	// }
	// }
	// }
	//
	// request.setOnHttpRequestListener(new OnHttpRequestListener() {
	//
	// @Override
	// public void onSuccess(String url, String result) {
	// if (isNeedsDialog) {
	// mProgressDialog.dismiss();
	// }
	// try {
	// if (isRefresh) {
	// mPeopleData.clear();
	// }
	//
	// JSONObject dataJson = (new
	// JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
	// mTotalPeople = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);
	// Log.d(TAG, "Total People Count: " + mTotalPeople);
	//
	// JSONArray peopleArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
	// for (int i = 0; i < peopleArray.length(); i++) {
	// PeopleInfo peopleInfo = new PeopleInfo();
	// JSONObject userJson = peopleArray.getJSONObject(i);
	//
	// peopleInfo.setId(userJson.getLong(ServerKeys.KEY_USER_ID));
	// // peopleInfo.setEmail(userJson.getString(ServerKeys.KEY_EMAIL));
	// if (!userJson.isNull(ServerKeys.KEY_NAME)) {
	// peopleInfo.setName(userJson.getString(ServerKeys.KEY_NAME));
	// }
	// // if (!userJson.isNull(ServerKeys.KEY_UNIVERSITY)) {
	// //
	// peopleInfo.setUniversity(userJson.getString(ServerKeys.KEY_UNIVERSITY));
	// // }
	// if (!userJson.isNull(ServerKeys.KEY_AVATAR)) {
	// peopleInfo.setAvatarUri(userJson.getString(ServerKeys.KEY_AVATAR));
	// }
	// peopleInfo.setDistance(-1); // for do not show this item
	//
	// JSONArray tagArray = userJson.getJSONArray(ServerKeys.KEY_TAGS);
	// for (int j = 0; j < tagArray.length(); j++) {
	// TagInfo tagInfo = new TagInfo();
	// JSONObject tagJson = tagArray.getJSONObject(j);
	// tagInfo.setId(tagJson.getLong(ServerKeys.KEY_ID));
	// if (!tagJson.isNull(ServerKeys.KEY_TITLE)) {
	// tagInfo.setTitle(tagJson.getString(ServerKeys.KEY_TITLE));
	// }
	// peopleInfo.addTopTag(tagInfo);
	// }
	//
	// mPeopleData.add(peopleInfo);
	// }
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// // ToastHelper.showToast(R.string.server_response_exception,
	// // Toast.LENGTH_LONG);
	// } finally {
	// updatePeopleListView();
	// }
	// }
	//
	// @Override
	// public void onFailure(String url, int errorNo, String errorMsg) {
	// if (isNeedsDialog) {
	// mProgressDialog.dismiss();
	// }
	// ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
	// updatePeopleListView();
	// }
	// });
	//
	// request.get(ServerKeys.FULL_URL_GET_USER_CONNECTION_LIST + "/" + mUserId
	// + "/?pageindex=" + pageIndex
	// + "&pagesize=" + ServerKeys.PAGE_SIZE, null);
	//
	// if (isNeedsDialog) {
	// mProgressDialog.show();
	// }
	// }

	/**
	 * get Peoples from server
	 * 
	 * @param pageIndex
	 *            load page index
	 * @param mLat
	 *            location
	 * @param mLon
	 *            location
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
						peopleInfo.setEmail(userJson.getString(ServerKeys.KEY_EMAIL));
						if (!userJson.isNull(ServerKeys.KEY_NAME)) {
							peopleInfo.setName(userJson.getString(ServerKeys.KEY_NAME));
						}
						if (!userJson.isNull(ServerKeys.KEY_UNIVERSITY)) {
							peopleInfo.setUniversity(userJson.getString(ServerKeys.KEY_UNIVERSITY));
						}
						// Log.d(TAG, "---People Name: " + peopleInfo.getName()
						// + "; UNIV: " +
						// peopleInfo.getUniversity());
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

		PeopleInfo mUserInfo = UserInfoKeeper.readUserInfo(this);
		float mLat = mUserInfo.getLatitude();
		float mLon = mUserInfo.getLongitude();

		request.get(ServerKeys.FULL_URL_GET_UESR_LIST + "/" + mUserId + "/?pageindex=" + pageIndex + "&pagesize="
				+ ServerKeys.PAGE_SIZE + "&lat=" + mLat + "&lon=" + mLon + "&tagIDs=" + mTagIDs + "&name=", null);

		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}

}
