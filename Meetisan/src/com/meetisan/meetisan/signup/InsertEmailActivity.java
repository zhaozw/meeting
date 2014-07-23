package com.meetisan.meetisan.signup;

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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.utils.FormatUtils;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

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

	private CustomizedProgressDialog mProgressDialog = null;

	private void doSendCodeToEmail(final String email) {
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
				String code = null;
				try {
					JSONObject json = new JSONObject(result);
					code = json.getString(ServerKeys.KEY_DATA);

					UserInfoKeeper.writeUserInfo(InsertEmailActivity.this,
							UserInfoKeeper.KEY_USER_EMAIL, email);

					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("ActivationCode", code);
					intent.setClass(InsertEmailActivity.this, ActivationActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
					InsertEmailActivity.this.finish();
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.server_response_exception, Toast.LENGTH_LONG);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		request.get(ServerKeys.FULL_URL_SEND_CODE + "/" + email, null);
		mProgressDialog.show();
	}

}
