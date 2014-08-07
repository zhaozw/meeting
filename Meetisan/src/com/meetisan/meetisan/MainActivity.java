package com.meetisan.meetisan;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.notifications.NotificationsActivity;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.view.create.CreateActivity;
import com.meetisan.meetisan.view.dashboard.DashboardActivity;
import com.meetisan.meetisan.view.meet.MeetActivity;
import com.meetisan.meetisan.view.tags.TagsActivity;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnCheckedChangeListener {
	// private static final String LOG_TAG = MainActivity.class.getSimpleName();

	public static final String LOG_ACTIVITY_SERVICE = "=====MainActivity====";

	private static final int[] RADIO_BTN_IDS = new int[] { R.id.rb_create, R.id.rb_meet, R.id.rb_tags,
			R.id.rb_dashboard, R.id.rb_notifications };

	private static final String TAB_1 = "create";
	private static final String TAB_2 = "meet";
	private static final String TAB_3 = "tags";
	private static final String TAB_4 = "dashboard";
	private static final String TAB_5 = "notifications";
	private static final String[] TABS = { TAB_1, TAB_2, TAB_3, TAB_4, TAB_5 };
	// private static final int[] TABS_TITLE = { R.string.title_create,
	// R.string.title_meet,
	// R.string.title_tags,
	// R.string.title_dashboard, R.string.title_notification };

	private Intent mCreateIntent, mMeetIntent, mTagsIntent, mDashboardIntent, mNotificationsIntent;
	private Intent[] mIntents = new Intent[TABS.length];
	private TabHost mHost;
	private RadioGroup mRadioGroup;

	// is start from Let's Meet action
	private boolean isMeetPerson = false;
	private long meetPersonID = -1;

	// private TextView mTitleTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			isMeetPerson = bundle.getBoolean("IsMeetPerson", false);
			meetPersonID = bundle.getLong("PersonID", -1L);
		}
		// if do not get meet PersonID
		if (isMeetPerson && meetPersonID < 0) {
			isMeetPerson = false;
		}

		syncUserLocationToServer();

		setup();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void setup() {
		mCreateIntent = new Intent(this, CreateActivity.class);
		if (isMeetPerson && meetPersonID >= 0) {
			Bundle bundle = this.getIntent().getExtras();
			mCreateIntent.putExtras(bundle);
		}
		mMeetIntent = new Intent(this, MeetActivity.class);
		mTagsIntent = new Intent(this, TagsActivity.class);
		mDashboardIntent = new Intent(this, DashboardActivity.class);
		mNotificationsIntent = new Intent(this, NotificationsActivity.class);
		mIntents = new Intent[] { mCreateIntent, mMeetIntent, mTagsIntent, mDashboardIntent, mNotificationsIntent };
		initTab();
	}

	private void initTab() {
		// mTitleTxt = (TextView) findViewById(R.id.txt_title);
		// mTitleTxt.setVisibility(View.VISIBLE);

		mHost = this.getTabHost();
		for (int i = 0; i < TABS.length; i++) {
			mHost.addTab(mHost.newTabSpec(TABS[i]).setIndicator(TABS[i]).setContent(mIntents[i]));
		}

		mRadioGroup = (RadioGroup) findViewById(R.id.rg_tabgroup);
		if (!isMeetPerson) {
			mHost.setCurrentTab(1); // for first visible MeetActivity
		} else {
			mRadioGroup.check(RADIO_BTN_IDS[0]);
		}

		mRadioGroup.setOnCheckedChangeListener(this);
		// mTitleTxt.setText(TABS_TITLE[mHost.getCurrentTab()]);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		for (int i = 0; i < RADIO_BTN_IDS.length; i++) {
			if (checkedId == RADIO_BTN_IDS[i]) {
				mHost.setCurrentTabByTag(TABS[i]);
				// mTitleTxt.setText(TABS_TITLE[i]);
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		System.exit(0);
	}

	private void syncUserLocationToServer() {
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(false);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		String bestProvider = manager.getBestProvider(criteria, true);
		if (bestProvider != null) {
			Location location = manager.getLastKnownLocation(bestProvider);
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			HttpRequest request = new HttpRequest();
			// request.setOnHttpRequestListener(new OnHttpRequestListener() {
			//
			// @Override
			// public void onSuccess(String url, String result) {
			// // TODO Auto-generated method stub
			// Log.d("--------", result);
			// }
			//
			// @Override
			// public void onFailure(String url, int errorNo, String errorMsg) {
			// // TODO Auto-generated method stub
			// Log.e("--------", errorMsg);
			// }
			// });
			// Save current location information to SharedPreferences for use
			UserInfoKeeper.writeUserInfo(MainActivity.this, UserInfoKeeper.KEY_USER_LAT, (float) latitude);
			UserInfoKeeper.writeUserInfo(MainActivity.this, UserInfoKeeper.KEY_USER_LON, (float) longitude);

			long mUserID = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_USER_ID, -1L);
			request.post(ServerKeys.FULL_URL_UPDATE_LOCATION + "/" + mUserID + "/?lat=" + latitude + "&lon="
					+ longitude, null);
		} else {
			ToastHelper.showToast(R.string.failed_get_location);
		}
	}
}
