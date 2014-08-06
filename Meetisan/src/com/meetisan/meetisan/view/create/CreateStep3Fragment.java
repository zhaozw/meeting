package com.meetisan.meetisan.view.create;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetisan.meetisan.GoogleMapActivity;
import com.meetisan.meetisan.R;

/**
 * A fragment with a Google +1 button. Activities that contain this fragment must implement the
 * {@link CreateStep1Fragment.OnFragmentInteractionListener} interface to handle interaction events.
 * Use the {@link CreateStep3Fragment#newInstance} factory method to create an instance of this
 * fragment.
 * 
 */
public class CreateStep3Fragment extends Fragment implements OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;

	private RelativeLayout mLocationLayout;
	private TextView mLocationTextView;

	private RadioGroup mMaximumNumber;
	private RadioGroup mSetTime;
	private LinearLayout mStartTimeLayout;
	private LinearLayout mEndTimeLayout;
	private TextView mStartTimeTextView;
	private TextView mEndTimeTextView;
	private long startTime = 0;
	private long endTime = 0;
	private double mLatitude;
	private double mLongitude;
	private String address;

	/**
	 * Use this factory method to create a new instance of this fragment using the provided
	 * parameters.
	 * 
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment CreateStep1Fragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static CreateStep3Fragment newInstance(String param1, String param2) {
		CreateStep3Fragment fragment = new CreateStep3Fragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public CreateStep3Fragment() {
		// Required empty public constructor
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
		View view = inflater.inflate(R.layout.fragment_create_step3, container, false);
		mMaximumNumber = (RadioGroup) view.findViewById(R.id.rg_create_maximum_number);
		mSetTime = (RadioGroup) view.findViewById(R.id.rg_create_set_time);
		mStartTimeLayout = (LinearLayout) view.findViewById(R.id.ll_set_start_time);
		mStartTimeLayout.setOnClickListener(this);
		mEndTimeLayout = (LinearLayout) view.findViewById(R.id.ll_set_end_time);
		mEndTimeLayout.setOnClickListener(this);
		Calendar calendar = Calendar.getInstance();
		startTime = calendar.getTimeInMillis();
		endTime = startTime + 3600 * 1000;
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
		String start = formatter.format(calendar.getTime());
		calendar.setTimeInMillis(endTime);
		String end = formatter.format(calendar.getTime());
		mStartTimeTextView = (TextView) view.findViewById(R.id.tv_create_start_time);
		mEndTimeTextView = (TextView) view.findViewById(R.id.tv_create_end_time);
		mStartTimeTextView.setText(start);
		mEndTimeTextView.setText(end);
		mLocationLayout = (RelativeLayout) view.findViewById(R.id.rl_create_location);
		mLocationLayout.setOnClickListener(this);
		mLocationTextView = (TextView) view.findViewById(R.id.tv_create_location);
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

	public Map<String, Object> getData() {
		Map<String, Object> data = new TreeMap<String, Object>();
		data.put(
				"MaxPerson",
				mMaximumNumber.getCheckedRadioButtonId() == R.id.rb_create_maximum_number_single ? 1
						: 2);
		data.put("Lon", mLongitude);
		data.put("Lat", mLatitude);
		data.put("Address", address);
		data.put("TimeSetType",
				mSetTime.getCheckedRadioButtonId() == R.id.rb_create_set_time_by_me ? 1 : 2);

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
				Locale.getDefault());
		calendar.setTimeInMillis(startTime);
		String start = formatter.format(calendar.getTime()).replace(" ", "T");
		calendar.setTimeInMillis(endTime);
		String end = formatter.format(calendar.getTime()).replace(" ", "T");
		data.put("DetermineStartTime", start);
		data.put("DetermineEndTime", end);
		data.put("StartTime1", start);
		data.put("EndTime1", end);
		data.put("StartTime", startTime);
		data.put("EndTime", endTime);
		return data;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.rl_create_location:
			Intent intent = new Intent(getActivity(), GoogleMapActivity.class);
			// intent.putExtra("MeetTitle", "Location");
			intent.putExtra("IsSetLocation", true);
			startActivityForResult(intent, 3);
			break;
		case R.id.ll_set_start_time:
			Intent startTimeIntent = new Intent(getActivity(), SetTimeActivity.class);
			startTimeIntent.putExtra("time", startTime);
			startTimeIntent.putExtra("title", "Start Time");
			startActivityForResult(startTimeIntent, 1);
			break;
		case R.id.ll_set_end_time:
			Intent endTimeIntent = new Intent(getActivity(), SetTimeActivity.class);
			endTimeIntent.putExtra("time", endTime);
			endTimeIntent.putExtra("title", "End Time");
			startActivityForResult(endTimeIntent, 2);
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED) {
			super.onActivityResult(requestCode, resultCode, data);
		} else if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 3) {
				mLatitude = data.getDoubleExtra("Latitude", 0.0);
				mLongitude = data.getDoubleExtra("Longitude", 0.0);
				address = data.getStringExtra("Address");
				mLocationTextView.setText(address);
			} else {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(data.getLongExtra("time", 0));
				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm",
						Locale.getDefault());
				String string = formatter.format(calendar.getTime());
				if (requestCode == 1) {
					startTime = calendar.getTimeInMillis();
					mStartTimeTextView.setText(string);
					if (startTime > endTime) {
						endTime = startTime + 3600 * 1000;
						calendar.setTimeInMillis(endTime);
						string = formatter.format(calendar.getTime());
						mEndTimeTextView.setText(string);
					}
				} else if (requestCode == 2) {
					endTime = calendar.getTimeInMillis();
					mEndTimeTextView.setText(string);
					if (endTime < startTime) {
						startTime = endTime - 3600 * 1000;
						calendar.setTimeInMillis(startTime);
						string = formatter.format(calendar.getTime());
						mStartTimeTextView.setText(string);
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
