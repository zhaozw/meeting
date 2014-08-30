package com.meetisan.meetisan.view.create;

import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.internal.v;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.HttpBitmap;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.widget.CircleImageView;
import com.meetisan.meetisan.widget.DeleteTouchListener;
import com.meetisan.meetisan.widget.LabelWithSwitchButton;
import com.meetisan.meetisan.widget.DeleteTouchListener.OnDeleteCallback;

/**
 * A fragment with a Google +1 button. Activities that contain this fragment
 * must implement the {@link com.meetisan.meetisan.view.create.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link CreateStep1Fragment#newInstance} factory method to create an instance
 * of this fragment.
 * 
 */
public class CreateStep1Fragment extends Fragment {
	private OnFragmentInteractionListener mListener;
	private EditText mMeetingTitle;
	private LabelWithSwitchButton mPrivateMeeting;
	private LinearLayout mMeetPersonLayout;
	private RelativeLayout mMeetPersonItemLayout;
	private CircleImageView mMeetPersonCircleImageView;
	private TextView mMeetPersonTextView;
	
	private Bundle mMeetPersonBundle = null;
	
	public CreateStep1Fragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMeetPersonBundle = getArguments();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_create_step1, container, false);
		mMeetingTitle = (EditText) view.findViewById(R.id.et_create_meeting_title);
		mPrivateMeeting = (LabelWithSwitchButton) view.findViewById(R.id.switch_create_private_meeting);
		mMeetPersonLayout = (LinearLayout) view.findViewById(R.id.ll_the_people_your_invite);
		mMeetPersonItemLayout = (RelativeLayout) view.findViewById(R.id.rl_create_let_us_meeting_invite_people);
		mMeetPersonCircleImageView = (CircleImageView) view.findViewById(R.id.iv_portrait);
		mMeetPersonTextView = (TextView) view.findViewById(R.id.tv_create_invite_people_name);
		
		if (mMeetPersonBundle != null) {
			if (mMeetPersonBundle.getBoolean("IsMeetPerson")) {
				String name = mMeetPersonBundle.getString("PersonName");
				String avatarUri = mMeetPersonBundle.getString("AvatarUri");
				if (name != null) {
					mMeetPersonTextView.setText(name);
				}
				if (avatarUri != null) {
					HttpBitmap httpBitmap = new HttpBitmap(getActivity());
					httpBitmap.displayBitmap(mMeetPersonCircleImageView, avatarUri);
				} else {
					mMeetPersonCircleImageView.setImageResource(R.drawable.portrait_person_default);
				}
//				mMeetPersonItemLayout.setOnTouchListener(new DeleteTouchListener(null, new OnDeleteCallback() {
//
//					@Override
//					public void onDelete(ListView listView, int position) {
//						mMeetPersonLayout.setVisibility(View.GONE);
//					}
//				}));
				mMeetPersonLayout.setVisibility(View.VISIBLE);
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
	
	public boolean checkUserInput() {
		if (mMeetingTitle.getText().toString().length() <= 0) {
			mMeetingTitle.setError(getString(R.string.please_input_meeting_title));
			return false;
		}
		return true;
	}

	public Map<String, Object> getData() {
		Map<String, Object> data = new TreeMap<String, Object>();
		data.put(ServerKeys.KEY_TITLE, mMeetingTitle.getText().toString());
		data.put(ServerKeys.KEY_IS_PRIVATE, mPrivateMeeting.isChecked() ? 1 : 2);
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
