package com.meetisan.meetisan.view.tags;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.TagMoment;
import com.meetisan.meetisan.utils.HttpBitmap;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

public class TagMomentsActivity extends Activity implements OnClickListener {

	private GridView mGridView;
	private List<TagMoment> mMomentData = new ArrayList<TagMoment>();

	private long mTagID = -1;
	private long mMaxCount = 0;
	private TagMomentAdapter mAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tag_moments);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			mTagID = bundle.getLong("TagID");
		}

		if (mTagID < 0) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		initView();
		getTagMomentsFromServer(1, true, true);
	}

	private void initView() {
		ImageButton imageButton = (ImageButton) findViewById(R.id.btn_title_left);
		imageButton.setOnClickListener(this);
		imageButton.setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.tv_title_text)).setText(R.string.tag_moments);
		;

		mGridView = (GridView) findViewById(R.id.gridview);
		mAdapter = new TagMomentAdapter(this, mMomentData);
		mGridView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		Bundle bundle = null;
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		default:
			break;
		}

		if (intent != null) {
			startActivity(intent);
		}
	}

	private void updateTagMomentsGridView() {
		mAdapter.notifyDataSetChanged();
	}

	private CustomizedProgressDialog mProgressDialog = null;

	private void getTagMomentsFromServer(int pageIndex, final boolean isRefresh, final boolean isNeedsDialog) {

		HttpRequest request = new HttpRequest();

		if (isNeedsDialog) {
			if (mProgressDialog == null) {
				mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);
			} else {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				try {
					if (isRefresh) {
						mMomentData.clear();
					}

					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
					mMaxCount = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);
					JSONArray momentArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < momentArray.length(); i++) {
						TagMoment info = new TagMoment();
						JSONObject json = momentArray.getJSONObject(i);
						info.setId(json.getLong(ServerKeys.KEY_ID));
						info.setTagId(json.getLong(ServerKeys.KEY_TAG_ID));
						info.setImageUri(json.getString(ServerKeys.KEY_IMAGE));
						info.setTitle(json.getString(ServerKeys.KEY_TITLE));
						// info.setUserId(json.getLong(ServerKeys.KEY_USER_ID));
						info.setCreateDate(json.getString(ServerKeys.KEY_CREATE_DATE));
						mMomentData.add(info);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					updateTagMomentsGridView();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updateTagMomentsGridView();
			}
		});

		request.get(ServerKeys.FULL_URL_GET_TAG_MOMENT_LIST + "/" + mTagID + "/?pageindex=" + pageIndex + "&pagesize="
				+ 1000000, null);
		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}

	public class TagMomentAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<TagMoment> mMomentList;
		private HttpBitmap httpBitmap;
		private int width;

		public TagMomentAdapter(Context mContext, List<TagMoment> mMomentList) {
			inflater = LayoutInflater.from(mContext);
			this.mMomentList = mMomentList;
			httpBitmap = new HttpBitmap(mContext);
			width = Util.getWindowsSize(TagMomentsActivity.this, true);
		}

		@Override
		public int getCount() {
			return mMomentList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			final ImageView imageView;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_gridview_tag_moment, parent, false);
				int size = (width - 60) / 3;
				LayoutParams params = new LayoutParams(size, size);
				imageView = (ImageView) convertView.findViewById(R.id.iv_moment);
				imageView.setLayoutParams(params);
				convertView.setTag(imageView);
			} else {
				imageView = (ImageView) convertView.getTag();
			}

			String uri = mMomentList.get(position).getImageUri();
			httpBitmap.displayBitmap(imageView, uri);

			return convertView;
		}

	}
}
