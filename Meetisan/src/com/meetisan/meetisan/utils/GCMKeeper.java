package com.meetisan.meetisan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GCMKeeper {
	private static final String PREFERENCES_NAME = "meetisan_prefer_gcm";

	private static final String KEY_GCM_REG_ID = "gcm_reg_id";

	/**
	 * write GCM regId to SharedPreferences
	 * 
	 * @param context
	 * @param regId GCM regId
	 * @return true if success, else false
	 */
	public static boolean writeGCMRegId(Context context, String regId) {
		if (null == context || regId == null) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putString(KEY_GCM_REG_ID, regId);

		return edit.commit();
	}

	/**
	 * read GCM regId from SharedPreferences
	 * 
	 * @param context
	 * @param defValue default value
	 * @return GCM regId or default value
	 */
	public static String readGCMRegId(Context context, String defValue) {
		if (context == null) {
			return defValue;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);

		return preferences.getString(KEY_GCM_REG_ID, defValue);
	}
}
