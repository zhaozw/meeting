package com.meetisan.meetisan.signup;

import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.LoginActivity;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.ClearEditText;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

public class SetNameActivity extends Activity implements OnClickListener {

	private ClearEditText mNameTxt;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_set_name);

		initView();
	}

	private void initView() {
		Button mRightBtn = (Button) findViewById(R.id.btn_title_right);
		mRightBtn.setText(R.string.save);
		mRightBtn.setVisibility(View.VISIBLE);
		mRightBtn.setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title_text)).setText(R.string.your_name);

		mNameTxt = (ClearEditText) findViewById(R.id.txt_name);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_right:
			syncNameToServer();
			break;
		default:
			break;
		}
	}

	private void syncNameToServer() {
		final CustomizedProgressDialog mProgressDialog = new CustomizedProgressDialog(this, R.string.updating);
		final String name = mNameTxt.getText().toString();
		if (TextUtils.isEmpty(name)) {
			ToastHelper.showToast(R.string.empty_name_tips);
			return;
		}

		HttpRequest request = new HttpRequest();

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				UserInfoKeeper.writeUserInfo(SetNameActivity.this, UserInfoKeeper.KEY_USER_NAME, name);
				Intent intent = new Intent(SetNameActivity.this, SetTaglineActivity.class);
				startActivity(intent);
				SetNameActivity.this.finish();
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		Map<String, String> data = new TreeMap<String, String>();
		data.put(ServerKeys.KEY_ID, String.valueOf(UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L)));
		data.put(ServerKeys.KEY_PASSWORD, UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_PWD, ""));
		data.put(ServerKeys.KEY_NAME, name);

		request.post(ServerKeys.FULL_URL_UPDATE_USER_INFO, data);

		mProgressDialog.show();
	}
}
