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

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.widget.LabelWithSwitchButton;
import com.meetisan.meetisan.widget.SwitchButton;

/**
 * A fragment with a Google +1 button. Activities that contain this fragment
 * must implement the {@link CreateStep1Fragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link CreateStep1Fragment#newInstance} factory method to create an instance
 * of this fragment.
 * 
 */
public class CreateStep1Fragment extends Fragment {
	private OnFragmentInteractionListener mListener;
	private EditText mMeetingTitle;
	private LabelWithSwitchButton mPrivateMeeting;
	
	public CreateStep1Fragment() {
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
		View view = inflater.inflate(R.layout.fragment_create_step1, container, false);
		mMeetingTitle = (EditText) view.findViewById(R.id.et_create_meeting_title);
		mPrivateMeeting = (LabelWithSwitchButton) view.findViewById(R.id.switch_create_private_meeting);

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
