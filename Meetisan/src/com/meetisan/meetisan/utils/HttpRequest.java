package com.meetisan.meetisan.utils;

import java.util.Map;
import java.util.Map.Entry;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.impl.client.BasicCookieStore;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

public class HttpRequest {
	private static final String LOG_CAT = HttpRequest.class.getSimpleName();
	private OnHttpRequestListener mListener;
	private static FinalHttp finalHttp;

	public HttpRequest() {
		if (finalHttp == null) {
			finalHttp = new FinalHttp();
			finalHttp.configCookieStore(new BasicCookieStore());
		}
	}

	public interface OnHttpRequestListener {
		public void onFailure(String url, int errorNo, String errorMsg);

		// public void onSuccess(String url, JSONObject data);
		public void onSuccess(String url, JSONObject result);
	}

	public void setOnHttpRequestListener(OnHttpRequestListener listener) {
		mListener = listener;
	}

	public void get(String fullUrl, Map<String, String> data) {
		Uri uri = Uri.parse(fullUrl);
		Builder builder = uri.buildUpon();
		if (data != null) {
			for (Entry<String, String> entry : data.entrySet()) {
				builder.appendQueryParameter(entry.getKey(), entry.getValue());
			}
		}
		String buildUrl = builder.build().toString();
		Log.d(LOG_CAT, "Get Url:" + buildUrl);
		// String encodeUrl = URLEncoder.encode(url, HTTP.UTF_8);
		finalHttp.get(buildUrl, new MyAjaxCallBack(buildUrl));
	}

	public void post(String fullUrl, Map<String, String> data) {
		AjaxParams params = new AjaxParams();
		if (data != null) {
			for (Entry<String, String> entry : data.entrySet()) {
				params.put(entry.getKey(), entry.getValue());
			}
		}
		Log.d(LOG_CAT, "Post Url:" + fullUrl + ", params:" + params.toString());
		finalHttp.post(fullUrl, params, new MyAjaxCallBack(fullUrl));
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
			Log.d(LOG_CAT, "Request data:" + t);
			if (mListener == null) {
				return;
			}
			try {
				JSONObject json = new JSONObject(t);
				if (json.getInt(ServerKeys.KEY_STATUS_CODE) == ServerKeys.STATUS_SUCCESS) {
					mListener.onSuccess(myUrl, json.getJSONObject(ServerKeys.KEY_DATA));
				} else {
					mListener.onFailure(myUrl, ServerKeys.STATUS_FAILED,
							json.getString(ServerKeys.KEY_MSG));
				}
			} catch (JSONException e) {
				mListener.onFailure(myUrl, ServerKeys.STATUS_FAILED,
						"Request data is not correct json object");
				e.printStackTrace();
			}
		}
	}
}
