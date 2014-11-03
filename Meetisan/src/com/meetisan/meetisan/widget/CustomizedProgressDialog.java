package com.meetisan.meetisan.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.meetisan.meetisan.R;

public class CustomizedProgressDialog extends ProgressDialog {
	private Context context = null;
	private int msgId = R.string.loading;
	private DialogStyle style = DialogStyle.PROGRESS;

	public enum DialogStyle {
		PROGRESS, ERROR, OK
	}

	public CustomizedProgressDialog(Context context, int msgId) {
		super(context);
		this.context = context;
		this.msgId = msgId;
	}

	public CustomizedProgressDialog(Context context, int msgId, DialogStyle style) {
		super(context);
		this.context = context;
		this.msgId = msgId;
		this.style = style;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (style == DialogStyle.PROGRESS) {
			setContentView(R.layout.dialog_progress);
		} else if (style == DialogStyle.ERROR) {
			setContentView(R.layout.dialog_error);
		} else if (style == DialogStyle.OK) {
			setContentView(R.layout.dialog_ok);
		}
		setScreenBrightness();
		this.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				// ImageView image = (ImageView)
				// CustomizedProgressDialog.this.findViewById(R.id.loading_imgeview);
				// AnimationDrawable animationDrawable = (AnimationDrawable)
				// image.getBackground();
				// animationDrawable.stop();
				// animationDrawable.start();
				// Animation animation = AnimationUtils.loadAnimation(context,
				// R.anim.progress_indicator);
				// image.startAnimation(animation);

				TextView tipsTxt = (TextView) CustomizedProgressDialog.this.findViewById(R.id.txt_tips);
				if (tipsTxt != null) {
					tipsTxt.setText(context.getResources().getString(msgId));
				}
			}
		});
		this.setCancelable(false);

		if (style == DialogStyle.ERROR || style == DialogStyle.OK) {
			new Thread() {
				@Override
				public void run() {
					long start = System.currentTimeMillis();
					while (System.currentTimeMillis() - start <= 1000) {
						try {
							sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					CustomizedProgressDialog.this.dismiss();
				}
			}.start();
		}
	}

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}

	private void setScreenBrightness() {
		Window window = getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.5f;
		window.setAttributes(lp);
	}
}
