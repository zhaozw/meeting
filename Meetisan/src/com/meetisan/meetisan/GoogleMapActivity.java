package com.meetisan.meetisan;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
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
	private Location mLocation;
	private String mBestProvider;

	/** 标识是需要定位还是显示某一位置 */
	private boolean isLocation = true;
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
			isLocation = bundle.getBoolean("IsLocation");
			if (!isLocation) {
				mLatitude = bundle.getDouble("Latitude", 0.0f);
				mLongitude = bundle.getDouble("Longitude", 0.0f);
				mMeetTitle = bundle.getString("MeetTitle", null);
			}
			Log.d(TAG, "Location: " + isLocation + "; Latitude: " + mLatitude + "; Longitude: " + mLongitude
					+ "; Title: " + mMeetTitle);
		} else {
			Log.e(TAG, "Bundle is NULL");
			// assume is location
			isLocation = true;
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

			if (isLocation) {
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

	private void initTitleBar() {
		TextView mTitleTxt = (TextView) findViewById(R.id.tv_title_text);
		if (isLocation) {
			mTitleTxt.setText(R.string.set_meeting_location);
		} else {
			mTitleTxt.setText(R.string.meeting_location);
		}
		mTitleTxt.setVisibility(View.VISIBLE);
		ImageButton mBackBtn = (ImageButton) findViewById(R.id.btn_title_left);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GoogleMapActivity.this.finish();
			}
		});
		mBackBtn.setVisibility(View.VISIBLE);
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
		snippet = "Location: " + snippet;
		LatLng latLng = new LatLng(mLatitude, mLongitude);
		addMarkerOptions(mMap, latLng, snippet);
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
		mBestProvider = mLocationManager.getBestProvider(criteria, false);
		mLocation = mLocationManager.getLastKnownLocation(mBestProvider);
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

	private void addMarkerOptions(GoogleMap map, LatLng latLng, String snippet) {
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
	private String getAddressInfoByLocation(LatLng latLng) {
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

	@Override
	public void onMapClick(LatLng arg0) {
		String snippet = "Location: " + getAddressInfoByLocation(arg0);
		addMarkerOptions(mMap, arg0, snippet);
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
