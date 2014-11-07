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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetisan.meetisan.GoogleMapActivity;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.utils.DebugUtils;
import com.meetisan.meetisan.utils.DialogUtils;
import com.meetisan.meetisan.widget.LabelWithIcon;

/**
 * A fragment with a Google +1 button. Activities that contain this fragment
 * must implement the {@link CreateStep1Fragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link CreateStep3Fragment#newInstance} factory method to create an instance
 * of this fragment.
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

	private LabelWithIcon mLocationLabel;

	private RadioGroup mMaximumNumber;
	private RadioGroup mSetTime;
	private RadioButton mThemChooseBtn;
	private RelativeLayout mAddLayout;
	private LinearLayout mTime1Layout, mTime2Layout, mTime3Layout;
	private TextView mStartTime1Txt, mStartTime2Txt, mStartTime3Txt;
	private TextView mEndTime1Txt, mEndTime2Txt, mEndTime3Txt;
	private long startTime1 = -1, startTime2 = -1, startTime3 = -1;
	private long endTime1 = -1, endTime2 = -1, endTime3 = -1;;
	private double mLatitude = -1;
	private double mLongitude = -1;
	private String address;

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
		mSetTime = (RadioGroup) view.findViewById(R.id.rg_create_set_time);
		mSetTime.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.rb_create_set_time_by_him_her) {
					if (!mTime3Layout.isShown()) {
						mAddLayout.setVisibility(View.VISIBLE);
					}
				} else {
					mAddLayout.setVisibility(View.GONE);
					mTime2Layout.setVisibility(View.GONE);
					mTime3Layout.setVisibility(View.GONE);
				}
			}
		});
		mThemChooseBtn = (RadioButton) view.findViewById(R.id.rb_create_set_time_by_him_her);
		mMaximumNumber = (RadioGroup) view.findViewById(R.id.rg_create_maximum_number);
		mMaximumNumber.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.rb_create_maximum_number_single) {
					mThemChooseBtn.setVisibility(View.VISIBLE);
				} else {
					mThemChooseBtn.setVisibility(View.GONE);
					mSetTime.check(R.id.rb_create_set_time_by_me);
				}
			}
		});

		mAddLayout = (RelativeLayout) view.findViewById(R.id.layout_add_option);
		mAddLayout.setOnClickListener(this);
		mLocationLabel = (LabelWithIcon) view.findViewById(R.id.btn_meet_location);
		mLocationLabel.setOnClickListener(this);

		mStartTime1Txt = (TextView) view.findViewById(R.id.tv_create_start_time_1);
		mEndTime1Txt = (TextView) view.findViewById(R.id.tv_create_end_time_1);
		mStartTime2Txt = (TextView) view.findViewById(R.id.tv_create_start_time_2);
		mEndTime2Txt = (TextView) view.findViewById(R.id.tv_create_end_time_2);
		mStartTime3Txt = (TextView) view.findViewById(R.id.tv_create_start_time_3);
		mEndTime3Txt = (TextView) view.findViewById(R.id.tv_create_end_time_3);
		updateTimeLayout(1);

		mTime1Layout = (LinearLayout) view.findViewById(R.id.layout_time_1);
		mTime1Layout.setOnClickListener(this);
		mTime2Layout = (LinearLayout) view.findViewById(R.id.layout_time_2);
		mTime2Layout.setOnClickListener(this);
		mTime3Layout = (LinearLayout) view.findViewById(R.id.layout_time_3);
		mTime3Layout.setOnClickListener(this);

		return view;
	}

	private void updateTimeLayout(int index) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
		long startTime = calendar.getTimeInMillis() + 3600 * 1000 * 3;
		long endTime = startTime + 3600 * 1000;

		if (index == 1) {
			if (startTime1 < 0 && endTime1 < 0) {
				startTime1 = startTime;
				endTime1 = endTime;
			}
			calendar.setTimeInMillis(startTime1);
			mStartTime1Txt.setText(formatter.format(calendar.getTime()));
			calendar.setTimeInMillis(endTime1);
			mEndTime1Txt.setText(formatter.format(calendar.getTime()));
		} else if (index == 2) {
			if (startTime2 < 0 && endTime2 < 0) {
				startTime2 = startTime;
				endTime2 = endTime;
			}
			calendar.setTimeInMillis(startTime2);
			mStartTime2Txt.setText(formatter.format(calendar.getTime()));
			calendar.setTimeInMillis(endTime2);
			mEndTime2Txt.setText(formatter.format(calendar.getTime()));
		} else {
			if (startTime3 < 0 && endTime3 < 0) {
				startTime3 = startTime;
				endTime3 = endTime;
			}
			calendar.setTimeInMillis(startTime3);
			mStartTime3Txt.setText(formatter.format(calendar.getTime()));
			calendar.setTimeInMillis(endTime3);
			mEndTime3Txt.setText(formatter.format(calendar.getTime()));
		}
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

	public boolean checkUserInput() {
		if (DebugUtils.IS_DEBUG) {
			mLongitude = UserInfoKeeper.readUserInfo(getActivity(), UserInfoKeeper.KEY_USER_LON, 0.0f);
			mLatitude = UserInfoKeeper.readUserInfo(getActivity(), UserInfoKeeper.KEY_USER_LAT, 0.0f);
		}

		if (mLongitude == -1 || mLatitude == -1) {
			DialogUtils.showDialog(getActivity(), "Wait!", "Please select a location", "OK", null, null);
			return false;
		}
		return true;
	}

	public Map<String, Object> getData() {
		Map<String, Object> data = new TreeMap<String, Object>();
		data.put("MaxPerson", (mMaximumNumber.getCheckedRadioButtonId() == R.id.rb_create_maximum_number_single) ? 1
				: 2);
		data.put("Lon", mLongitude);
		data.put("Lat", mLatitude);
		// if (DebugUtils.IS_DEBUG) {
		// float lon = UserInfoKeeper.readUserInfo(getActivity(),
		// UserInfoKeeper.KEY_USER_LON, 0.0f);
		// float lat = UserInfoKeeper.readUserInfo(getActivity(),
		// UserInfoKeeper.KEY_USER_LAT, 0.0f);
		// data.put("Lon", String.valueOf(lon)); // for test
		// data.put("Lat", String.valueOf(lat)); // for test
		// }
		data.put("Address", address);
		data.put("TimeSetType", (mSetTime.getCheckedRadioButtonId() == R.id.rb_create_set_time_by_me) ? 1 : 2);

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		calendar.setTimeInMillis(startTime1);
		String start1 = formatter.format(calendar.getTime());
		start1 = start1.replace(" ", "T");
		data.put("StartTime1", start1);
		calendar.setTimeInMillis(endTime1);
		String end1 = formatter.format(calendar.getTime());
		end1 = end1.replace(" ", "T");
		data.put("EndTime1", end1);
		if (mSetTime.getCheckedRadioButtonId() == R.id.rb_create_set_time_by_me) {
			data.put("DetermineStartTime", start1);
			data.put("DetermineEndTime", end1);
		} else {
			data.put("DetermineStartTime", null);
			data.put("DetermineEndTime", null);
		}

		if (startTime2 > 0 && endTime2 > 0) {
			calendar.setTimeInMillis(startTime2);
			String start2 = formatter.format(calendar.getTime());
			start2 = start2.replace(" ", "T");
			data.put("StartTime2", start2);
			calendar.setTimeInMillis(endTime2);
			String end2 = formatter.format(calendar.getTime());
			end2 = end2.replace(" ", "T");
			data.put("EndTime2", end2);
		}

		if (startTime3 > 0 && endTime3 > 0) {
			calendar.setTimeInMillis(startTime3);
			String start3 = formatter.format(calendar.getTime());
			start3 = start3.replace(" ", "T");
			data.put("StartTime3", start3);
			calendar.setTimeInMillis(endTime3);
			String end3 = formatter.format(calendar.getTime());
			end3 = end3.replace(" ", "T");
			data.put("EndTime3", end3);
		}
		return data;
	}

	public Map<String, Object> getMeetTime() {
		Map<String, Object> data = new TreeMap<String, Object>();

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
		calendar.setTimeInMillis(startTime1);
		String start1 = formatter.format(calendar.getTime());
		data.put("StartTime1", start1);
		calendar.setTimeInMillis(endTime1);
		String end1 = formatter.format(calendar.getTime());
		data.put("EndTime1", end1);

		if (startTime2 > 0 && endTime2 > 0) {
			calendar.setTimeInMillis(startTime2);
			String start2 = formatter.format(calendar.getTime());
			data.put("StartTime2", start2);
			calendar.setTimeInMillis(endTime2);
			String end2 = formatter.format(calendar.getTime());
			data.put("EndTime2", end2);
		}

		if (startTime3 > 0 && endTime3 > 0) {
			calendar.setTimeInMillis(startTime3);
			String start3 = formatter.format(calendar.getTime());
			data.put("StartTime3", start3);
			calendar.setTimeInMillis(endTime3);
			String end3 = formatter.format(calendar.getTime());
			data.put("EndTime3", end3);
		}
		return data;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_meet_location:
			Intent intent = new Intent(getActivity(), GoogleMapActivity.class);
			// intent.putExtra("MeetTitle", "Location");
			intent.putExtra("IsSetLocation", true);
			startActivityForResult(intent, 10);
			break;
		case R.id.layout_time_1:
			Intent Time1Intent = new Intent(getActivity(), SetTimeActivity.class);
			Time1Intent.putExtra("StartTime", startTime1);
			Time1Intent.putExtra("EndTime", endTime1);
			Time1Intent.putExtra("SetIndex", 1);
			startActivityForResult(Time1Intent, 1);
			break;
		case R.id.layout_time_2:
			Intent Time2Intent = new Intent(getActivity(), SetTimeActivity.class);
			Time2Intent.putExtra("StartTime", startTime2);
			Time2Intent.putExtra("EndTime", endTime2);
			Time2Intent.putExtra("SetIndex", 2);
			startActivityForResult(Time2Intent, 2);
			break;
		case R.id.layout_time_3:
			Intent Time3Intent = new Intent(getActivity(), SetTimeActivity.class);
			Time3Intent.putExtra("StartTime", startTime3);
			Time3Intent.putExtra("EndTime", endTime3);
			Time3Intent.putExtra("SetIndex", 3);
			startActivityForResult(Time3Intent, 3);
			break;
		case R.id.layout_add_option:
			int index = 0;
			if (startTime2 < 0 && endTime2 < 0) {
				mTime2Layout.setVisibility(View.VISIBLE);
				index = 2;
			} else {
				mTime3Layout.setVisibility(View.VISIBLE);
				index = 3;
				mAddLayout.setVisibility(View.GONE);
			}

			Intent TimeNewIntent = new Intent(getActivity(), SetTimeActivity.class);
			TimeNewIntent.putExtra("StartTime", -1);
			TimeNewIntent.putExtra("EndTime", -1);
			TimeNewIntent.putExtra("SetIndex", index);
			startActivityForResult(TimeNewIntent, index);
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
			if (requestCode == 10) {
				mLatitude = data.getDoubleExtra("Latitude", 0.0);
				mLongitude = data.getDoubleExtra("Longitude", 0.0);
				address = data.getStringExtra("Address");
				mLocationLabel.setText(address);
			} else {
				long start = data.getLongExtra("StartTime", 0);
				long end = data.getLongExtra("EndTime", 0);
				int index = data.getIntExtra("SetIndex", 0);

				if (index == 1) {
					startTime1 = start;
					endTime1 = end;
				} else if (index == 2) {
					startTime2 = start;
					endTime2 = end;
				} else {
					startTime3 = start;
					endTime3 = end;
				}

				updateTimeLayout(index);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
