package com.meetisan.meetisan.view.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.TagCategory;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.LabelWithIcon;
import com.meetisan.meetisan.widget.spinner.AbstractSpinerAdapter.OnItemSelectListener;
import com.meetisan.meetisan.widget.spinner.CustemSpinerAdapter;
import com.meetisan.meetisan.widget.spinner.SpinnerObject;
import com.meetisan.meetisan.widget.spinner.SpinnerPopWindow;

public class CreateTagsActivity extends Activity implements OnClickListener {

	private CustemSpinerAdapter mAdapter;
	private SpinnerPopWindow mSpinerPopWindow;
	private List<TagCategory> mCategoryData = new ArrayList<TagCategory>();
	private List<SpinnerObject> mCategroyList = new ArrayList<SpinnerObject>();
	private LabelWithIcon mCategroyView;
	private View mGapView;
	private String mTagName = null;
	private long mCategoryID = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_create_tag);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			mTagName = bundle.getString("TagName");
		}
		if (mTagName == null) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		getTagCategoryFromServer();

		initView();
	}

	private void initView() {
		ImageButton imageButton = (ImageButton) findViewById(R.id.btn_title_left);
		imageButton.setOnClickListener(this);
		imageButton.setVisibility(View.VISIBLE);
		// Button mRightBtn = (Button) findViewById(R.id.btn_title_right);
		// mRightBtn.setText(R.string.send);
		// mRightBtn.setVisibility(View.VISIBLE);
		// mRightBtn.setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title_text)).setText(R.string.create_tag);
		((TextView) findViewById(R.id.txt_tag_name)).setText(mTagName);
		mCategroyView = (LabelWithIcon) findViewById(R.id.btn_tag_category);
		mCategroyView.setOnClickListener(this);
		((Button) findViewById(R.id.btn_create)).setOnClickListener(this);

		mGapView = (View) findViewById(R.id.view_gap_below);
		mAdapter = new CustemSpinerAdapter(this);
		mAdapter.refreshData(mCategroyList, 0);
		mSpinerPopWindow = new SpinnerPopWindow(this);
		mSpinerPopWindow.setAdatper(mAdapter);
		mSpinerPopWindow.setOnItemSelectListener(new OnItemSelectListener() {

			@Override
			public void onItemClick(int position) {
				TagCategory mCategory = mCategoryData.get(position);
				mCategoryID = mCategory.getId();
				mCategroyView.setContentText("(" + mCategory.getTitle() + ")");
			}
		});
		mSpinerPopWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				mCategroyView.setImageResource(R.drawable.icon_arrow_down);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			onBackPressed();
			// confirmExitDialog();
			break;
		case R.id.btn_title_right:
			// sendReportToServer();
			break;
		case R.id.btn_tag_category:
			showCategorySpinner(mGapView);
			break;
		case R.id.btn_create:
			applyCreateTagToServer();
			break;
		default:
			break;
		}
	}

	private void showCategorySpinner(View view) {
		mCategroyList.clear();
		for (TagCategory category : mCategoryData) {
			SpinnerObject object = new SpinnerObject();
			object.data = category.getTitle();
			mCategroyList.add(object);
		}
		mAdapter.refreshData(mCategroyList, 0);

		mCategroyView.setImageResource(R.drawable.icon_arrow_up);
		mSpinerPopWindow.setWidth(Util.getWindowsSize(this, true));
		mSpinerPopWindow.showAsDropDown(view);
	}

	private void applyCreateTagToServer() {
		if (mCategoryID == -1) {
			ToastHelper.showToast("Please select Tag Category first!");
			return;
		}

		final CustomizedProgressDialog mProgressDialog = new CustomizedProgressDialog(this, R.string.creating);

		HttpRequest request = new HttpRequest();

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				Intent intent = new Intent(CreateTagsActivity.this, CreateTagsResultActivity.class);
				intent.putExtra("TagName", mTagName);
				startActivity(intent);
				CreateTagsActivity.this.finish();
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		long mUserID = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);
		Map<String, String> data = new TreeMap<String, String>();
		data.put(ServerKeys.KEY_TITLE, mTagName);
		data.put(ServerKeys.KEY_CATEGORY_ID, String.valueOf(mCategoryID));
		data.put(ServerKeys.KEY_USER_ID, String.valueOf(mUserID));
		request.post(ServerKeys.FULL_URL_CREATE_TAG, data);

		mProgressDialog.show();
	}

	// private void confirmExitDialog() {
	// DialogUtils.showDialog(this, R.string.warning,
	// R.string.give_up_add_tag_tips, R.string.sure, R.string.cancel,
	// new OnDialogClickListener() {
	//
	// @Override
	// public void onClick(boolean isPositiveBtn) {
	// if (isPositiveBtn) {
	// CreateTagsActivity.this.finish();
	// }
	// }
	// });
	// }
	//
	// @Override
	// public void onBackPressed() {
	// if (!TextUtils.isEmpty(mEvidenceTxt.getText())) {
	// confirmExitDialog();
	// } else {
	// super.onBackPressed();
	// }
	// }

	private void getTagCategoryFromServer() {

		final CustomizedProgressDialog mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);

		HttpRequest request = new HttpRequest();

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				try {
					mCategoryData.clear();

					Log.d("TagsActivity", "All Tags: " + result);
					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);

					JSONArray categoryArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < categoryArray.length(); i++) {
						TagCategory tagCategory = new TagCategory();
						JSONObject json = categoryArray.getJSONObject(i);
						tagCategory.setId(json.getLong(ServerKeys.KEY_ID));
						tagCategory.setTitle(json.getString(ServerKeys.KEY_TITLE));
						// tagCategory.setLogoUri(json.getString(ServerKeys.KEY_LOGO));
						mCategoryData.add(tagCategory);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.server_response_exception, Toast.LENGTH_LONG);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				CreateTagsActivity.this.finish();
			}
		});

		request.get(ServerKeys.FULL_URL_GET_TAG_LIST + "/?pageindex=" + 1 + "&pagesize=" + 10000, null);
		mProgressDialog.show();
	}
}
