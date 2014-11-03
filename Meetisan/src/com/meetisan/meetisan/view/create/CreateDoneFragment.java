package com.meetisan.meetisan.view.create;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Tools;
import com.meetisan.meetisan.widget.CircleImageView;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.CustomizedProgressDialog.DialogStyle;
import com.meetisan.meetisan.widget.LabelWithIcon;

/**
 * A fragment with a Google +1 button. Activities that contain this fragment
 * must implement the {@link CreateStep1Fragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link CreateDoneFragment#newInstance} factory method to create an instance
 * of this fragment.
 * 
 */
public class CreateDoneFragment extends Fragment implements OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String TAG = CreateDoneFragment.class.getSimpleName();
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	public static final int REQUEST_CODE_CREATE_DONE_INVITE_PEOPLE = 5;

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;
	private TextView mTitleTextView;
	private TextView mLocationTextView;
	private TextView mStartTime1Txt, mStartTime2Txt, mStartTime3Txt;
	private TextView mEndTime1Txt, mEndTime2Txt, mEndTime3Txt;
	private LinearLayout mTime2Layout, mTime3Layout;
	private TextView mTagOneTxt, mTagTwoTxt, mTagThreeTxt;
	private Button mCreateDoneButton;
	private CircleImageView mLogoImageView;
	private LabelWithIcon mInviteLabel;
	// private long mInvitePeopleID = -1;
	private ArrayList<Long> mInviteList = new ArrayList<Long>();
	private String mInvitePeopleName = null;

	Map<String, Object> data = new TreeMap<String, Object>();
	Map<String, Object> timeMap = new TreeMap<String, Object>();
	List<TagInfo> tagList = new ArrayList<TagInfo>();

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment CreateStep1Fragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static CreateDoneFragment newInstance(String param1, String param2) {
		CreateDoneFragment fragment = new CreateDoneFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public CreateDoneFragment() {
		Bundle bundle = getArguments();
		if (bundle != null && bundle.getBoolean("IsMeetPerson")) {
			long mInvitePeopleID = bundle.getLong("PersonID");
			mInviteList.add(mInvitePeopleID);
			mInvitePeopleName = bundle.getString("PersonName");
			mInviteLabel.setText(mInvitePeopleName);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_create_done, container, false);
		mTitleTextView = (TextView) view.findViewById(R.id.tv_create_done_title);
		mLocationTextView = (TextView) view.findViewById(R.id.tv_create_done_location);

		mStartTime1Txt = (TextView) view.findViewById(R.id.tv_create_start_time_1);
		mEndTime1Txt = (TextView) view.findViewById(R.id.tv_create_end_time_1);
		mStartTime2Txt = (TextView) view.findViewById(R.id.tv_create_start_time_2);
		mEndTime2Txt = (TextView) view.findViewById(R.id.tv_create_end_time_2);
		mStartTime3Txt = (TextView) view.findViewById(R.id.tv_create_start_time_3);
		mEndTime3Txt = (TextView) view.findViewById(R.id.tv_create_end_time_3);
		mTime2Layout = (LinearLayout) view.findViewById(R.id.layout_time_2);
		mTime3Layout = (LinearLayout) view.findViewById(R.id.layout_time_3);

		mTagOneTxt = (TextView) view.findViewById(R.id.txt_tag_one);
		mTagTwoTxt = (TextView) view.findViewById(R.id.txt_tag_two);
		mTagThreeTxt = (TextView) view.findViewById(R.id.txt_tag_three);

		mCreateDoneButton = (Button) view.findViewById(R.id.btn_create_done);
		mCreateDoneButton.setOnClickListener(this);
		mLogoImageView = (CircleImageView) view.findViewById(R.id.iv_portrait);
		mInviteLabel = (LabelWithIcon) view.findViewById(R.id.rl_create_invite_people);
		mInviteLabel.setOnClickListener(this);
		mInviteLabel.setText(mInvitePeopleName);
		FragmentActivity activity = getActivity();

		if (activity instanceof CreateActivity) {
			CreateActivity createActivity = (CreateActivity) activity;
			data = createActivity.getData();
			timeMap = createActivity.getMeetTime();
			tagList = createActivity.getTagInfos();
			mTitleTextView.setText((String) data.get(ServerKeys.KEY_TITLE));
			if (data.get("Address") != null) {
				mLocationTextView.setText((String) data.get("Address"));
			}

			int tagsCount = tagList.size();
			if (tagsCount >= 1) {
				mTagOneTxt.setText(tagList.get(0).getTitle());
				mTagOneTxt.setVisibility(View.VISIBLE);
			}
			if (tagsCount >= 2) {
				mTagTwoTxt.setText(tagList.get(1).getTitle());
				mTagTwoTxt.setVisibility(View.VISIBLE);
			}
			if (tagsCount >= 3) {
				mTagThreeTxt.setText(tagList.get(2).getTitle());
				mTagThreeTxt.setVisibility(View.VISIBLE);
			}

			String startTime = (String) timeMap.get("StartTime1");
			mStartTime1Txt.setText(startTime);
			String endTime = (String) timeMap.get("EndTime1");
			mEndTime1Txt.setText(endTime);

			startTime = (String) timeMap.get("StartTime2");
			if (startTime != null) {
				mStartTime2Txt.setText(startTime);
				endTime = (String) timeMap.get("EndTime2");
				mEndTime2Txt.setText(endTime);
				mTime2Layout.setVisibility(View.VISIBLE);
			}

			startTime = (String) timeMap.get("StartTime3");
			if (startTime != null) {
				mStartTime3Txt.setText(startTime);
				endTime = (String) timeMap.get("EndTime3");
				mEndTime3Txt.setText(endTime);
				mTime3Layout.setVisibility(View.VISIBLE);
			}

			if (data.get("Logo") != null) {
				mLogoImageView.setImageBitmap(Tools.base64ToBitmap((String) data.get("Logo")));
			}
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_create_done:
			doneRequest(data, tagList);
			break;
		case R.id.rl_create_invite_people:
			CreateActivity createActivity = (CreateActivity) getActivity();
			data = createActivity.getData();
			int maxPerson = (Integer) data.get("MaxPerson");
			boolean isMultiModel = maxPerson == 1 ? false : true;
			List<TagInfo> tagList = createActivity.getTagInfos();
			String tagIDs = "";
			if (tagList != null && tagList.size() > 0) {
				for (int i = 0; i < tagList.size(); i++) {
					if (i != tagList.size() - 1) {
						tagIDs += tagList.get(i).getId() + ",";
					} else {
						tagIDs += tagList.get(i).getId();
					}
				}
			}

			Intent intent = new Intent(getActivity().getApplicationContext(), SelectPeopleActivity.class);
			intent.putExtra("isMulitSelect", isMultiModel);
			intent.putExtra("TagIDs", tagIDs);
			startActivityForResult(intent, REQUEST_CODE_CREATE_DONE_INVITE_PEOPLE);
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_CREATE_DONE_INVITE_PEOPLE) {
				String mInvitePeopleName = data.getStringExtra("inviteName");
				String mInvitePeopleID = data.getStringExtra("inviteID");
				Log.d(TAG, "Name = " + mInvitePeopleName);
				Log.d(TAG, "IDs = " + mInvitePeopleID);
				if (mInvitePeopleName != null && mInvitePeopleID != null) {
					try {
						JSONArray idArray = new JSONArray(mInvitePeopleID);
						mInviteList.clear();
						for (int i = 0; i < idArray.length(); i++) {
							mInviteList.add(idArray.getLong(i));
						}

						String names = "";
						JSONArray nameArray = new JSONArray(mInvitePeopleName);
						for (int i = 0; i < nameArray.length(); i++) {
							names += (nameArray.getString(i) + "  ");
						}
						mInviteLabel.setText(names);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private CustomizedProgressDialog mProgressDialog = null;
	private boolean isNeedsDialog = true;

	private void doneRequest(Map<String, Object> map, List<TagInfo> tagInfos) {
		HttpRequest request = new HttpRequest();
		if (isNeedsDialog) {
			if (mProgressDialog == null) {
				mProgressDialog = new CustomizedProgressDialog(getActivity(), R.string.sending_request);
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
				CustomizedProgressDialog mDialog = new CustomizedProgressDialog(getActivity(),
						R.string.meeting_created, DialogStyle.OK);
				mDialog.show();

				FragmentActivity activity = getActivity();

				if (activity instanceof CreateActivity) {
					CreateActivity createActivity = (CreateActivity) activity;
					createActivity.createMeetingDone();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast("Create Meeting Failed");
			}
		});

		try {
			request.postJsonString(ServerKeys.FULL_URL_MEETING_ADD, convert(map, tagInfos));

			Log.e("CreateDoneFragment", convert(map, tagInfos));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (isNeedsDialog) {
			mProgressDialog.show();
		}

	}

	private String convert(Map<String, Object> map, List<TagInfo> tagInfos) throws JSONException {
		JSONObject data = new JSONObject();

		JSONObject meetJson = new JSONObject(map);
		meetJson.put("Status", 0);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		String createTime = formatter.format(calendar.getTime());
		createTime = createTime.replace(" ", "T");
		meetJson.put("CreateDate", createTime);
		data.put("Meeting", meetJson);

		JSONArray tagJson = new JSONArray();
		for (TagInfo tagInfo : tagInfos) {
			tagJson.put(tagInfo.getId());
		}
		data.put("Tags", tagJson);

		JSONArray invitedJson = new JSONArray(mInviteList);
		data.put("Invited", invitedJson);

		return data.toString();
	}

}
