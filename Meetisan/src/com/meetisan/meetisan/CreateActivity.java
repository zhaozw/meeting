package com.meetisan.meetisan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CreateActivity extends Activity {
	// private static final String TAG = CreateActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);

		((Button) findViewById(R.id.btn_test_map)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CreateActivity.this, GoogleMapActivity.class);
				startActivity(intent);
			}
		});
	}

}
