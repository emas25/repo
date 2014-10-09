package com.example.eskulap.localization.mobile.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfacePanel extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder holder;
	private Context activityContext;
	private boolean isFinished;

	private Thread handle;

	public SurfacePanel(Context context, AttributeSet attrSet) {
		super(context, attrSet);

		holder = getHolder();
		holder.addCallback(this);
		activityContext = context;
		isFinished = false;

		// TODO Auto-generated constructor stub
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		HandleCanvas handleCanvas = new HandleCanvas(holder, activityContext,
				this);
		handle = new Thread(handleCanvas);
		handle.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		isFinished = true;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void doDraw(Canvas canvas)

	{
		if (((MapActivity) activityContext).isEnabled()) {
			canvas.drawColor(Color.GRAY);
			Picture picture = null;
			try {
				picture = ((MapActivity) activityContext).getPicture();
			} catch (NullPointerException ex) {
				;
			}
			if (picture != null)
				canvas.drawPicture(picture);
		}
	}

	public boolean isFinished() {
		return isFinished;
	}

}
