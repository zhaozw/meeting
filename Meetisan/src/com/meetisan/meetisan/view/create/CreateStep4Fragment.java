package com.meetisan.meetisan.view.create;

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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.Tools;
import com.meetisan.meetisan.widget.CircleImageView;

/**
 * A fragment with a Google +1 button. Activities that contain this fragment
 * must implement the {@link CreateStep1Fragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link CreateStep4Fragment#newInstance} factory method to create an instance
 * of this fragment.
 * 
 */
public class CreateStep4Fragment extends Fragment {
	private OnFragmentInteractionListener mListener;
	private EditText mDescriptionEditText;
	/* 组件 */
	private RelativeLayout switchAvatar;
	private CircleImageView faceImage;
	private String mLogoBase64String = null;
	private String[] items = null;
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "com.cn.finessBand/faceImage.jpg";

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	public CreateStep4Fragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_create_step4, container, false);
		switchAvatar = (RelativeLayout) view.findViewById(R.id.rl_create_set_icon);
		faceImage = (CircleImageView) view.findViewById(R.id.iv_portrait);

		items = new String[] { getString(R.string.select_picture), getString(R.string.take_picture) };
		// 设置事件监听
		switchAvatar.setOnClickListener(listener);
		mDescriptionEditText = (EditText) view.findViewById(R.id.et_create_meeting_description);

		return view;
	}

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			showDialog();
		}
	};

	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(getActivity()).setTitle(R.string.profile_picture)
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

								intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
										.fromFile(new File(Environment
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
					}

				}).show();

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
			mLogoBase64String = Tools.bitmapToBase64(photo);

			faceImage.setImageBitmap(photo);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
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
					File tempFile = new File(Environment.getExternalStorageDirectory() + "/"
							+ IMAGE_FILE_NAME);

					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(getActivity(), R.string.can_not_find_sd_card, Toast.LENGTH_LONG)
							.show();
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

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	public boolean checkUserInput() {
		if (mDescriptionEditText.getText().toString().length() <= 0) {
			mDescriptionEditText
					.setError(getString(R.string.please_input_a_description_for_your_meeting));
			return false;
		}
		return true;
	}

	public Map<String, Object> getData() {
		Map<String, Object> data = new TreeMap<String, Object>();
		data.put("Description", mDescriptionEditText.getText().toString());
		data.put("Logo", mLogoBase64String);
		return data;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

}
