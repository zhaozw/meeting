package com.meetisan.meetisan.view.tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.widget.listview.indexable.IndexableAdapter;
import com.meetisan.meetisan.widget.listview.indexable.IndexableListView;

public class IndexableListActivity extends Activity {
	private static final String TAG = IndexableListActivity.class.getSimpleName();

	private List<String> mDataList = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_test_indexable_list);

		initData();
		initView();
	}

	private void initView() {
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(TAG);
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_icon_left);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IndexableListActivity.this.finish();
			}
		});
		mBackBtn.setVisibility(View.VISIBLE);

		IndexableAdapter mAdapter = new IndexableAdapter(this, R.layout.item_indexable_listview,
				mDataList);
		IndexableListView mListView = (IndexableListView) findViewById(R.id.listView);
		mListView.setAdapter(mAdapter);
		mListView.setFastScrollEnabled(true);
	}

	private void initData() {
		for (int i = 0; i < 15; i++) {
			String testStr = "";
			int index = i % 5;
			if (index == 0) {
				testStr = "Aheic";
			} else if (index == 1) {
				testStr = "Bicss";
			} else if (index == 2) {
				testStr = "Ricss";
			} else if (index == 3) {
				testStr = "Tscss";
			} else {
				testStr = "Wscss";
			}
			mDataList.add(testStr + i);
		}
		Collections.sort(mDataList);
	}

}
