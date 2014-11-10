package com.meetisan.meetisan.view.tags;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.ToastHelper;

public class CreateTagsResultActivity extends Activity implements OnClickListener {

	private static final String FORMAT_CREATE_TAG_RESULT_TIPS = "Thanks for creating a \n'%s' Tag!\n\n We will review this submission and notify you in 48 hours. Thanks for your patience!";

	private TextView mEmptyTxt;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_create_tag_result);

		String mTagName = null;
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			mTagName = bundle.getString("TagName");
		}
		if (mTagName == null) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		initView(mTagName);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			CreateTagsResultActivity.this.finish();
			break;
		}
	}

	private void initView(String tagName) {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		mTitleTxt.setText(R.string.create_tag);
		mTitleTxt.setVisibility(View.VISIBLE);

		mEmptyTxt = (TextView) findViewById(R.id.txt_result);
		mEmptyTxt.setText(String.format(FORMAT_CREATE_TAG_RESULT_TIPS, tagName));

		((Button) findViewById(R.id.btn_back)).setOnClickListener(this);
	}
}
