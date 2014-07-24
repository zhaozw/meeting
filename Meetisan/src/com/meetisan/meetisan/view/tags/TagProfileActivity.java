package com.meetisan.meetisan.view.tags;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.model.TagMoment;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.CircleImageView;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

public class TagProfileActivity extends Activity implements OnClickListener {
	private static final String TAG = TagProfileActivity.class.getSimpleName();

	private ImageView mMomentView;
	private CircleImageView mPortraitView;
	private TextView mNameTxt, mDescriptionTxt, mHostTxt, mLinkTxt;

	private long mTagID = -1, mUserID = -1;
	private TagInfo mTagInfo = new TagInfo();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tags_profile);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			mTagID = bundle.getLong("TagID");
			mUserID = bundle.getLong("UserID");
		}

		if (mTagID < 0 || mUserID < 0) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}
		mTagID = 4; // TODO.. for test

		initView();

		getTagProfileFromServer();
	}

	private void initView() {
		((ImageButton) findViewById(R.id.btn_title_icon_left)).setOnClickListener(this);

		mMomentView = (ImageView) findViewById(R.id.iv_moments);
		mPortraitView = (CircleImageView) findViewById(R.id.iv_portrait);
		mNameTxt = (TextView) findViewById(R.id.txt_name);
		mDescriptionTxt = (TextView) findViewById(R.id.txt_tag_description);
		mHostTxt = (TextView) findViewById(R.id.txt_tag_host);
		mLinkTxt = (TextView) findViewById(R.id.txt_tag_link);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_icon_left:
			finish();
			break;
		default:
			break;
		}
	}

	private void updateTagProfileUI() {
		if (mTagInfo == null) {
			return;
		}

		if (mTagInfo.getLogo() != null) {
			mPortraitView.setImageBitmap(mTagInfo.getLogo());
		}
		mNameTxt.setText(mTagInfo.getName());
		mDescriptionTxt.setText(mTagInfo.getDescription());
		mHostTxt.setText(mTagInfo.getTagHost().getHostName());
		mLinkTxt.setText(mTagInfo.getLink());
		setMomentView(mTagInfo.getTagMoments());
	}

	private void setMomentView(List<TagMoment> mTagMoments) {
		List<Bitmap> mBitmaps = new ArrayList<Bitmap>();
		for (TagMoment tagMoment : mTagMoments) {
			mBitmaps.add(tagMoment.getImage());
		}

		Bitmap mBitmap = Util.inFrameFoto(mBitmaps, Util.getWindowsSize(this, true) - 20);
		if (mBitmap != null) {
			mMomentView.setImageBitmap(mBitmap);
		} else {
			Log.e(TAG, "moment bitmap is null");
		}
	}

	private CustomizedProgressDialog mProgressDialog = null;

	private void getTagProfileFromServer() {
		HttpRequest request = new HttpRequest();

		if (mProgressDialog == null) {
			mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);
		} else {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				try {
					JSONObject dataJson = (new JSONObject(result))
							.getJSONObject(ServerKeys.KEY_DATA);

					mTagInfo.setFollow(dataJson.getInt(ServerKeys.KEY_FOLLOW_STATUS));

					JSONObject tagJson = dataJson.getJSONObject(ServerKeys.KEY_TAG);
					mTagInfo.setId(tagJson.getLong(ServerKeys.KEY_ID));
					// mTagInfo.setCategroyId(tagJson.getLong(ServerKeys.KEY_CATEGORY_ID));
					mTagInfo.setTitle(tagJson.getString(ServerKeys.KEY_TITLE));
					mTagInfo.setLink(tagJson.getString(ServerKeys.KEY_LINK));
					mTagInfo.setLogo(Util.base64ToBitmap(tagJson.getString(ServerKeys.KEY_LOGO)));
					mTagInfo.setDescription(tagJson.getString(ServerKeys.KEY_DESCRIPTION));
					mTagInfo.setCreateDate(tagJson.getString(ServerKeys.KEY_CREATE_DATE));
					mTagInfo.setState(tagJson.getInt(ServerKeys.KEY_STATUS));

					// JSONObject hostJson = dataJson.getJSONObject(ServerKeys.KEY_TAG_HOST);
					// mTagInfo.getTagHost().setHostId(hostJson.getLong(ServerKeys.KEY_USER_ID));
					// mTagInfo.getTagHost().setHostName(hostJson.getString(ServerKeys.KEY_NAME));

					JSONArray momentsArray = dataJson.getJSONArray(ServerKeys.KEY_TAG_MOMENTS);
					for (int i = 0; i < momentsArray.length(); i++) {
						JSONObject momJson = momentsArray.getJSONObject(i);
						TagMoment tagMoment = new TagMoment();
						tagMoment.setId(momJson.getLong(ServerKeys.KEY_ID));
						tagMoment.setTagId(momJson.getLong(ServerKeys.KEY_TAG_ID));
						tagMoment.setUserId(momJson.getLong(ServerKeys.KEY_USER_ID));
						tagMoment.setImage(Util.base64ToBitmap(momJson
								.getString(ServerKeys.KEY_IMAGE)));
						tagMoment.setTitle(momJson.getString(ServerKeys.KEY_TITLE));
						tagMoment.setCreateDate(momJson.getString(ServerKeys.KEY_CREATE_DATE));
						mTagInfo.addTagMoment(tagMoment);
					}

					updateTagProfileUI();
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.server_response_exception, Toast.LENGTH_LONG);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		request.get(ServerKeys.FULL_URL_GET_TAG_INFO + "/" + mTagID + "/?UserID=" + mUserID, null);
		mProgressDialog.show();
	}

}
