package com.meetisan.meetisan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetisan.meetisan.R;

public class LabelWithIcon extends RelativeLayout {

	private static int DEFAULT_TEXT_COLOR = android.R.color.darker_gray;
	private static int DEFAULT_TEXT_SIZE = R.dimen.text_size_middle;

	private ImageButton mImageView;
	private TextView mTextView;

	public LabelWithIcon(Context context) {
		super(context);
	}

	public LabelWithIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = LayoutInflater.from(context).inflate(R.layout.layout_label_with_icon, this,
				true);
		mTextView = (TextView) view.findViewById(R.id.txt_label);
		mImageView = (ImageButton) view.findViewById(R.id.iv_icon);

		TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.LabelWithIcon);
		int textColor = type.getColor(R.styleable.LabelWithIcon_android_textColor, getResources()
				.getColor(DEFAULT_TEXT_COLOR));
		int textSize = type.getDimensionPixelSize(R.styleable.LabelWithIcon_android_textSize,
				getResources().getDimensionPixelSize(DEFAULT_TEXT_SIZE));
		CharSequence text = type.getText(R.styleable.LabelWithIcon_android_text);

		mTextView.setTextColor(textColor);
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
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
