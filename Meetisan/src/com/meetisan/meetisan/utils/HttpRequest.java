package com.meetisan.meetisan.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
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
		public void onSuccess(String url, String result);
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

	public void postJsonString(String fullUrl, String jsonString) {
		Log.d(LOG_CAT, "Post Url:" + fullUrl + ", json string:" + jsonString);
		try {
			finalHttp.post(fullUrl, new StringEntity(jsonString, HTTP.UTF_8), "application/json", new MyAjaxCallBack(
					fullUrl));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void delete(String fullUrl) {
		delete(fullUrl, null);
	}

	public void delete(final String fullUrl, Map<String, String> data) {
		if (data == null) {
			finalHttp.delete(fullUrl, new MyAjaxCallBack(fullUrl));
		} else {
			HttpDeleteAsyncTask deleteTask = new HttpDeleteAsyncTask(fullUrl, data);
			deleteTask.execute(0);
		}
	}

	private class HttpDeleteAsyncTask extends AsyncTask<Integer, Integer, String[]> {

		private String url;
		private Map<String, String> data = new HashMap<String, String>();
		MyAjaxCallBack ajaxCallBack;
		private boolean ret = false;
		private String resultStr = null;

		public HttpDeleteAsyncTask(String url, Map<String, String> data) {
			this.url = url;
			this.data = data;
		}

		@Override
		public void onPreExecute() {
			super.onPreExecute();
			ajaxCallBack = new MyAjaxCallBack(url);
		}

		@Override
		protected String[] doInBackground(Integer... param) {
			try {

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				Set<String> keys = data.keySet();
				for (String key : keys) {
					params.add(new BasicNameValuePair(key, data.get(key)));
				}

				DefaultHttpClient httpClient = new DefaultHttpClient();
				MyHttpDelete delete = new MyHttpDelete(url);
				delete.setEntity(new UrlEncodedFormEntity(params));
				HttpResponse response = httpClient.execute(delete);

				if (response.getStatusLine().getStatusCode() == 200) {
					ret = true;
					resultStr = EntityUtils.toString(response.getEntity(), "UTF-8");
					Log.d(LOG_CAT, "Response: " + resultStr);
				} else {
					resultStr = response.getStatusLine().getReasonPhrase();
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
				resultStr = "ClientProtocolException";
			} catch (IOException e) {
				e.printStackTrace();
				resultStr = "IOException";
			}

			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			if (ret) {
				ajaxCallBack.onSuccess(resultStr);
			} else {
				ajaxCallBack.onFailure(null, 1, resultStr);
			}
		}

		private class MyHttpDelete extends HttpEntityEnclosingRequestBase {

			public static final String METHOD_NAME = "DELETE";

			public String getMethod() {
				return METHOD_NAME;
			}

			public MyHttpDelete(final String uri) {
				super();
				setURI(URI.create(uri));
			}

		}

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
		public void onSuccess(String result) {
			super.onSuccess(result);
			Log.d(LOG_CAT, "Request data:" + result);
			if (mListener == null) {
				return;
			}
			try {
				JSONObject json = new JSONObject(result);
				if (json.getInt(ServerKeys.KEY_STATUS_CODE) == ServerKeys.STATUS_SUCCESS) {
					// if (json.isNull(ServerKeys.KEY_DATA)) {
					// mListener.onSuccess(myUrl, null);
					// } else {
					mListener.onSuccess(myUrl, result);
					// }
				} else {
					mListener.onFailure(myUrl, ServerKeys.STATUS_FAILED, json.getString(ServerKeys.KEY_MSG));
				}
			} catch (JSONException e) {
				mListener.onFailure(myUrl, ServerKeys.STATUS_FAILED, "Request data is not correct json object");
				e.printStackTrace();
			}
		}
	}
}
