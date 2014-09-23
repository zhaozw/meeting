package com.meetisan.meetisan.view.tags;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.DialogUtils.OnDialogClickListener;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.DialogUtils;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Tools;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

public class AddTagMomentsActivity extends Activity implements OnClickListener {

	private long mTagID = -1, mUserID = -1;
	private TextView mTitleTxt;
	private ImageView mMomentView;
	private RelativeLayout mBottomLayout;

	private String[] items = null;
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "com.cn.finessBand/faceImage.jpg";
	private String mBase64Str = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_tag_moments);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			mTagID = bundle.getLong("TagID");
			mUserID = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);
		}

		if (mTagID < 0 || mUserID < 0) {
			ToastHelper.showToast(R.string.app_occurred_exception);
			this.finish();
		}

		initView();
		showPickImageDialog(true);
	}

	private void initView() {
		ImageButton imageButton = (ImageButton) findViewById(R.id.btn_title_left);
		imageButton.setOnClickListener(this);
		imageButton.setVisibility(View.VISIBLE);
		Button mRightBtn = (Button) findViewById(R.id.btn_title_right);
		mRightBtn.setText(R.string.send);
		mRightBtn.setVisibility(View.VISIBLE);
		mRightBtn.setOnClickListener(this);

		((TextView) findViewById(R.id.tv_title_text)).setText(R.string.tag_moments);
		mTitleTxt = (TextView) findViewById(R.id.txt_tag_title);
		mMomentView = (ImageView) findViewById(R.id.iv_moment_add);
		mBottomLayout = (RelativeLayout) findViewById(R.id.layout_bottom);
		mBottomLayout.setOnClickListener(this);

		items = new String[] { getString(R.string.select_picture), getString(R.string.take_picture) };
	}

	private void setMomentPreview(Bitmap photo) {
		int size = (Util.getWindowsSize(this, true) - 40) / 3;
		LayoutParams params = new LayoutParams(size, size);
		mMomentView.setLayoutParams(params);
		mMomentView.setImageBitmap(photo);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			confirmExitDialog();
			break;
		case R.id.btn_title_right:
			attempUploadPreview();
			break;
		case R.id.layout_bottom:
			showPickImageDialog(false);
			break;
		default:
			break;
		}
	}

	private void attempUploadPreview() {
		if (mBase64Str == null || TextUtils.isEmpty(mBase64Str)) {
			ToastHelper.showToast(R.string.error_empty_moment_pic, Toast.LENGTH_LONG);
			return;
		}

		String title = mTitleTxt.getText().toString();
		if (title == null || TextUtils.isEmpty(title)) {
			ToastHelper.showToast(R.string.error_empty_moment_title, Toast.LENGTH_LONG);
			return;
		}

		uploadTagMomentToServer(title);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 96);
		intent.putExtra("outputY", 96);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			mBase64Str = Tools.bitmapToBase64(photo);
			setMomentPreview(photo);
		}
	}

	/**
	 * 显示选择对话框
	 */
	private void showPickImageDialog(final boolean isFinish) {

		new AlertDialog.Builder(this).setTitle(R.string.profile_picture)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
							break;
						case 1:
							Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (Tools.hasSdcard()) {
								intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
										.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
							}
							startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
							break;
						}
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (isFinish) {
							AddTagMomentsActivity.this.finish();
						}
					}

				}).show();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(this, R.string.can_not_find_sd_card, Toast.LENGTH_LONG).show();
				}
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private CustomizedProgressDialog mProgressDialog = null;

	private void uploadTagMomentToServer(String title) {

		if (title == null || mBase64Str == null) {
			return;
		}

		HttpRequest request = new HttpRequest();

		if (mProgressDialog == null) {
			mProgressDialog = new CustomizedProgressDialog(this, R.string.uploading_image);
		} else {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(R.string.upload_success, Toast.LENGTH_LONG);
				AddTagMomentsActivity.this.finish();
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
			}
		});

		Map<String, String> data = new TreeMap<String, String>();
		data.put(ServerKeys.KEY_TAG_ID, String.valueOf(mTagID));
		data.put(ServerKeys.KEY_IMAGE, mBase64Str);
		data.put(ServerKeys.KEY_TITLE, title);
		data.put(ServerKeys.KEY_USER_ID, String.valueOf(mUserID));
		request.post(ServerKeys.FULL_URL_ADD_TAG_MOMENT, data);

		mProgressDialog.show();
	}

	private void confirmExitDialog() {
		DialogUtils.showDialog(this, R.string.give_up_add_tag_tips, R.string.warning, R.string.sure, R.string.cancel,
				new OnDialogClickListener() {

					@Override
					public void onClick(boolean isPositiveBtn) {
						if (isPositiveBtn) {
							AddTagMomentsActivity.this.finish();
						}
					}
				});
	}

	@Override
	public void onBackPressed() {
		if (mBase64Str != null) {
			confirmExitDialog();
		} else {
			super.onBackPressed();
		}
	}
}
