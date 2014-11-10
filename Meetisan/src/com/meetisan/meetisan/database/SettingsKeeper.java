package com.meetisan.meetisan.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsKeeper {
	// private static final String TAG = SettingsKeeper.class.getSimpleName();

	private static final String PERFER_NAME = "com_meetisan_settings";
	/** settings of notify when someone joins meeting */
	public static final String KEY_NOTIFY_JOIN = "settings_notify_join";
	/** settings of notify when receive an invitation to a meeting */
	public static final String KEY_NOTIFY_INVITE = "settings_notify_invite";
	/** settings of privacy, allow others to see my tags */
	public static final String KEY_PRIVACY_MY_TAGS = "settings_privacy_my_tags";
	/** settings of privacy, allow others to see meetings I have create */
	public static final String KEY_PRIVACY_MEETINGS_CREATED = "settings_privacy_meetings_create";
	/** settings of privacy, allow others to see meetings I have attend */
	public static final String KEY_PRIVACY_MEETINGS_ATTENDED = "settings_privacy_meetings_attend";
	/** settings of push notification, when someone joins my meetings */
	public static final String KEY_PUSH_MEETINGS_JOINS = "settings_push_meetings_joins";
	/** settings of push notification, when I receive an invitation to a meeting */
	public static final String KEY_PUSH_MEETINGS_INVITATION = "settings_push_meetings_invitation";
	
	public static final String KEY_IS_FIRST_USE_APP = "is_first_use_app";

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
