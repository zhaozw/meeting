package com.meetisan.meetisan.signup;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.utils.DialogUtils;
import com.meetisan.meetisan.utils.DialogUtils.OnDialogClickListener;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

public class InsertEmailActivity extends Activity implements OnClickListener {

	private ImageButton mBackBtn;
	private Button mSendBtn;
	private EditText mEmailTxt;
	private TextView mTipsTxt, mPrivacyTxt;

	private boolean isRegistion = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_inset_email);

		isRegistion = getIntent().getBooleanExtra("isRegistion", false);

		initView();
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.insert_email);
		mTitleTxt.setVisibility(View.VISIBLE);
		mTipsTxt = (TextView) findViewById(R.id.txt_tips);
		if (isRegistion) {
			mTipsTxt.setText(R.string.insert_email_registe_tips);
		} else {
			mTipsTxt.setText(R.string.insert_email_reset_tips);
		}
		mBackBtn = (ImageButton) findViewById(R.id.btn_title_icon_left);
		mBackBtn.setOnClickListener(this);
		mBackBtn.setVisibility(View.VISIBLE);
		mSendBtn = (Button) findViewById(R.id.btn_send_code);
		mSendBtn.setOnClickListener(this);
		mEmailTxt = (EditText) findViewById(R.id.email);

		mPrivacyTxt = (TextView) findViewById(R.id.txt_privacy_link);

		SpannableString sp = new SpannableString(
				"By signing up, you agree to UNSW Social\'s Terms and that you have read our Privacy Policy");
		sp.setSpan(new URLSpan("http://www.meetisan.com/legal/terms"), 41, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new URLSpan("http://www.meetisan.com/legal/privacy"), 75, 89, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		mPrivacyTxt.setText(sp);
		mPrivacyTxt.setAutoLinkMask(Linkify.WEB_URLS);
		mPrivacyTxt.setMovementMethod(LinkMovementMethod.getInstance());
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

	private class URLSpan extends ClickableSpan {
		private String url;

		public URLSpan(String url) {
			this.url = url;
		}

		@Override
		public void onClick(View widget) {
			try {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void attemptSendCode() {
		String email = mEmailTxt.getText().toString();

		// if (TextUtils.isEmpty(email)) {
		// ToastHelper.showToast(R.string.empty_email_tips);
		// return;
		// }
		// if (!FormatUtils.checkEmailAvailable(email)) {
		if (!email.endsWith("unsw.edu.au")) {
			DialogUtils
					.showDialog(
							this,
							"Sorry!",
							"UNSW Social is only available to UNSW students. Register for other networking apps by Meetisan at www.meetisan.com.",
							"OK", "Cancel", new OnDialogClickListener() {

								@Override
								public void onClick(boolean isPositiveBtn) {
									if (isPositiveBtn) {
										try {
											Uri url = Uri.parse("http://www.meetisan.com");
											Intent intent = new Intent(Intent.ACTION_VIEW, url);
											startActivity(intent);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							});
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

					UserInfoKeeper.writeUserInfo(InsertEmailActivity.this, UserInfoKeeper.KEY_USER_EMAIL, email);

					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("ActivationCode", code);
					bundle.putBoolean("isRegistion", isRegistion);
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

		if (isRegistion) {
			request.get(ServerKeys.FULL_URL_SEND_CODE + "/" + email, null);
		} else {
			request.get(ServerKeys.FULL_URL_SEND_CODE + "/" + email + "?isRegister=false", null);
		}
		mProgressDialog.show();
	}

}
