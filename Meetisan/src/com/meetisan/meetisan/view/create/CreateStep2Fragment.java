package com.meetisan.meetisan.view.create;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.ToastHelper;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link CreateStep2Fragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link CreateStep2Fragment#newInstance} factory method to create an instance
 * of this fragment.
 * 
 */
public class CreateStep2Fragment extends Fragment implements OnItemClickListener {
	private OnFragmentInteractionListener mListener;
	private ListView mSelectTagsListView;
	private SelectTagsAdapter mAdapter;
	private List<TagInfo> mTagsData = new ArrayList<TagInfo>();
	
	public CreateStep2Fragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create_step2, container, false);
		mSelectTagsListView = (ListView) view.findViewById(R.id.lv_create_select_tags_list);
		mSelectTagsListView.setOnItemClickListener(this);
		initTagsData();
		mAdapter = new SelectTagsAdapter(getActivity(), mTagsData);
		mSelectTagsListView.setAdapter(mAdapter);
		
		return view;
	}

	public boolean checkUserInput() {
		for (TagInfo tagInfo : mTagsData) {
			if (tagInfo.getState() == 1) {
				return true;
			}
		}
		ToastHelper.showToast(R.string.please_select_some_tags);
		return false;
	}
	
	private void initTagsData() {
		mTagsData.clear();
		// data for test
		for (int i = 0; i < 15; i++) {
			TagInfo mInfo = new TagInfo();
			mInfo.setTitle("Tags Name " + i);
			mInfo.setDescription("tags description" + 1);
			mInfo.setState(0);
			mInfo.setEndorsed(i * 5);
			mInfo.setPeople(i * 3);
			mInfo.setMeetings(i * 8);
			
			mTagsData.add(mInfo);
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TagInfo info = mTagsData.get(position);
		if (info.getState() == 0) {
			info.setState(1);
		} else {
			info.setState(0);
		}
		mAdapter.notifyDataSetChanged();
	}
}
