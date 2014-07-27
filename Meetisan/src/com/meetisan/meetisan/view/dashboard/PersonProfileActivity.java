package com.meetisan.meetisan.view.dashboard;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.MyApplication;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.CircleImageView;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.LabelWithIcon;

public class PersonProfileActivity extends Activity implements OnClickListener {

	private Bitmap mUserLogo;
	private TextView mSignatureTxt, mUniversityTxt, mTagOneTxt, mTagTwoTxt, mTagThreeTxt,
			mTagFourTxt, mTagFiveTxt, mTagNoTxt;
	private LabelWithIcon mMoreBtn;
	private PeopleInfo mUserInfo;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dashboard_profile);

		syncUserInfoFromPerfer();

		initView();
	}

	@Override
	public void onResume() {
		super.onResume();
		syncUserInfoFromServer(mUserInfo.getId());
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setText(R.string.person_profile);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

		mMoreBtn = (LabelWithIcon) findViewById(R.id.btn_more);
		mMoreBtn.setOnClickListener(this);
		TextView mNameTxt = (TextView) findViewById(R.id.txt_name);
		if (mUserInfo.getName() != null) {
			mNameTxt.setText(mUserInfo.getName());
		}
		CircleImageView mPortraitView = (CircleImageView) findViewById(R.id.iv_portrait);
		if (mUserLogo != null) {
			mPortraitView.setImageBitmap(mUserLogo);
		} else {
			mPortraitView.setImageResource(R.drawable.portrait_default);
		}
		mSignatureTxt = (TextView) findViewById(R.id.txt_signature);
		mUniversityTxt = (TextView) findViewById(R.id.txt_university);
		mTagOneTxt = (TextView) findViewById(R.id.txt_tag_one);
		mTagTwoTxt = (TextView) findViewById(R.id.txt_tag_two);
		mTagThreeTxt = (TextView) findViewById(R.id.txt_tag_three);
		mTagFourTxt = (TextView) findViewById(R.id.txt_tag_four);
		mTagFiveTxt = (TextView) findViewById(R.id.txt_tag_five);
		mTagNoTxt = (TextView) findViewById(R.id.txt_no_tags);
	}

	private void syncUserInfoFromPerfer() {
		mUserInfo = UserInfoKeeper.readUserInfo(this);
		mUserLogo = MyApplication.getmLogoBitmap();
	}

	private void updateUIData() {
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
			if (tagsCount <= 1) {
				mTagOneTxt.setVisibility(View.VISIBLE);
			}
			if (tagsCount <= 2) {
				mTagTwoTxt.setVisibility(View.VISIBLE);
			}
			if (tagsCount <= 3) {
				mTagThreeTxt.setVisibility(View.VISIBLE);
			}
			if (tagsCount <= 4) {
				mTagFourTxt.setVisibility(View.VISIBLE);
			}
			if (tagsCount <= 5) {
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
			Intent intent = new Intent(this, MoreInfoActivity.class);
			startActivity(intent);
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
					Log.d("PersonProfileActivity", "Result: " + result);
					json = new JSONObject(result);
					json2PeopleInfo(json, mUserInfo);
					UserInfoKeeper.writeUserInfo(PersonProfileActivity.this, mUserInfo);
					updateUIData();
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.app_occurred_exception);
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
		} else {
			userInfo.setName(null);
		}
		if (!userData.isNull(ServerKeys.KEY_SIGNATURE)) {
			userInfo.setSignature(userData.getString(ServerKeys.KEY_SIGNATURE));
		} else {
			userInfo.setSignature(null);
		}
		if (!userData.isNull(ServerKeys.KEY_UNIVERSITY)) {
			userInfo.setUniversity(userData.getString(ServerKeys.KEY_UNIVERSITY));
		} else {
			userInfo.setUniversity(null);
		}
		if (!userData.isNull(ServerKeys.KEY_CITY)) {
			userInfo.setCity(userData.getString(ServerKeys.KEY_CITY));
		} else {
			userInfo.setCity(null);
		}
		userInfo.setAge(userData.getInt(ServerKeys.KEY_AGE));
		userInfo.setGender(userData.getInt(ServerKeys.KEY_GENDER));
		if (!userData.isNull(ServerKeys.KEY_EXPERIENCE)) {
			userInfo.setExperience(userData.getString(ServerKeys.KEY_EXPERIENCE));
		} else {
			userInfo.setExperience(null);
		}
		if (!userData.isNull(ServerKeys.KEY_EDUCATION)) {
			userInfo.setEducation(userData.getString(ServerKeys.KEY_EDUCATION));
		} else {
			userInfo.setEducation(null);
		}
		if (!userData.isNull(ServerKeys.KEY_SKILLS)) {
			userInfo.setSkills(userData.getString(ServerKeys.KEY_SKILLS));
		} else {
			userInfo.setSkills(null);
		}
		userInfo.setLongitude((float) userData.getDouble(ServerKeys.KEY_LON));
		userInfo.setLatitude((float) userData.getDouble(ServerKeys.KEY_LAT));
		userInfo.setStatus(userData.getInt(ServerKeys.KEY_STATUS));
		if (!userData.isNull(ServerKeys.KEY_CREATE_DATE)) {
			userInfo.setCreateDate(userData.getString(ServerKeys.KEY_CREATE_DATE));
		} else {
			userInfo.setCreateDate(null);
		}
		if (!userData.isNull(ServerKeys.KEY_REG_ID)) {
			userInfo.setRegId(userData.getString(ServerKeys.KEY_REG_ID));
		} else {
			userInfo.setRegId(null);
		}
	}
}