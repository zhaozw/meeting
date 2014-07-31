package com.meetisan.meetisan.notifications;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.NotificationAdapter;
import com.meetisan.meetisan.model.NotificationInfo;
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

public class NotificationsActivity extends Activity implements OnItemClickListener {
	private static final String TAG = NotificationsActivity.class.getSimpleName();
	private PullToRefreshListView mPullView;
	private ListView mListView;
	private List<NotificationInfo> mData = new ArrayList<NotificationInfo>();
	private NotificationAdapter mAdapter;

	private long mTotal = 0;
	private long mUserId = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_notifications);
		mUserId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);

		initTitle();
		initContentView();
		getNotificationsFromServer(1, true, true);
	}

	private void initTitle() {
		TextView textView = (TextView) findViewById(R.id.tv_title_text);
		textView.setText(R.string.notifications);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initContentView() {
		/** -----------Init People ListView-------------- */
		mPullView = (PullToRefreshListView) findViewById(R.id.list_notifications);
		TextView mEmptyView = new TextView(this);
		mEmptyView.setText(R.string.content_empty_default);
		mPullView.setEmptyView(mEmptyView);
		mPullView.setMode(Mode.BOTH);
		mPullView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(
						"Last Refresh: " + Util.getCurFormatDate());
				getNotificationsFromServer(1, true, false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(
						"Last Loading: " + Util.getCurFormatDate());
				int count = mListView.getCount() - 2; // reduce header and
														// footer item
				// Log.d(TAG, "-------total = " + mTotalPeople + "; count = " +
				// count);
				if (count < mTotal) {
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getNotificationsFromServer(pageIndex, false, false);
				} else {
					ToastHelper.showToast("All the data has been loaded ");
					updateListView();
				}
			}
		});
		mListView = mPullView.getRefreshableView();
		registerForContextMenu(mListView);
		mAdapter = new NotificationAdapter(this, mData);
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		mPullView.setVisibility(View.VISIBLE);
		mListView.setOnItemClickListener(this);
		mPullView.setVisibility(View.VISIBLE);
	}

	private CustomizedProgressDialog mProgressDialog = null;

	private void getNotificationsFromServer(int pageIndex, final boolean isRefresh, final boolean isNeedsDialog) {
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
						mData.clear();
					}
					JSONObject dataJson = new JSONObject();
					try {
						dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
					} catch (JSONException e) {
						updateListView();
						return;
					}
					mTotal = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);
					Log.d(TAG, "Total People Count: " + mTotal);

					JSONArray notificationArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < notificationArray.length(); i++) {
						NotificationInfo info = new NotificationInfo();
						// JSONObject peopleJson =
						// notificationArray.getJSONObject(i);

						JSONObject userJson = notificationArray.getJSONObject(i);
						info.setId(userJson.getLong(ServerKeys.KEY_ID));
						info.setUserID(userJson.getLong(ServerKeys.KEY_USER_ID));
						info.setReportObjectID(userJson.getLong(ServerKeys.KEY_REPORT_OBJECT_ID));
						info.setTitle(userJson.getString(ServerKeys.KEY_TITLE));
						info.setType(userJson.getInt(ServerKeys.KEY_TYPE));
						info.setStatus(userJson.getInt(ServerKeys.KEY_STATUS));
						info.setCreateDate(userJson.getString(ServerKeys.KEY_CREATE_DATE));
						mData.add(info);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.server_response_exception, Toast.LENGTH_LONG);
				} finally {
					updateListView();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updateListView();
			}
		});

		request.get(ServerKeys.FULL_URL_GET_NOTIFICATION + "/" + mUserId + "/?pageindex=" + pageIndex + "&pagesize="
				+ ServerKeys.PAGE_SIZE, null);

		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}

	private void updateListView() {
		mAdapter.notifyDataSetChanged();
		mPullView.onRefreshComplete();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		//
		// Intent intent = new Intent();
		// Bundle bundle = new Bundle();
		// bundle.putLong("UserID", mPeopleData.get(arg2 - 1).getId());
		// intent.setClass(MeetActivity.this, PersonProfileActivity.class);
		// intent.putExtras(bundle);
		// startActivity(intent);
	}

}
