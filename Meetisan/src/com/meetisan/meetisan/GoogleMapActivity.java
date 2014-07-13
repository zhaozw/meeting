package com.meetisan.meetisan;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapActivity extends FragmentActivity {
	private static final String TAG = GoogleMapActivity.class.getSimpleName();
	private GoogleMap mMap;
	private CameraPosition cameraPosition;
	private LocationManager locationManager;
	private Location location;
	private String bestProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_map);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		bestProvider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(bestProvider);

		// Fragment mapFragment = new Fragment();
		// getSupportFragmentManager().beginTransaction().add(R.id.map,
		// mapFragment).commit();
		// mMap = ((SupportMapFragment) mapFragment).getMap();

		// SupportMapFragment fragment = new SupportMapFragment();
		// getSupportFragmentManager().beginTransaction().add(R.id.map,
		// fragment).commit();
		// mMap = fragment.getMap();

		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		updateMapLocation(location, "Meetisan");

		// 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N
		locationManager.requestLocationUpdates(bestProvider, 3 * 1000, 8, new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// 当Provider的状态改变时
			}

			@Override
			public void onProviderEnabled(String provider) {
				// 当GPS LocationProvider可用时，更新位置
				location = locationManager.getLastKnownLocation(provider);
			}

			@Override
			public void onProviderDisabled(String provider) {
				updateMapLocation(null, "Meetisan");
			}

			@Override
			public void onLocationChanged(Location location) {
				// 当GPS定位信息发生改变时，更新位置
				updateMapLocation(location, "Meetisan");
			}
		});
	}

	/**
	 * Update Google Map to new location
	 * 
	 * @param location
	 *            new location
	 * @param tips
	 *            location tips
	 */
	private void updateMapLocation(Location location, String tips) {
		if (location == null) {
			Log.e(TAG, "Locate Current Location Failed");
			return;
		}

		mMap.clear();
		MarkerOptions markerOpt = new MarkerOptions();
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		markerOpt.position(latLng);
		markerOpt.draggable(false);
		if (tips != null) {
			markerOpt.title(tips);
		}
		markerOpt.visible(true);
		markerOpt.anchor(0.5f, 0.5f);// set current location to center
		markerOpt.snippet("Population: 4,137,400");
		markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon));
		mMap.addMarker(markerOpt);
		// set camera to set position
		cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17)// set
																				// scale
				.bearing(0) // Sets the orientation of the camera to east
				.tilt(30) // Sets the tilt of the camera to 30 degrees
				.build(); // Creates a CameraPosition from the builder
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
