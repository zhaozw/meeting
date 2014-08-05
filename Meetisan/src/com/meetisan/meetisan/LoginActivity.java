package com.meetisan.meetisan;

import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.signup.InsertEmailActivity;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

public class LoginActivity extends Activity implements OnClickListener {
	// private static final String TAG = LoginActivity.class.getSimpleName();

	private EditText mEmailTxt, mPwdTxt;
	private TextView mForgotPwdTxt, mSignUpTxt;
	private Button mLoginBtn;

	private String email, pwd;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		initView();
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.login);
		mTitleTxt.setVisibility(View.VISIBLE);

		mEmailTxt = (EditText) findViewById(R.id.email);
		mPwdTxt = (EditText) findViewById(R.id.password);
		mPwdTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});
		mForgotPwdTxt = (TextView) findViewById(R.id.txt_forget_pwd);
		mForgotPwdTxt.setOnClickListener(this);
		mSignUpTxt = (TextView) findViewById(R.id.txt_sign_up);
		mSignUpTxt.setOnClickListener(this);
		mLoginBtn = (Button) findViewById(R.id.btn_login);
		mLoginBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_forget_pwd:
			Intent intent1 = new Intent(this, InsertEmailActivity.class);
			startActivity(intent1);
			break;
		case R.id.txt_sign_up:
			Intent intent2 = new Intent(this, InsertEmailActivity.class);
			startActivity(intent2);
			break;
		case R.id.btn_login:
			attemptLogin();
			break;
		default:
			break;
		}
	}

	private void attemptLogin() {
		email = mEmailTxt.getText().toString();
		pwd = mPwdTxt.getText().toString();

		if (TextUtils.isEmpty(email)) {
			ToastHelper.showToast(R.string.empty_email_tips);
			return;
		}

		// if (!FormatUtils.checkEmailAvailable(email)) {
		// ToastHelper.showToast(R.string.error_invalid_email);
		// return;
		// }

		if (TextUtils.isEmpty(pwd)) {
			ToastHelper.showToast(R.string.empty_pwd_tips);
			return;
		}

		doLogin(email, pwd);
	}

	CustomizedProgressDialog mProgressDialog = null;

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
				UserInfoKeeper.writeUserInfo(LoginActivity.this, UserInfoKeeper.KEY_USER_EMAIL, email);
				UserInfoKeeper.writeUserInfo(LoginActivity.this, UserInfoKeeper.KEY_USER_PWD, pwd);

				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
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
		String registrationID = JPushInterface.getRegistrationID(getApplicationContext());
		if (registrationID != null) {
			data.put(ServerKeys.KEY_REG_ID, registrationID);
		}
		request.post(ServerKeys.FULL_URL_LOGIN, data);
		mProgressDialog.show();
		// boolean loginResult = true;
		// if (loginResult) {
		// Intent intent = new Intent(this, MainActivity.class);
		// startActivity(intent);
		// this.finish();
		// } else {
		// ToastHelper.showToast(R.string.error_incorrect_email_or_pwd);
		// }
	}

}
