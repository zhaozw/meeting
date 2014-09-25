package com.meetisan.meetisan.view.create;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.DialogUtils;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.view.create.SelectTagsAdapter.OnTagDetailsClickListener;
import com.meetisan.meetisan.view.tags.TagProfileActivity;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.Mode;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.OnRefreshListener2;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshListView;

public class CreateStep2Fragment extends Fragment implements OnItemClickListener, OnTagDetailsClickListener {
	private OnFragmentInteractionListener mListener;
	private PullToRefreshListView mPullTagsListView;
	private ListView mTagsListView;
	private Activity mParentActivity;

	private SelectTagsAdapter mAdapter;
	private List<TagInfo> mTagsData = new ArrayList<TagInfo>();

	private long mMaxMyTags = 0;

	private long mUserId = -1;

	public CreateStep2Fragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserId = UserInfoKeeper.readUserInfo(getActivity(), UserInfoKeeper.KEY_USER_ID, -1L);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create_step2, container, false);
		mParentActivity = getActivity();

		mPullTagsListView = (PullToRefreshListView) view.findViewById(R.id.list_create_select_tags_list);
		mPullTagsListView.setOnItemClickListener(this);
		mPullTagsListView.setMode(Mode.BOTH);
		TextView mEmptyTagsView = (TextView) view.findViewById(R.id.txt_empty_tags);
		mPullTagsListView.setEmptyView(mEmptyTagsView);
		mPullTagsListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(
						"Last Refresh: " + Util.getCurFormatDate());
				getMyTagsFromServer(1, true, false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(
						"Last Loading: " + Util.getCurFormatDate());
				int count = mTagsListView.getCount() - 2;
				if (count < mMaxMyTags) {
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getMyTagsFromServer(pageIndex, false, false);
				} else {
					ToastHelper.showToast("All the data has been loaded ");
					updateMyTagsListView();
				}
			}
		});
		mTagsListView = mPullTagsListView.getRefreshableView();
		registerForContextMenu(mTagsListView);
		mAdapter = new SelectTagsAdapter(getActivity(), mTagsData);
		mAdapter.setOnDetailsBtnClickListener(this);
		mTagsListView.setAdapter(mAdapter);

		mPullTagsListView.setVisibility(View.VISIBLE);
		getMyTagsFromServer(1, true, true);
		return view;
	}

	private void updateMyTagsListView() {
		mAdapter.notifyDataSetChanged();
		mPullTagsListView.onRefreshComplete();
		if (mTagsData.size() >= mMaxMyTags) {
			mPullTagsListView.setMode(Mode.PULL_FROM_START);
		} else {
			mPullTagsListView.setMode(Mode.BOTH);
		}
	}

	private CustomizedProgressDialog mProgressDialog = null;

	/**
	 * get My Tags from server
	 * 
	 * @param pageIndex
	 *            load page index
	 */
	private void getMyTagsFromServer(int pageIndex, final boolean isRefresh, final boolean isNeedsDialog) {

		HttpRequest request = new HttpRequest();

		if (isNeedsDialog) {
			if (mProgressDialog == null) {
				mProgressDialog = new CustomizedProgressDialog(getActivity(), R.string.please_waiting);
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
				try {
					if (isRefresh) {
						mTagsData.clear();
					}

					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
					mMaxMyTags = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);
					JSONArray tagArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < tagArray.length(); i++) {
						TagInfo info = new TagInfo();
						JSONObject json = tagArray.getJSONObject(i);
						info.setId(json.getLong(ServerKeys.KEY_TAG_ID));
						info.setUserTagId(json.getLong(ServerKeys.KEY_USER_TAG_ID));
						// info.setUserId(json.getLong(ServerKeys.KEY_USER_ID));
						info.setCategroyId(json.getLong(ServerKeys.KEY_CATEGORY_ID));
						info.setTitle(json.getString(ServerKeys.KEY_TITLE));
						// info.setLogo(Util.base64ToBitmap(json.getString(ServerKeys.KEY_LOGO)));
						info.setLogoUri(json.getString(ServerKeys.KEY_LOGO));
						info.setEndorsed(json.getLong(ServerKeys.KEY_ENDORSEMENTS));
						info.setPeople(json.getLong(ServerKeys.KEY_PEOPLES));
						info.setMeetings(json.getLong(ServerKeys.KEY_MEETINGS));
						mTagsData.add(info);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					// ToastHelper.showToast(R.string.server_response_exception,
					// Toast.LENGTH_LONG);
				} finally {
					updateMyTagsListView();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updateMyTagsListView();
			}
		});

		request.get(ServerKeys.FULL_URL_GET_USER_TAG + "/" + mUserId + "/?pageindex=" + pageIndex + "&pagesize="
				+ ServerKeys.PAGE_SIZE + "&name=", null);
		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}

	private void showErrorDialog(int count) {
		if (count <= 0) {
			DialogUtils.showDialog(mParentActivity, R.string.wait, R.string.please_select_some_tags, R.string.ok, -1,
					null);
		} else if (count > 3) {
			DialogUtils.showDialog(mParentActivity, R.string.sorry, R.string.please_select_less_tags, R.string.ok, -1,
					null);
		}
	}

	public boolean checkUserInput() {
		int selectCount = 0;
		for (TagInfo tagInfo : mTagsData) {
			if (tagInfo.getState() == 1) {
				selectCount++;
			}
		}
		if (selectCount <= 0 || selectCount > 3) {
			showErrorDialog(selectCount);
			return false;
		}
		return true;
	}

	public List<TagInfo> getData() {
		List<TagInfo> tagInfos = new ArrayList<TagInfo>();
		for (TagInfo tagInfo : mTagsData) {
			if (tagInfo.getState() == 1) {
				tagInfos.add(tagInfo);
			}
		}

		return tagInfos;
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TagInfo info = mTagsData.get(position - 1);
		if (info.getState() == 0) {
			// is checked
			info.setState(1);
		} else {
			// is unchecked
			info.setState(0);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(TagInfo info) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putLong("TagID", info.getId());
		bundle.putLong("UserID", mUserId);
		intent.setClass(mParentActivity, TagProfileActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
