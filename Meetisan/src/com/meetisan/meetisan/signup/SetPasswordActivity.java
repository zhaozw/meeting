package com.meetisan.meetisan.signup;

import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.meetisan.meetisan.MainActivity;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

public class SetPasswordActivity extends Activity implements OnClickListener {

	private Button mSetPwdBtn;
	private EditText mPwdTxt, mConfirmPwdTxt;
	private String email = null;
	private String mActivationCode = null, mInputCode = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_set_password);

		initView();

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			mActivationCode = bundle.getString("ActivationCode");
			mInputCode = bundle.getString("InputCode");
		}

		email = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_EMAIL, null);
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.password);
		mTitleTxt.setVisibility(View.VISIBLE);
		mSetPwdBtn = (Button) findViewById(R.id.btn_set_pwd);
		mSetPwdBtn.setOnClickListener(this);
		mPwdTxt = (EditText) findViewById(R.id.txt_pwd);
		mConfirmPwdTxt = (EditText) findViewById(R.id.txt_confirm_pwd);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_set_pwd:
			attemptSetPassword();
			break;
		default:
			break;
		}
	}

	private void attemptSetPassword() {
		String pwd = mPwdTxt.getText().toString();
		String confirmPwd = mConfirmPwdTxt.getText().toString();

		if (TextUtils.isEmpty(pwd)) {
			ToastHelper.showToast(R.string.empty_pwd_tips);
			return;
		}
		if (TextUtils.isEmpty(confirmPwd)) {
			ToastHelper.showToast(R.string.empty_confirm_pwd_tips);
			return;
		}
		if (!confirmPwd.equals(pwd)) {
			ToastHelper.showToast(R.string.error_pwd_not_match);
			return;
		}

		doSetPassword(email, pwd);
	}

	private CustomizedProgressDialog mProgressDialog = null;

	private void doSetPassword(final String email, final String pwd) {
		if (email == null || TextUtils.isEmpty(email)) {
			return;
		}

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

				doLogin(email, pwd);
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		Map<String, String> data = new TreeMap<String, String>();
		data.put(ServerKeys.KEY_EMAIL, email);
		data.put(ServerKeys.KEY_PASSWORD, pwd);
		if (mActivationCode != null && mInputCode != null) {
			data.put(ServerKeys.KEY_CODE_ID, mActivationCode);
			data.put(ServerKeys.KEY_CODE, mInputCode);
			request.post(ServerKeys.FULL_URL_UPDATE_USER_PWD, data);
		} else {
			request.post(ServerKeys.FULL_URL_REGISTER, data);
		}
		mProgressDialog.show();
	}

	private String registrationID = null;

	private void doLogin(final String email, final String pwd) {
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

				JSONObject json;
				try {
					PeopleInfo mUserInfo = new PeopleInfo();
					json = new JSONObject(result);
					JSONObject data = json.getJSONObject(ServerKeys.KEY_DATA);
					mUserInfo.setId(data.getLong(ServerKeys.KEY_ID));
					if (!data.isNull(ServerKeys.KEY_NAME)) {
						mUserInfo.setName(data.getString(ServerKeys.KEY_NAME));
					}
					if (!data.isNull(ServerKeys.KEY_AVATAR)) {
						mUserInfo.setAvatarUri(data.getString(ServerKeys.KEY_AVATAR));
					}
					mUserInfo.setEmail(email);
					mUserInfo.setPwd(pwd);
					mUserInfo.setRegId(registrationID);

					if (UserInfoKeeper.writeUserInfo(SetPasswordActivity.this, mUserInfo)) {
						Intent intent = new Intent(SetPasswordActivity.this, MainActivity.class);
						startActivity(intent);
						finish();
					}
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

		Map<String, String> data = new TreeMap<String, String>();
		data.put(ServerKeys.KEY_EMAIL, email);
		data.put(ServerKeys.KEY_PASSWORD, pwd);
		registrationID = JPushInterface.getRegistrationID(getApplicationContext());
		if (registrationID != null) {
			data.put(ServerKeys.KEY_REG_ID, registrationID);
		}
		request.post(ServerKeys.FULL_URL_LOGIN, data);
		mProgressDialog.show();
	}

}
