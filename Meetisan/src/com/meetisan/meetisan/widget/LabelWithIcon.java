package com.meetisan.meetisan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetisan.meetisan.R;

public class LabelWithIcon extends RelativeLayout {
	private ImageButton mImageView;
	private TextView mTextView;

	public LabelWithIcon(Context context) {
		super(context);
	}

	public LabelWithIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = LayoutInflater.from(context).inflate(R.layout.layout_label_with_icon, this, true);
		mTextView = (TextView) view.findViewById(R.id.txt_label);
		mImageView = (ImageButton) view.findViewById(R.id.iv_icon);

		TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.LabelWithIcon);

		CharSequence text = type.getText(R.styleable.LabelWithIcon_android_text);
		if (text != null) {
			mTextView.setText(text);
		}
		int resId = type.getResourceId(R.styleable.LabelWithIcon_android_src, -1);
		if (resId > 0) {
			mImageView.setImageResource(resId);
		}
		type.recycle();
	}

	public void setImageResource(int resId) {
		mImageView.setImageResource(resId);
	}

	public void setText(String text) {
		mTextView.setText(text);
	}

}
