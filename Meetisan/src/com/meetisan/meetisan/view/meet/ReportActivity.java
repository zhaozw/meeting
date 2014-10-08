package com.meetisan.meetisan.view.meet;

import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.DialogUtils;
import com.meetisan.meetisan.utils.DialogUtils.OnDialogClickListener;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.ClearEditText;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

public class ReportActivity extends Activity implements OnClickListener {

	private long mReportID = -1;
	private boolean isReportUser = true;
	private ClearEditText mEvidenceTxt;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_report);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			mReportID = bundle.getLong("ReportID");
			isReportUser = bundle.getBoolean("IsReportUser");
		}

		if (mReportID < 0) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		initView();
	}

	private void initView() {
		ImageButton imageButton = (ImageButton) findViewById(R.id.btn_title_left);
		imageButton.setOnClickListener(this);
		imageButton.setVisibility(View.VISIBLE);
		Button mRightBtn = (Button) findViewById(R.id.btn_title_right);
		mRightBtn.setText(R.string.send);
		mRightBtn.setVisibility(View.VISIBLE);
		mRightBtn.setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title_text)).setText(R.string.report);

		mEvidenceTxt = (ClearEditText) findViewById(R.id.txt_evidence);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			confirmExitDialog();
			break;
		case R.id.btn_title_right:
			sendReportToServer();
			break;
		default:
			break;
		}
	}

	private CustomizedProgressDialog mProgressDialog = null;

	private void sendReportToServer() {
		String evidence = mEvidenceTxt.getText().toString();
		if (TextUtils.isEmpty(evidence)) {
			ToastHelper.showToast(R.string.report_tips);
			return;
		}

		HttpRequest request = new HttpRequest();

		if (mProgressDialog == null) {
			mProgressDialog = new CustomizedProgressDialog(this, R.string.uploading_image);
		} else {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(R.string.report_success, Toast.LENGTH_LONG);
				ReportActivity.this.finish();
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		Map<String, String> data = new TreeMap<String, String>();
		data.put(ServerKeys.KEY_REPORT_ID, String.valueOf(mReportID));
		data.put(ServerKeys.KEY_REPORT_TYPE, isReportUser ? "2" : "1");
		request.post(ServerKeys.FULL_URL_SEND_REPORT, data);

		mProgressDialog.show();
	}

	private void confirmExitDialog() {
		DialogUtils.showDialog(this, R.string.warning, R.string.give_up_add_tag_tips, R.string.sure, R.string.cancel,
				new OnDialogClickListener() {

					@Override
					public void onClick(boolean isPositiveBtn) {
						if (isPositiveBtn) {
							ReportActivity.this.finish();
						}
					}
				});
	}

	@Override
	public void onBackPressed() {
		if (!TextUtils.isEmpty(mEvidenceTxt.getText())) {
			confirmExitDialog();
		} else {
			super.onBackPressed();
		}
	}
}
