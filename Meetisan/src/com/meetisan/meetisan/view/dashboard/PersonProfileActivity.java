package com.meetisan.meetisan.view.dashboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.MainActivity;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.HttpBitmap;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.view.meet.ReportActivity;
import com.meetisan.meetisan.view.tags.PersonTagsActivity;
import com.meetisan.meetisan.widget.CircleImageView;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.LabelWithIcon;
import com.meetisan.meetisan.widget.TagLabelLayout;

public class PersonProfileActivity extends Activity implements OnClickListener {

	private CircleImageView mPortraitView;
	private TextView mNameTxt, mSignatureTxt, mUniversityTxt, mTagNoTxt, mTagTitleTxt, mReportTxt;
	private TagLabelLayout mTagOneTxt, mTagTwoTxt, mTagThreeTxt, mTagFourTxt, mTagFiveTxt;
	private RelativeLayout mTagsLayout;

	private LabelWithIcon mMoreBtn;
	private LinearLayout mMeetLayout;
	private PeopleInfo mUserInfo = new PeopleInfo();
	private long userId = -1;
	private long curUserId = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_person_profile);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			userId = bundle.getLong("UserID");
		}

		curUserId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);
		if (userId < 0 || curUserId < 0) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		initView();

	}

	@Override
	public void onResume() {
		super.onResume();
		syncUserInfoFromServer(userId);
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

		mMeetLayout = (LinearLayout) findViewById(R.id.layout_meet);
		if (userId == curUserId) {
			mTitleTxt.setText(R.string.profile_summary);
			mMeetLayout.setVisibility(View.GONE);
		} else {
			mTitleTxt.setText(R.string.profile);
		}

		findViewById(R.id.btn_meet).setOnClickListener(this);
		mMoreBtn = (LabelWithIcon) findViewById(R.id.btn_more);
		mMoreBtn.setOnClickListener(this);
		mNameTxt = (TextView) findViewById(R.id.txt_name);
		mPortraitView = (CircleImageView) findViewById(R.id.iv_portrait);
		mSignatureTxt = (TextView) findViewById(R.id.txt_signature);
		mUniversityTxt = (TextView) findViewById(R.id.txt_university);
		mTagOneTxt = (TagLabelLayout) findViewById(R.id.txt_tag_one);
		mTagTwoTxt = (TagLabelLayout) findViewById(R.id.txt_tag_two);
		mTagThreeTxt = (TagLabelLayout) findViewById(R.id.txt_tag_three);
		mTagFourTxt = (TagLabelLayout) findViewById(R.id.txt_tag_four);
		mTagFiveTxt = (TagLabelLayout) findViewById(R.id.txt_tag_five);
		mTagNoTxt = (TextView) findViewById(R.id.txt_no_tags);
		mTagTitleTxt = (TextView) findViewById(R.id.txt_profile_tag_title);

		mReportTxt = (TextView) findViewById(R.id.txt_report);
		mReportTxt.setOnClickListener(this);

		mTagsLayout = (RelativeLayout) findViewById(R.id.layout_top_tags);
		mTagsLayout.setOnClickListener(this);
	}

	private void updateUIData() {
		String formatName = Util.formatOutput(mUserInfo.getName());
		mNameTxt.setText(formatName);
		mTagTitleTxt.setText(formatName + " \'s Tags");

		if (mUserInfo.getAvatarUri() != null) {
			HttpBitmap httpBitmap = new HttpBitmap(this);
			httpBitmap.displayBitmap(mPortraitView, mUserInfo.getAvatarUri());
		} else {
			mPortraitView.setImageResource(R.drawable.portrait_person_default);
		}
		mSignatureTxt.setText(Util.formatOutput(mUserInfo.getSignature()));
		mUniversityTxt.setText(Util.formatOutput(mUserInfo.getUniversity()));
		int tagsCount = mUserInfo.getTopTags().size();
		mTagOneTxt.setTitleText(null);
		mTagTwoTxt.setTitleText(null);
		mTagThreeTxt.setTitleText(null);
		mTagFourTxt.setTitleText(null);
		mTagFiveTxt.setTitleText(null);
		if (tagsCount <= 0) {
			mTagNoTxt.setVisibility(View.VISIBLE);
			mTagNoTxt.setText("-");
		} else {
			mTagNoTxt.setVisibility(View.GONE);
			TagInfo tagInfo = null;
			if (tagsCount >= 1) {
				tagInfo = mUserInfo.getTopTags().get(0);
				mTagOneTxt.setTagText(tagInfo.getEndorsed(), tagInfo.getTitle());
			}
			if (tagsCount >= 2) {
				tagInfo = mUserInfo.getTopTags().get(1);
				mTagTwoTxt.setTagText(tagInfo.getEndorsed(), tagInfo.getTitle());
			}
			if (tagsCount >= 3) {
				tagInfo = mUserInfo.getTopTags().get(2);
				mTagThreeTxt.setTagText(tagInfo.getEndorsed(), tagInfo.getTitle());
			}
			if (tagsCount >= 4) {
				tagInfo = mUserInfo.getTopTags().get(3);
				mTagFourTxt.setTagText(tagInfo.getEndorsed(), tagInfo.getTitle());
			}
			if (tagsCount >= 5) {
				tagInfo = mUserInfo.getTopTags().get(4);
				mTagFiveTxt.setTagText(tagInfo.getEndorsed(), tagInfo.getTitle());
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_more:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putLong("UserID", userId);
			intent.setClass(this, PersonMoreInfoActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case R.id.btn_meet:
			Intent meetIntent = new Intent();
			Bundle meetBundle = new Bundle();
			meetBundle.putBoolean("IsMeetPerson", true);
			meetBundle.putLong("PersonID", userId);
			meetBundle.putString("PersonName", mUserInfo.getName());
			meetBundle.putString("AvatarUri", mUserInfo.getAvatarUri());
			meetIntent.setClass(PersonProfileActivity.this, MainActivity.class);
			meetIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// 设置不要刷新将要跳到的界面
			meetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 它可以关掉所要到的界面中间的activity
			meetIntent.putExtras(meetBundle);
			startActivity(meetIntent);
			break;
		case R.id.layout_top_tags:
			Intent tagIntent = new Intent();
			Bundle tagBundle = new Bundle();
			tagBundle.putLong("UserID", userId);
			tagBundle.putString("PersonName", mUserInfo.getName());
			tagIntent.setClass(this, PersonTagsActivity.class);
			tagIntent.putExtras(tagBundle);
			startActivity(tagIntent);
			break;
		case R.id.txt_report:
			Intent reportIntent = new Intent();
			Bundle reportBundle = new Bundle();
			reportBundle.putLong("ReportID", userId);
			reportBundle.putBoolean("IsReportUser", true);
			reportIntent.setClass(this, ReportActivity.class);
			reportIntent.putExtras(reportBundle);
			startActivity(reportIntent);
			break;
		default:
			break;
		}
	}

	private CustomizedProgressDialog mProgressDialog = null;

	private void syncUserInfoFromServer(long userId) {
		if (userId < 0) {
			return;
		}
		HttpRequest request = new HttpRequest();

		if (mProgressDialog == null) {
			mProgressDialog = new CustomizedProgressDialog(this, R.string.loading_userinfo);
		} else {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				JSONObject json;
				try {
					json = new JSONObject(result);
					json2PeopleInfo(json, mUserInfo);
					// UserInfoKeeper.writeUserInfo(PersonProfileActivity.this,
					// mUserInfo);
					// TODO.. for edit current user info
				} catch (JSONException e) {
					e.printStackTrace();
					// TODO.. server response exception, if Data is null, return
					// is JSONArray not
					// JSONObject
					// ToastHelper.showToast(R.string.app_occurred_exception);
				} finally {
					updateUIData();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		request.get(ServerKeys.FULL_URL_GET_USER_INFO + "/" + userId, null);
		mProgressDialog.show();
	}

	private void json2PeopleInfo(JSONObject json, PeopleInfo userInfo) throws JSONException {
		if (json == null) {
			return;
		}

		JSONObject data = json.getJSONObject(ServerKeys.KEY_DATA);
		JSONObject userData = data.getJSONObject(ServerKeys.KEY_USER);
		if (!userData.isNull(ServerKeys.KEY_NAME)) {
			userInfo.setName(userData.getString(ServerKeys.KEY_NAME));
		}
		if (!userData.isNull(ServerKeys.KEY_AVATAR)) {
			userInfo.setAvatarUri(userData.getString(ServerKeys.KEY_AVATAR));
		}
		if (!userData.isNull(ServerKeys.KEY_BG)) {
			userInfo.setBgUri(userData.getString(ServerKeys.KEY_BG));
		}
		if (!userData.isNull(ServerKeys.KEY_SIGNATURE)) {
			userInfo.setSignature(userData.getString(ServerKeys.KEY_SIGNATURE));
		}
		if (!userData.isNull(ServerKeys.KEY_UNIVERSITY)) {
			userInfo.setUniversity(userData.getString(ServerKeys.KEY_UNIVERSITY));
		}
		// if (!userData.isNull(ServerKeys.KEY_CITY)) {
		// userInfo.setCity(userData.getString(ServerKeys.KEY_CITY));
		// } else {
		// userInfo.setCity(null);
		// }
		// userInfo.setAge(userData.getInt(ServerKeys.KEY_AGE));
		// userInfo.setGender(userData.getInt(ServerKeys.KEY_GENDER));
		// if (!userData.isNull(ServerKeys.KEY_EXPERIENCE)) {
		// userInfo.setExperience(userData.getString(ServerKeys.KEY_EXPERIENCE));
		// } else {
		// userInfo.setExperience(null);
		// }
		// if (!userData.isNull(ServerKeys.KEY_EDUCATION)) {
		// userInfo.setEducation(userData.getString(ServerKeys.KEY_EDUCATION));
		// } else {
		// userInfo.setEducation(null);
		// }
		// if (!userData.isNull(ServerKeys.KEY_SKILLS)) {
		// userInfo.setSkills(userData.getString(ServerKeys.KEY_SKILLS));
		// } else {
		// userInfo.setSkills(null);
		// }
		// userInfo.setLongitude((float)
		// userData.getDouble(ServerKeys.KEY_LON));
		// userInfo.setLatitude((float) userData.getDouble(ServerKeys.KEY_LAT));
		userInfo.setStatus(userData.getInt(ServerKeys.KEY_STATUS));
		// if (!userData.isNull(ServerKeys.KEY_CREATE_DATE)) {
		// userInfo.setCreateDate(userData.getString(ServerKeys.KEY_CREATE_DATE));
		// } else {
		// userInfo.setCreateDate(null);
		// }
		if (!userData.isNull(ServerKeys.KEY_REG_ID)) {
			userInfo.setRegId(userData.getString(ServerKeys.KEY_REG_ID));
		}

		userInfo.clearTopTags();
		JSONArray tagArray = data.getJSONArray(ServerKeys.KEY_TOP_TAGS);
		for (int j = 0; j < tagArray.length(); j++) {
			TagInfo tagInfo = new TagInfo();
			JSONObject tagJson = tagArray.getJSONObject(j);
			// tagInfo.setId(tagJson.getLong(ServerKeys.KEY_ID));
			if (!tagJson.isNull(ServerKeys.KEY_TITLE)) {
				tagInfo.setTitle(tagJson.getString(ServerKeys.KEY_TITLE));
			}
			tagInfo.setEndorsed(tagJson.getLong(ServerKeys.KEY_ENDORSEMENTS));
			userInfo.addTopTag(tagInfo);
		}
	}
}
