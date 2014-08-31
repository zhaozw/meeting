package com.meetisan.meetisan.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.HttpBitmap;

public class TagInFrameFoto extends LinearLayout {

	private ImageView mFirstView, mSecondView, mThirdView, mForthView, mFifthView;
	private LinearLayout mUpLayout, mDownLayout;
	private int width = 0;
	private HttpBitmap httpBitmap;

	public TagInFrameFoto(Context context) {
		super(context);
	}

	public TagInFrameFoto(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = LayoutInflater.from(context).inflate(R.layout.layout_tag_inframe_foto, this, true);
		httpBitmap = new HttpBitmap(context);
		Log.e(VIEW_LOG_TAG, "-----width: " + width);

		mUpLayout = (LinearLayout) view.findViewById(R.id.layout_up);
		mDownLayout = (LinearLayout) view.findViewById(R.id.layout_down);
		mFirstView = (ImageView) view.findViewById(R.id.iv_moments_1);
		mSecondView = (ImageView) view.findViewById(R.id.iv_moments_2);
		mThirdView = (ImageView) view.findViewById(R.id.iv_moments_3);
		mForthView = (ImageView) view.findViewById(R.id.iv_moments_4);
		mFifthView = (ImageView) view.findViewById(R.id.iv_moments_5);

		setVisibility(View.GONE);
	}

	public void setLayoutSize(int width) {
		this.width = width;
		initViewSize(width);
	}

	public void setOnInFrameClickListener(OnClickListener mListener) {
		if (mListener != null) {
			this.setOnClickListener(mListener);
		}
	}

	private void initViewSize(int width) {
		LayoutParams params = new LayoutParams(width / 2, width / 4);
		mUpLayout.setLayoutParams(params);
		mDownLayout.setLayoutParams(params);
		params = new LayoutParams(width / 2, width / 2);
		mFirstView.setLayoutParams(params);
		params = new LayoutParams(width / 4, width / 4);
		mSecondView.setLayoutParams(params);
		mThirdView.setLayoutParams(params);
		mForthView.setLayoutParams(params);
		mFifthView.setLayoutParams(params);
	}

	public void setInframeForos(List<String> mImageUriList) {
		if (mImageUriList == null || mImageUriList.size() <= 0) {
			setVisibility(View.GONE);
			return;
		}
		setVisibility(View.VISIBLE);

		int size = mImageUriList.size();
		Log.e("--", "--------" + size);
		if (size >= 1) {
			Log.e("--", "---1----");
			httpBitmap.displayBitmap(mFirstView, mImageUriList.get(0));
		}
		if (size >= 2) {
			httpBitmap.displayBitmap(mSecondView, mImageUriList.get(1));
		}
		if (size >= 3) {
			httpBitmap.displayBitmap(mThirdView, mImageUriList.get(2));
		}
		if (size >= 4) {
			httpBitmap.displayBitmap(mForthView, mImageUriList.get(3));
		}
		if (size >= 5) {
			httpBitmap.displayBitmap(mFifthView, mImageUriList.get(4));
		}
	}

}
