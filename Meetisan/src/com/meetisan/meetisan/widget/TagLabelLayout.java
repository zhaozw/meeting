package com.meetisan.meetisan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetisan.meetisan.R;

public class TagLabelLayout extends RelativeLayout {

	private TextView mNumText, mTitleText;

	public TagLabelLayout(Context context) {
		super(context);
	}

	public TagLabelLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = LayoutInflater.from(context).inflate(R.layout.layout_label_tag, this, true);
		mNumText = (TextView) view.findViewById(R.id.txt_tag_num);
		mTitleText = (TextView) view.findViewById(R.id.txt_tag_title);
		setVisibility(View.GONE);
	}

	public void setNumText(long num) {
		mNumText.setText("+" + num);
	}

	public void setTitleText(String title) {
		if (title != null) {
			mTitleText.setText(title);
			setVisibility(View.VISIBLE);
		} else {
			setVisibility(View.GONE);
		}
	}

	public void setTagText(long num, String title) {
		setNumText(num);
		setTitleText(title);
	}

}
