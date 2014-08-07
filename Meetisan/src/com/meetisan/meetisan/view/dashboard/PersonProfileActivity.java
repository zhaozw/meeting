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
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.MainActivity;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.HttpBitmap;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.CircleImageView;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.LabelWithIcon;

public class PersonProfileActivity extends Activity implements OnClickListener {

	private CircleImageView mPortraitView;
	private TextView mNameTxt, mSignatureTxt, mUniversityTxt, mTagOneTxt, mTagTwoTxt, mTagThreeTxt,
			mTagFourTxt, mTagFiveTxt, mTagNoTxt;
	private LabelWithIcon mMoreBtn;
	private PeopleInfo mUserInfo = new PeopleInfo();
	private long userId = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_person_profile);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			userId = bundle.getLong("UserID");
		}

		if (userId < 0) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		initView();

		syncUserInfoFromServer(userId);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setText(R.string.person_profile);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

		findViewById(R.id.btn_meet).setOnClickListener(this);
		mMoreBtn = (LabelWithIcon) findViewById(R.id.btn_more);
		mMoreBtn.setOnClickListener(this);
		mNameTxt = (TextView) findViewById(R.id.txt_name);
		mPortraitView = (CircleImageView) findViewById(R.id.iv_portrait);
		mSignatureTxt = (TextView) findViewById(R.id.txt_signature);
		mUniversityTxt = (TextView) findViewById(R.id.txt_university);
		mTagOneTxt = (TextView) findViewById(R.id.txt_tag_one);
		mTagTwoTxt = (TextView) findViewById(R.id.txt_tag_two);
		mTagThreeTxt = (TextView) findViewById(R.id.txt_tag_three);
		mTagFourTxt = (TextView) findViewById(R.id.txt_tag_four);
		mTagFiveTxt = (TextView) findViewById(R.id.txt_tag_five);
		mTagNoTxt = (TextView) findViewById(R.id.txt_no_tags);
	}

	private void updateUIData() {
		if (mUserInfo.getName() != null) {
			mNameTxt.setText(mUserInfo.getName());
		}
		if (mUserInfo.getAvatarUri() != null) {
			HttpBitmap httpBitmap = new HttpBitmap(this);
			httpBitmap.displayBitmap(mPortraitView, mUserInfo.getAvatarUri());
		} else {
			mPortraitView.setImageResource(R.drawable.portrait_default);
		}
		mSignatureTxt.setText(mUserInfo.getSignature());
		mUniversityTxt.setText(mUserInfo.getUniversity());
		int tagsCount = mUserInfo.getTopTags().size();
		if (tagsCount <= 0) {
			mTagNoTxt.setText("You do not have Tag!");
			mTagNoTxt.setVisibility(View.VISIBLE);
			mTagOneTxt.setVisibility(View.GONE);
			mTagTwoTxt.setVisibility(View.GONE);
			mTagThreeTxt.setVisibility(View.GONE);
			mTagFourTxt.setVisibility(View.GONE);
			mTagFiveTxt.setVisibility(View.GONE);
		} else {
			mTagNoTxt.setVisibility(View.GONE);
			if (tagsCount >= 1) {
				mTagOneTxt.setText(mUserInfo.getTopTags().get(0).getTitle());
				mTagOneTxt.setVisibility(View.VISIBLE);
			}
			if (tagsCount >= 2) {
				mTagTwoTxt.setText(mUserInfo.getTopTags().get(1).getTitle());
				mTagTwoTxt.setVisibility(View.VISIBLE);
			}
			if (tagsCount >= 3) {
				mTagThreeTxt.setText(mUserInfo.getTopTags().get(2).getTitle());
				mTagThreeTxt.setVisibility(View.VISIBLE);
			}
			if (tagsCount >= 4) {
				mTagFourTxt.setText(mUserInfo.getTopTags().get(3).getTitle());
				mTagFourTxt.setVisibility(View.VISIBLE);
			}
			if (tagsCount >= 5) {
				mTagFiveTxt.setText(mUserInfo.getTopTags().get(4).getTitle());
				mTagFiveTxt.setVisibility(View.VISIBLE);
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
