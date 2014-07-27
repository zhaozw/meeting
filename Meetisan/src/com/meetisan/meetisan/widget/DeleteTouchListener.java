package com.meetisan.meetisan.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meetisan.meetisan.R;

@SuppressLint("NewApi")
public class DeleteTouchListener implements OnTouchListener {

	public static final int ANIM_DURATION = 300;

	private int mSlop;
	private int mMinFlingVelocity;
	private int mMaxFlingVelocity;

	private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero

	private float mDownX;
	private boolean mSwiping;
	private VelocityTracker mVelocityTracker;
	private int mDownPosition;
	private View mDownView;
	private boolean mPaused;
	private ListView mListView;

	// Transient properties
	private boolean mShaderShow = false;
	private ShaderAnimLayout mShaderView = null;
	// private Button mDelBtn = null;

	private OnSlipLimitedListener mOnSlipLimitedListener = null;
	private OnDeleteCallback mCallback = null;

	public interface OnSlipLimitedListener {
		void onSlipLimited(AdapterView<?> parent, View view, int position, long id, boolean show);
	}

	/**
	 * The callback interface used by {@link SwipeDismissListViewTouchListener}
	 * to inform its client about a successful dismissal of one or more list
	 * item positions.
	 */
	public interface OnDeleteCallback {
		/**
		 * Called when the user has indicated they she would like to dismiss one
		 * or more list item positions.
		 * 
		 * @param listView
		 *            The originating {@link ListView}.
		 * @param reverseSortedPositions
		 *            An array of positions to dismiss, sorted in descending
		 *            order for convenience.
		 */
		void onDelete(ListView listView, int position);
	}

	public DeleteTouchListener(ListView listview, OnSlipLimitedListener listener, OnDeleteCallback callback) {
		mListView = listview;
		mOnSlipLimitedListener = listener;
		mCallback = callback;
		ViewConfiguration vc = ViewConfiguration.get(mListView.getContext());
		mSlop = vc.getScaledTouchSlop();
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
	}

	public DeleteTouchListener(ListView listview, OnDeleteCallback callback) {
		mListView = listview;
		mOnSlipLimitedListener = null;
		mCallback = callback;
		ViewConfiguration vc = ViewConfiguration.get(mListView.getContext());
		mSlop = vc.getScaledTouchSlop();
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (mViewWidth < 2) {
			mViewWidth = mListView.getWidth();
		}

		switch (event.getActionMasked()) {

		case MotionEvent.ACTION_DOWN: {
			if (mPaused) {
				return false;
			}
			if (mShaderShow)
				return true;
			Rect rect = new Rect();
			int childCount = mListView.getChildCount();
			int[] listViewCoords = new int[2];
			mListView.getLocationOnScreen(listViewCoords);
			int x = (int) event.getRawX() - listViewCoords[0];
			int y = (int) event.getRawY() - listViewCoords[1];
			View child;
			for (int i = 0; i < childCount; i++) {
				child = mListView.getChildAt(i);
				child.getHitRect(rect);
				if (rect.contains(x, y)) {
					mDownView = child;
					break;
				}
			}

			if (mDownView != null) {
				mDownX = event.getRawX();
				mDownPosition = mListView.getPositionForView(mDownView);

				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
				mVelocityTracker.recycle();
			}
			v.onTouchEvent(event);
			return true;
		}
		case MotionEvent.ACTION_UP: {
			if (mShaderShow && mShaderView != null) {
				mShaderShow = false;
				mShaderView.show(false);
				return true;
			}
			if (mVelocityTracker == null) {
				break;
			}
			float deltaX = event.getRawX() - mDownX;
			mVelocityTracker.addMovement(event);
			mVelocityTracker.computeCurrentVelocity(1000);
			float velocityX = Math.abs(mVelocityTracker.getXVelocity());
			float velocityY = Math.abs(mVelocityTracker.getYVelocity());
			boolean dismiss = false;
			if (Math.abs(deltaX) > mViewWidth / 2) {
				dismiss = true;
			} else if (mMinFlingVelocity <= velocityX && velocityX <= mMaxFlingVelocity && velocityY < velocityX) {
				dismiss = true;
			}
			if (dismiss && !mShaderShow) {
				if (mOnSlipLimitedListener != null) {
					mOnSlipLimitedListener.onSlipLimited(mListView, mDownView, mDownPosition, mListView.getAdapter()
							.getItemId(mDownPosition), true);
				}
				mShaderView = (ShaderAnimLayout) mDownView.findViewById(R.id.item_right);
				mShaderView.show(true);
				// mDelBtn = (Button)
				// mShaderView.findViewById(R.id.item_right_view);
				final View downView = mDownView;
				final int downPosition = mDownPosition;
				mShaderView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						Log.d("-----", "On Delete");
						mShaderView.setVisibility(View.GONE);
						performDismiss(downView, downPosition);
					}
				});
				mShaderShow = true;
			}
			mDownView.setAlpha(1.0f);
			// mDownView.setBackground(null);
			mVelocityTracker = null;
			mDownX = 0;
			mDownView = null;
			mDownPosition = ListView.INVALID_POSITION;
			mSwiping = false;
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			if (mShaderShow)
				return true;
			if (mVelocityTracker == null || mPaused) {
				break;
			}
			mVelocityTracker.addMovement(event);
			float deltaX = event.getRawX() - mDownX;
			if (Math.abs(deltaX) > mSlop) {
				mSwiping = true;
				mListView.requestDisallowInterceptTouchEvent(true);

				// Cancel ListView's touch (un-highlighting the item)
				MotionEvent cancelEvent = MotionEvent.obtain(event);
				cancelEvent.setAction(MotionEvent.ACTION_CANCEL
						| (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
				mListView.onTouchEvent(cancelEvent);
				cancelEvent.recycle();
			}

			if (mSwiping) {
				// mDownView.setTranslationX(deltaX);
				// mDownView.setAlpha(Math.max(0f,
				// Math.min(1f, 1f - 2f * Math.abs(deltaX) / mViewWidth)));
				// mDownView.setBackground(new ColorDrawable(Color.argb(
				// (int) (255 * 1f * Math.abs(deltaX)
				// / mViewWidth), 0, 0, 196)));
				return true;
			}
			break;
		}
		}
		return false;
	}

	public AbsListView.OnScrollListener makeScrollListener() {
		return new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int scrollState) {
				setScrollEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
			}

			@Override
			public void onScroll(AbsListView absListView, int i, int i1, int i2) {
			}
		};
	}

	/**
	 * Enables or disables (pauses or resumes) watching for swipe-to-dismiss
	 * gestures.
	 * 
	 * @param enabled
	 *            Whether or not to watch for gestures.
	 */
	public void setScrollEnabled(boolean enabled) {
		mPaused = !enabled;
	}

	public void setOnSlipLimitedListener(OnSlipLimitedListener listener) {
		mOnSlipLimitedListener = listener;
	}

	private void performDismiss(final View dismissView, final int dismissPosition) {

		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
		final int originalHeight = dismissView.getHeight();

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(ANIM_DURATION);

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (mCallback != null) {
					mCallback.onDelete(mListView, dismissPosition);
				}

				dismissView.setAlpha(1f);
				dismissView.setTranslationX(0);
				lp.height = originalHeight;
				dismissView.setLayoutParams(lp);
			}
		});

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});

		animator.start();
	}

	public boolean hideDeleteLayout() {
		if (mShaderView != null) {
			mShaderView.show(false);
			return true;
		}

		return false;
	}
}
