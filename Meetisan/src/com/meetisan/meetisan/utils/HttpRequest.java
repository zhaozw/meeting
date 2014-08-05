package com.meetisan.meetisan.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
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
	
	public void delete(String fullUrl) {
		delete(fullUrl, null);
	}

	public void delete(final String fullUrl, Map<String, Object> data) {
		if (data == null) {
			finalHttp.delete(fullUrl, new MyAjaxCallBack(fullUrl));
		} else {
			final HttpDelete delete = new HttpDelete(fullUrl);
			BasicHttpParams params = new BasicHttpParams();
			for (Entry<String, Object> entry : data.entrySet()) {
				params.setParameter(entry.getKey(), entry.getValue());
			}
			delete.setParams(params);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					MyAjaxCallBack ajaxCallBack = new MyAjaxCallBack(fullUrl);
					try {
						HttpResponse response = new DefaultHttpClient().execute(delete);
						if (response.getStatusLine().getStatusCode() == 200) {
							InputStream stream = response.getEntity().getContent();
							ajaxCallBack.onSuccess(changeInputStream(stream, "utf-8"));
						} else {
							ajaxCallBack.onFailure(null, response.getStatusLine().getStatusCode(),
									response.getStatusLine().getReasonPhrase());
						}
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			Log.d(LOG_CAT, "URL of Delete method:" + fullUrl + ", params:" + data.toString());
		}
	}

	/**
	 * 把Web站点返回的响应流转换为字符串格式
	 * 
	 * @param inputStream
	 *            响应流
	 * @param encode
	 *            编码格式
	 * @return 转换后的字符串
	 */
	private String changeInputStream(InputStream inputStream, String encode) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = "";
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					outputStream.write(data, 0, len);
				}
				result = new String(outputStream.toByteArray(), encode);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
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
