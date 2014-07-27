package com.meetisan.meetisan.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

public class ShaderAnimLayout extends RelativeLayout implements AnimationListener {

	public static final int ANIM_DURATION = 200;
	private boolean mShow = false;
	private float mWidth = 0.0f;
	private Path mClipPath = new Path();
	private ShaderAnimation mAnimation = new ShaderAnimation();;

	public ShaderAnimLayout(Context context) {
		this(context, null, 0);
	}

	public ShaderAnimLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ShaderAnimLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mAnimation.setDuration(ANIM_DURATION);
		mAnimation.setInterpolator(new LinearInterpolator());
	}

	public void show(boolean show) {
		if (show) {
			if (getVisibility() == View.VISIBLE) {
				return;
			}
			mShow = true;
			clearAnimation();
			mAnimation.setAnimationListener(this);
			startAnimation(mAnimation);
		} else {
			if (getVisibility() == View.GONE) {
				return;
			}
			mShow = false;
			mAnimation.setAnimationListener(this);
			clearAnimation();
			setVisibility(0);
			startAnimation(mAnimation);
		}
	}

	public boolean isShow() {
		return mShow;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		mClipPath.reset();
		mClipPath.addRect(getWidth() * (1.0F - mWidth), 0.0F, getWidth(), getBottom(), Path.Direction.CW);
		canvas.clipPath(mClipPath, Region.Op.INTERSECT);
		super.dispatchDraw(canvas);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		setVisibility(mShow ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	private class ShaderAnimation extends Animation {
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			// TODO Auto-generated method stub
			mWidth = mShow ? interpolatedTime : 1.0f - interpolatedTime;
			invalidate();
			super.applyTransformation(interpolatedTime, t);
		}
	}
}
