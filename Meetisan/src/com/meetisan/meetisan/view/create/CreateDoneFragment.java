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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Tools;
import com.meetisan.meetisan.view.dashboard.MyConnectionsActivity;
import com.meetisan.meetisan.widget.CircleImageView;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

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
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	public static final int REQUEST_CODE_CREATE_DONE_INVITE_PEOPLE = 5;

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;
	private TextView mTitleTextView;
	private TextView mLocationTextView;
	private TextView mStartTimeTextView;
	private TextView mEndTimeTextView;
	private Button mCreateDoneButton;
	private CircleImageView mLogoImageView;
	private RelativeLayout mInvitePeopleLayout;
	private TextView mInviteTextView;
	private long mInvitePeopleID = -1;
	private String mInvitePeopleName = null;

	Map<String, Object> data = new TreeMap<String, Object>();
	List<TagInfo> tagInfos = new ArrayList<TagInfo>();

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
		if (bundle.getBoolean("IsMeetPerson")) {
			mInvitePeopleID = bundle.getLong("PersonID");
			mInvitePeopleName = bundle.getString("PersonName");
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
		mStartTimeTextView = (TextView) view.findViewById(R.id.tv_create_done_start_time);
		mEndTimeTextView = (TextView) view.findViewById(R.id.tv_create_done_end_time);
		mCreateDoneButton = (Button) view.findViewById(R.id.btn_create_done);
		mCreateDoneButton.setOnClickListener(this);
		mLogoImageView = (CircleImageView) view.findViewById(R.id.iv_portrait);
		mInvitePeopleLayout = (RelativeLayout) view.findViewById(R.id.rl_create_invite_people);
		mInvitePeopleLayout.setOnClickListener(this);
		mInviteTextView = (TextView) view.findViewById(R.id.tv_create_done_invite);
		if (mInvitePeopleName != null) {
			mInviteTextView.setText(mInvitePeopleName);
			mInviteTextView.setVisibility(View.VISIBLE);
		}
		FragmentActivity activity = getActivity();

		if (activity instanceof CreateActivity) {
			CreateActivity createActivity = (CreateActivity) activity;
			data = createActivity.getData();
			tagInfos = createActivity.getTagInfos();
			mTitleTextView.setText((String) data.get(ServerKeys.KEY_TITLE));
			if (data.get("Address") != null) {
				mLocationTextView.setText((String) data.get("Address"));
			}

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm",
					Locale.getDefault());
			long startTime = (Long) data.get("StartTime");
			calendar.setTimeInMillis(startTime);
			mStartTimeTextView.setText(formatter.format(calendar.getTime()));
			long endTime = (Long) data.get("EndTime");
			calendar.setTimeInMillis(endTime);
			mEndTimeTextView.setText(formatter.format(calendar.getTime()));
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
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
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
			doneRequest(data, tagInfos);
			break;
		case R.id.rl_create_invite_people:
			Intent intent = new Intent(getActivity().getApplicationContext(), MyConnectionsActivity.class);
			intent.putExtra("isInvitePeople", true);
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
				mInviteTextView.setText(data.getStringExtra("inviteName"));
				mInviteTextView.setVisibility(View.VISIBLE);
				mInvitePeopleID = data.getLongExtra("inviteID", -1);
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
				mProgressDialog = new CustomizedProgressDialog(getActivity(),
						R.string.please_waiting);
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
				ToastHelper.showToast("Create Meeting Success");

				FragmentActivity activity = getActivity();

				if (activity instanceof CreateActivity) {
					CreateActivity createActivity = (CreateActivity) activity;
					createActivity.showFirstFragment();
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
			request.post(ServerKeys.FULL_URL_MEETING_ADD, convert(map, tagInfos));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (isNeedsDialog) {
			mProgressDialog.show();
		}

	}

	private Map<String, String> convert(Map<String, Object> map, List<TagInfo> tagInfos)
			throws JSONException {
		Map<String, String> data = new TreeMap<String, String>();
		JSONObject meeting = new JSONObject(map);
		meeting.put("Description", "A Party");
		long mUserId = UserInfoKeeper.readUserInfo(getActivity(), UserInfoKeeper.KEY_USER_ID, -1L);
		meeting.put("CreateUserID", mUserId);
		meeting.put("Status", 0);

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
				Locale.getDefault());
		meeting.put("CreateDate", formatter.format(calendar.getTime()).replace(" ", "T"));
		meeting.remove("StartTime");
		meeting.remove("EndTime");
		data.put("Meeting", meeting.toString());
		JSONArray tags = new JSONArray();
		for (TagInfo tagInfo : tagInfos) {
			tags.put(tagInfo.getId());
		}
		data.put("Tags", tags.toString());
		JSONArray invitedArray = new JSONArray();
		if (mInvitePeopleID != -1) {
			invitedArray.put(mInvitePeopleID);
		}
		data.put("Invited", invitedArray.toString());

		return data;
	}
}
