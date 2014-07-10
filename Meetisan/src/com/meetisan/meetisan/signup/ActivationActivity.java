package com.meetisan.meetisan.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.ToastHelper;

public class ActivationActivity extends Activity implements OnClickListener {

	private Button mSubmitBtn;
	private EditText mCodeTxt;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_activation_code);

		initView();
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.activation_code);
		mTitleTxt.setVisibility(View.VISIBLE);
		mSubmitBtn = (Button) findViewById(R.id.btn_submit);
		mSubmitBtn.setOnClickListener(this);
		mCodeTxt = (EditText) findViewById(R.id.txt_code);
		TextView mResendTxt = (TextView) findViewById(R.id.txt_resend_code);
		mResendTxt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_submit:
			attemptSubmitCode();
			break;
		case R.id.btn_title_icon_left:
			this.finish();
			break;
		case R.id.txt_resend_code:
			resendActivationCode();
			break;
		default:
			break;
		}
	}

	private void resendActivationCode() {
		// TODO Auto-generated method stub

	}

	private void attemptSubmitCode() {
		String code = mCodeTxt.getText().toString();

		if (TextUtils.isEmpty(code)) {
			ToastHelper.showToast(R.string.enter_activation_code_tips);
			return;
		}

		doCheckActivationCode(code);
	}

	private void doCheckActivationCode(String email) {
		// TODO..
		
		//assume check success
		Intent intent = new Intent(this, SetPasswordActivity.class);
		startActivity(intent);
		this.finish();
	}

}
