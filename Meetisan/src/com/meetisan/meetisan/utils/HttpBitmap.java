package com.meetisan.meetisan.utils;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import net.tsz.afinal.bitmap.display.Displayer;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.meetisan.meetisan.R;

public class HttpBitmap {
	private static FinalBitmap finalBitmap;

	public HttpBitmap(Context context) {
		if (finalBitmap == null) {
			finalBitmap = FinalBitmap.create(context);
			// finalBitmap.configLoadingImage(R.drawable.portrait_person_default);
			finalBitmap.configBitmapLoadThreadSize(3);
			finalBitmap.configDisplayer(new Displayer() {

				@Override
				public void loadFailDisplay(View imageView, Bitmap bitmap) {
					// TODO Auto-generated method stub
					// ((ImageView)
					// imageView).setImageResource(R.drawable.icon_portrait_load_failed);
				}

				@Override
				public void loadCompletedisplay(View imageView, Bitmap bitmap, BitmapDisplayConfig config) {
					((ImageView) imageView).setImageBitmap(bitmap);
				}
			});
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
