package com.meetisan.meetisan.utils;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.View;

import com.meetisan.meetisan.R;

public class HttpBitmap {
	private static FinalBitmap finalBitmap;

	public HttpBitmap(Context context) {
		if (finalBitmap == null) {
			finalBitmap = FinalBitmap.create(context);
			finalBitmap.configLoadingImage(R.drawable.portrait_default);
			finalBitmap.configBitmapLoadThreadSize(3);
		}
	}

	public void displayBitmap(View imageView, String uri) {
		if (finalBitmap != null) {
			finalBitmap.display(imageView, uri);
		}
	}

	// public void onPause() {
	// if (finalBitmap != null) {
	// finalBitmap.onPause();
	// }
	// }
	//
	// public void onResume() {
	// if (finalBitmap != null) {
	// finalBitmap.onResume();
	// }
	// }
	//
	// public void onDestroy() {
	// if (finalBitmap != null) {
	// finalBitmap.onDestroy();
	// }
	// }
}
