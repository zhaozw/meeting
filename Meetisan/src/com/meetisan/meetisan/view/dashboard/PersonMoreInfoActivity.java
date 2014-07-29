package com.meetisan.meetisan.view.dashboard;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

public class PersonMoreInfoActivity extends Activity implements OnClickListener {

	private TextView mNameTxt, mSchoolTxt, mCityTxt, mAgeTxt, mGenderTxt, mExperienceTxt,
			mEducationTxt, mSkillsTxt;
	private PeopleInfo mUserInfo = new PeopleInfo();
	private long userId = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_person_profile_more);

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
		mTitleTxt.setText(R.string.information);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

		mNameTxt = (TextView) findViewById(R.id.txt_name);
		mSchoolTxt = (TextView) findViewById(R.id.txt_school);
		mCityTxt = (TextView) findViewById(R.id.txt_city);
		mAgeTxt = (TextView) findViewById(R.id.txt_age);
		mGenderTxt = (TextView) findViewById(R.id.txt_gender);
		mExperienceTxt = (TextView) findViewById(R.id.txt_experience);
		mEducationTxt = (TextView) findViewById(R.id.txt_education);
		mSkillsTxt = (TextView) findViewById(R.id.txt_skills);
	}

	private void updateUIData() {
		mNameTxt.setText(mUserInfo.getName());
		mSchoolTxt.setText(mUserInfo.getUniversity());
		mCityTxt.setText(mUserInfo.getCity());
		mAgeTxt.setText(String.valueOf(mUserInfo.getAge()));
		mGenderTxt.setText(mUserInfo.getGender() == 0 ? "Male" : "Female");
		mExperienceTxt.setText(mUserInfo.getExperience());
		mEducationTxt.setText(mUserInfo.getEducation());
		mSkillsTxt.setText(mUserInfo.getSkills());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
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
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.app_occurred_exception);
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
		// userInfo.setAvatar(Util.base64ToBitmap(userData.getString(ServerKeys.KEY_AVATAR)));
		// if (!userData.isNull(ServerKeys.KEY_SIGNATURE)) {
		// userInfo.setSignature(userData.getString(ServerKeys.KEY_SIGNATURE));
		// }
		if (!userData.isNull(ServerKeys.KEY_UNIVERSITY)) {
			userInfo.setUniversity(userData.getString(ServerKeys.KEY_UNIVERSITY));
		}
		if (!userData.isNull(ServerKeys.KEY_CITY)) {
			userInfo.setCity(userData.getString(ServerKeys.KEY_CITY));
		}
		userInfo.setAge(userData.getInt(ServerKeys.KEY_AGE));
		userInfo.setGender(userData.getInt(ServerKeys.KEY_GENDER));
		if (!userData.isNull(ServerKeys.KEY_EXPERIENCE)) {
			userInfo.setExperience(userData.getString(ServerKeys.KEY_EXPERIENCE));
		}
		if (!userData.isNull(ServerKeys.KEY_EDUCATION)) {
			userInfo.setEducation(userData.getString(ServerKeys.KEY_EDUCATION));
		}
		if (!userData.isNull(ServerKeys.KEY_SKILLS)) {
			userInfo.setSkills(userData.getString(ServerKeys.KEY_SKILLS));
		}
		// userInfo.setLongitude((float) userData.getDouble(ServerKeys.KEY_LON));
		// userInfo.setLatitude((float) userData.getDouble(ServerKeys.KEY_LAT));
		userInfo.setStatus(userData.getInt(ServerKeys.KEY_STATUS));
		if (!userData.isNull(ServerKeys.KEY_CREATE_DATE)) {
			userInfo.setCreateDate(userData.getString(ServerKeys.KEY_CREATE_DATE));
		}
		if (!userData.isNull(ServerKeys.KEY_REG_ID)) {
			userInfo.setRegId(userData.getString(ServerKeys.KEY_REG_ID));
		}
	}
}
