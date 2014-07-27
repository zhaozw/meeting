package com.meetisan.meetisan.view.meet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.android.segmented.SegmentedGroup;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.MeetingAdapter;
import com.meetisan.meetisan.model.MeetingInfo;
import com.meetisan.meetisan.model.PeopleAdapter;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.listview.refresh.DropDownListView;
import com.meetisan.meetisan.widget.listview.refresh.DropDownListView.OnDropDownListener;

public class MeetActivity extends Activity {
	private static final String TAG = MeetActivity.class.getSimpleName();

	private DropDownListView mPeopleListView, mMeetingsListView;
	private List<PeopleInfo> mPeopleData = new ArrayList<PeopleInfo>();
	private List<MeetingInfo> mMeetingData = new ArrayList<MeetingInfo>();

	private PeopleAdapter mPeopleAdapter;
	private MeetingAdapter mMeetingAdapter;

	private long mTotalPeople = 0, mTotalMeetings = 0;
	private long mUserId = -1;
	private float mLat = 200.3f, mLon = 100.0f;
	private int mOrderType = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_meet);

		mUserId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);

		getPeoplesFromServer(1, mLat, mLon, true, true);
		getMeetingsFromServer(1, mOrderType, mLat, mLon, true, true);

		initView();
	}

	private void initView() {
		SegmentedGroup mTagsGroup = (SegmentedGroup) findViewById(R.id.group_meet);
		mTagsGroup.setTintColor(getResources().getColor(R.color.segment_group_bg_check),
				getResources().getColor(R.color.segment_group_text_check));
		mTagsGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.radio_people) {
					mMeetingsListView.setVisibility(View.GONE);
					mPeopleListView.setVisibility(View.VISIBLE);
				} else {
					mPeopleListView.setVisibility(View.GONE);
					mMeetingsListView.setVisibility(View.VISIBLE);
				}
			}
		});

		mPeopleListView = (DropDownListView) findViewById(R.id.list_people);
		mPeopleAdapter = new PeopleAdapter(this, mPeopleData);
		mPeopleListView.setAdapter(mPeopleAdapter);
		mPeopleAdapter.notifyDataSetChanged();
		mPeopleListView.setVisibility(View.VISIBLE);
		mPeopleListView.setOnDropDownListener(new OnDropDownListener() {
			@Override
			public void onDropDown() {
				getPeoplesFromServer(1, mLat, mLon, true, false);
			}
		});
		mPeopleListView.setOnBottomListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count = mPeopleListView.getCount();
				if (count < mTotalPeople) {
					mPeopleListView.setHasMore(true);
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getPeoplesFromServer(pageIndex, mLat, mLon, false, false);
				} else {
					mPeopleListView.setHasMore(false);
					mPeopleListView.onBottomComplete();
				}
			}
		});
		mPeopleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				ToastHelper.showToast("Click Item: " + arg2);
			}
		});

		mMeetingsListView = (DropDownListView) findViewById(R.id.list_meetings);
		mMeetingAdapter = new MeetingAdapter(this, mMeetingData);
		mMeetingsListView.setAdapter(mMeetingAdapter);
		mMeetingAdapter.notifyDataSetChanged();
		mMeetingsListView.setOnDropDownListener(new OnDropDownListener() {
			@Override
			public void onDropDown() {
				getMeetingsFromServer(1, mOrderType, mLat, mLon, true, false);
			}
		});
		mMeetingsListView.setOnBottomListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count = mMeetingsListView.getCount();
				if (count < mTotalMeetings) {
					mMeetingsListView.setHasMore(true);
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getMeetingsFromServer(pageIndex, mOrderType, mLat, mLon, true, true);
				} else {
					mMeetingsListView.setHasMore(false);
					mMeetingsListView.onBottomComplete();
				}
			}
		});
	}

	private void updatePeopleListView(boolean isRefresh) {
		mPeopleAdapter.notifyDataSetChanged();
		if (isRefresh) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
			mPeopleListView.onDropDownComplete("Last: " + dateFormat.format(new Date()));
		} else {
			mPeopleListView.onBottomComplete();
		}
	}

	private void updateMeetingsListView(boolean isRefresh) {
		mMeetingAdapter.notifyDataSetChanged();
		if (isRefresh) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
			mMeetingsListView.onDropDownComplete("Last: " + dateFormat.format(new Date()));
		} else {
			mMeetingsListView.onBottomComplete();
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
	private void getPeoplesFromServer(int pageIndex, float mLat, float mLon, final boolean isRefresh,
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
						mPeopleData.clear();
					}

					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
					mTotalPeople = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);

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
						Log.d(TAG, "---People Name: " + peopleInfo.getName() + "; UNIV: " + peopleInfo.getUniversity());
						peopleInfo.setAvatar(Util.base64ToBitmap(userJson.getString(ServerKeys.KEY_AVATAR)));
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

					updatePeopleListView(isRefresh);
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.server_response_exception, Toast.LENGTH_LONG);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		request.get(ServerKeys.FULL_URL_GET_UESR_LIST + "/" + mUserId + "/?pageindex=" + pageIndex + "&pagesize="
				+ ServerKeys.PAGE_SIZE + "&lat=" + mLat + "&lon=" + mLon + "&tagIDs=" + "&name=", null);

		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}

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
					mTotalPeople = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);

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
						meetingInfo.setLogo(Util.base64ToBitmap(meetingJson.getString(ServerKeys.KEY_LOGO)));

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

					updateMeetingsListView(isRefresh);
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.server_response_exception, Toast.LENGTH_LONG);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		request.get(ServerKeys.FULL_URL_GET_MEET_LIST + "/?pageindex=" + pageIndex + "&pagesize="
				+ ServerKeys.PAGE_SIZE + "&ordertype=" + orderType + "&lat=" + mLat + "&lon=" + mLon + "&tagIDs="
				+ "&title=", null);

		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}
}
