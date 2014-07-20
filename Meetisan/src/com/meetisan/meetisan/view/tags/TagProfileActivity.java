package com.meetisan.meetisan.view.tags;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.Util;

public class TagProfileActivity extends Activity {
	private static final String TAG = TagProfileActivity.class.getSimpleName();

	private ImageView mMomentView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tag_profile);

		initView();

		setMomentView();

	}

	private void initView() {
		mMomentView = (ImageView) findViewById(R.id.iv_moments);
	}

	// for test
	private void setMomentView() {
		List<Bitmap> mBitmaps = new ArrayList<Bitmap>();
		Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.pic_moment_test);
		Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.pic_background);
		for (int i = 0; i < 5; i++) {
			if (i == 1 || i == 4) {
				mBitmaps.add(bitmap1);
			} else {
				mBitmaps.add(bitmap2);
			}
		}
		Bitmap mBitmap = Util.inFrameFoto(mBitmaps, Util.getWindowsSize(this, true) - 20);
		if (mBitmap != null) {
			mMomentView.setImageBitmap(mBitmap);
		} else {
			Log.e(TAG, "moment bitmap is null");
		}
	}

}
