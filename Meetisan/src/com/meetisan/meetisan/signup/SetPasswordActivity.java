package com.meetisan.meetisan.signup;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.ToastHelper;

public class SetPasswordActivity extends Activity implements OnClickListener {

	private Button mSetPwdBtn;
	private EditText mPwdTxt, mConfirmPwdTxt;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_set_password);

		initView();
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

		doSetPassword(pwd);
	}

	private void doSetPassword(String pwd) {
		// TODO..
	}

}
