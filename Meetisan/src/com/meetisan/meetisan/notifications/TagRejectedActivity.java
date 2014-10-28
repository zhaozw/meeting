package com.meetisan.meetisan.notifications;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meetisan.meetisan.R;

public class TagRejectedActivity extends Activity {
	private static final String TAG = TagRejectedActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_notification_create_tag_rejected);
		initTitle();
	}

	private void initTitle() {
		TextView textView = (TextView) findViewById(R.id.tv_title_text);
		textView.setText(R.string.tag_rejected);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		mBackBtn.setVisibility(View.VISIBLE);
	}
}
