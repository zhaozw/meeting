package com.meetisan.meetisan.view.meet;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.segmented.SegmentedGroup;
import com.meetisan.meetisan.R;
import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.MeetingAdapter;
import com.meetisan.meetisan.model.MeetingInfo;
import com.meetisan.meetisan.model.PeopleAdapter;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.model.TagInfo;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.view.dashboard.PersonProfileActivity;
import com.meetisan.meetisan.widget.CustomizePopupView;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;
import com.meetisan.meetisan.widget.SearchPanel;
import com.meetisan.meetisan.widget.SearchPanel.SearchListener;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.Mode;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshBase.OnRefreshListener2;
import com.meetisan.meetisan.widget.listview.refresh.PullToRefreshListView;

public class MeetActivity extends Activity {
	private static final String TAG = MeetActivity.class.getSimpleName();

	private RelativeLayout mListLayout;
	private CustomizePopupView mMeetingTagPopupView, mPeopleTagPopupView, mMeetingSortPopupView, mPeopleSortPopupView;
	private PullToRefreshListView mPullPeopleView, mPullMeetingsView;
	private ListView mPeopleListView, mMeetingsListView;
	private ImageButton mFilterBtn;
	private SearchPanel mSearchPanel;
	private RadioGroup mControlGroup;
	private List<PeopleInfo> mPeopleData = new ArrayList<PeopleInfo>();
	private List<MeetingInfo> mMeetingData = new ArrayList<MeetingInfo>();

	private PeopleAdapter mPeopleAdapter;
	private MeetingAdapter mMeetingAdapter;

	private int mListLayoutHeight = 0;
	private long mTotalPeople = 0, mTotalMeetings = 0;
	private long mUserId = -1;
	private float mLat = 0f, mLon = 0f;
	/** Meeting List order type */
	private int mMeetingOrder = OrderType.SORT_DISTANCE;
	/** People List order type */
	private int mPeopleOrder = OrderType.SORT_DISTANCE;
	/** People List Search Condition */
	private String mPeopleSearchFilter = "";
	/** Meet List Search Condition */
	private String mMeetSearchFilter = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_meet);
		Log.d(TAG, "On Create");

		PeopleInfo mUserInfo = UserInfoKeeper.readUserInfo(this);
		mUserId = mUserInfo.getId();
		mLat = mUserInfo.getLatitude();
		mLon = mUserInfo.getLongitude();
		Log.d(TAG, "Current Location: lat = " + mLat + "; lon = " + mLon);

		getPeoplesFromServer(1, mLat, mLon, mPeopleSearchFilter, true, true);
		getMeetingsFromServer(1, mMeetingOrder, mLat, mLon, mMeetSearchFilter, true, true);

		initView();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {

		SegmentedGroup mTagsGroup = (SegmentedGroup) findViewById(R.id.group_meet);
		mTagsGroup.setTintColor(getResources().getColor(R.color.segment_group_bg_check),
				getResources().getColor(R.color.segment_group_text_check));
		mTagsGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.radio_people) {
					mSearchPanel.setTextQuery(mPeopleSearchFilter);
					mPullMeetingsView.setVisibility(View.GONE);
					mPullPeopleView.setVisibility(View.VISIBLE);
				} else {
					mSearchPanel.setTextQuery(mMeetSearchFilter);
					mPullPeopleView.setVisibility(View.GONE);
					mPullMeetingsView.setVisibility(View.VISIBLE);
				}
			}
		});

		mSearchPanel = (SearchPanel) findViewById(R.id.search_panel);
		mSearchPanel.setSearchListener(new SearchListener() {

			@Override
			public void onClickSearchResult(String query) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onClear() {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(mPeopleSearchFilter)) {
					mPeopleSearchFilter = "";
					getPeoplesFromServer(1, mLat, mLon, mPeopleSearchFilter, true, false);
				}
				if (!TextUtils.isEmpty(mMeetSearchFilter)) {
					mMeetSearchFilter = "";
					getMeetingsFromServer(1, mMeetingOrder, mLat, mLon, mMeetSearchFilter, true, false);
				}
			}

			@Override
			public void onAutoSuggestion(String query) {
				// TODO Auto-generated method stub
				if (query == null) {
					return;
				}
				if (mPullPeopleView.isShown()) {
					if (!mPeopleSearchFilter.equals(query)) {
						mPeopleSearchFilter = query;
						getPeoplesFromServer(1, mLat, mLon, mPeopleSearchFilter, true, false);
					}
				} else {
					if (!mMeetSearchFilter.equals(query)) {
						mMeetSearchFilter = query;
						getMeetingsFromServer(1, mMeetingOrder, mLat, mLon, mMeetSearchFilter, true, false);
					}
				}
			}
		});
		mControlGroup = (RadioGroup) findViewById(R.id.group_control);
		mControlGroup.setOnCheckedChangeListener(new PopupCheckChangeListener());
		mFilterBtn = (ImageButton) findViewById(R.id.btn_filter);
		mFilterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSearchPanel.getVisibility() == View.VISIBLE) {
					mSearchPanel.setVisibility(View.GONE);
					mControlGroup.clearCheck();
					mControlGroup.setVisibility(View.VISIBLE);
					mFilterBtn.setImageResource(R.drawable.selector_button_search);
				} else {
					mControlGroup.setVisibility(View.GONE);
					mSearchPanel.setVisibility(View.VISIBLE);
					mFilterBtn.setImageResource(R.drawable.common_filter);
				}
			}
		});

		/** -----------Init People ListView-------------- */
		mPullPeopleView = (PullToRefreshListView) findViewById(R.id.list_people);
		TextView mPeopleEmptyView = (TextView) findViewById(R.id.txt_empty_people);
		mPullPeopleView.setEmptyView(mPeopleEmptyView);
		mPullPeopleView.setMode(Mode.BOTH);
		mPullPeopleView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(
						"Last Refresh: " + Util.getCurFormatDate());
				getPeoplesFromServer(1, mLat, mLon, mPeopleSearchFilter, true, false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(
						"Last Loading: " + Util.getCurFormatDate());
				int count = mPeopleListView.getCount() - 2; // reduce header and
															// footer item
				// Log.d(TAG, "-------total = " + mTotalPeople + "; count = " +
				// count);
				if (count < mTotalPeople) {
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getPeoplesFromServer(pageIndex, mLat, mLon, mPeopleSearchFilter, false, false);
				} else {
					ToastHelper.showToast("All the data has been loaded ");
					updatePeopleListView();
				}
			}
		});
		mPeopleListView = mPullPeopleView.getRefreshableView();
		registerForContextMenu(mPeopleListView);
		mPeopleAdapter = new PeopleAdapter(this, mPeopleData);
		mPeopleListView.setAdapter(mPeopleAdapter);
		mPeopleAdapter.notifyDataSetChanged();
		mPullPeopleView.setVisibility(View.VISIBLE);
		mPeopleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putLong("UserID", mPeopleData.get(arg2 - 1).getId());
				intent.setClass(MeetActivity.this, PersonProfileActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mPullPeopleView.setVisibility(View.VISIBLE);

		/** -----------Init Meetings ListView-------------- */
		mPullMeetingsView = (PullToRefreshListView) findViewById(R.id.list_meetings);
		TextView mMeetingsEmptyView = (TextView) findViewById(R.id.txt_empty_meetings);
		mPullMeetingsView.setEmptyView(mMeetingsEmptyView);
		mPullMeetingsView.setMode(Mode.BOTH);
		mPullMeetingsView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(
						"Last Refresh: " + Util.getCurFormatDate());
				getMeetingsFromServer(1, mMeetingOrder, mLat, mLon, mMeetSearchFilter, true, false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				refreshView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel(
						"Last Loading: " + Util.getCurFormatDate());
				int count = mMeetingsListView.getCount() - 2; // reduce header
																// and footer
																// item
				if (count < mTotalMeetings) {
					int pageIndex = count / ServerKeys.PAGE_SIZE + 1;
					getMeetingsFromServer(pageIndex, mMeetingOrder, mLat, mLon, mMeetSearchFilter, false, false);
				} else {
					ToastHelper.showToast("All the data has been loaded ");
					updateMeetingsListView();
				}
			}
		});
		mMeetingsListView = mPullMeetingsView.getRefreshableView();
		registerForContextMenu(mMeetingsListView);
		mMeetingAdapter = new MeetingAdapter(this, mMeetingData);
		mMeetingsListView.setAdapter(mMeetingAdapter);
		mMeetingsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putLong("MeetingID", mMeetingData.get(arg2 - 1).getId());
				bundle.putLong("UserID", mUserId);
				intent.setClass(MeetActivity.this, MeetProfileActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mMeetingAdapter.notifyDataSetChanged();
		mPullMeetingsView.setVisibility(View.GONE);

		mListLayout = (RelativeLayout) findViewById(R.id.layout_list);
		mListLayout.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if (mListLayoutHeight <= 0) {
					mListLayoutHeight = mListLayout.getMeasuredHeight();
					if (mListLayoutHeight > 0) {
						initPopupViewMenu(mListLayoutHeight);
					}
				}
				return true;
			}
		});
	}

	private void initPopupViewMenu(int height) {
		String[] sortMeetingItems = new String[] { "sort by distance", "sort by meeting time", "sort by create time" };
		mMeetingSortPopupView = new CustomizePopupView(MeetActivity.this, sortMeetingItems,
				new PopupItemClickListener(), new PopupItemDismissListener(), height);

		String[] sortPeopleItems = new String[] { "sort by distance" };
		mPeopleSortPopupView = new CustomizePopupView(MeetActivity.this, sortPeopleItems, new PopupItemClickListener(),
				new PopupItemDismissListener(), height);

		String[] tagItems = new String[] { "sort by tag name" };
		mMeetingTagPopupView = new CustomizePopupView(MeetActivity.this, tagItems, new PopupItemClickListener(),
				new PopupItemDismissListener(), height);

		mPeopleTagPopupView = new CustomizePopupView(MeetActivity.this, tagItems, new PopupItemClickListener(),
				new PopupItemDismissListener(), height);
	}

	private void updatePeopleListView() {
		mPeopleAdapter.notifyDataSetChanged();
		mPullPeopleView.onRefreshComplete();
	}

	private void updateMeetingsListView() {
		mMeetingAdapter.notifyDataSetChanged();
		mPullMeetingsView.onRefreshComplete();
	}

	private class PopupCheckChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			if (((RadioButton) findViewById(R.id.rb_tag)).isChecked()) {
				if (mMeetingsListView.isShown()) {
					mMeetingTagPopupView.showPopupDown(group);
				} else {
					mPeopleTagPopupView.showPopupDown(group);
				}
			} else {
				mMeetingTagPopupView.dismiss();
				mPeopleTagPopupView.dismiss();
			}

			if (((RadioButton) findViewById(R.id.rb_sort)).isChecked()) {
				if (mMeetingsListView.isShown()) {
					mMeetingSortPopupView.showPopupDown(group);
				} else {
					mPeopleSortPopupView.showPopupDown(group);
				}
			} else {
				mMeetingSortPopupView.dismiss();
				mPeopleSortPopupView.dismiss();
			}
		}
	}

	private class PopupItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mMeetingTagPopupView.isShowing()) {
				Log.d(TAG, "---Meeting Tag Item Click: " + position);
				mMeetingTagPopupView.dismiss();
			}
			if (mPeopleTagPopupView.isShowing()) {
				Log.d(TAG, "---People Tag Item Click: " + position);
				mPeopleTagPopupView.dismiss();
			}
			if (mMeetingSortPopupView.isShowing()) {
				Log.d(TAG, "---Meeting Sort Item Click: " + position);
				mMeetingSortPopupView.dismiss();
				mMeetingOrder = position; // Item Order equals Server API Order
				getMeetingsFromServer(1, mMeetingOrder, mLat, mLon, mMeetSearchFilter, true, true);
			}
			if (mPeopleSortPopupView.isShowing()) {
				Log.d(TAG, "---People Sort Item Click: " + position);
				mPeopleSortPopupView.dismiss();
				mPeopleOrder = position; // Item Order equals Server API Order
				// TODO.. Server API does not work
				// getPeoplesFromServer(1, mLat, mLon, true, true);
			}
		}

	}

	private class PopupItemDismissListener implements OnDismissListener {
		@Override
		public void onDismiss() {
			mControlGroup.clearCheck();
		}
	}

	private CustomizedProgressDialog mProgressDialog = null;

	/**
	 * get Peoples from server
	 * 
	 * @param pageIndex
	 *            load page index
	 * @param mLat
	 *            location
	 * @param mLon
	 *            location
	 * @param isRefresh
	 *            is refresh or load more
	 * @param isNeedsDialog
	 *            weather show progress dialog
	 */
	private void getPeoplesFromServer(int pageIndex, float mLat, float mLon, String mFilter, final boolean isRefresh,
			final boolean isNeedsDialog) {
		HttpRequest request = new HttpRequest();

		if (isNeedsDialog) {
			if (mProgressDialog == null) {
				mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);
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
						mPeopleData.clear();
					}

					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
					mTotalPeople = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);
					Log.d(TAG, "Total People Count: " + mTotalPeople);

					JSONArray peopleArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < peopleArray.length(); i++) {
						PeopleInfo peopleInfo = new PeopleInfo();
						JSONObject peopleJson = peopleArray.getJSONObject(i);

						JSONObject userJson = peopleJson.getJSONObject(ServerKeys.KEY_USER);
						peopleInfo.setId(userJson.getLong(ServerKeys.KEY_ID));
						peopleInfo.setEmail(userJson.getString(ServerKeys.KEY_EMAIL));
						if (!userJson.isNull(ServerKeys.KEY_NAME)) {
							peopleInfo.setName(userJson.getString(ServerKeys.KEY_NAME));
						}
						if (!userJson.isNull(ServerKeys.KEY_UNIVERSITY)) {
							peopleInfo.setUniversity(userJson.getString(ServerKeys.KEY_UNIVERSITY));
						}
						// Log.d(TAG, "---People Name: " + peopleInfo.getName()
						// + "; UNIV: " +
						// peopleInfo.getUniversity());
						if (!userJson.isNull(ServerKeys.KEY_AVATAR)) {
							peopleInfo.setAvatarUri(userJson.getString(ServerKeys.KEY_AVATAR));
						}
						peopleInfo.setDistance(userJson.getDouble(ServerKeys.KEY_DISTANCE));

						JSONArray tagArray = peopleJson.getJSONArray(ServerKeys.KEY_TAGS);
						for (int j = 0; j < tagArray.length(); j++) {
							TagInfo tagInfo = new TagInfo();
							JSONObject tagJson = tagArray.getJSONObject(j);
							tagInfo.setId(tagJson.getLong(ServerKeys.KEY_ID));
							if (!tagJson.isNull(ServerKeys.KEY_TITLE)) {
								tagInfo.setTitle(tagJson.getString(ServerKeys.KEY_TITLE));
							}
							peopleInfo.addTopTag(tagInfo);
						}

						mPeopleData.add(peopleInfo);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					// ToastHelper.showToast(R.string.server_response_exception,
					// Toast.LENGTH_LONG);
				} finally {
					updatePeopleListView();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updatePeopleListView();
			}
		});

		request.get(ServerKeys.FULL_URL_GET_UESR_LIST + "/" + mUserId + "/?pageindex=" + pageIndex + "&pagesize="
				+ ServerKeys.PAGE_SIZE + "&lat=" + mLat + "&lon=" + mLon + "&tagIDs=" + "&name=" + mFilter, null);

		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}

	/**
	 * get Meetings from server
	 * 
	 * @param pageIndex
	 *            load page index
	 * @param orderType
	 *            get list order by edition
	 * @param mLat
	 *            location
	 * @param mLon
	 *            location
	 * @param isRefresh
	 *            is refresh or load more
	 * @param isNeedsDialog
	 *            weather show progress dialog
	 */
	private void getMeetingsFromServer(int pageIndex, int orderType, float mLat, float mLon, String title,
			final boolean isRefresh, final boolean isNeedsDialog) {
		HttpRequest request = new HttpRequest();

		if (isNeedsDialog) {
			if (mProgressDialog == null) {
				mProgressDialog = new CustomizedProgressDialog(this, R.string.please_waiting);
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
						mMeetingData.clear();
					}

					JSONObject dataJson = (new JSONObject(result)).getJSONObject(ServerKeys.KEY_DATA);
					mTotalMeetings = dataJson.getLong(ServerKeys.KEY_TOTAL_COUNT);

					JSONArray peopleArray = dataJson.getJSONArray(ServerKeys.KEY_DATA_LIST);
					for (int i = 0; i < peopleArray.length(); i++) {
						MeetingInfo meetingInfo = new MeetingInfo();

						JSONObject meetJson = peopleArray.getJSONObject(i);

						JSONObject meetingJson = meetJson.getJSONObject(ServerKeys.KEY_MEETING);
						meetingInfo.setId(meetingJson.getLong(ServerKeys.KEY_ID));
						if (!meetingJson.isNull(ServerKeys.KEY_TITLE)) {
							meetingInfo.setTitle(meetingJson.getString(ServerKeys.KEY_TITLE));
						}
						meetingInfo.setDistance(meetingJson.getDouble(ServerKeys.KEY_DISTANCE));
						meetingInfo.setLogoUri(meetingJson.getString(ServerKeys.KEY_LOGO));

						JSONArray tagArray = meetJson.getJSONArray(ServerKeys.KEY_TAGS);
						for (int j = 0; j < tagArray.length(); j++) {
							TagInfo tagInfo = new TagInfo();
							JSONObject tagJson = tagArray.getJSONObject(j);
							tagInfo.setId(tagJson.getLong(ServerKeys.KEY_ID));
							if (!tagJson.isNull(ServerKeys.KEY_TITLE)) {
								tagInfo.setTitle(tagJson.getString(ServerKeys.KEY_TITLE));
							}
							meetingInfo.addTag(tagInfo);
						}

						mMeetingData.add(meetingInfo);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					// ToastHelper.showToast(R.string.server_response_exception,
					// Toast.LENGTH_LONG);
				} finally {
					updateMeetingsListView();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				if (isNeedsDialog) {
					mProgressDialog.dismiss();
				}
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				updateMeetingsListView();
			}
		});

		request.get(ServerKeys.FULL_URL_GET_MEET_LIST + "/?pageindex=" + pageIndex + "&pagesize="
				+ ServerKeys.PAGE_SIZE + "&ordertype=" + orderType + "&lat=" + mLat + "&lon=" + mLon + "&tagIDs="
				+ "&title=" + title, null);

		if (isNeedsDialog) {
			mProgressDialog.show();
		}
	}

	public class OrderType {
		/** sort by distance */
		public static final int SORT_DISTANCE = 0;
		/** sort by time */
		public static final int SORT_TIME = 1;
		/** sort by create time */
		public static final int SORT_CREATE_TIME = 2;
		/** sort by tag distance */
		public static final int TAG_DISTANCE = 3;
	}
}
