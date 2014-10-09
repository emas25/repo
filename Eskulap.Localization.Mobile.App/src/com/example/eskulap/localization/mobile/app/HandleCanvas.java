package com.example.eskulap.localization.mobile.app;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class HandleCanvas implements Runnable {

	boolean finished;
	Canvas canvas;
	SurfaceHolder surfaceHolder;
	Context context;
	SurfacePanel surfacePanel;
	private float skala;
	private float moveX;
	private float moveY;
	
	public HandleCanvas(SurfaceHolder sholder, Context ctx, SurfacePanel spanel){
		surfaceHolder = sholder;
		surfacePanel = spanel;
		context = ctx;
		finished = false;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!surfacePanel.isFinished()){
			canvas = surfaceHolder.lockCanvas();
			if(canvas != null){
				skala = ((MapActivity)context).getScale();
				moveX = ((MapActivity)context).getMoveX();
				moveY = ((MapActivity)context).getMoveY();
				canvas.translate(moveX, moveY);
				canvas.scale(skala, skala);
				//Log.d("macierz",canvas.getMatrix().toString());
				((MapActivity)context).setSvgMap(canvas);
				surfacePanel.doDraw(canvas);
				surfaceHolder.unlockCanvasAndPost(canvas);
				//Log.d("qwerty",String.valueOf(canvas.getHeight()));
			}
			
		}
		
	}
	
	public void setFinished(boolean value){
		finished = value;
	}

}
