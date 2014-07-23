package com.meetisan.meetisan.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.meetisan.meetisan.model.PeopleInfo;

public class UserInfoKeeper {
	// private static final String TAG = SettingsKeeper.class.getSimpleName();

	private static final String PERFER_NAME = "com_meetisan_userinfo";
	/** user id information */
	public static final String KEY_USER_ID = "user_id";
	/** user name information */
	public static final String KEY_USER_NAME = "user_name";
	/** user email information */
	public static final String KEY_USER_EMAIL = "user_email";
	/** user password information */
	public static final String KEY_USER_PWD = "user_password";
	/** user signature */
	public static final String KEY_USER_SIGNATURE = "user_signature";
	/** user university */
	public static final String KEY_USER_UNIVERSITY = "user_university";
	/** user city */
	public static final String KEY_USER_CITY = "user_city";
	/** user age */
	public static final String KEY_USER_AGE = "user_age";
	/** user gender */
	public static final String KEY_USER_GENDER = "user_gender";
	/** user experience */
	public static final String KEY_USER_EXPERIENCE = "user_experience";
	/** user education */
	public static final String KEY_USER_EDUCATION = "user_education";
	/** user skills */
	public static final String KEY_USER_SKILLS = "user_skills";
	/** user Longitude */
	public static final String KEY_USER_LON = "user_Longitude";
	/** user Latitude */
	public static final String KEY_USER_LAT = "user_Latitude";
	/** user status */
	public static final String KEY_USER_STATUS = "user_status";
	/** user create date */
	public static final String KEY_USER_CREATE_DATE = "user_create_date";
	/** user registration ID */
	public static final String KEY_USER_REG_ID = "user_reg_id";

	/**
	 * write user all base information to SharedPreferences
	 * 
	 * @param context
	 * @param userInfo
	 * @return true if success, else false
	 */
	public static boolean writeUserInfo(Context context, PeopleInfo userInfo) {
		if (context == null || userInfo == null) {
			return false;
		}

		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME,
				Context.MODE_PRIVATE);
		Editor editor = perferences.edit();
		if (userInfo.getId() > 0) {
			editor.putLong(KEY_USER_ID, userInfo.getId());
		}
		if (userInfo.getEmail() != null) {
			editor.putString(KEY_USER_EMAIL, userInfo.getEmail());
		}
		if (userInfo.getName() != null) {
			editor.putString(KEY_USER_NAME, userInfo.getName());
		}
		if (userInfo.getPwd() != null) {
			editor.putString(KEY_USER_PWD, userInfo.getPwd());
		}
		if (userInfo.getSignature() != null) {
			editor.putString(KEY_USER_SIGNATURE, userInfo.getSignature());
		}
		if (userInfo.getUniversity() != null) {
			editor.putString(KEY_USER_UNIVERSITY, userInfo.getUniversity());
		}
		if (userInfo.getCity() != null) {
			editor.putString(KEY_USER_CITY, userInfo.getCity());
		}
		if (userInfo.getAge() > 0) {
			editor.putInt(KEY_USER_AGE, userInfo.getAge());
		}
		if (userInfo.getGender() > 0) {
			editor.putInt(KEY_USER_GENDER, userInfo.getGender());
		}
		if (userInfo.getExperience() != null) {
			editor.putString(KEY_USER_EXPERIENCE, userInfo.getExperience());
		}
		if (userInfo.getEducation() != null) {
			editor.putString(KEY_USER_EDUCATION, userInfo.getEducation());
		}
		if (userInfo.getSkills() != null) {
			editor.putString(KEY_USER_SKILLS, userInfo.getSkills());
		}
		if (userInfo.getLongitude() > 0) { // TODO.. for further confirm
			editor.putFloat(KEY_USER_LON, userInfo.getLongitude());
		}
		if (userInfo.getLatitude() > 0) { // TODO.. for further confirm
			editor.putFloat(KEY_USER_LAT, userInfo.getLatitude());
		}
		if (userInfo.getStatus() > 0) {
			editor.putInt(KEY_USER_STATUS, userInfo.getStatus());
		}
		if (userInfo.getCreateDate() != null) {
			editor.putString(KEY_USER_CREATE_DATE, userInfo.getCreateDate());
		}
		if (userInfo.getRegId() != null) {
			editor.putString(KEY_USER_REG_ID, userInfo.getRegId());
		}

		return editor.commit();
	}

	/**
	 * read all user base information from SharedPreferences
	 * 
	 * @param context
	 * @return preferences value or null
	 */
	public static PeopleInfo readUserInfo(Context context) {
		if (context == null) {
			return null;
		}

		PeopleInfo userInfo = new PeopleInfo();
		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME,
				Context.MODE_PRIVATE);

		userInfo.setId(perferences.getLong(KEY_USER_ID, -1));
		userInfo.setEmail(perferences.getString(KEY_USER_EMAIL, null));
		userInfo.setPwd(perferences.getString(KEY_USER_PWD, null));
		userInfo.setName(perferences.getString(KEY_USER_NAME, null));

		userInfo.setSignature(perferences.getString(KEY_USER_SIGNATURE, null));
		userInfo.setUniversity(perferences.getString(KEY_USER_UNIVERSITY, null));
		userInfo.setCity(perferences.getString(KEY_USER_CITY, null));
		userInfo.setGender(perferences.getInt(KEY_USER_GENDER, 0));

		userInfo.setExperience(perferences.getString(KEY_USER_EXPERIENCE, null));
		userInfo.setEducation(perferences.getString(KEY_USER_EDUCATION, null));
		userInfo.setSkills(perferences.getString(KEY_USER_SKILLS, null));
		userInfo.setLongitude(perferences.getFloat(KEY_USER_LON, -1f));
		userInfo.setLatitude(perferences.getFloat(KEY_USER_LAT, -1f));
		userInfo.setStatus(perferences.getInt(KEY_USER_STATUS, 0));
		userInfo.setCreateDate(perferences.getString(KEY_USER_CREATE_DATE, null));
		userInfo.setRegId(perferences.getString(KEY_USER_REG_ID, null));

		return userInfo;
	}

	/**
	 * write user information to preferences
	 * 
	 * @param context
	 * @param key preferences key
	 * @param value preferences value
	 * @return true if success, else false
	 */
	public static boolean writeUserInfo(Context context, String key, String value) {
		if (context == null || key == null || value == null) {
			return false;
		}

		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME,
				Context.MODE_PRIVATE);
		Editor editor = perferences.edit();
		editor.putString(key, value);

		return editor.commit();
	}

	/**
	 * write user information to preferences
	 * 
	 * @param context
	 * @param key preferences key
	 * @param value preferences value
	 * @return true if success, else false
	 */
	public static boolean writeUserInfo(Context context, String key, int value) {
		if (context == null || key == null) {
			return false;
		}

		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME,
				Context.MODE_PRIVATE);
		Editor editor = perferences.edit();
		editor.putInt(key, value);

		return editor.commit();
	}

	/**
	 * write user information to preferences
	 * 
	 * @param context
	 * @param key preferences key
	 * @param value preferences value
	 * @return true if success, else false
	 */
	public static boolean writeUserInfo(Context context, String key, long value) {
		if (context == null || key == null) {
			return false;
		}

		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME,
				Context.MODE_PRIVATE);
		Editor editor = perferences.edit();
		editor.putLong(key, value);

		return editor.commit();
	}

	/**
	 * write user information to preferences
	 * 
	 * @param context
	 * @param key preferences key
	 * @param value preferences value
	 * @return true if success, else false
	 */
	public static boolean writeUserInfo(Context context, String key, float value) {
		if (context == null || key == null) {
			return false;
		}

		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME,
				Context.MODE_PRIVATE);
		Editor editor = perferences.edit();
		editor.putFloat(key, value);

		return editor.commit();
	}

	/**
	 * read user information from preferences
	 * 
	 * @param context
	 * @param key preferences key
	 * @param defValue default value
	 * @return preferences value or default value
	 */
	public static String readUserInfo(Context context, String key, String defValue) {
		if (context == null) {
			return defValue;
		}

		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME,
				Context.MODE_PRIVATE);

		return perferences.getString(key, defValue);
	}

	/**
	 * read user information from preferences
	 * 
	 * @param context
	 * @param key preferences key
	 * @param defValue default value
	 * @return preferences value or default value
	 */
	public static int readUserInfo(Context context, String key, int defValue) {
		if (context == null) {
			return defValue;
		}

		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME,
				Context.MODE_PRIVATE);

		return perferences.getInt(key, defValue);
	}

	/**
	 * read user information from preferences
	 * 
	 * @param context
	 * @param key preferences key
	 * @param defValue default value
	 * @return preferences value or default value
	 */
	public static long readUserInfo(Context context, String key, long defValue) {
		if (context == null) {
			return defValue;
		}

		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME,
				Context.MODE_PRIVATE);

		return perferences.getLong(key, defValue);
	}

	/**
	 * read user information from preferences
	 * 
	 * @param context
	 * @param key preferences key
	 * @param defValue default value
	 * @return preferences value or default value
	 */
	public static float readUserInfo(Context context, String key, float defValue) {
		if (context == null) {
			return defValue;
		}

		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME,
				Context.MODE_PRIVATE);

		return perferences.getFloat(key, defValue);
	}

	/**
	 * clear all user information
	 * 
	 * @param context
	 * @return true if success, else false
	 */
	public static boolean clearUserInfo(Context context) {
		if (context == null) {
			return false;
		}

		SharedPreferences perferences = context.getSharedPreferences(PERFER_NAME,
				Context.MODE_PRIVATE);
		Editor editor = perferences.edit();
		editor.clear();

		return editor.commit();
	}
}
