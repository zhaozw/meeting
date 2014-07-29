package com.meetisan.meetisan;

import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.meetisan.meetisan.database.UserInfoKeeper;
import com.meetisan.meetisan.model.PeopleInfo;
import com.meetisan.meetisan.notifications.NotificationsActivity;
import com.meetisan.meetisan.utils.HttpRequest;
import com.meetisan.meetisan.utils.HttpRequest.OnHttpRequestListener;
import com.meetisan.meetisan.utils.ServerKeys;
import com.meetisan.meetisan.utils.ToastHelper;
import com.meetisan.meetisan.utils.Util;
import com.meetisan.meetisan.view.create.CreateActivity;
import com.meetisan.meetisan.view.dashboard.DashboardActivity;
import com.meetisan.meetisan.view.meet.MeetActivity;
import com.meetisan.meetisan.view.tags.TagsActivity;
import com.meetisan.meetisan.widget.CustomizedProgressDialog;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnCheckedChangeListener {
	// private static final String LOG_TAG = MainActivity.class.getSimpleName();

	public static final String LOG_ACTIVITY_SERVICE = "=====MainActivity====";

	private static final int[] RADIO_BTN_IDS = new int[] { R.id.rb_create, R.id.rb_meet,
			R.id.rb_tags, R.id.rb_dashboard, R.id.rb_notifications };

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

	private PeopleInfo mUserInfo;

	// private TextView mTitleTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		setup();

		syncUserInfoFromServer();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void setup() {
		mCreateIntent = new Intent(this, CreateActivity.class);
		mMeetIntent = new Intent(this, MeetActivity.class);
		mTagsIntent = new Intent(this, TagsActivity.class);
		mDashboardIntent = new Intent(this, DashboardActivity.class);
		mNotificationsIntent = new Intent(this, NotificationsActivity.class);
		mIntents = new Intent[] { mCreateIntent, mMeetIntent, mTagsIntent, mDashboardIntent,
				mNotificationsIntent };
		initTab();
	}

	private void syncUserInfoFromServer() {
		mUserInfo = UserInfoKeeper.readUserInfo(this);
		String email = mUserInfo.getEmail();
		String pwd = mUserInfo.getPwd();
		if (email == null || pwd == null) {
			reLogin();
			return;
		}
		getUserInfoFromServer(email, pwd);
	}

	private void initTab() {
		// mTitleTxt = (TextView) findViewById(R.id.txt_title);
		// mTitleTxt.setVisibility(View.VISIBLE);

		mHost = this.getTabHost();
		for (int i = 0; i < TABS.length; i++) {
			mHost.addTab(mHost.newTabSpec(TABS[i]).setIndicator(TABS[i]).setContent(mIntents[i]));
		}

		mRadioGroup = (RadioGroup) findViewById(R.id.rg_tabgroup);
		mRadioGroup.setOnCheckedChangeListener(this);
		// mTitleTxt.setText(TABS_TITLE[mHost.getCurrentTab()]);
	}

	/**
	 * re login if needs
	 */
	private void reLogin() {
		ToastHelper.showToast(R.string.login_expired, Toast.LENGTH_LONG);
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		MainActivity.this.finish();
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

	private CustomizedProgressDialog mProgressDialog = null;

	private void getUserInfoFromServer(String email, String pwd) {
		HttpRequest request = new HttpRequest();

		if (mProgressDialog == null) {
			mProgressDialog = new CustomizedProgressDialog(this, R.string.loading_userinfo);
		} else {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}

		request.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				mProgressDialog.dismiss();
				JSONObject json;
				try {
					json = new JSONObject(result);
					JSONObject data = json.getJSONObject(ServerKeys.KEY_DATA);
					mUserInfo.setId(data.getLong(ServerKeys.KEY_ID));
					mUserInfo.setEmail(data.getString(ServerKeys.KEY_EMAIL));
					mUserInfo.setPwd(data.getString(ServerKeys.KEY_PASSWORD));
					if (!data.isNull(ServerKeys.KEY_NAME)) {
						mUserInfo.setName(data.getString(ServerKeys.KEY_NAME));
					}
					UserInfoKeeper.writeUserInfo(MainActivity.this, mUserInfo);

					if (!data.isNull(ServerKeys.KEY_AVATAR)) {
						String base64Data = data.getString(ServerKeys.KEY_AVATAR);
						if (base64Data != null) {
							MyApplication.setmLogoBitmap(Util.base64ToBitmap(base64Data));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.app_occurred_exception);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				mProgressDialog.dismiss();
				ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
				reLogin();
			}
		});

		Map<String, String> data = new TreeMap<String, String>();
		data.put(ServerKeys.KEY_EMAIL, email);
		data.put(ServerKeys.KEY_PASSWORD, pwd);
		String registrationID = JPushInterface.getRegistrationID(getApplicationContext());
		if (registrationID != null) {
			data.put(ServerKeys.KEY_REG_ID, registrationID);
		}
		request.post(ServerKeys.FULL_URL_LOGIN, data);
		mProgressDialog.show();
	}
}
