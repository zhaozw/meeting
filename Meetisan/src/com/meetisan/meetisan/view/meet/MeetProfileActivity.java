package com.meetisan.meetisan.view.meet;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.MeetingInfo;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.CircleImageView;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.LabelWithIcon;

public class MeetProfileActivity extends Activity implements OnClickListener {
	private static final String TAG = MeetProfileActivity.class.getSimpleName();

	private ImageButton mConnectionView;
	private CircleImageView mLogoView;
	private LabelWithIcon mLocationBtn;
	private TextView mTitleTxt, mDescriptionTxt, mStartTimeTxt, mEndTimeTxt, mFirstTagTxt, mSecondTagTxt, mThirdTagTxt,
			mNoTagTxt;

	private long mMeetingID = -1, mUserID = -1;
	private MeetingInfo mMeetInfo = new MeetingInfo();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_meet_profile);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			mMeetingID = bundle.getLong("MeetingID");
			mUserID = bundle.getLong("UserID");
		}

		if (mMeetingID < 0 || mUserID < 0) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		initView();

		getMeetProfileFromServer();
	}

	private void initView() {
		((ImageButton) findViewById(R.id.btn_title_icon_left)).setOnClickListener(this);

		mConnectionView = (ImageButton) findViewById(R.id.btn_connections);
		mConnectionView.setOnClickListener(this);
		mLogoView = (CircleImageView) findViewById(R.id.iv_logo);
		mTitleTxt = (TextView) findViewById(R.id.txt_meet_title);
		mDescriptionTxt = (TextView) findViewById(R.id.txt_meet_description);
		mStartTimeTxt = (TextView) findViewById(R.id.txt_time_start);
		mEndTimeTxt = (TextView) findViewById(R.id.txt_time_end);
		mLocationBtn = (LabelWithIcon) findViewById(R.id.btn_location);
		mLocationBtn.setOnClickListener(this);

		mFirstTagTxt = (TextView) findViewById(R.id.txt_meet_one);
		mSecondTagTxt = (TextView) findViewById(R.id.txt_meet_two);
		mThirdTagTxt = (TextView) findViewById(R.id.txt_meet_three);
		mNoTagTxt = (TextView) findViewById(R.id.txt_no_tags);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_icon_left:
			finish();
			break;
		case R.id.btn_location:

			break;
		case R.id.btn_connections:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putLong("MeetingID", mMeetingID);
			intent.setClass(MeetProfileActivity.this, MeetMemberActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void updateMeetProfileUI() {
		if (mMeetInfo == null) {
			return;
		}

		if (mMeetInfo.getLogo() != null) {
			mLogoView.setImageBitmap(mMeetInfo.getLogo());
		}
		if (mMeetInfo.getTitle() != null) {
			mTitleTxt.setText(mMeetInfo.getTitle());
		}
		if (mMeetInfo.getDescription() != null) {
			mDescriptionTxt.setText(mMeetInfo.getDescription());
		}
		mStartTimeTxt.setText(Util.convertDateTime(mMeetInfo.getStartTime()));
		mEndTimeTxt.setText(Util.convertDateTime(mMeetInfo.getEndTime()));
		if (mMeetInfo.getAddress() != null) {
			mLocationBtn.setText("Location   " + mMeetInfo.getAddress());
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
			mNoTagTxt.setText("Don\'t have any Tag !");
			mNoTagTxt.setVisibility(View.VISIBLE);
		}
	}

	private CustomizedProgressDialog mProgressDialog = null;

	private void getMeetProfileFromServer() {
		HttpRequest request = new HttpRequest();

		if (mProgressDialog == null) {
			mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);
		} else {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				try {
					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);

					mMeetInfo.setJoin(dataJson.getInt(ServerKeys.KEY_JOIN_STATUS));

					JSONObject meetJson = dataJson.getJSONObject(ServerKeys.KEY_MEETING);
					mMeetInfo.setId(meetJson.getLong(ServerKeys.KEY_ID));
					if (!meetJson.isNull(ServerKeys.KEY_TITLE)) {
						mMeetInfo.setTitle(meetJson.getString(ServerKeys.KEY_TITLE));
					}
					if (!meetJson.isNull(ServerKeys.KEY_DESCRIPTION)) {
						mMeetInfo.setDescription(meetJson.getString(ServerKeys.KEY_DESCRIPTION));
					}
					mMeetInfo.setLatitude((float) meetJson.getDouble(ServerKeys.KEY_LAT));
					mMeetInfo.setLongitude((float) meetJson.getDouble(ServerKeys.KEY_LON));
					if (!meetJson.isNull(ServerKeys.KEY_ADDRESS)) {
						mMeetInfo.setAddress(meetJson.getString(ServerKeys.KEY_ADDRESS));
					}
					mMeetInfo.setStartTime(meetJson.getString(ServerKeys.KEY_START_TIME));
					mMeetInfo.setEndTime(meetJson.getString(ServerKeys.KEY_END_TIME));
					mMeetInfo.setCreateDate(meetJson.getString(ServerKeys.KEY_CREATE_DATE));
					mMeetInfo.setLogo(Util.base64ToBitmap(meetJson.getString(ServerKeys.KEY_LOGO)));
					mMeetInfo.setCreateDate(meetJson.getString(ServerKeys.KEY_CREATE_DATE));
					mMeetInfo.setStatus(meetJson.getInt(ServerKeys.KEY_STATUS));

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

}
