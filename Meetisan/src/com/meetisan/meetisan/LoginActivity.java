package com.meetisan.meetisan;

import com.meetisan.meetisan.signup.InsertEmailActivity;
import com.meetisan.meetisan.utils.FormatUtils;
import com.meetisan.meetisan.utils.ToastHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

		if (!FormatUtils.checkEmailAvailable(email)) {
			ToastHelper.showToast(R.string.error_invalid_email);
			return;
		}

		if (TextUtils.isEmpty(pwd)) {
			ToastHelper.showToast(R.string.empty_pwd_tips);
			return;
		}

		doLogin();
	}

	private void doLogin() {
		// assume login result
		boolean loginResult = false;
		if (loginResult) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			this.finish();
		} else {
			ToastHelper.showToast(R.string.error_incorrect_email_or_pwd);
		}
	}

}
