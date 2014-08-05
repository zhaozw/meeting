package com.meetisan.meetisan.view.tags;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.TagHost;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.model.TagMoment;
import com.meetisan.meetisan.utils.HttpBitmap;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.widget.CircleImageView;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

public class TagProfileActivity extends Activity implements OnClickListener {
	private static final String TAG = TagProfileActivity.class.getSimpleName();

	private ImageView mMomentView;
	private CircleImageView mPortraitView;
	private TextView mNameTxt, mDescriptionTxt, mHostTxt, mLinkTxt, mNoMomentTxt;

	private long mTagID = -1, mUserID = -1;
	private TagInfo mTagInfo = new TagInfo();

	private HttpBitmap httpBitmap = new HttpBitmap(this);

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

		initView();

		getTagProfileFromServer();
	}

	private void initView() {
		((ImageButton) findViewById(R.id.btn_title_icon_left)).setOnClickListener(this);

		mMomentView = (ImageView) findViewById(R.id.iv_moments);
		mNoMomentTxt = (TextView) findViewById(R.id.txt_no_moments);
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

		if (mTagInfo.getLogoUri() != null) {
			httpBitmap.displayBitmap(mPortraitView, mTagInfo.getLogoUri());
		}
		mNameTxt.setText(mTagInfo.getTitle());
		mDescriptionTxt.setText("Tag Description:	" + mTagInfo.getDescription());
		mLinkTxt.setText("Link:		" + mTagInfo.getLink());

		String hostNames = "";
		for (TagHost host : mTagInfo.getTagHosts()) {
			hostNames += "  " + host.getHostName();
		}
		mHostTxt.setText("Tag Hosts:	" + hostNames);
		setMomentView(mTagInfo.getTagMoments());
	}

	private void setMomentView(List<TagMoment> mTagMoments) {
		List<String> mBitmaps = new ArrayList<String>();
		for (TagMoment tagMoment : mTagMoments) {
			if (tagMoment.getImageUri() != null) {
				mBitmaps.add(tagMoment.getImageUri());
			}
		}
		if (mBitmaps.size() <= 0) {
			mMomentView.setVisibility(View.GONE);
			mNoMomentTxt.setVisibility(View.VISIBLE);
			return;
		}

		// Bitmap mBitmap = Util.inFrameFoto(mBitmaps, Util.getWindowsSize(this, true) - 20);
		Bitmap mBitmap = null;
		if (mBitmap != null) {
			mMomentView.setImageBitmap(mBitmap);
			mNoMomentTxt.setVisibility(View.GONE);
			mMomentView.setVisibility(View.VISIBLE);
		} else {
			mMomentView.setVisibility(View.GONE);
			mNoMomentTxt.setVisibility(View.VISIBLE);
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
					mTagInfo.setCategroyId(tagJson.getLong("CategroyID"));
					mTagInfo.setTitle(tagJson.getString(ServerKeys.KEY_TITLE));
					mTagInfo.setLink(tagJson.getString(ServerKeys.KEY_LINK));
					mTagInfo.setLogoUri(tagJson.getString(ServerKeys.KEY_LOGO));
					mTagInfo.setDescription(tagJson.getString(ServerKeys.KEY_DESCRIPTION));
					mTagInfo.setCreateDate(tagJson.getString(ServerKeys.KEY_CREATE_DATE));
					mTagInfo.setState(tagJson.getInt(ServerKeys.KEY_STATUS));

					JSONArray hostArray = dataJson.getJSONArray(ServerKeys.KEY_TAG_HOST);
					for (int i = 0; i < hostArray.length(); i++) {
						JSONObject hostJson = hostArray.getJSONObject(i);
						TagHost tagHost = new TagHost();
						tagHost.setHostId(hostJson.getLong(ServerKeys.KEY_USER_ID));
						tagHost.setHostName(hostJson.getString(ServerKeys.KEY_NAME));
						mTagInfo.addTagHost(tagHost);
					}

					JSONArray momentsArray = dataJson.getJSONArray(ServerKeys.KEY_TAG_MOMENTS);
					for (int i = 0; i < momentsArray.length(); i++) {
						JSONObject momJson = momentsArray.getJSONObject(i);
						TagMoment tagMoment = new TagMoment();
						tagMoment.setId(momJson.getLong(ServerKeys.KEY_ID));
						tagMoment.setTagId(momJson.getLong(ServerKeys.KEY_TAG_ID));
						// tagMoment.setUserId(momJson.getLong(ServerKeys.KEY_USER_ID));
						tagMoment.setUserId(momJson.getLong("UserId"));
						tagMoment.setImageUri(momJson.getString(ServerKeys.KEY_IMAGE));
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
