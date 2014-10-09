package com.example.eskulap.localization.mobile.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogWindow {

	private String title;
	private String message;
	private String answer;
	private AlertDialog.Builder builder;
	private SVGAdapter svgAdapter;
	private Context context;

	public DialogWindow(Context context, String title, String message,
			SVGAdapter svgAdapter, boolean isQuestion) {
		this.title = title;
		this.message = message;
		this.svgAdapter = svgAdapter;
		this.context = context;
		builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		if (isQuestion) {
			builder.setPositiveButton("TAK",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							answer = "YES";
							dialog.dismiss();
							if (DialogWindow.this.message
									.contains("zmieniæ lokalizacjê obiektu")) {
								((MapActivity) DialogWindow.this.context)
										.setCreateNew(true);
							} else if (DialogWindow.this.message
									.contains("wylogowaæ?")) {
								((MapActivity) DialogWindow.this.context)
										.finish();
							} else if (DialogWindow.this.message
									.contains("odœwie¿yæ po³o¿enie")) {
								ServiceHandler handler = new ServiceHandler(
										(MapActivity) DialogWindow.this.context);
								OBJECTS objectInfo = handler.getObjectInfo(
										Integer.valueOf(DialogWindow.this.svgAdapter
												.getChosenObject()),
										((MapActivity) DialogWindow.this.context)
												.getUser(),
										((MapActivity) DialogWindow.this.context)
												.getPassword());
								String roomId = DialogWindow.this.svgAdapter
										.moveObject(objectInfo.OB_ID,
												objectInfo.OB_POSX,
												objectInfo.OB_POSY);
								objectInfo.OB_PL_ID = Integer.valueOf(roomId);
								DbAdapter db = new DbAdapter(
										DialogWindow.this.context);
								db.open();
								db.updateObject(objectInfo);
								db.close();
								((MapActivity) DialogWindow.this.context)
										.setCreateNew(true);

							}
						}

					});
			builder.setNegativeButton("NIE",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							answer = "NIE";
							dialog.dismiss();
							if (DialogWindow.this.message
									.contains("zmieniæ lokalizacjê obiektu")) {
								DialogWindow.this.svgAdapter
										.setElementOriginalPosition();
								((MapActivity) DialogWindow.this.context)
										.setCreateNew(true);
							} else if (DialogWindow.this.message
									.contains("wylogowaæ?")) {
								;
							} else if (DialogWindow.this.message
									.contains("odœwie¿yæ po³o¿enie")) {
								DialogWindow.this.svgAdapter
										.setElementOriginalPosition();
								((MapActivity) DialogWindow.this.context)
										.setCreateNew(true);
							}
						}
					});
		} else {
			builder.setNeutralButton("OK", null);
		}

	}

	public void showDialog() {
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public String getAnswer() {
		return answer;
	}
}
