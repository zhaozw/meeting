package com.meetisan.meetisan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetisan.meetisan.R;

public class LabelWithSwitchButton extends RelativeLayout {
	private SwitchButton mSwitchBtn;
	private TextView mTextView;

	public LabelWithSwitchButton(Context context) {
		super(context);
	}

	public LabelWithSwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = LayoutInflater.from(context).inflate(R.layout.layout_label_with_switch_btn, this, true);
		mTextView = (TextView) view.findViewById(R.id.txt_label);
		mSwitchBtn = (SwitchButton) view.findViewById(R.id.btn_switch);

		TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.LabelWithIcon);

		CharSequence text = type.getText(R.styleable.LabelWithIcon_android_text);
		if (text != null) {
			mTextView.setText(text);
		}
		type.recycle();
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mSwitchBtn.setOnCheckedChangeListener(listener);
	}

	public void setText(String text) {
		mTextView.setText(text);
	}

	public void setChecked(boolean checked) {
		mSwitchBtn.setChecked(checked);
	}

	public boolean isChecked() {
		return mSwitchBtn.isChecked();
	}
}
