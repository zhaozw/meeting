package com.meetisan.meetisan.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsKeeper {
	private static final String TAG = SettingsKeeper.class.getSimpleName();

	private static final String PERFER_NAME = "com_meetisan_settings";
	/** settings of notify when someone joins meeting */
	public static final String KEY_NOTIFY_JOIN = "settings_notify_join";
	/** settings of notify when receive an invitation to a meeting */
	public static final String KEY_NOTIFY_INVITE = "settings_notify_invite";

	/**
	 * write settings to preferences
	 * 
	 * @param context
	 * @param key
	 *            preferences key
	 * @param value
	 *            preferences value
	 * @return true if success, else false
	 */
	public static boolean writePreferSettings(Context context, String key, boolean value) {
		if (context == null || key == null) {
			return false;
		}

		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME, Context.MODE_PRIVATE);
		Editor editor = perferences.edit();
		editor.putBoolean(key, value);

		return editor.commit();
	}

	/**
	 * read settings from preferences
	 * 
	 * @param context
	 * @param key
	 *            preferences key
	 * @param defValue
	 *            default value
	 * @return preferences value or default value
	 */
	public static boolean readPreferSettings(Context context, String key, boolean defValue) {
		if (context == null) {
			return defValue;
		}

		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME, Context.MODE_PRIVATE);

		return perferences.getBoolean(key, defValue);
	}

}
