package com.meetisan.meetisan;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.meetisan.meetisan.utils.ToastHelper;

public class GoogleMapActivity extends Activity implements OnMapClickListener {
	private static final String TAG = GoogleMapActivity.class.getSimpleName();
	private GoogleMap mMap;
	/** 设置的Meeting位置 */
	private LatLng mSetLatLng;
	private String mAddressName;

	/** 标识是否是设置位置 */
	private boolean isSetLocation = true;
	/** 需要定位到位置的 纬度 */
	private double mLatitude = 0.0f;
	/** 需要定位到位置的 经度 */
	private double mLongitude = 0.0f;
	/** Meeting的Title */
	private String mMeetTitle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_map);
		try {
			Class.forName("com.google.android.maps.MapActivity");
		} catch (Exception e) {
			ToastHelper.showToast(R.string.can_not_use_google_map);
			return;
		}
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			isSetLocation = bundle.getBoolean("IsSetLocation");
			if (!isSetLocation) {
				mLatitude = bundle.getDouble("Latitude", 0.0f);
				mLongitude = bundle.getDouble("Longitude", 0.0f);
				mMeetTitle = bundle.getString("MeetTitle", null);
			}
			Log.d(TAG, "Location: " + isSetLocation + "; Latitude: " + mLatitude + "; Longitude: " + mLongitude
					+ "; Title: " + mMeetTitle);
		} else {
			Log.e(TAG, "Bundle is NULL");
			// assume is location
			isSetLocation = true;
		}
		initTitleBar();

		// Fragment mapFragment = new Fragment();
		// getSupportFragmentManager().beginTransaction().add(R.id.map,
		// mapFragment).commit();
		// mMap = ((SupportMapFragment) mapFragment).getMap();

		// SupportMapFragment fragment = new SupportMapFragment();
		// getSupportFragmentManager().beginTransaction().add(R.id.map,
		// fragment).commit();
		// mMap = fragment.getMap();
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		if (mMap != null) {
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			if (isSetLocation) {
				// mMap.setOnMarkerDragListener(this); // 设置拖动监听器
				mMap.setOnMapClickListener(this);// 设置点击地图监听器
				zoomToCurrentLocation(mMap); // 定位到用户当前位置
			} else {
				zoomToMeetingLocation(mMap, mLatitude, mLongitude);
			}
		} else {
			ToastHelper.showToast("Can not Load Google Maps on your device");
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {
		mSetLatLng = arg0;
		mAddressName = getAddressByLatLng(arg0);
		String snippet = "Location: " + mAddressName;
		setMarkerOptions(mMap, arg0, snippet);
	}

	private void initTitleBar() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		if (isSetLocation) {
			mTitleTxt.setText(R.string.set_meeting_location);
		} else {
			mTitleTxt.setText(R.string.meeting_location);
		}
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSetLocation) {
					if (mSetLatLng == null) {
						ToastHelper.showToast(R.string.error_set_meeting_location_empty);
					} else {
						Intent data = new Intent();
						data.putExtra("Latitude", mSetLatLng.latitude);
						data.putExtra("Longitude", mSetLatLng.longitude);
						data.putExtra("Address", mAddressName);
						Log.d(TAG, "Return Info: Latitude=" + mSetLatLng.latitude + "; Longitude="
								+ mSetLatLng.longitude + "; Address=" + mAddressName);
						setResult(RESULT_OK, data);
						GoogleMapActivity.this.finish();
					}
				} else {
					GoogleMapActivity.this.finish();
				}
			}
		});
		mBackBtn.setVisibility(View.VISIBLE);

		if (isSetLocation) {
			RelativeLayout mSearchLayout = (RelativeLayout) findViewById(R.id.layout_search);
			mSearchLayout.setVisibility(View.VISIBLE);
			Button mSearchBtn = (Button) findViewById(R.id.btn_search);
			final EditText mSearchTxt = (EditText) findViewById(R.id.search_edit_text);
			mSearchBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String locationName = mSearchTxt.getText().toString();
					if (locationName == null || TextUtils.isEmpty(locationName)) {
						ToastHelper.showToast("Please enter your search position!");
					} else {
						List<Address> addressList = getAddressByName(locationName);
						if (addressList.size() > 0) {
							Address address = addressList.get(0);
							LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
							mSetLatLng = latLng;
							mAddressName = getAddressByLatLng(latLng);
							String snippet = "Location: " + mAddressName;
							setMarkerOptions(mMap, latLng, snippet);
						} else {
							ToastHelper.showToast("Can not find you enter position!");
						}
					}
				}
			});
		}
	}

	/**
	 * Zoom to Meeting Location
	 * 
	 * @param mMap
	 * @param mLatitude
	 * @param mLongitude
	 */
	private void zoomToMeetingLocation(GoogleMap mMap, double mLatitude, double mLongitude) {
		if (mMap == null) {
			return;
		}

		String snippet = mMeetTitle;
		if (snippet == null) {
			snippet = "Do not set Meeting Title !";
		}
		snippet = "Title: " + snippet;
		LatLng latLng = new LatLng(mLatitude, mLongitude);
		setMarkerOptions(mMap, latLng, snippet);
	}

	/**
	 * zoom to current location
	 * 
	 * @param location
	 * @param isDraggable
	 * @param snippet
	 */
	private void zoomToCurrentLocation(GoogleMap mMap) {
		if (mMap == null) {
			return;
		}
		mMap.clear();

		LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String mBestProvider = mLocationManager.getBestProvider(criteria, false);
		Location mLocation = mLocationManager.getLastKnownLocation(mBestProvider);
		LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
		zoomCameraPosition(latLng);
	}

	private void zoomCameraPosition(LatLng latLng) {
		CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17)// set
				// scale
				.bearing(0) // Sets the orientation of the camera to east
				.tilt(30) // Sets the tilt of the camera to 30 degrees
				.build(); // Creates a CameraPosition from the builder
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	private void setMarkerOptions(GoogleMap map, LatLng latLng, String snippet) {
		map.clear();

		MarkerOptions markerOpt = new MarkerOptions();
		markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon));
		markerOpt.position(latLng);
		// markerOpt.draggable(isDraggable); // 设置标记是否可拖动
		markerOpt.title("Meetisan");
		markerOpt.anchor(0.5f, 0.5f);// set current location to center
		markerOpt.snippet(snippet);
		markerOpt.visible(true);
		map.addMarker(markerOpt);

		zoomCameraPosition(latLng);
	}

	/**
	 * get address location details
	 * 
	 * @param latLng
	 * @return location details string
	 */
	private String getAddressByLatLng(LatLng latLng) {
		if (latLng == null) {
			return null;
		}
		String addressInfo = null;

		try {
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

			StringBuilder builder = new StringBuilder();
			if (address.size() > 0) {
				Address adsLocation = address.get(0);
				String line0 = adsLocation.getAddressLine(0); // getAddressLine(0)表示国家
				if (line0 != null) {
					builder.append(line0).append(" ");
				}
				String line1 = adsLocation.getAddressLine(1); // getAddressLine(1)表示精确到某个区
				if (line1 != null) {
					builder.append(line1).append(" ");
				}
				String line2 = adsLocation.getAddressLine(2); // getAddressLine(2)表示精确到具体的街道
				if (line2 != null) {
					builder.append(line2);
				}

				addressInfo = builder.toString();
			} else {
				addressInfo = "Location information not found!";
			}
		} catch (IOException e) {
			e.printStackTrace();
			addressInfo = null;
		}

		return addressInfo;
	}

	private List<Address> getAddressByName(String locationName) {
		if (locationName == null) {
			return null;
		}

		List<Address> address = null;
		try {
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			address = geocoder.getFromLocationName(locationName, 5);

			for (Address add : address) {
				Log.d(TAG, "GetFromLocationName: Latitude: " + add.getLatitude() + "; Longitude: " + add.getLongitude());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return address;
	}
}

// private void checkGoogleMapsInstalled() {
// if (!this.isGoogleMapsInstalled()) {
// Log.e(TAG, "You do not install Google Maps");
// AlertDialog.Builder builder = new AlertDialog.Builder(this);
// builder.setMessage("Install Google Map ?");
// builder.setCancelable(false);
// builder.setPositiveButton("Install", getGoogleMapsListener());
// AlertDialog dialog = builder.create();
// dialog.show();
// }
// }
//
// private boolean isGoogleMapsInstalled() {
// try {
// getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
// return true;
// } catch (PackageManager.NameNotFoundException e) {
// return false;
// }
// }
//
// public OnClickListener getGoogleMapsListener() {
// return new OnClickListener() {
// @Override
// public void onClick(DialogInterface dialog, int which) {
// Intent intent = new Intent(Intent.ACTION_VIEW,
// Uri.parse("market://details?id=com.google.android.apps.maps"));
// startActivity(intent);
//
// finish();
// }
// };
// }
