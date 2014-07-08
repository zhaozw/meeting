package com.meetisan.meetisan.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class GCMKeeper {
	private static final String TAG = GCMKeeper.class.getSimpleName();

	private static final String PREFERENCE_NAME = "meetisan_gcm_info";
	private static final String KEY_GCM_REG_ID = "gcm_registration_id";

	/**
	 * Write GCM registration ID to SharedPreferences
	 * 
	 * @param context
	 * @param regId GCM registration ID
	 * @return return true if success, else return false
	 */
	public static boolean writeGCMRegistrationId(Context context, String regId) {
		if (context == null || regId == null) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(KEY_GCM_REG_ID, regId);

		return editor.commit();
	}

	/**
	 * Read GCM registration ID from SharedPreferences
	 * 
	 * @param context
	 * @param defValue default value
	 * @return return GCM registration ID if exist, else return default value
	 */
	public static String readGCMRegistrationId(Context context, String defValue) {
		if (context == null) {
			return defValue;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		defValue = preferences.getString(KEY_GCM_REG_ID, defValue);
		Log.d(TAG, "Read GCM registration id: " + defValue);

		return defValue;
	}

	/**
	 * Clear GCMRegistration ID
	 * 
	 * @param context
	 * @return clear result
	 */
	public static boolean clearGCMRegistrationId(Context context) {
		if (context == null) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear();

		return editor.commit();
	}
}
