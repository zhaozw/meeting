package com.meetisan.meetisan.view.meet;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.GoogleMapActivity;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.MeetingInfo;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.DialogUtils;
import com.meetisan.meetisan.utils.DialogUtils.OnDialogClickListener;
import com.meetisan.meetisan.utils.HttpBitmap;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.CircleImageView;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.CustomizedProgressDialog.DialogStyle;
import com.meetisan.meetisan.widget.LabelWithIcon;

public class MeetProfileActivity extends Activity implements OnClickListener {
	private static final String TAG = MeetProfileActivity.class.getSimpleName();

	private ImageButton mConnectionView;
	private CircleImageView mLogoView;
	private LabelWithIcon mLocationBtn;
	private Button mMeetBtn, mMeetTimeBtn, mCancelMeetBtn, mRejectBtn;
	private TextView mTitleTxt, mDescriptionTxt, mFirstTagTxt, mSecondTagTxt, mThirdTagTxt, mNoTagTxt, mCannotJoinTxt;
	private TextView mStartTime1Txt, mEndTime1Txt, mStartTime2Txt, mEndTime2Txt, mStartTime3Txt, mEndTime3Txt,
			mReportTxt;
	private LinearLayout mMeetLayout, mTime1Layout, mTime2Layout, mTime3Layout;

	private long mMeetingID = -1, mUserID = -1;
	private String mUserName = null;
	private MeetingInfo mMeetInfo = new MeetingInfo();

	private HttpBitmap httpBitmap;
	private int mSelectTimeIndex = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_meet_profile);

		boolean isMeetCanceled = false;

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			mMeetingID = bundle.getLong("MeetingID");
			mUserID = bundle.getLong("UserID");
			isMeetCanceled = bundle.getBoolean("IsMeetCanceled", false);
		}

		if (mMeetingID < 0 || mUserID < 0) {
			// ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		mUserName = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_NAME, "");

		httpBitmap = new HttpBitmap(this);

		initView();

		if (isMeetCanceled) {
			DialogUtils.showDialog(this, R.string.warning, R.string.meet_canceled_tips, R.string.ok, -1,
					new OnDialogClickListener() {
						@Override
						public void onClick(boolean isPositiveBtn) {
							MeetProfileActivity.this.finish();
						}
					});
		} else {
			getMeetProfileFromServer();
		}
	}

	private void initView() {
		((ImageButton) findViewById(R.id.btn_title_icon_left)).setOnClickListener(this);

		mMeetLayout = (LinearLayout) findViewById(R.id.layout_meet_and_report);
		mMeetBtn = (Button) findViewById(R.id.btn_meet);
		mMeetBtn.setOnClickListener(this);
		mMeetTimeBtn = (Button) findViewById(R.id.btn_meet_set_time);
		mMeetTimeBtn.setOnClickListener(this);
		mCancelMeetBtn = (Button) findViewById(R.id.btn_cancel_meet);
		mCancelMeetBtn.setOnClickListener(this);
		mRejectBtn = (Button) findViewById(R.id.btn_meet_reject);
		mRejectBtn.setOnClickListener(this);
		mCannotJoinTxt = (TextView) findViewById(R.id.txt_cannot_join);

		mConnectionView = (ImageButton) findViewById(R.id.btn_connections);
		mConnectionView.setOnClickListener(this);
		mLogoView = (CircleImageView) findViewById(R.id.iv_logo);
		mTitleTxt = (TextView) findViewById(R.id.txt_meet_title);
		mDescriptionTxt = (TextView) findViewById(R.id.txt_meet_description);
		mStartTime1Txt = (TextView) findViewById(R.id.txt_time_start_1);
		mEndTime1Txt = (TextView) findViewById(R.id.txt_time_end_1);
		mStartTime2Txt = (TextView) findViewById(R.id.txt_time_start_2);
		mEndTime2Txt = (TextView) findViewById(R.id.txt_time_end_2);
		mStartTime3Txt = (TextView) findViewById(R.id.txt_time_start_3);
		mEndTime3Txt = (TextView) findViewById(R.id.txt_time_end_3);
		mTime2Layout = (LinearLayout) findViewById(R.id.layout_time_2);
		mTime2Layout.setOnClickListener(new TimeLayoutClickListener());
		mTime3Layout = (LinearLayout) findViewById(R.id.layout_time_3);
		mTime3Layout.setOnClickListener(new TimeLayoutClickListener());
		mTime1Layout = (LinearLayout) findViewById(R.id.layout_time_1);
		mTime1Layout.setOnClickListener(new TimeLayoutClickListener());

		mLocationBtn = (LabelWithIcon) findViewById(R.id.btn_location);
		mLocationBtn.setOnClickListener(this);

		mFirstTagTxt = (TextView) findViewById(R.id.txt_meet_one);
		mSecondTagTxt = (TextView) findViewById(R.id.txt_meet_two);
		mThirdTagTxt = (TextView) findViewById(R.id.txt_meet_three);
		mNoTagTxt = (TextView) findViewById(R.id.txt_no_tags);

		mReportTxt = (TextView) findViewById(R.id.txt_report);
		mReportTxt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_icon_left:
			finish();
			break;
		case R.id.btn_location:
			Intent mapIntent = new Intent();
			Bundle mapBundle = new Bundle();
			mapBundle.putBoolean("IsSetLocation", false);
			mapBundle.putDouble("Latitude", mMeetInfo.getLatitude());
			mapBundle.putDouble("Longitude", mMeetInfo.getLongitude());
			mapBundle.putString("MeetTitle", mMeetInfo.getTitle());
			Log.d(TAG,
					"Location: " + false + "; Latitude: " + mMeetInfo.getLatitude() + "; Longitude: "
							+ mMeetInfo.getLongitude() + "; Title: " + mMeetInfo.getTitle());
			mapIntent.setClass(this, GoogleMapActivity.class);
			mapIntent.putExtras(mapBundle);
			startActivity(mapIntent);
			break;
		case R.id.btn_connections:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putLong("MeetingID", mMeetingID);
			intent.setClass(MeetProfileActivity.this, MeetMemberActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case R.id.btn_meet:
			if (mMeetInfo.getJoinStatus() == 4) {
				attendMeeting();
			} else {
				doAttendMeeting();
			}
			break;
		case R.id.btn_meet_set_time:
			showSetMeetTimeDialog();
			break;
		case R.id.btn_cancel_meet:
			if (mMeetInfo.getJoinStatus() == 2) {
				DialogUtils.showDialog(MeetProfileActivity.this, "Warning", "Are you sure cancel this meeting?", "Yes",
						"No", new OnDialogClickListener() {
							@Override
							public void onClick(boolean isPositiveBtn) {
								if (isPositiveBtn) {
									doCancelMeeting();
								}
							}
						});
			} else if (mMeetInfo.getJoinStatus() == 0) {
				doDeleteMeeting();
			}
			break;
		case R.id.btn_meet_reject:
			if (mMeetInfo.getJoinStatus() == 4) {
				doRejectOrAgreeMeeting(true);
			}
			break;
		case R.id.txt_report:
			Intent reportIntent = new Intent();
			Bundle reportBundle = new Bundle();
			reportBundle.putLong("ReportID", mMeetingID);
			reportBundle.putBoolean("IsReportUser", false);
			reportIntent.setClass(this, ReportActivity.class);
			reportIntent.putExtras(reportBundle);
			startActivity(reportIntent);
			break;
		default:
			break;
		}
	}

	private void showSetMeetTimeDialog() {
		DialogUtils.showDialog(this, "Wait!", "Please select one of the Meeting Time Options", "OK", null, null);
	}

	private void updateMeetProfileUI() {
		if (mMeetInfo == null) {
			return;
		}

		if (mMeetInfo.getLogoUri() != null) {
			httpBitmap.displayBitmap(mLogoView, mMeetInfo.getLogoUri());
		}
		mTitleTxt.setText(Util.formatOutput(mMeetInfo.getTitle()));
		mDescriptionTxt.setText(Util.formatOutput(mMeetInfo.getDescription()));

		if (mMeetInfo.getDeterminStartTime() != null && mMeetInfo.getDeterminEndTime() != null) {
			try {
				mStartTime1Txt.setText(Util.convertDateToMeetTime(mMeetInfo.getDeterminStartTime()));
				mEndTime1Txt.setText(Util.convertDateToMeetTime(mMeetInfo.getDeterminEndTime()));
			} catch (ParseException e) {
				e.printStackTrace();
				mStartTime1Txt.setText(Util.formatOutput(null));
				mEndTime1Txt.setText(Util.formatOutput(null));
			}
			mTime1Layout.setBackgroundResource(android.R.color.white);
			mTime1Layout.setVisibility(View.VISIBLE);
			mTime2Layout.setVisibility(View.GONE);
			mTime3Layout.setVisibility(View.GONE);
		} else {
			if (mMeetInfo.getStartTime1() != null && mMeetInfo.getEndTime1() != null) {
				try {
					mStartTime1Txt.setText(Util.convertDateToMeetTime(mMeetInfo.getStartTime1()));
					mEndTime1Txt.setText(Util.convertDateToMeetTime(mMeetInfo.getEndTime1()));
				} catch (ParseException e) {
					e.printStackTrace();
					mStartTime1Txt.setText(Util.formatOutput(null));
					mEndTime1Txt.setText(Util.formatOutput(null));
				}
			}
			mTime1Layout.setBackgroundResource(android.R.color.white);
			mTime1Layout.setVisibility(View.VISIBLE);

			if (mMeetInfo.getStartTime2() != null && mMeetInfo.getEndTime2() != null) {
				try {
					mStartTime2Txt.setText(Util.convertDateToMeetTime(mMeetInfo.getStartTime2()));
					mEndTime2Txt.setText(Util.convertDateToMeetTime(mMeetInfo.getEndTime2()));
				} catch (ParseException e) {
					e.printStackTrace();
					mStartTime2Txt.setText(Util.formatOutput(null));
					mEndTime2Txt.setText(Util.formatOutput(null));
				}
				mTime2Layout.setBackgroundResource(android.R.color.white);
				mTime2Layout.setVisibility(View.VISIBLE);
			} else {
				mTime2Layout.setVisibility(View.GONE);
			}

			if (mMeetInfo.getStartTime3() != null && mMeetInfo.getEndTime3() != null) {
				try {
					mStartTime3Txt.setText(Util.convertDateToMeetTime(mMeetInfo.getStartTime3()));
					mEndTime3Txt.setText(Util.convertDateToMeetTime(mMeetInfo.getEndTime3()));
				} catch (ParseException e) {
					e.printStackTrace();
					mStartTime3Txt.setText(Util.formatOutput(null));
					mEndTime3Txt.setText(Util.formatOutput(null));
				}
				mTime3Layout.setBackgroundResource(android.R.color.white);
				mTime3Layout.setVisibility(View.VISIBLE);
			} else {
				mTime3Layout.setVisibility(View.GONE);
			}
		}
		// mTime3Txt.setText(Util.convertTime2FormatMeetTime(mMeetInfo.getStartTime3(),
		// mMeetInfo.getEndTime3()));
		mLocationBtn.setText(Util.formatOutput(mMeetInfo.getAddress()));

		// JoinStatus: 0:已参加; 1：未参加; 2：当前用户为meeting创建人; 3: 拒绝邀请 ; 4: 收到邀请;
		// CanJoin当前用户是否可以加入meeting,false:不可以;true:可以;
		// Status标识meeting是否被后台管理员禁用的标识, 0:正常, 1:被禁用, 2:取消
		// 使用CanJoin字段，另外status为1、2也不能参加

		// 已过时
		if (mMeetInfo.getDeterminEndTime() != null && Util.isMeetOverByTime(mMeetInfo.getDeterminEndTime())) {
			mMeetLayout.setVisibility(View.GONE);
			mRejectBtn.setVisibility(View.GONE);
			mMeetBtn.setVisibility(View.GONE);
			mCancelMeetBtn.setVisibility(View.GONE);
			mReportTxt.setVisibility(View.GONE);
			mCannotJoinTxt.setVisibility(View.VISIBLE);
		} else if (mMeetInfo.getJoinStatus() == 2) {
			// 2:当前用户为meeting创建人;
			mCancelMeetBtn.setVisibility(View.VISIBLE);
			mRejectBtn.setVisibility(View.GONE);
			mMeetBtn.setVisibility(View.GONE);
			mMeetTimeBtn.setVisibility(View.GONE);
			mReportTxt.setVisibility(View.GONE);
			mCannotJoinTxt.setVisibility(View.GONE);
		} else if (mMeetInfo.getStatus() == 1 || mMeetInfo.getStatus() == 2) {
			// 该用户不可参加Meeting
			mMeetLayout.setVisibility(View.GONE);
			mCancelMeetBtn.setVisibility(View.GONE);
			mRejectBtn.setVisibility(View.GONE);
			mMeetBtn.setVisibility(View.GONE);
			mMeetTimeBtn.setVisibility(View.GONE);
			mReportTxt.setVisibility(View.GONE);
			mCannotJoinTxt.setVisibility(View.VISIBLE);
		} else {
			// 可以参加
			mCannotJoinTxt.setVisibility(View.GONE);
			mMeetTimeBtn.setVisibility(View.GONE);

			if (mMeetInfo.getJoinStatus() == 0) {
				// 0:已参加;
				mCancelMeetBtn.setVisibility(View.VISIBLE);
				mRejectBtn.setVisibility(View.GONE);
				mMeetBtn.setVisibility(View.GONE);
				mReportTxt.setVisibility(View.VISIBLE);
			} else if (mMeetInfo.isCanJoin()) {
				if (mMeetInfo.getJoinStatus() == 4) {
					// 4: 收到邀请
					mRejectBtn.setText("Reject");
					mMeetBtn.setText("Agree to Meet");
					mRejectBtn.setVisibility(View.VISIBLE);
					mMeetBtn.setVisibility(View.VISIBLE);
					mCancelMeetBtn.setVisibility(View.GONE);
					mReportTxt.setVisibility(View.GONE);
				} else if (mMeetInfo.getJoinStatus() == 2) {
					// 2:当前用户为meeting创建人;
					mCancelMeetBtn.setVisibility(View.VISIBLE);
					mRejectBtn.setVisibility(View.GONE);
					mMeetBtn.setVisibility(View.GONE);
					mReportTxt.setVisibility(View.GONE);
				} else if (mMeetInfo.getJoinStatus() == 1 || mMeetInfo.getJoinStatus() == 3) {
					// 1：未参加; 3: 拒绝邀请 ;
					mMeetBtn.setVisibility(View.VISIBLE);
					mReportTxt.setVisibility(View.VISIBLE);
					mRejectBtn.setVisibility(View.GONE);
					mCancelMeetBtn.setVisibility(View.GONE);
				}
			} else {
				// 该用户不可参加Meeting
				if (mMeetInfo.getJoinStatus() == 4) {
					// 4: 收到邀请，但没有选择Meeting时间
					if (null != mMeetInfo.getStartTime1() && null == mMeetInfo.getStartTime2()
							&& null == mMeetInfo.getStartTime3()) {
						mSelectTimeIndex = 1;
						mMeetTimeBtn.setVisibility(View.GONE);
						mMeetBtn.setText("Agree to Meet");
						mMeetBtn.setVisibility(View.VISIBLE);
					} else {
						mMeetTimeBtn.setVisibility(View.VISIBLE);
						mMeetBtn.setVisibility(View.GONE);
					}
					mRejectBtn.setText("Reject");
					mRejectBtn.setVisibility(View.VISIBLE);
					mCancelMeetBtn.setVisibility(View.GONE);
					mReportTxt.setVisibility(View.GONE);
				} else {
					mMeetLayout.setVisibility(View.GONE);
					mCancelMeetBtn.setVisibility(View.GONE);
					mRejectBtn.setVisibility(View.GONE);
					mMeetBtn.setVisibility(View.GONE);
					mMeetTimeBtn.setVisibility(View.GONE);
					mReportTxt.setVisibility(View.GONE);
					mCannotJoinTxt.setVisibility(View.VISIBLE);
				}
			}
		}

		List<TagInfo> tagsList = mMeetInfo.getTags();
		int tagsCount = tagsList.size();
		if (tagsCount >= 1) {
			mFirstTagTxt.setText(tagsList.get(0).getTitle());
			mFirstTagTxt.setVisibility(View.VISIBLE);
		}
		if (tagsCount >= 2) {
			mSecondTagTxt.setText(tagsList.get(1).getTitle());
			mSecondTagTxt.setVisibility(View.VISIBLE);
		}
		if (tagsCount >= 3) {
			mThirdTagTxt.setText(tagsList.get(2).getTitle());
			mThirdTagTxt.setVisibility(View.VISIBLE);
		}
		if (tagsCount <= 0) {
			mNoTagTxt.setText("-");
			mNoTagTxt.setVisibility(View.VISIBLE);
			mFirstTagTxt.setVisibility(View.GONE);
			mSecondTagTxt.setVisibility(View.GONE);
			mThirdTagTxt.setVisibility(View.GONE);
		}
	}

	private void attendMeeting() {
		if (mSelectTimeIndex > 0) {
			doSelectMeetingTime(mSelectTimeIndex);
		} else {
			doRejectOrAgreeMeeting(false);
		}
	}

	private class TimeLayoutClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (mMeetInfo == null || mMeetInfo.isCanJoin() || mMeetInfo.getJoinStatus() != 4) {
				return;
			}

			if (v == mTime1Layout) {
				mSelectTimeIndex = 1;
				mTime1Layout.setBackgroundResource(R.color.gray);
			} else {
				mTime1Layout.setBackgroundResource(android.R.color.white);
			}

			if (v == mTime2Layout) {
				mSelectTimeIndex = 2;
				mTime2Layout.setBackgroundResource(R.color.gray);
			} else {
				mTime2Layout.setBackgroundResource(android.R.color.white);
			}

			if (v == mTime3Layout) {
				mSelectTimeIndex = 3;
				mTime3Layout.setBackgroundResource(R.color.gray);
			} else {
				mTime3Layout.setBackgroundResource(android.R.color.white);
			}

			mMeetTimeBtn.setVisibility(View.GONE);
			mMeetBtn.setText("Agree to Meet");
			mMeetBtn.setVisibility(View.VISIBLE);
		}

	}

	private void getMeetProfileFromServer() {
		final CustomizedProgressDialog mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);
		HttpRequest request = new HttpRequest();

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				try {
					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);

					mMeetInfo.setJoinStatus(dataJson.getInt(ServerKeys.KEY_JOIN_STATUS));
					mMeetInfo.setCanJoin(dataJson.getBoolean(ServerKeys.KEY_CAN_JOIN));

					JSONObject meetJson = dataJson.getJSONObject(ServerKeys.KEY_MEETING);
					mMeetInfo.setId(meetJson.getLong(ServerKeys.KEY_ID));
					if (!meetJson.isNull(ServerKeys.KEY_TITLE)) {
						mMeetInfo.setTitle(meetJson.getString(ServerKeys.KEY_TITLE));
					}
					if (!meetJson.isNull(ServerKeys.KEY_DESCRIPTION)) {
						mMeetInfo.setDescription(meetJson.getString(ServerKeys.KEY_DESCRIPTION));
					}
					mMeetInfo.setLatitude(meetJson.getDouble(ServerKeys.KEY_LAT));
					mMeetInfo.setLongitude(meetJson.getDouble(ServerKeys.KEY_LON));
					if (!meetJson.isNull(ServerKeys.KEY_ADDRESS)) {
						mMeetInfo.setAddress(meetJson.getString(ServerKeys.KEY_ADDRESS));
					}
					if (!meetJson.isNull(ServerKeys.KEY_DETERMIN_START_TIME)) {
						mMeetInfo.setDeterminStartTime(meetJson.getString(ServerKeys.KEY_DETERMIN_START_TIME));
					}
					if (!meetJson.isNull(ServerKeys.KEY_DETERMIN_END_TIME)) {
						mMeetInfo.setDeterminEndTime(meetJson.getString(ServerKeys.KEY_DETERMIN_END_TIME));
					}
					if (!meetJson.isNull(ServerKeys.KEY_START_TIME1)) {
						mMeetInfo.setStartTime1(meetJson.getString(ServerKeys.KEY_START_TIME1));
					}
					if (!meetJson.isNull(ServerKeys.KEY_END_TIME1)) {
						mMeetInfo.setEndTime1(meetJson.getString(ServerKeys.KEY_END_TIME1));
					}
					if (!meetJson.isNull(ServerKeys.KEY_START_TIME2)) {
						mMeetInfo.setStartTime2(meetJson.getString(ServerKeys.KEY_START_TIME2));
					}
					if (!meetJson.isNull(ServerKeys.KEY_END_TIME2)) {
						mMeetInfo.setEndTime2(meetJson.getString(ServerKeys.KEY_END_TIME2));
					}
					if (!meetJson.isNull(ServerKeys.KEY_START_TIME3)) {
						mMeetInfo.setStartTime3(meetJson.getString(ServerKeys.KEY_START_TIME3));
					}
					if (!meetJson.isNull(ServerKeys.KEY_END_TIME3)) {
						mMeetInfo.setEndTime3(meetJson.getString(ServerKeys.KEY_END_TIME3));
					}
					mMeetInfo.setCreateDate(meetJson.getString(ServerKeys.KEY_CREATE_DATE));
					mMeetInfo.setLogoUri(meetJson.getString(ServerKeys.KEY_LOGO));
					mMeetInfo.setCreateUserId(meetJson.getLong(ServerKeys.KEY_CREATE_USER_ID));
					mMeetInfo.setCreateDate(meetJson.getString(ServerKeys.KEY_CREATE_DATE));
					mMeetInfo.setStatus(meetJson.getInt(ServerKeys.KEY_STATUS));
					mMeetInfo.setTimeSetType(meetJson.getInt(ServerKeys.KEY_TIME_SET_TYPE));

					JSONArray tagArray = dataJson.getJSONArray(ServerKeys.KEY_TAGS);
					for (int i = 0; i < tagArray.length(); i++) {
						JSONObject tagJson = tagArray.getJSONObject(i);
						TagInfo tagInfo = new TagInfo();
						tagInfo.setId(tagJson.getLong(ServerKeys.KEY_ID));
						tagInfo.setTitle(tagJson.getString(ServerKeys.KEY_TITLE));
						mMeetInfo.addTag(tagInfo);
					}

					updateMeetProfileUI();
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.server_response_exception, Toast.LENGTH_LONG);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		request.get(ServerKeys.FULL_URL_GET_MEET_INFO + "/" + mMeetingID + "/?UserID=" + mUserID, null);
		mProgressDialog.show();
	}

	private void doSelectMeetingTime(final int mSelectTimeIndex) {
		Log.d(TAG, "Select Meeting Time");
		HttpRequest request = new HttpRequest();

		String mBeginTime = null;
		String mEndTime = null;

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				// new CustomizedProgressDialog(MeetProfileActivity.this,
				// R.string.meet_scheduled, DialogStyle.OK).show();
				mMeetInfo.setCanJoin(true);
				// mMeetInfo.setDeterminStartTime(mBeginTime);
				// mMeetInfo.setDeterminEndTime(mEndTime);
				updateMeetProfileUI();
				doRejectOrAgreeMeeting(false);
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.attend_meeting_failure, Toast.LENGTH_LONG);
			}
		});

		if (mSelectTimeIndex == 1) {
			mBeginTime = mMeetInfo.getStartTime1();
			mEndTime = mMeetInfo.getEndTime1();
		} else if (mSelectTimeIndex == 2) {
			mBeginTime = mMeetInfo.getStartTime2();
			mEndTime = mMeetInfo.getEndTime2();
		} else if (mSelectTimeIndex == 3) {
			mBeginTime = mMeetInfo.getStartTime3();
			mEndTime = mMeetInfo.getEndTime3();
		}

		request.post(ServerKeys.FULL_URL_SELECT_MEETING_TIME + "/" + mMeetingID + "?beginTime=" + mBeginTime
				+ "&endTime=" + mEndTime, null);
	}

	private void doAttendMeeting() {
		Log.d(TAG, "Attend Meeting");
		HttpRequest request = new HttpRequest();
		final CustomizedProgressDialog mProgressDialog = new CustomizedProgressDialog(this, R.string.waiting);

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				new CustomizedProgressDialog(MeetProfileActivity.this, R.string.succeed, DialogStyle.OK).show();

				mMeetInfo.setJoinStatus(0);
				updateMeetProfileUI();
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(R.string.attend_meeting_failure, Toast.LENGTH_LONG);
			}
		});
		Map<String, String> data = new TreeMap<String, String>();
		data.put(ServerKeys.KEY_MEETING_ID, String.valueOf(mMeetingID));
		data.put(ServerKeys.KEY_USER_ID, String.valueOf(mUserID));
		data.put(ServerKeys.KEY_USER_NAME, mUserName);
		data.put(ServerKeys.KEY_MEETING_USER_ID, String.valueOf(mMeetInfo.getCreateUserId()));
		request.post(ServerKeys.FULL_URL_ATTEND_MEET, data);
		mProgressDialog.show();
	}

	/** do cancel Meeting for Meeting Creator */
	private void doCancelMeeting() {
		HttpRequest request = new HttpRequest();

		final CustomizedProgressDialog mProgressDialog = new CustomizedProgressDialog(this, R.string.waiting);
		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				DialogUtils.showDialog(MeetProfileActivity.this, null, "Meeting cancelled", "OK", null,
						new OnDialogClickListener() {
							@Override
							public void onClick(boolean isPositiveBtn) {
								MeetProfileActivity.this.finish();
							}
						});
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mProgressDialog.dismiss();
						ToastHelper.showToast(R.string.cancel_attend_meeting_failure, Toast.LENGTH_LONG);
					}
				});
			}
		});
		// Map<String, String> data = new HashMap<String, String>();
		// data.put(ServerKeys.KEY_MEETING_ID, String.valueOf(mMeetingID));
		request.post(ServerKeys.FULL_URL_CANCEL_MEET + "/" + mMeetingID, null);

		mProgressDialog.show();
	}

	private void doDeleteMeeting() {
		HttpRequest request = new HttpRequest();

		final CustomizedProgressDialog mProgressDialog = new CustomizedProgressDialog(this, R.string.waiting);
		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				new CustomizedProgressDialog(MeetProfileActivity.this, R.string.meet_canceled, DialogStyle.OK).show();
				mMeetInfo.setJoinStatus(1);
				mMeetInfo.setCanJoin(true);
				updateMeetProfileUI();
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				// TODO Auto-generated method stub
				mProgressDialog.dismiss();
				ToastHelper.showToast(R.string.cancel_attend_meeting_failure, Toast.LENGTH_LONG);
			}
		});
		Map<String, String> data = new HashMap<String, String>();
		data.put(ServerKeys.KEY_MEETING_ID, String.valueOf(mMeetingID));
		data.put(ServerKeys.KEY_USER_ID, String.valueOf(mUserID));
		request.delete(ServerKeys.FULL_URL_CANCEL_ATTEND_MEET, data);

		mProgressDialog.show();
	}

	private void doRejectOrAgreeMeeting(final boolean isReject) {
		HttpRequest request = new HttpRequest();
		final CustomizedProgressDialog mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();

				CustomizedProgressDialog mDialog = null;
				if (isReject) {
					mDialog = new CustomizedProgressDialog(MeetProfileActivity.this, R.string.meet_canceled,
							DialogStyle.OK);
					mMeetInfo.setJoinStatus(3);
				} else {
					mDialog = new CustomizedProgressDialog(MeetProfileActivity.this, R.string.meet_succeed,
							DialogStyle.OK);
					mMeetInfo.setJoinStatus(0);
				}
				mDialog.show();
				updateMeetProfileUI();
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				// CustomizedProgressDialog mDialog = new
				// CustomizedProgressDialog(this, R.string.fa, style)
			}
		});
		Map<String, String> data = new HashMap<String, String>();
		data.put(ServerKeys.KEY_MEETING_ID, String.valueOf(mMeetingID));
		data.put(ServerKeys.KEY_USER_ID, String.valueOf(mUserID));
		if (isReject) {
			data.put(ServerKeys.KEY_STATUS, String.valueOf(2));
		} else {
			data.put(ServerKeys.KEY_STATUS, String.valueOf(1));
		}
		data.put(ServerKeys.KEY_MEETING_USER_ID, String.valueOf(mMeetInfo.getCreateUserId()));
		data.put(ServerKeys.KEY_USER_NAME, mUserName);

		mProgressDialog.show();
		request.post(ServerKeys.FULL_URL_UPDATE_MEETING_MEMBER_STATUS, data);
	}

}
