package com.meetisan.meetisan.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetisan.meetisan.R;

public class DialogUtils {
	private static final String TAG = DialogUtils.class.getSimpleName();
	
	public static final int RESOURCE_ID_NONE = -1;

	/**
	 * Show customized dialog, parameter determines the dialog UI
	 * 
	 * @param activity
	 *            current Activity, it is necessary
	 * @param contentId
	 *            dialog content text resource id
	 * @param positiveId
	 *            positive button text resource id
	 * @param negativeId
	 *            negative button text resource id
	 * @param mListener
	 *            dialog button click listener
	 */
	public static void showDialog(Activity activity, int contentId, int positiveId, int negativeId,
			final OnDialogClickListener mListener) {
		try {
			String content = contentId > 0 ? activity.getResources().getString(contentId) : null;
			String positive = positiveId > 0 ? activity.getResources().getString(positiveId) : null;
			String negative = negativeId > 0 ? activity.getResources().getString(negativeId) : null;
			showDialog(activity, content, positive, negative, mListener);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show customized dialog, parameter determines the dialog UI
	 * 
	 * @param activity
	 *            current Activity, it is necessary
	 * @param contentTxt
	 *            dialog content text
	 * @param positiveTxt
	 *            positive button text
	 * @param negativeTxt
	 *            negative button text
	 * @param mListener
	 *            dialog button click listener
	 */
	public static void showDialog(Activity activity, String contentTxt, String positiveTxt, String negativeTxt,
			final OnDialogClickListener mListener) {
		if (activity == null || contentTxt == null) {
			Log.e(TAG, "activity or dialog content is null");
			return;
		}

		if (positiveTxt == null && negativeTxt == null) {
			Log.e(TAG, "positive and negative content is null");
			return;
		}

		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_confirm, null);
		final Dialog mDialog = new Dialog(activity, R.style.DialogTheme);

		if (contentTxt != null) {
			TextView contentTextView = (TextView) dialogView.findViewById(R.id.layout_content);
			contentTextView.setText(contentTxt);
		}
		mDialog.setContentView(dialogView);

		if (positiveTxt != null) {
			Button positiveBtn = (Button) dialogView.findViewById(R.id.positive);
			positiveBtn.setText(positiveTxt);
			positiveBtn.setVisibility(View.VISIBLE);
			positiveBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onClick(true);
					}
					mDialog.dismiss();
				}
			});
		}

		if (negativeTxt != null) {
			Button negativeBtn = (Button) dialogView.findViewById(R.id.negative);
			negativeBtn.setText(negativeTxt);
			negativeBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onClick(false);
					}
					mDialog.dismiss();
				}
			});
			negativeBtn.setVisibility(View.VISIBLE);
		}

		if (positiveTxt != null && negativeTxt != null) {
			ImageView line = (ImageView) dialogView.findViewById(R.id.line_btn);
			line.setVisibility(View.VISIBLE);
		}

		mDialog.show();
	}

	/**
	 * Customized Dialog Click Listener
	 * 
	 * @author shz
	 * 
	 */
	public interface OnDialogClickListener {
		/**
		 * On dialog button click
		 * 
		 * @param isPositiveBtn
		 *            if true is positive button clicked, else is negative
		 *            button clicked
		 */
		void onClick(boolean isPositiveBtn);
	}
}
