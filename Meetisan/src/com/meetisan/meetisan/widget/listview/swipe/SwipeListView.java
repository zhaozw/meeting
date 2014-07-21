package com.meetisan.meetisan.widget.listview.swipe;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.meetisan.meetisan.R;

public class SwipeListView extends ListView {
	private static final String TAG = SwipeListView.class.getSimpleName();

	private int mClickTimeout;

	private boolean isShown = false;
	private View mPreItemView;
	private View mCurItemView;
	private int mCurPosition;
	private float mFirstX;
	private float mFirstY;
	private boolean mIsHorizontalScroll;

	private OnItemDeleteListener mDeleteListener;

	public SwipeListView(Context context) {
		super(context);
		mClickTimeout = ViewConfiguration.getPressedStateDuration()
				+ ViewConfiguration.getTapTimeout();
	}

	public SwipeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mClickTimeout = ViewConfiguration.getPressedStateDuration()
				+ ViewConfiguration.getTapTimeout();
	}

	public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mClickTimeout = ViewConfiguration.getPressedStateDuration()
				+ ViewConfiguration.getTapTimeout();
	}

	public void setOnItemDeleteListener(OnItemDeleteListener mListener) {
		this.mDeleteListener = mListener;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		float lastX = ev.getX();
		float lastY = ev.getY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:

			mIsHorizontalScroll = false;

			mFirstX = lastX;
			mFirstY = lastY;
			int motionPosition = pointToPosition((int) mFirstX, (int) mFirstY);

			Log.e(TAG, "onInterceptTouchEvent----->ACTION_DOWN position=" + motionPosition);

			if (motionPosition >= 0) {
				View currentItemView = getChildAt(motionPosition - getFirstVisiblePosition());
				mPreItemView = mCurItemView;
				mCurItemView = currentItemView;
			}
			mCurPosition = motionPosition;
			break;

		case MotionEvent.ACTION_MOVE:
			float dx = lastX - mFirstX;
			float dy = lastY - mFirstY;

			if (Math.abs(dx) >= 5 && Math.abs(dy) >= 5) {
				return true;
			}
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:

			Log.i(TAG, "onInterceptTouchEvent----->ACTION_UP");
			if (isShown && mPreItemView != mCurItemView) {
				Log.i(TAG, "1---> hiddenRight");
				/**
				 * 情况一：
				 * <p>
				 * 一个Item的右边布局已经显示，
				 * <p>
				 * 这时候点击任意一个item, 那么那个右边布局显示的item隐藏其右边布局
				 */
				hiddenRight(mPreItemView);
			}
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		float lastX = ev.getX();
		float lastY = ev.getY();

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "---->ACTION_DOWN");
			break;

		case MotionEvent.ACTION_MOVE:
			float dx = lastX - mFirstX;
			float dy = lastY - mFirstY;

			mIsHorizontalScroll = isHorizontalDirectionScroll(dx, dy);

			if (!mIsHorizontalScroll) {
				break;
			}
			Log.i(TAG, "onTouchEvent ACTION_MOVE");
			// if (mIsHorizontalScroll) {
			// if (isShown && mPreItemView != mCurItemView) {
			// Log.i(TAG, "2---> hiddenRight");
			// /**
			// * 情况二：
			// * <p>
			// * 一个Item的右边布局已经显示，
			// * <p>
			// * 这时候左右滑动另外一个item,那个右边布局显示的item隐藏其右边布局
			// * <p>
			// * 向左滑动只触发该情况，向右滑动还会触发情况五
			// */
			// hiddenRight(mPreItemView);
			// }
			// } else {
			// if (isShown) {
			// Log.i(TAG, "3---> hiddenRight");
			// /**
			// * 情况三：
			// * <p>
			// * 一个Item的右边布局已经显示，
			// * <p>
			// * 这时候上下滚动ListView,那么那个右边布局显示的item隐藏其右边布局
			// */
			// hiddenRight(mPreItemView);
			// }
			// }
			break;
		case MotionEvent.ACTION_UP:

		case MotionEvent.ACTION_CANCEL:
			Log.i(TAG, "============ACTION_UP");
			if (isShown) {
				Log.i(TAG, "4---> hiddenRight");
				/**
				 * 情况四：
				 * <p>
				 * 一个Item的右边布局已经显示，
				 * <p>
				 * 这时候左右滑动当前一个item,那个右边布局显示的item隐藏其右边布局
				 */
				hiddenRight(mPreItemView);
				return true;
			}

			if (mIsHorizontalScroll) {
				if (mFirstX - lastX > 30) {
					showRight(mCurItemView, mCurPosition);
				} else {
					long time = ev.getEventTime() - ev.getDownTime();
					if (time < mClickTimeout) {
						Log.d(TAG, "-----On Item Click-----");
						performItemClick(mCurItemView, mCurPosition, mCurItemView.getId());
					} else {
						Log.i(TAG, "5---> hiddenRight");
						/**
						 * 情况五：
						 * <p>
						 * 向右滑动一个item,且滑动的距离超过了右边View的宽度的一半，隐藏之。
						 */
						hiddenRight(mCurItemView);
					}
				}
				return true;
			}
			break;
		}

		return super.onTouchEvent(ev);
	}

	private void showRight(final View rightView, final int curPosition) {
		RelativeLayout rightLayout = (RelativeLayout) rightView.findViewById(R.id.item_right);
		rightLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mDeleteListener != null) {
					mDeleteListener.onSwipeDelete(rightView, curPosition);
				}
				hiddenRight(rightView);
			}
		});
		rightLayout.setVisibility(View.VISIBLE);

		isShown = true;
	}

	private void hiddenRight(View rightView) {

		RelativeLayout rightLayout = (RelativeLayout) rightView.findViewById(R.id.item_right);
		rightLayout.setVisibility(View.GONE);

		isShown = false;
	}

	/**
	 * @param dx
	 * @param dy
	 * @return judge if can judge scroll direction
	 */
	private boolean isHorizontalDirectionScroll(float dx, float dy) {
		boolean mIsHorizontal = true;

		if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
			mIsHorizontal = true;
		} else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
			mIsHorizontal = false;
		}

		return mIsHorizontal;
	}

	public interface OnItemDeleteListener {
		void onSwipeDelete(View view, int position);
	}
}
