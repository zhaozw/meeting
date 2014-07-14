package com.meetisan.meetisan.view.tags;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.meetisan.meetisan.R;

public class TagsActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tags);
		
		((Button) findViewById(R.id.btn_test_swipe)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TagsActivity.this, SwipeListActivity.class);
				startActivity(intent);
			}
		});
		((Button) findViewById(R.id.btn_test_indexable)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TagsActivity.this, IndexableListActivity.class);
				startActivity(intent);
			}
		});
	}
}
