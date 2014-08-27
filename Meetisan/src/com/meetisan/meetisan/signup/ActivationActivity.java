package com.meetisan.meetisan.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

public class ActivationActivity extends Activity implements OnClickListener {

	private Button mSubmitBtn;
	private EditText mCodeTxt;
	private String mActivationCode = null;
	private boolean isRegistion = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_activation_code);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			mActivationCode = bundle.getString("ActivationCode");
			isRegistion = bundle.getBoolean("isRegistion");
		}

		if (mActivationCode == null) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}
		Log.d("ActivationActivity", "Activation Code: " + mActivationCode);

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

	private void jumpToUpdatePassword(String mEnterCode) {
		Intent intent = new Intent(ActivationActivity.this, SetPasswordActivity.class);
		intent.putExtra("ActivationCode", mActivationCode);
		intent.putExtra("InputCode", mEnterCode);
		startActivity(intent);
		ActivationActivity.this.finish();
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

		if (isRegistion) {
			doCheckActivationCode(code);
		} else {
			jumpToUpdatePassword(code);
		}
	}

	private CustomizedProgressDialog mProgressDialog = null;

	private void doCheckActivationCode(String code) {
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
				Intent intent = new Intent(ActivationActivity.this, SetPasswordActivity.class);
				startActivity(intent);
				ActivationActivity.this.finish();
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		request.get(ServerKeys.FULL_URL_CHECK_CODE + "/" + mActivationCode + "/?code=" + code, null);
		mProgressDialog.show();
	}

}
