package com.meetisan.meetisan.view.dashboard;

import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.ClearEditText;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

public class PersonMoreInfoActivity extends Activity implements OnClickListener {

	private ClearEditText mSchoolTxt, mCityTxt;
	private EditText mExperienceTxt, mEducationTxt, mSkillsTxt;
	private TextView mSaveTxt, mBirthdayTxt, mGenderTxt;
	private PeopleInfo mUserInfo = new PeopleInfo();
	private long userId = -1;
	private long curUserId = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_person_profile_more);

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

		syncUserInfoFromServer(userId);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setText(R.string.more_information);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);

		// mNameTxt = (EditText) findViewById(R.id.txt_name);
		mSchoolTxt = (ClearEditText) findViewById(R.id.txt_school);
		mCityTxt = (ClearEditText) findViewById(R.id.txt_city);
		mBirthdayTxt = (TextView) findViewById(R.id.txt_age);
		// mBirthdayTxt.setEnabled(false);
		mBirthdayTxt.setOnClickListener(this);
		mGenderTxt = (TextView) findViewById(R.id.txt_gender);
		mGenderTxt.setOnClickListener(this);
		mExperienceTxt = (EditText) findViewById(R.id.txt_experience);
		mEducationTxt = (EditText) findViewById(R.id.txt_education);
		mSkillsTxt = (EditText) findViewById(R.id.txt_skills);

		Log.e("-------", "Profile User ID: " + userId + "; Cur User ID: " + curUserId);
		if (userId != curUserId) {
			// mNameTxt.setEnabled(false);
			mSchoolTxt.setEnabled(false);
			mCityTxt.setEnabled(false);
			mBirthdayTxt.setEnabled(false);
			mGenderTxt.setEnabled(false);
			mExperienceTxt.setEnabled(false);
			mEducationTxt.setEnabled(false);
			mSkillsTxt.setEnabled(false);
		} else {
			mSaveTxt = (TextView) findViewById(R.id.txt_title_right);
			mSaveTxt.setText(R.string.save);
			mSaveTxt.setOnClickListener(this);
			mSaveTxt.setVisibility(View.VISIBLE);
		}
	}

	private void updateUIData() {
		// mNameTxt.setText(mUserInfo.getName());
		mSchoolTxt.setText(Util.formatOutput(mUserInfo.getUniversity()));
		mCityTxt.setText(Util.formatOutput(mUserInfo.getCity()));
		mBirthdayTxt.setText(Util.formatOutput(mUserInfo.getBirthday()));
		mGenderTxt.setText(mUserInfo.getGender() == 0 ? "Male" : "Female");
		mExperienceTxt.setText(Util.formatOutput(mUserInfo.getExperience()));
		mEducationTxt.setText(Util.formatOutput(mUserInfo.getEducation()));
		mSkillsTxt.setText(Util.formatOutput(mUserInfo.getSkills()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			PersonMoreInfoActivity.this.finish();
			break;
		case R.id.txt_title_right:
			if (userId == curUserId) {
				syncUserInfoToServer();
			} else {
				PersonMoreInfoActivity.this.finish();
			}
			break;
		case R.id.txt_age:
			Intent intent = new Intent(this, SettingsBirthdayActivity.class);
			startActivityForResult(intent, SettingsBirthdayActivity.REQUEST_CODE);
			break;
		case R.id.txt_gender:
			Intent intent1 = new Intent(this, SettingsGenderActivity.class);
			startActivityForResult(intent1, SettingsGenderActivity.REQUEST_CODE);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		switch (requestCode) {
		case SettingsBirthdayActivity.REQUEST_CODE:
			String birthday = data.getStringExtra(SettingsBirthdayActivity.RESPONSE_NAME_VALUE);
			mBirthdayTxt.setText(birthday);
			break;
		case SettingsGenderActivity.REQUEST_CODE:
			int gender = data.getIntExtra(SettingsGenderActivity.RESPONSE_NAME_VALUE, mUserInfo.getGender());
			mGenderTxt.setText(gender == 0 ? "Female" : "Male");
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
			mProgressDialog = new CustomizedProgressDialog(this, R.string.loading);
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
		// else {
		// userInfo.setName("-");
		// }
		// userInfo.setAvatar(Util.base64ToBitmap(userData.getString(ServerKeys.KEY_AVATAR)));
		// if (!userData.isNull(ServerKeys.KEY_SIGNATURE)) {
		// userInfo.setSignature(userData.getString(ServerKeys.KEY_SIGNATURE));
		// }
		if (!userData.isNull(ServerKeys.KEY_UNIVERSITY)) {
			userInfo.setUniversity(userData.getString(ServerKeys.KEY_UNIVERSITY));
		}
		// else {
		// userInfo.setUniversity("-");
		// }
		if (!userData.isNull(ServerKeys.KEY_CITY)) {
			userInfo.setCity(userData.getString(ServerKeys.KEY_CITY));
		}
		// else {
		// userInfo.setCity("-");
		// }
		userInfo.setBirthday(userData.getString(ServerKeys.KEY_AGE));
		userInfo.setGender(userData.getInt(ServerKeys.KEY_GENDER));
		if (!userData.isNull(ServerKeys.KEY_EXPERIENCE)) {
			userInfo.setExperience(userData.getString(ServerKeys.KEY_EXPERIENCE));
		}
		// else {
		// userInfo.setExperience("-");
		// }
		if (!userData.isNull(ServerKeys.KEY_EDUCATION)) {
			userInfo.setEducation(userData.getString(ServerKeys.KEY_EDUCATION));
		}
		// else {
		// userInfo.setEducation("-");
		// }
		if (!userData.isNull(ServerKeys.KEY_SKILLS)) {
			userInfo.setSkills(userData.getString(ServerKeys.KEY_SKILLS));
		} else {
			userInfo.setSkills("-");
		}
		// userInfo.setLongitude((float)
		// userData.getDouble(ServerKeys.KEY_LON));
		// userInfo.setLatitude((float) userData.getDouble(ServerKeys.KEY_LAT));
		userInfo.setStatus(userData.getInt(ServerKeys.KEY_STATUS));
		if (!userData.isNull(ServerKeys.KEY_CREATE_DATE)) {
			userInfo.setCreateDate(userData.getString(ServerKeys.KEY_CREATE_DATE));
		}
		// else {
		// userInfo.setCreateDate("-");
		// }
		if (!userData.isNull(ServerKeys.KEY_REG_ID)) {
			userInfo.setRegId(userData.getString(ServerKeys.KEY_REG_ID));
		}
	}

	private void syncUserInfoToServer() {
		HttpRequest request = new HttpRequest();

		// String mName = mNameTxt.getText().toString();
		String mSchool = mSchoolTxt.getText().toString();
		String mCity = mCityTxt.getText().toString();
		String mBirthday = mBirthdayTxt.getText().toString();
		int mGender = mGenderTxt.getText().toString().equals("Male") ? 0 : 1;
		String mExperience = mExperienceTxt.getText().toString();
		String mEducation = mEducationTxt.getText().toString();
		String mSkills = mSkillsTxt.getText().toString();

		Map<String, String> data = new TreeMap<String, String>();
		data.put(ServerKeys.KEY_ID, String.valueOf(curUserId));
		data.put(ServerKeys.KEY_PASSWORD, UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_PWD, ""));

		// if (!mUserInfo.getName().equals(mName)) {
		// data.put(ServerKeys.KEY_NAME, mName);
		// }
		if ((mUserInfo.getUniversity() == null && mSchool != null) || !mUserInfo.getUniversity().equals(mSchool)) {
			data.put(ServerKeys.KEY_UNIVERSITY, mSchool);
		}
		if ((mUserInfo.getCity() == null && mCity != null) || !mUserInfo.getCity().equals(mCity)) {
			data.put(ServerKeys.KEY_CITY, mCity);
		}
		if (!mUserInfo.getBirthday().equals(mBirthday)) {
			data.put(ServerKeys.KEY_AGE, mBirthday);
		}
		if ((mUserInfo.getExperience() == null && mExperience != null)
				|| !mUserInfo.getExperience().equals(mExperience)) {
			data.put(ServerKeys.KEY_EXPERIENCE, mExperience);
		}
		if ((mUserInfo.getEducation() == null && mEducation != null) || !mUserInfo.getEducation().equals(mEducation)) {
			data.put(ServerKeys.KEY_EDUCATION, mEducation);
		}
		if ((mUserInfo.getSkills() == null && mSkills != null) || !mUserInfo.getSkills().equals(mSkills)) {
			data.put(ServerKeys.KEY_SKILLS, mSkills);
		}
		if (mUserInfo.getGender() != mGender) {
			data.put(ServerKeys.KEY_GENDER, String.valueOf(mGender));
		}

		if (data.size() <= 2) {
			this.finish();
			return;
		}

		if (mProgressDialog == null) {
			mProgressDialog = new CustomizedProgressDialog(this, R.string.update_user_info);
		} else {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			mProgressDialog.setMsgId(R.string.update_user_info);
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				PersonMoreInfoActivity.this.finish();
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		request.post(ServerKeys.FULL_URL_UPDATE_USER_INFO, data);
		mProgressDialog.show();
	}
}
