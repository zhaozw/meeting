package com.meetisan.meetisan.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.meetisan.meetisan.MyApplication;

public class InputMethodUtils {

	private static InputMethodManager inputMethodManager;

	private static InputMethodManager getInputMethodManager() {
		if (inputMethodManager == null) {
			inputMethodManager = (InputMethodManager) MyApplication.getAppContext().getSystemService(
					Context.INPUT_METHOD_SERVICE);
		}

		return inputMethodManager;
	}

	/**
	 * hide Input Method if is shown
	 * 
	 * @param activity
	 *            current Activity
	 */
	public static boolean hideInputMethod(Activity activity) {
		return getInputMethodManager().hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	// TODO..
	private static void showInputMethod(Activity activity) {

	}

}
