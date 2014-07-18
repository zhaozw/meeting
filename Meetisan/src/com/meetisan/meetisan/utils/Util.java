package com.meetisan.meetisan.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class Util {
	private static final String SHARE_PREFERENCES_NAME = "tle.preferences";
	public static final long SECONDS_OF_ONE_DAY = 24 * 3600;

	/**
	 * @param level
	 *            level can be Log.INFO, Log.DEBUG, Log.VERBOSE, Log.WARN or
	 *            Log.ERROR
	 * @param tag
	 *            log tag
	 * @param msg
	 *            log message
	 */
	private static void log(int level, String tag, String msg) {
		int key = level;
		switch (key) {
		case Log.VERBOSE:
			Log.v(tag, msg);
			break;
		case Log.DEBUG:
			Log.d(tag, msg);
			break;
		case Log.INFO:
			Log.i(tag, msg);
			break;
		case Log.WARN:
			Log.w(tag, msg);
			break;
		case Log.ERROR:
			Log.e(tag, msg);
			break;
		default:
			break;
		}
	}

	public static void logV(String tag, String msg) {
		log(Log.VERBOSE, tag, msg);
	}

	public static void logD(String tag, String msg) {
		log(Log.DEBUG, tag, msg);
	}

	public static void logI(String tag, String msg) {
		log(Log.INFO, tag, msg);
	}

	public static void logW(String tag, String msg) {
		log(Log.WARN, tag, msg);
	}

	public static void logE(String tag, String msg) {
		log(Log.ERROR, tag, msg);
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static boolean readPreferences(Context context, String key, boolean defValue) {
		if (context == null || key == null) {
			return defValue;
		}
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		return preferences.getBoolean(key, defValue);
	}

	public static void writePreferences(Context context, String key, boolean value) {
		if (context == null || key == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * bitmap转为base64
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * base64转为bitmap
	 * 
	 * @param base64Data
	 * @return
	 */
	public static Bitmap base64ToBitmap(String base64Data) {
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
}
