package com.meetisan.meetisan.view.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
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
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.view.dashboard.PersonProfileActivity;
import com.meetisan.meetisan.widget.CircleImageView;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.CustomizedProgressDialog.DialogStyle;
import com.meetisan.meetisan.widget.LabelWithIcon;
import com.meetisan.meetisan.widget.TagInFrameFoto;

public class TagProfileActivity extends Activity implements OnClickListener {
	private static final String TAG = TagProfileActivity.class.getSimpleName();

	private TagInFrameFoto mMomentView;
	private CircleImageView mPortraitView;
	private LabelWithIcon mHostLabel;
	private ImageButton mAddBtn;
	private TextView mNameTxt, mDescriptionTxt, mLinkTxt, mNoMomentTxt;
	// private TextView mFirstTagTxt, mSecondTagTxt, mThirdTagTxt, mNoTagTxt;

	private long mTagID = -1, mUserID = -1, mHostID = -1;
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
		((ImageButton) findViewById(R.id.im_btn_connection)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.im_btn_meeting)).setOnClickListener(this);

		mMomentView = (TagInFrameFoto) findViewById(R.id.iv_moments);
		mMomentView.setLayoutSize(Util.getWindowsSize(this, true) - 20);
		mMomentView.setOnInFrameClickListener(this);
		mNoMomentTxt = (TextView) findViewById(R.id.txt_no_moments);
		mNoMomentTxt.setOnClickListener(this);
		mPortraitView = (CircleImageView) findViewById(R.id.iv_portrait);
		mNameTxt = (TextView) findViewById(R.id.txt_name);
		mDescriptionTxt = (TextView) findViewById(R.id.txt_tag_description);
		mHostLabel = (LabelWithIcon) findViewById(R.id.label_tag_host);
		mHostLabel.setOnClickListener(this);
		mLinkTxt = (TextView) findViewById(R.id.txt_tag_link);

		// mFirstTagTxt = (TextView) findViewById(R.id.txt_tag_one);
		// mSecondTagTxt = (TextView) findViewById(R.id.txt_tag_two);
		// mThirdTagTxt = (TextView) findViewById(R.id.txt_tag_three);
		// mNoTagTxt = (TextView) findViewById(R.id.txt_no_tags);

		mAddBtn = (ImageButton) findViewById(R.id.btn_title_icon_right);
		mAddBtn.setOnClickListener(this);

		LabelWithIcon mConnectedLabel = (LabelWithIcon) findViewById(R.id.btn_connected);
		mConnectedLabel.setOnClickListener(this);
		LabelWithIcon mAttendedLabel = (LabelWithIcon) findViewById(R.id.btn_attended);
		mAttendedLabel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		Bundle bundle = null;
		switch (v.getId()) {
		case R.id.btn_title_icon_left:
			finish();
			break;
		case R.id.im_btn_connection:
			intent = new Intent(this, TagAssociatedPeopleActivity.class);
			bundle = new Bundle();
			bundle.putLong("TagID", mTagID);
			intent.putExtras(bundle);
			break;
		case R.id.btn_connected:
			intent = new Intent(this, TagConnectedPeopleActivity.class);
			bundle = new Bundle();
			bundle.putLong("TagID", mTagID);
			intent.putExtras(bundle);
			break;
		case R.id.im_btn_meeting:
			intent = new Intent(this, TagAssociatedMeetingsActivity.class);
			bundle = new Bundle();
			bundle.putLong("TagID", mTagID);
			intent.putExtras(bundle);
			break;
		case R.id.btn_attended:
			intent = new Intent(this, TagAttendedMeetingsActivity.class);
			bundle = new Bundle();
			bundle.putLong("TagID", mTagID);
			intent.putExtras(bundle);
			break;
		case R.id.label_tag_host:
			if (mHostID >= 0) {
				intent = new Intent(this, PersonProfileActivity.class);
				bundle = new Bundle();
				bundle.putLong("UserID", mHostID);
				intent.putExtras(bundle);
			}
			break;
		case R.id.txt_no_moments:
			if (mTagID > 0) {
				intent = new Intent(this, TagMomentsActivity.class);
				bundle = new Bundle();
				bundle.putLong("TagID", mTagID);
				bundle.putInt("FollowState", mTagInfo.getFollow());
				intent.putExtras(bundle);
			}
		case R.id.iv_moments:
			if (mTagID > 0) {
				intent = new Intent(this, TagMomentsActivity.class);
				bundle = new Bundle();
				bundle.putLong("TagID", mTagID);
				bundle.putInt("FollowState", mTagInfo.getFollow());
				intent.putExtras(bundle);
			}
			break;
		case R.id.btn_title_icon_right:
			addTagToUser();
			break;
		default:
			break;
		}

		if (intent != null) {
			startActivity(intent);
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
		mLinkTxt.setText("Website:		" + mTagInfo.getLink());

		TagHost host = mTagInfo.getTagHost();
		if (host != null) {
			String hostNames = host.getHostName();
			mHostLabel.setContentText(hostNames);
			mHostID = host.getHostId();
		}

		if (mTagInfo.getFollow() == 0) {
			mAddBtn.setVisibility(View.VISIBLE);
		} else {
			mAddBtn.setVisibility(View.GONE);
		}

		setMomentView(mTagInfo.getTagMoments());
	}

	// private void setAssociateTag(List<TagInfo> tagsList) {
	// if (tagsList == null) {
	// mNoTagTxt.setVisibility(View.VISIBLE);
	// }
	// int tagsCount = tagsList.size();
	// if (tagsCount <= 0) {
	// mNoTagTxt.setVisibility(View.VISIBLE);
	// }
	// if (tagsCount >= 1) {
	// mFirstTagTxt.setText(tagsList.get(0).getTitle());
	// mFirstTagTxt.setVisibility(View.VISIBLE);
	// }
	// if (tagsCount >= 2) {
	// mSecondTagTxt.setText(tagsList.get(1).getTitle());
	// mSecondTagTxt.setVisibility(View.VISIBLE);
	// }
	// if (tagsCount >= 3) {
	// mThirdTagTxt.setText(tagsList.get(2).getTitle());
	// mThirdTagTxt.setVisibility(View.VISIBLE);
	// }
	// }

	private void setMomentView(List<TagMoment> mTagMoments) {
		List<String> mUriList = new ArrayList<String>();
		for (TagMoment tagMoment : mTagMoments) {
			if (tagMoment.getImageUri() != null) {
				mUriList.add(tagMoment.getImageUri());
			}
		}
		if (mUriList.size() <= 0) {
			mNoMomentTxt.setVisibility(View.VISIBLE);
		} else {
			mNoMomentTxt.setVisibility(View.GONE);
			mMomentView.setInframeForos(mUriList);
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
					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);

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

					JSONObject hostJson = dataJson.getJSONObject(ServerKeys.KEY_TAG_HOST);
					if (hostJson != null) {
						TagHost tagHost = new TagHost();
						tagHost.setHostId(hostJson.getLong(ServerKeys.KEY_ID));
						tagHost.setHostName(hostJson.getString(ServerKeys.KEY_NAME));
						mTagInfo.setTagHost(tagHost);
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

	private CustomizedProgressDialog mAddTagDialog = null;

	private void addTagToUser() {
		HttpRequest request = new HttpRequest();

		if (mAddTagDialog == null) {
			mAddTagDialog = new CustomizedProgressDialog(this, R.string.adding_tag);
		} else {
			if (mAddTagDialog.isShowing()) {
				mAddTagDialog.dismiss();
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mAddTagDialog.dismiss();
				mTagInfo.setFollow(1);
				mAddBtn.setVisibility(View.GONE);
				CustomizedProgressDialog mDialog = new CustomizedProgressDialog(TagProfileActivity.this,
						R.string.success_add_tag, DialogStyle.OK);
				mDialog.show();
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mAddTagDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		Map<String, String> data = new TreeMap<String, String>();
		data.put(ServerKeys.KEY_TAG_ID, String.valueOf(mTagID));
		data.put(ServerKeys.KEY_USER_ID, String.valueOf(mUserID));
		request.post(ServerKeys.FULL_URL_USER_ADD_TAG, data);
		mAddTagDialog.show();
	}

}
