package com.meetisan.meetisan.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

public class Util {
	private static final String SHARE_PREFERENCES_NAME = "tle.preferences";
	public static final long SECONDS_OF_ONE_DAY = 24 * 3600;
	public static final String[] MONTH_ABBREVIATION = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
			"Oct", "Nov", "Dec" };
	public static final String[] WEEK_ABBREVIATION = { "Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat" };

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
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(key, defValue);
	}

	public static void writePreferences(Context context, String key, boolean value) {
		if (context == null || key == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
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
		Bitmap bitmap = null;
		try {
			byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * InFrame Foto, for tag moments view
	 * 
	 * @param mFoto
	 *            moment pictures
	 * @param width
	 *            widget width
	 * @return bitmap
	 */
	public static Bitmap inFrameFoto(List<Bitmap> mFotos, int width) {
		int size = width / 2;
		if (size / 2 <= 0 || mFotos == null || mFotos.size() <= 0) {
			Log.e("--Util--", "size = " + size);
			return null;
		}
		Log.d("--Util--", "size = " + size);

		Bitmap mFotoBitmap = Bitmap.createBitmap(size * 2, size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mFotoBitmap);

		Bitmap mBitmap1 = null, mBitmap2 = null, mBitmap3 = null, mBitmap4 = null, mBitmap5 = null;
		int count = mFotos.size();
		Log.d("--Util--", "count = " + count);
		if (count >= 1) {
			mBitmap1 = cropSquareBitmap(mFotos.get(0), size);
			if (mBitmap1 != null) {
				canvas.drawBitmap(mBitmap1, 0, 0, null);
				mBitmap1.recycle();
			}
		}
		if (count >= 2) {
			mBitmap2 = cropSquareBitmap(mFotos.get(1), size / 2);
			if (mBitmap2 != null) {
				canvas.drawBitmap(mBitmap2, size, 0, null);
				mBitmap2.recycle();
			}
		}
		if (count >= 3) {
			mBitmap3 = cropSquareBitmap(mFotos.get(2), size / 2);
			if (mBitmap3 != null) {
				canvas.drawBitmap(mBitmap3, size + size / 2, 0, null);
				mBitmap3.recycle();
			}
		}
		if (count >= 4) {
			mBitmap4 = cropSquareBitmap(mFotos.get(3), size / 2);
			if (mBitmap4 != null) {
				canvas.drawBitmap(mBitmap4, size, size / 2, null);
				mBitmap4.recycle();
			}
		}
		if (count >= 5) {
			mBitmap5 = cropSquareBitmap(mFotos.get(4), size / 2);
			if (mBitmap5 != null) {
				canvas.drawBitmap(mBitmap5, size + size / 2, size / 2, null);
				mBitmap5.recycle();
			}
		}

		return mFotoBitmap;
	}

	/**
	 * cut pictures into square
	 * 
	 * @param bitmap
	 *            source picture
	 * @param edgeLen
	 *            square picture size
	 * @return square picture
	 */
	public static Bitmap cropSquareBitmap(Bitmap bitmap, int edgeLen) {
		if (null == bitmap || edgeLen <= 0) {
			return null;
		}

		Bitmap squareBitmap = bitmap;
		int widthOrg = bitmap.getWidth();
		int heightOrg = bitmap.getHeight();

		if (widthOrg > edgeLen && heightOrg > edgeLen) {
			int longerEdge = (int) (edgeLen * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
			int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLen;
			int scaledHeight = widthOrg > heightOrg ? edgeLen : longerEdge;
			Bitmap scaledBitmap;

			try {
				scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
			} catch (Exception e) {
				return null;
			}

			int xTopLeft = (scaledWidth - edgeLen) / 2;
			int yTopLeft = (scaledHeight - edgeLen) / 2;

			try {
				squareBitmap = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLen, edgeLen);
				scaledBitmap.recycle();
			} catch (Exception e) {
				return null;
			}
		}

		return squareBitmap;
	}

	/**
	 * get windows size (width or height)
	 * 
	 * @param activity
	 * @param isWidth
	 *            is needs windows width size
	 * @return windows width or height
	 */
	public static int getWindowsSize(Activity activity, boolean isWidth) {
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (isWidth)
			return dm.widthPixels;
		else
			return dm.heightPixels;
	}

	/**
	 * get current Format Date
	 * 
	 * @param format
	 *            Date Format, default is [MM-dd HH:mm:ss]
	 * @return current Format Date
	 */
	public static String getCurFormatDate(String format) {
		if (format == null) {
			format = "MM-dd HH:mm:ss";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(new Date());
	}

	/**
	 * get current Format Date
	 * 
	 * @return current Date [MM-dd HH:mm:ss]
	 */
	public static String getCurFormatDate() {
		return getCurFormatDate("MM-dd HH:mm:ss");
	}

	/**
	 * convert Date[2014-07-18T10:03:41.753] to [2014-07-18 10:03]
	 * 
	 * @param date
	 * @return
	 */
	public static String convertDateTime(String date) {
		if (date == null || !date.contains("T")) {
			return null;
		}
		int gapIndex = date.lastIndexOf("T");
		String day = date.substring(0, gapIndex);
		String time = date.substring(gapIndex + 1, gapIndex + 1 + 5);
		return time + " " + day;
	}

	private static boolean isSameDay(String startTime, String endTime) {
		if (startTime == null || !startTime.contains("T") || endTime == null || !endTime.contains("T")) {
			return false;
		}
		String start = startTime.substring(0, startTime.lastIndexOf("T"));
		String end = endTime.substring(0, endTime.lastIndexOf("T"));

		return start.equals(end);
	}

	/**
	 * convert [2012-01-12T10:03:41.753] to [12 Jan, 2012]
	 * 
	 * @param dateTime
	 * @return
	 * @throws ParseException
	 */
	public static String convertDateToMeetTime(String dateTime) throws ParseException {
		dateTime = dateTime.replaceAll("T", " ");
		int index = dateTime.lastIndexOf(".");
		if (index > 0) {
			dateTime = dateTime.substring(index, dateTime.length());
		}
		long time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime).getTime();
		Date date = new Date(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);

		return hour + ":" + min + "    " + WEEK_ABBREVIATION[week - 1] + ", " + day + " " + MONTH_ABBREVIATION[month]
				+ ", " + year;
	}

	public static String convertTime2FormatMeetTime(String startTime, String endTime) {
		boolean isSameDay = isSameDay(startTime, endTime);

		String meetTime = null;
		try {
			if (isSameDay) {
				String date = convertDateToMeetTime(startTime);
				int gap = startTime.lastIndexOf("T");
				String sTime = startTime.substring(gap + 1, gap + 6);
				String eTime = endTime.substring(gap + 1, gap + 6);

				meetTime = date + "  " + sTime + " - " + eTime;
			} else {
				String sDate = convertDateToMeetTime(startTime);
				String eDate = convertDateToMeetTime(endTime);
				int gap = startTime.lastIndexOf("T");
				String sTime = startTime.substring(gap + 1, gap + 6);
				String eTime = endTime.substring(gap + 1, gap + 6);

				meetTime = sDate + "  " + sTime + " -\n" + eDate + "  " + eTime;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			meetTime = "";
		}

		return meetTime;
	}

	public static boolean isEmpty(String s) {
		if (null == s)
			return true;
		if (s.length() == 0)
			return true;
		if (s.trim().length() == 0)
			return true;
		return false;
	}

	/**
	 * To format the output string
	 * 
	 * @param src
	 *            src string
	 * @return format string
	 */
	public static String formatOutput(String src) {
		return ((src == null) || (src.equals(""))) ? "-" : src;
	}
}
