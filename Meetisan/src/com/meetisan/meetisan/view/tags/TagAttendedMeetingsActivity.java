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
import com.meetisan.meetisan.model.MeetingAdapter;
import com.meetisan.meetisan.model.MeetingInfo;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.view.meet.MeetProfileActivity;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.Mode;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.OnRefreshListener2;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshListView;

public class TagAttendedMeetingsActivity extends Activity {
	private static final String TAG = TagAttendedMeetingsActivity.class.getSimpleName();

	private PullToRefreshListView mPullMeetingsView;
	private ListView mMeetingsListView;
	private List<MeetingInfo> mMeetingData = new ArrayList<MeetingInfo>();

	private MeetingAdapter mMeetingAdapter;

	private long mTotalMeetings = 0;
	private long mTagId = -1;
	private long mUserId = -1;
	private float mLat = 200.3f, mLon = 100.0f;
	private PeopleInfo mUserInfo;
	private int mOrderType = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dashboard_meetings);

		mUserInfo = UserInfoKeeper.readUserInfo(this);
		mUserId = mUserInfo.getId();
		mLat = mUserInfo.getLatitude();
		mLon = mUserInfo.getLongitude();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mTagId = bundle.getLong("TagID");
		}

		if (mTagId < 0 || mUserId < 0) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		getMeetingsFromServer(1, mOrderType, mLat, mLon, true, true);

		initView();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setText(R.string.meetings_attended);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TagAttendedMeetingsActivity.this.finish();
			}
		});
		mBackBtn.setVisibility(View.VISIBLE);

		/** -----------Init Meetings ListView-------------- */
		mPullMeetingsView = (PullToRefreshListView) findViewById(R.id.list_meetings);
		TextView mMeetingsEmptyView = (TextView) findViewById(R.id.txt_empty_meetings);
		mPullMeetingsView.setEmptyView(mMeetingsEmptyView);
		mPullMeetingsView.setMode(Mode.BOTH);
		mPullMeetingsView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(
						"Last Refresh: " + Util.getCurFormatDate());
				getMeetingsFromServer(1, mOrderType, mLat, mLon, true, false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(
						"Last Loading: " + Util.getCurFormatDate());
				int count = mMeetingsListView.getCount() - 2; // reduce header
																// and footer
																// item
				if (count < mTotalMeetings) {
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getMeetingsFromServer(pageIndex, mOrderType, mLat, mLon, true, true);
				} else {
					ToastHelper.showToast("All the data has been loaded ");
					updateMeetingsListView();
				}
			}
		});
		mMeetingsListView = mPullMeetingsView.getRefreshableView();
		registerForContextMenu(mMeetingsListView);
		mMeetingAdapter = new MeetingAdapter(this, mMeetingData);
		mMeetingsListView.setAdapter(mMeetingAdapter);
		mMeetingsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putLong("MeetingID", mMeetingData.get(arg2 - 1).getId());
				bundle.putLong("UserID", mUserId);
				intent.setClass(TagAttendedMeetingsActivity.this, MeetProfileActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mMeetingAdapter.notifyDataSetChanged();
	}

	private void updateMeetingsListView() {
		mMeetingAdapter.notifyDataSetChanged();
		mPullMeetingsView.onRefreshComplete();
	}

	private CustomizedProgressDialog mProgressDialog = null;

	/**
	 * get Meetings from server
	 * 
	 * @param pageIndex
	 *            load page index
	 * @param orderType
	 *            get list order by edition
	 * @param mLat
	 *            location
	 * @param mLon
	 *            location
	 * @param isRefresh
	 *            is refresh or load more
	 * @param isNeedsDialog
	 *            weather show progress dialog
	 */
	private void getMeetingsFromServer(int pageIndex, int orderType, float mLat, float mLon, final boolean isRefresh,
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
						mMeetingData.clear();
					}

					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
					dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);

					JSONArray peopleArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < peopleArray.length(); i++) {
						MeetingInfo meetingInfo = new MeetingInfo();

						JSONObject meetJson = peopleArray.getJSONObject(i);

						JSONObject meetingJson = meetJson.getJSONObject(ServerKeys.KEY_MEETING);
						meetingInfo.setId(meetingJson.getLong(ServerKeys.KEY_ID));
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
					updateMeetingsListView();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updateMeetingsListView();
			}
		});

		request.get(ServerKeys.FULL_URL_GET_ATTENDED_MEET + "/" + mUserId + "/?pageindex=" + pageIndex + "&pagesize="
				+ ServerKeys.PAGE_SIZE + "&lat=" + mLat + "&lon=" + mLon + "&TagIDs=" + mTagId, null);

		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}
}
