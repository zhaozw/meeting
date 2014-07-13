package com.meetisan.meetisan.view.tags;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.listview.swipe.SwipeAdapter;
import com.meetisan.meetisan.widget.listview.swipe.SwipeListView;

public class SwipeListActivity extends Activity {
	private static final String TAG = SwipeListActivity.class.getSimpleName();

	private List<String> mDataList = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_test_swipe_list);

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
				SwipeListActivity.this.finish();
			}
		});
		mBackBtn.setVisibility(View.VISIBLE);

		SwipeListView mListView = (SwipeListView) findViewById(R.id.listView);
		final SwipeAdapter mAdapter = new SwipeAdapter(this, mDataList);
		mAdapter.setOnRightItemClickListener(new SwipeAdapter.onRightItemClickListener() {
			@Override
			public void onRightItemClick(View v, int position) {
				ToastHelper.showToast("Delete " + position + " Itme");
				mDataList.remove(position);
				mAdapter.notifyDataSetChanged();
			}
		});

		mListView.setAdapter(mAdapter);
	}

	private void initData() {
		for (int i = 0; i < 15; i++) {
			mDataList.add("Test itme " + i);
		}
	}
}
