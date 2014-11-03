package com.meetisan.meetisan.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.meetisan.meetisan.view.dashboard.PersonProfileActivity;
import com.meetisan.meetisan.view.meet.MeetProfileActivity;
import com.meetisan.meetisan.view.tags.TagProfileActivity;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.DeleteTouchListener;
import com.meetisan.meetisan.widget.DeleteTouchListener.OnDeleteCallback;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.Mode;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.OnRefreshListener2;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshListView;

public class NotificationsActivity extends Activity implements OnItemClickListener {
	private static final String TAG = NotificationsActivity.class.getSimpleName();
	private PullToRefreshListView mPullView;
	private DeleteTouchListener mNotificationTouchListener;
	private ListView mListView;
	private List<NotificationInfo> mData = new ArrayList<NotificationInfo>();
	private NotificationAdapter mAdapter;

	private long mTotal = 0;
	private long mUserId = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_notifications);
		mUserId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);

		initTitle();
		initContentView();
		getNotificationsFromServer(1, true, true);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		getNotificationsFromServer(1, true, false);
	}

	private void initTitle() {
		TextView textView = (TextView) findViewById(R.id.tv_title_text);
		textView.setText(R.string.notifications);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initContentView() {
		mPullView = (PullToRefreshListView) findViewById(R.id.list_notifications);
		TextView mEmptyView = (TextView) findViewById(R.id.txt_empty_notifications);
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
				// Log.d(TAG, "-------total = " + mTotalNotifications +
				// "; count = " +
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

		mNotificationTouchListener = new DeleteTouchListener(mListView, new OnDeleteCallback() {

			@Override
			public void onDelete(ListView listView, int position) {
				position = position - 1; // ListView Header
				if (position < mListView.getCount()) {
					deleteNotificationFromServer(mData.get(position).getId(), position, true);
				}
			}
		});
		mListView.setOnTouchListener(mNotificationTouchListener);
		mListView.setOnScrollListener(mNotificationTouchListener.makeScrollListener());
	}

	private CustomizedProgressDialog mProgressDialog = null;

	private void getNotificationsFromServer(int pageIndex, final boolean isRefresh, final boolean isNeedsDialog) {
		if (mNotificationTouchListener != null) {
			mNotificationTouchListener.hideDeleteLayout();// to hide delete
															// layout
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
					Log.d(TAG, "Total Notifications Count: " + mTotal);

					JSONArray notificationArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < notificationArray.length(); i++) {
						NotificationInfo info = new NotificationInfo();

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
		if (mData.size() >= mTotal) {
			mPullView.setMode(Mode.PULL_FROM_START);
		} else {
			mPullView.setMode(Mode.BOTH);
		}
	}

	private void updateDeleteResult(boolean result, int position) {
		if (result && mData.size() > position) {
			mData.remove(position);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		NotificationInfo info = mData.get(position - 1);
		if (info.getStatus() != 1) {
			updateNotificationsStatusToServer(info.getId(), 1);
			info.setStatus(1);
		}

		int type = info.getType();
		Log.d(TAG, "Click Item Type: " + type);
		switch (type) {
		case NotificationInfo.TYPE_MEETING_CANCEL:
			Intent cancelIntent = new Intent(getApplicationContext(), MeetProfileActivity.class);
			cancelIntent.putExtra(ServerKeys.KEY_USER_ID, mUserId);
			cancelIntent.putExtra(ServerKeys.KEY_MEETING_ID, info.getReportObjectID());
			cancelIntent.putExtra("IsMeetCanceled", true);
			cancelIntent.putExtra(ServerKeys.KEY_TYPE, type);
			startActivity(cancelIntent);
			break;
		case NotificationInfo.TYPE_MEETING_INVITATION:
			Intent inviteIntent = new Intent(getApplicationContext(), MeetProfileActivity.class);
			inviteIntent.putExtra(ServerKeys.KEY_USER_ID, mUserId);
			inviteIntent.putExtra(ServerKeys.KEY_MEETING_ID, info.getReportObjectID());
			inviteIntent.putExtra(ServerKeys.KEY_TYPE, type);
			startActivity(inviteIntent);
			break;
		case NotificationInfo.TYPE_MEETING_INVITE_REFUSE:
			Intent refuseIntent = new Intent(getApplicationContext(), PersonProfileActivity.class);
			refuseIntent.putExtra(ServerKeys.KEY_USER_ID, info.getReportObjectID());
			refuseIntent.putExtra("IsMeetBtnVisible", false);
			startActivity(refuseIntent);
			break;
		case NotificationInfo.TYPE_TAG_CREATE_FAILED:
			Intent rejectIntent = new Intent(getApplicationContext(), TagRejectedActivity.class);
			startActivity(rejectIntent);
			break;
		case NotificationInfo.TYPE_TAG_CREATE_SUCCESS:
			Intent tagIntent = new Intent(getApplicationContext(), TagProfileActivity.class);
			tagIntent.putExtra(ServerKeys.KEY_USER_ID, info.getUserID());
			tagIntent.putExtra(ServerKeys.KEY_TAG_ID, info.getReportObjectID());
			tagIntent.putExtra(ServerKeys.KEY_TYPE, type);
			startActivity(tagIntent);
			break;
		case NotificationInfo.TYPE_MEETING_INVITE_JOIN:
			Intent joinIntent = new Intent(getApplicationContext(), PersonProfileActivity.class);
			joinIntent.putExtra(ServerKeys.KEY_USER_ID, info.getReportObjectID());
			joinIntent.putExtra("IsMeetBtnVisible", false);
			startActivity(joinIntent);
			break;
		default:
			break;
		}
	}

	private void updateNotificationsStatusToServer(long notificationId, int status) {
		HttpRequest request = new HttpRequest();

		Map<String, String> data = new HashMap<String, String>();
		data.put(ServerKeys.KEY_ID, String.valueOf(notificationId));
		data.put(ServerKeys.KEY_STATUS, String.valueOf(status));
		request.post(ServerKeys.FULL_URL_UPDATE_NOTIFICATION_STATUS, data);
	}

	/**
	 * delete user Tag from server
	 * 
	 * @param notificationId
	 * @param position
	 * @param isNeedsDialog
	 */
	private void deleteNotificationFromServer(long notificationId, final int position, final boolean isNeedsDialog) {

		HttpRequest request = new HttpRequest();

		final CustomizedProgressDialog mProgressDialog = new CustomizedProgressDialog(this, R.string.waiting);

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				updateDeleteResult(true, position);
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updateDeleteResult(false, position);
			}
		});

		request.delete(ServerKeys.FULL_URL_DELETE_NOTIFICATION + "/" + notificationId);
		mProgressDialog.show();
	}
}
