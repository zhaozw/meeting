package com.meetisan.meetisan.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.FormatUtils;
import com.meetisan.meetisan.utils.ToastHelper;

public class InsertEmailActivity extends Activity implements OnClickListener {

	private ImageButton mBackBtn;
	private Button mSendBtn;
	private EditText mEmailTxt;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_inset_email);

		initView();
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.insert_email);
		mTitleTxt.setVisibility(View.VISIBLE);
		mBackBtn = (ImageButton) findViewById(R.id.btn_title_icon_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);
		mSendBtn = (Button) findViewById(R.id.btn_send_code);
		mSendBtn.setOnClickListener(this);
		mEmailTxt = (EditText) findViewById(R.id.email);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send_code:
			attemptSendCode();
			break;
		case R.id.btn_title_icon_left:
			this.finish();
			break;
		default:
			break;
		}
	}

	private void attemptSendCode() {
		String email = mEmailTxt.getText().toString();

		if (TextUtils.isEmpty(email)) {
			ToastHelper.showToast(R.string.empty_email_tips);
			return;
		}
		if (!FormatUtils.checkEmailAvailable(email)) {
			ToastHelper.showToast(R.string.error_invalid_email);
			return;
		}

		doSendCodeToEmail(email);
	}

	private void doSendCodeToEmail(String email) {
		// TODO..
		// assume send success
		Intent intent = new Intent(this, ActivationActivity.class);
		startActivity(intent);
		this.finish();
	}

}
