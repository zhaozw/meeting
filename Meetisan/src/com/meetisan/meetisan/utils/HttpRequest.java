package com.meetisan.meetisan.utils;

import java.util.Map;
import java.util.Map.Entry;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.impl.client.BasicCookieStore;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

public class HttpRequest {
	private static final String LOG_CAT = HttpRequest.class.getSimpleName();
	public static final String SERVER_ADDRESS = "http://113.105.115.30";
	private static final int SERVER_PORT = 5520;

	public static final String STATUS = "Status";
	public static final String STATUS_CODE = "StatusCode";
	public static final String MSG = "Msg";
	public static final int STATE_SUCCESS = 0;
	public static final int STATE_FAILED = 1;

	private static String MAIN_URL = SERVER_ADDRESS + ":" + SERVER_PORT + "/api/User";;
	private OnHttpRequestListener mListener;
	private static FinalHttp finalHttp;

	// private static HttpRequest singleInstance;

	// public enum HttpRequestType {
	// CHECK_LOGIN, CHECK_REGISTION, CHECK_EMAIL, CHECK_DEVICE, GET_USERINFO;
	// }
	//
	// public static HttpRequest instance() {
	// if (singleInstance == null) {
	// singleInstance = new HttpRequest();
	// }
	// return singleInstance;
	// }

	private HttpRequest() {
		if (finalHttp == null) {
			finalHttp = new FinalHttp();
			finalHttp.configCookieStore(new BasicCookieStore());
		}
	}

	public interface OnHttpRequestListener {
		public void onFailure(String url, int errorNo, String errorMsg);

		// public void onSuccess(String url, JSONObject data);
		public void onSuccess(String url, String result);
	}

	// public void setOnHttpRequestListener(OnHttpRequestListener listener) {
	// mListener = listener;
	// }

	public void setOnHttpRequestListener(OnHttpRequestListener listener) {
		mListener = listener;
	}

	public void get(String endUrl, Map<String, String> data) {
		String url = MAIN_URL + endUrl;
		Uri uri = Uri.parse(url);
		Builder builder = uri.buildUpon();
		if (data != null) {
			for (Entry<String, String> entry : data.entrySet()) {
				builder.appendQueryParameter(entry.getKey(), entry.getValue());
			}
		}
		String buildUrl = builder.build().toString();
		Log.d(LOG_CAT, buildUrl);
		// String encodeUrl = URLEncoder.encode(url, HTTP.UTF_8);
		finalHttp.get(buildUrl, new MyAjaxCallBack(buildUrl));
	}

	public void get(String fullUrl) {
		// String encodeUrl = URLEncoder.encode(url, HTTP.UTF_8);
		Util.logD(LOG_CAT, fullUrl);
		finalHttp.get(fullUrl, new MyAjaxCallBack(fullUrl));
	}

	public void post(String endUrl, Map<String, String> data) {
		AjaxParams params = new AjaxParams();
		if (data != null) {
			for (Entry<String, String> entry : data.entrySet()) {
				params.put(entry.getKey(), entry.getValue());
			}
		}
		String url = MAIN_URL + endUrl;
		// String encodedUrl = URLEncoder.encode(url, HTTP.UTF_8);
		Log.d(LOG_CAT, "Post Url:" + url + ", params:" + params.toString());
		finalHttp.post(url, params, new MyAjaxCallBack(endUrl));
	}

	private class MyAjaxCallBack extends AjaxCallBack<String> {
		private String myUrl;

		public MyAjaxCallBack(String url) {
			myUrl = url;
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			Log.e(LOG_CAT, t.toString());
			if (mListener != null) {
				mListener.onFailure(myUrl, errorNo, strMsg);
			}
		}

		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			Log.d(LOG_CAT, t);
			// try {
			if (mListener != null) {
				// JSONObject json = new JSONObject(t);
				mListener.onSuccess(myUrl, t);
			}
			// } catch (JSONException e) {
			// mListener.onFailure(myUrl, STATE_FAILED, "failed");
			// e.printStackTrace();
			// }
		}
	}
}
