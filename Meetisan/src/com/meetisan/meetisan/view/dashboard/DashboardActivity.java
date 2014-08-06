package com.meetisan.meetisan.view.dashboard;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.MeetingInfo;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.model.UpcomingMeetingAdapter;
import com.meetisan.meetisan.utils.HttpBitmap;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.view.meet.MeetProfileActivity;
import com.meetisan.meetisan.widget.CircleImageView;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.Mode;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.OnRefreshListener2;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshListView;

public class DashboardActivity extends Activity implements OnClickListener {

	private CircleImageView mPortraitView;
	private TextView mNameTxt, mCountTxt;
	private PeopleInfo mUserInfo;

	private PullToRefreshListView mPullMeetingsView;
	private ListView mMeetingsListView;
	private List<MeetingInfo> mMeetingData = new ArrayList<MeetingInfo>();
	private UpcomingMeetingAdapter mMeetingAdapter;
	private long mUpcomingMeetings = 0, mAllMeetingsCount = 0;
	private float mLat = 200.3f, mLon = 100.0f;

	private HttpBitmap httpBitmap = new HttpBitmap(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dashboard);

		syncUserInfoFromPerfer();

		initView();

		updateUserInfoUI();

		getUpcomingMeetingsFromServer(1, mLat, mLon, true, true);
	}

	private void syncUserInfoFromPerfer() {
		mUserInfo = UserInfoKeeper.readUserInfo(this);
	}

	private void updateUpcomingMeetingsListView() {
		if (mAllMeetingsCount >= 0) {
			mCountTxt.setText(String.valueOf(mAllMeetingsCount));
		}
		mMeetingAdapter.notifyDataSetChanged();
		mPullMeetingsView.onRefreshComplete();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {
		ImageButton mSettingsBtn = (ImageButton) findViewById(R.id.btn_settings);
		mSettingsBtn.setOnClickListener(this);
		ImageButton mConnectionsBtn = (ImageButton) findViewById(R.id.btn_connections);
		mConnectionsBtn.setOnClickListener(this);
		mPortraitView = (CircleImageView) findViewById(R.id.iv_portrait);
		mPortraitView.setOnClickListener(this);
		mNameTxt = (TextView) findViewById(R.id.txt_name);
		mCountTxt = (TextView) findViewById(R.id.txt_meetings_count);

		RelativeLayout mAllMeetingsLayout = (RelativeLayout) findViewById(R.id.layout_all_meetings);
		mAllMeetingsLayout.setOnClickListener(this);

		/** -----------Init Meetings ListView-------------- */
		mPullMeetingsView = (PullToRefreshListView) findViewById(R.id.list_upcoming_meetings);
		TextView mMeetingsEmptyView = (TextView) findViewById(R.id.txt_empty_meetings);
		mPullMeetingsView.setEmptyView(mMeetingsEmptyView);
		mPullMeetingsView.setMode(Mode.BOTH);
		mPullMeetingsView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(
						"Last Refresh: " + Util.getCurFormatDate());
				getUpcomingMeetingsFromServer(1, mLat, mLon, true, false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(
						"Last Loading: " + Util.getCurFormatDate());
				int count = mMeetingsListView.getCount() - 2; // reduce header
																// and footer
																// item
				if (count < mUpcomingMeetings) {
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getUpcomingMeetingsFromServer(pageIndex, mLat, mLon, true, true);
				} else {
					ToastHelper.showToast("All the data has been loaded ");
					updateUpcomingMeetingsListView();
				}
			}
		});
		mMeetingsListView = mPullMeetingsView.getRefreshableView();
		registerForContextMenu(mMeetingsListView);
		mMeetingAdapter = new UpcomingMeetingAdapter(this, mMeetingData, mUserInfo.getId());
		mMeetingsListView.setAdapter(mMeetingAdapter);
		mMeetingsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putLong("MeetingID", mMeetingData.get(arg2 - 1).getId());
				bundle.putLong("UserID", mUserInfo.getId());
				intent.setClass(DashboardActivity.this, MeetProfileActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mMeetingAdapter.notifyDataSetChanged();
	}

	private void updateUserInfoUI() {
		if (mUserInfo != null) {
			if (mUserInfo.getName() != null) {
				mNameTxt.setText(mUserInfo.getName());
			}
		}

		if (mUserInfo.getAvatarUri() != null) {
			httpBitmap.displayBitmap(mPortraitView, mUserInfo.getAvatarUri());
		} else {
			mPortraitView.setImageResource(R.drawable.portrait_default);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_settings:
			Intent intent1 = new Intent(this, SettingsActivity.class);
			startActivity(intent1);
			break;
		case R.id.iv_portrait:
			Intent intent2 = new Intent();
			Bundle bundle = new Bundle();
			bundle.putLong("UserID", mUserInfo.getId());
			intent2.setClass(DashboardActivity.this, PersonProfileActivity.class);
			intent2.putExtras(bundle);
			startActivity(intent2);
			break;
		case R.id.btn_connections:
			Intent intent3 = new Intent(this, MyConnectionsActivity.class);
			startActivity(intent3);
			break;
		case R.id.layout_all_meetings:
			Intent intent4 = new Intent(DashboardActivity.this, MyMeetingsActivity.class);
			startActivity(intent4);
			break;
		default:
			break;
		}
	}

	private CustomizedProgressDialog mProgressDialog = null;

	/**
	 * get Meetings from server
	 * 
	 * @param pageIndex load page index
	 * @param orderType get list order by edition
	 * @param mLat location
	 * @param mLon location
	 * @param isRefresh is refresh or load more
	 * @param isNeedsDialog weather show progress dialog
	 */
	private void getUpcomingMeetingsFromServer(int pageIndex, float mLat, float mLon,
			final boolean isRefresh, final boolean isNeedsDialog) {
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
						mMeetingData.clear();
					}

					JSONObject dataJson = (new JSONObject(result))
							.getJSONObject(ServerKeys.KEY_DATA);
					mAllMeetingsCount = dataJson.getLong(ServerKeys.KEY_MEET_COUNT);

					JSONObject meetingsJson = dataJson.getJSONObject(ServerKeys.KEY_UPCOMING_MEET);
					mUpcomingMeetings = meetingsJson.getLong(ServerKeys.KEY_TOTAL_COUNT);
					JSONArray meetArray = meetingsJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < meetArray.length(); i++) {
						MeetingInfo meetingInfo = new MeetingInfo();

						JSONObject meetJson = meetArray.getJSONObject(i);

						JSONObject meetingJson = meetJson.getJSONObject(ServerKeys.KEY_MEETING);
						meetingInfo.setId(meetingJson.getLong(ServerKeys.KEY_ID));
						meetingInfo.setCreateUserId(meetingJson
								.getLong(ServerKeys.KEY_CREATE_USER_ID));
						meetingInfo.setStartTime(meetingJson.getString(ServerKeys.KEY_START_TIME));
						if (!meetingJson.isNull(ServerKeys.KEY_TITLE)) {
							meetingInfo.setTitle(meetingJson.getString(ServerKeys.KEY_TITLE));
						}
						meetingInfo.setDistance(meetingJson.getDouble(ServerKeys.KEY_DISTANCE));
						if (!meetingJson.isNull(ServerKeys.KEY_LOGO)) {
							meetingInfo.setLogoUri(meetingJson.getString(ServerKeys.KEY_LOGO));
						}

						JSONArray tagArray = meetJson.getJSONArray(ServerKeys.KEY_TAGS);
						for (int j = 0; j < tagArray.length(); j++) {
							TagInfo tagInfo = new TagInfo();
							JSONObject tagJson = tagArray.getJSONObject(j);
							tagInfo.setId(tagJson.getLong(ServerKeys.KEY_ID));
							if (!tagJson.isNull(ServerKeys.KEY_TITLE)) {
								tagInfo.setTitle(tagJson.getString(ServerKeys.KEY_TITLE));
							}
							meetingInfo.addTag(tagInfo);
						}

						mMeetingData.add(meetingInfo);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					// ToastHelper.showToast(R.string.server_response_exception,
					// Toast.LENGTH_LONG);
				} finally {
					updateUpcomingMeetingsListView();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updateUpcomingMeetingsListView();
			}
		});

		request.get(ServerKeys.FULL_URL_GET_UPCOMING_MEET + "/" + mUserInfo.getId()
				+ "/?pageindex=" + pageIndex + "&pagesize=" + ServerKeys.PAGE_SIZE + "&lat=" + mLat
				+ "&lon=" + mLon, null);

		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}
}
