package com.meetisan.meetisan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class CreateActivity extends MapActivity {
	private static final String TAG = CreateActivity.class.getSimpleName();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_create);

		checkGoogleMapsInstalled(); // Is is necessary ?

		MapView mapView = (MapView) findViewById(R.id.map);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		MapController mMapController = mapView.getController();
		GeoPoint mGeoPoint = new GeoPoint((int) (30.659259 * 1000000), (int) (104.065762 * 1000000));
		mMapController.animateTo(mGeoPoint);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private void checkGoogleMapsInstalled() {
		if (!this.isGoogleMapsInstalled()) {
			Log.e(TAG, "You do not install Google Maps");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Install Google Map ?");
			builder.setCancelable(false);
			builder.setPositiveButton("Install", getGoogleMapsListener());
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

	private boolean isGoogleMapsInstalled() {
		try {
			getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	public OnClickListener getGoogleMapsListener() {
		return new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id=com.google.android.apps.maps"));
				startActivity(intent);

				finish();
			}
		};
	}
}
