package com.example.eskulap.localization.mobile.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.example.eskulap.localization.mobile.app.exceptions.ObjectNotFoundException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MapActivity extends ActionBarActivity implements OnTouchListener {

	private static int INVALID_POINTER_ID = -1;
	public static final int ADD_IN_POINT = 0;
	public static final int SHOW_OBJECT = 1;
	public static final int NORMAL_RETURN = 2;
	public static final int OBJECT_ADDED = 3;

	private Canvas svgMap;
	Picture picture;
	private ScaleGestureDetector scaleDetector;
	private GestureDetector gestureListener;
	private float scale = 1.0f;
	private float translateX = 0.0f;
	private float translateY = 0.0f;
	private float lastTouchX;
	private float lastTouchY;
	private int activePointerId;
	private int[] layoutLocation;
	private boolean zooming;
	private boolean moving;
	SVGAdapter svgAdapter;
	private MapActivity context;
	private boolean createNew = true;
	private String[] objectTypesArray;
	private Integer[] objectTypesArrayValues;
	private boolean[] chosenObjectTypes;
	private String[] placeTypesArray;
	private Integer[] placeTypesArrayValues;
	private boolean[] chosenPlaceTypes;
	private boolean enabled = true;
	private Menu mainMenu;
	private String selectedObject;
	private String selectedRoom;
	private float selectedX;
	private float selectedY;
	private String user;
	private String password;
	private boolean mainMap;
	private int screenWidth;
	private int screenHeight;
	private String currentMap;
	private Paint objectColor;
	private boolean beginMove = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Display display = getWindowManager().getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();

		context = this;
		layoutLocation = new int[2];
		createNew = true;
		zooming = false;
		moving = false;
		mainMap = true;
		user = getIntent().getExtras().getString("user");
		password = getIntent().getExtras().getString("password");

		getSupportActionBar().setTitle("I piêtro");
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// text = (TextView)findViewById(R.id.editText1);
		loadMap("map-2-head.svg","","");
		scaleDetector = new ScaleGestureDetector(this,
				new ScaleGestureListener());
		gestureListener = new GestureDetector(this, new GestureListener());

		setContentView(R.layout.map_activity);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_menu, menu);
		this.mainMenu = menu;
		return true;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		MenuItem item = mainMenu.findItem(R.id.addObject);
		item.setVisible(false);
		item = mainMenu.findItem(R.id.showObject);
		item.setVisible(false);
		item = mainMenu.findItem(R.id.objectTypesFiltr);
		item.setVisible(true);
		item = mainMenu.findItem(R.id.placeTypesFiltr);
		item.setVisible(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AlertDialog.Builder builder;
		switch (item.getItemId()) {
		case R.id.start_qr_read:
			IntentIntegrator integrator = new IntentIntegrator(this);
			integrator.initiateScan();
			return true;
		case R.id.objectTypesFiltr:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Filtruj wg typów");
			builder.setMultiChoiceItems(objectTypesArray, chosenObjectTypes,
					new DialogInterface.OnMultiChoiceClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which,
								boolean isChecked) {
							// TODO Auto-generated method stub

						}
					});
			builder.setPositiveButton("Filtruj",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String selectedObjectIds = "";
							for (int i = 0; i < objectTypesArrayValues.length; i++) {
								if (chosenObjectTypes[i])
									selectedObjectIds += objectTypesArrayValues[i]
											+ ",";
							}
							if (selectedObjectIds.length() > 0)
								selectedObjectIds = selectedObjectIds
										.substring(0,
												selectedObjectIds.length() - 1);
							if (selectedObjectIds.length() == 0)
								selectedObjectIds = "-999";

							String selectedPlaceIds = "";
							for (int i = 0; i < placeTypesArrayValues.length; i++) {
								if (chosenPlaceTypes[i])
									selectedPlaceIds += placeTypesArrayValues[i]
											+ ",";
							}
							if (selectedPlaceIds.length() > 0)
								selectedPlaceIds = selectedPlaceIds.substring(
										0, selectedPlaceIds.length() - 1);
							if (selectedPlaceIds.length() == 0)
								selectedPlaceIds = "-999";
							enabled = false;
							loadMap(currentMap,selectedObjectIds,selectedPlaceIds);
							enabled = true;
							dialog.cancel();
						}
					});
			builder.show();
			return true;
		case R.id.placeTypesFiltr:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Filtruj wg pomieszczeñ");
			builder.setMultiChoiceItems(placeTypesArray, chosenPlaceTypes,
					new DialogInterface.OnMultiChoiceClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which,
								boolean isChecked) {
							// TODO Auto-generated method stub

						}
					});
			builder.setPositiveButton("Filtruj",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String selectedPlaceIds = "";
							for (int i = 0; i < placeTypesArrayValues.length; i++) {
								if (chosenPlaceTypes[i])
									selectedPlaceIds += placeTypesArrayValues[i]
											+ ",";
							}
							if (selectedPlaceIds.length() > 0)
								selectedPlaceIds = selectedPlaceIds.substring(
										0, selectedPlaceIds.length() - 1);
							if (selectedPlaceIds.length() == 0)
								selectedPlaceIds = "-999";

							String selectedObjectIds = "";
							for (int i = 0; i < objectTypesArrayValues.length; i++) {
								if (chosenObjectTypes[i])
									selectedObjectIds += objectTypesArrayValues[i]
											+ ",";
							}
							if (selectedObjectIds.length() > 0)
								selectedObjectIds = selectedObjectIds
										.substring(0,
												selectedObjectIds.length() - 1);
							if (selectedObjectIds.length() == 0)
								selectedObjectIds = "-999";

							
							enabled = false;
							loadMap(currentMap,selectedObjectIds,selectedPlaceIds);
							enabled = true;
							dialog.cancel();
						}
					});
			builder.show();
			return true;
		case R.id.addObject: {

			Intent intent = new Intent(context, ObjectActivity.class);
			intent.putExtra("mapId", svgAdapter.getMapId());
			intent.putExtra("X", selectedX);
			intent.putExtra("Y", selectedY);
			intent.putExtra("room", selectedRoom);
			intent.putExtra("user", user);
			intent.putExtra("password", password);
			intent.putExtra("requestCode", MapActivity.ADD_IN_POINT);
			startActivityForResult(intent, MapActivity.ADD_IN_POINT);
			return true;
		}
		case R.id.showObject: {
			Intent intent = new Intent(context, ObjectActivity.class);
			intent.putExtra("object", selectedObject);
			intent.putExtra("mapId", svgAdapter.getMapId());
			intent.putExtra("requestCode", MapActivity.SHOW_OBJECT);
			intent.putExtra("user", user);
			intent.putExtra("password", password);
			startActivityForResult(intent, MapActivity.SHOW_OBJECT);
			return true;
		}
		}
		MenuItem item2 = mainMenu.findItem(R.id.addObject);
		item2.setVisible(false);
		item2 = mainMenu.findItem(R.id.showObject);
		item2.setVisible(false);
		item2 = mainMenu.findItem(R.id.objectTypesFiltr);
		item2.setVisible(true);
		item2 = mainMenu.findItem(R.id.placeTypesFiltr);
		item2.setVisible(true);
		return super.onOptionsItemSelected(item);
	}

	private void loadMap(String name, String objectIds, String placeIds) {
		AssetManager assetManager = this.getResources().getAssets();
		svgAdapter = null;
		try {
			svgAdapter = new SVGAdapter(assetManager.open(name), this);
			svgAdapter.refreshObjects(objectIds, placeIds, user);
			if(!name.equals(currentMap)){
				scale = 1.0f;
				translateX = 0.0f;
				translateY = 0.0f;
				getObjectTypes();
				getPlaceTypes();
			}
			InputStream input = svgAdapter.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input));
			String str;
			while ((str = reader.readLine()) != null) {
				Log.d("tre", str);
			}
		} catch (IOException e) {
			// TODO Auto-generated cseratch block
			e.printStackTrace();
		}
		currentMap = name;
		picture = svgAdapter.getPicture(true);
		createNew = true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setContentView(R.layout.map_activity);
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MapActivity.SHOW_OBJECT: {

		}
		case MapActivity.ADD_IN_POINT: {
			if (resultCode == MapActivity.OBJECT_ADDED) {
				Integer objId = data.getExtras().getInt("objectId");
				DbAdapter db = new DbAdapter(context);
				db.open();
				HashMap<String,Object> object = db.getObject(objId);
				db.close();
				svgAdapter.addObject(object, objId);
				markObject(objId);
			}
			return;
		}
		case IntentIntegrator.REQUEST_CODE: {
			IntentResult scanResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, data);
			Integer objectId = null;
			if (scanResult != null) {
				objectId = Integer.parseInt(scanResult.getContents());
				DbAdapter db = new DbAdapter(this);
				db.open();
				boolean enabled;
				try {
					enabled = db.isVisibleForUser(objectId, user);
				} catch (ObjectNotFoundException e) {
					DialogWindow dialog = new DialogWindow(this, "B³¹d",
							"Szukany obiekt nie istnieje w bazie danych",
							svgAdapter, false);
					dialog.showDialog();
					e.printStackTrace();
					return;
				}
				if (enabled) {
					
					
					Integer mapId = db.getObjectMapId(objectId);
					String map = "map-" + mapId + "-head.svg";
					if (!map.equals(currentMap))
						loadMap(map,"","");
					markObject(objectId);

				} else {
					DialogWindow dialog = new DialogWindow(
							this,
							"Ostrze¿enie",
							"Nie masz uprawnieñ do odczytania informacji o tym obiekcie!",
							svgAdapter, false);
					dialog.showDialog();
				}
				db.close();
			}
			return;
		}
		}
	}
	
	@Override
	public void onBackPressed() {
	    if(!mainMap){
	    	mainMap = true;
	    	loadMap("map-2-head.svg","","");
	    }
	}
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) 
	    {
	        DialogWindow dialog = new DialogWindow(this,"Ostrze¿enie","Czy na pewno chcesz siê wylogowaæ?",svgAdapter,true);
	        dialog.showDialog();
	        return true;
	    }
	    return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean value) {
		super.onWindowFocusChanged(true);
		SurfaceView view = (SurfaceView) findViewById(R.id.surfaceActivity1);
		view.getLocationOnScreen(layoutLocation);

	}

	public class ScaleGestureListener extends SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			scale *= detector.getScaleFactor();

			scale = Math.max(0.1f, Math.min(scale, 6.0f));

			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			if(!moving)
				zooming = true;
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			zooming = false;
		}
	}

	public class GestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onDoubleTapEvent(MotionEvent event) {
			if (!zooming) {

				selectedX = ((event.getX() - layoutLocation[0]) - translateX)
						/ scale;
				selectedY = ((event.getY() - layoutLocation[1]) - translateY)
						/ scale;

				MenuItem item;
				if (svgAdapter.getChosenObject() != null) {
					selectedObject = svgAdapter.getChosenObject();
					item = mainMenu.findItem(R.id.addObject);
					item.setVisible(false);
					item = mainMenu.findItem(R.id.showObject);
					item.setVisible(true);
					item = mainMenu.findItem(R.id.objectTypesFiltr);
					item.setVisible(false);
					item = mainMenu.findItem(R.id.placeTypesFiltr);
					item.setVisible(false);
					context.openOptionsMenu();
				} else if (svgAdapter.getChosenRoom() != null) {
					selectedRoom = svgAdapter.getChosenRoom();
					item = mainMenu.findItem(R.id.addObject);
					item.setVisible(true);
					item = mainMenu.findItem(R.id.showObject);
					item.setVisible(false);
					item = mainMenu.findItem(R.id.objectTypesFiltr);
					item.setVisible(false);
					item = mainMenu.findItem(R.id.placeTypesFiltr);
					item.setVisible(false);
					context.openOptionsMenu();
				}
			};

			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent event) {

			/*
			 * float realX;
			 * 
			 * float[] matrix = new float[9];
			 * svgMap.getMatrix().getValues(matrix); realX = ((event.getX() -
			 * layoutLocation[0]) - translateX) / scale; realY = ((event.getY()
			 * - layoutLocation[1]) - translateY) / scale;
			 * 
			 * svgAdapter.selectAtPoint(realX, realY); createNew = true;
			 */
			if (mainMap) {
				mainMap = false;
				float realX;
				float realY;
				realX = ((event.getX() - layoutLocation[0]) - translateX)
						/ scale;
				realY = ((event.getY() - layoutLocation[1]) - translateY)
						/ scale;
				svgAdapter.selectAtPoint(realX, realY);
				if (svgAdapter.getChosenRoom() != null) {
					String map = "map-" + svgAdapter.getChosenRoom()
							+ "-head.svg";
					loadMap(map,"","");
				}
			}

			return true;
		}

		@Override
		public void onLongPress(MotionEvent event) {
			if (!zooming) {

				selectedX = ((event.getX() - layoutLocation[0]) - translateX)
						/ scale;
				selectedY = ((event.getY() - layoutLocation[1]) - translateY)
						/ scale;

				MenuItem item;
				if (svgAdapter.getChosenObject() != null) {
					selectedObject = svgAdapter.getChosenObject();
					item = mainMenu.findItem(R.id.addObject);
					item.setVisible(false);
					item = mainMenu.findItem(R.id.showObject);
					item.setVisible(true);
					item = mainMenu.findItem(R.id.objectTypesFiltr);
					item.setVisible(false);
					item = mainMenu.findItem(R.id.placeTypesFiltr);
					item.setVisible(false);
					context.openOptionsMenu();
				} else if (svgAdapter.getChosenRoom() != null) {
					selectedRoom = svgAdapter.getChosenRoom();
					item = mainMenu.findItem(R.id.addObject);
					item.setVisible(true);
					item = mainMenu.findItem(R.id.showObject);
					item.setVisible(false);
					item = mainMenu.findItem(R.id.objectTypesFiltr);
					item.setVisible(false);
					item = mainMenu.findItem(R.id.placeTypesFiltr);
					item.setVisible(false);
					context.openOptionsMenu();
				}

				/*
				 * item = menu.findItem(R.id.addObject); item.setVisible(false);
				 * item = menu.findItem(R.id.objectTypesFiltr);
				 * item.setVisible(true); item =
				 * menu.findItem(R.id.placeTypesFiltr); item.setVisible(true);
				 */
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		scaleDetector.onTouchEvent(event);
		if (zooming)
			return true;
		gestureListener.onTouchEvent(event);
		// final int action = MotionEventCompat.getActionIndex(event);
		final int action = event.getAction();

		float realX = ((event.getX() - layoutLocation[0]) - translateX) / scale;
		float realY = ((event.getY() - layoutLocation[1]) - translateY) / scale;

		try {
			switch (action) {
			case MotionEvent.ACTION_DOWN: {

				final int pointerIndex = MotionEventCompat
						.getActionIndex(event);
				final float x = MotionEventCompat.getX(event, pointerIndex);
				final float y = MotionEventCompat.getY(event, pointerIndex);

				svgAdapter.selectAtPoint(realX, realY);
				if (svgAdapter.getMarkedObject() != null
						&& svgAdapter.getMarkedObject() != svgAdapter
								.getChosenObject())
					svgAdapter.unMarkObject(svgAdapter.getMarkedObject());
				createNew = true;
				if (svgAdapter.getChosenObject() != null && !zooming) {
					moving = true;
					// nale¿y zarezerwowaæ obiekt w bazie danych !!!!!
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(100);
					DbAdapter db = new DbAdapter(context);
					db.open();
					//objectColor = db.getObjectColor(svgAdapter.getChosenObject());
					//svgAdapter.hideObject(svgAdapter.getChosenObject());
					db.close();
				}

				lastTouchX = x;
				lastTouchY = y;
				activePointerId = MotionEventCompat.getPointerId(event,
						pointerIndex);
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				final int pointerIndex = MotionEventCompat.findPointerIndex(
						event, activePointerId);

				final float x = MotionEventCompat.getX(event, pointerIndex);
				final float y = MotionEventCompat.getY(event, pointerIndex);

				float dx = x - lastTouchX;
				float dy = y - lastTouchY;

				if (!zooming) {
					if (!moving) {
						translateX += dx;
						translateY += dy;
						lastTouchX = x;
						lastTouchY = y;
					} else {
						/*if(!beginMove){
							if(!movable(svgAdapter.getChosenObject())){
								DialogWindow dialog = new DialogWindow(context,"B³¹d","Nie mo¿na przenieœæ obiektu poniewa¿ zosta³ zmodyfikowany przez innego u¿ytkownika! Czy odœwie¿yæ po³o¿enie obiektu?",svgAdapter,true);
								dialog.showDialog();
								moving = false;
								return true;
							}
						}*/
						if (svgAdapter.selectRoom(realX, realY)) {
							svgAdapter.moveSelectedObject(dx / scale, dy
									/ scale);
							createNew = true;
							lastTouchX = x;
							lastTouchY = y;
						}
						beginMove = true;
					}
				}

				break;
			}
			case MotionEvent.ACTION_UP: {
				activePointerId = INVALID_POINTER_ID;
				if (moving) { // zapisanie w bazie informacji o przeniesieniu
					String roomName = svgAdapter.getElementName(
							((lastTouchX - layoutLocation[0]) - translateX)
									/ scale,
							((lastTouchY - layoutLocation[1]) - translateY)
									/ scale, "room");
					/*if(!movable(svgAdapter.getChosenObject())){
						DialogWindow dialog = new DialogWindow(context,"B³¹d","Nie mo¿na przenieœæ obiektu poniewa¿ zosta³ zmodyfikowany przez innego u¿ytkownika! Czy odœwie¿yæ po³o¿enie obiektu?",svgAdapter,true);
						dialog.showDialog();
						moving = false;
						return true;
					}*/
					if (roomName != null
							&& !roomName.equals(svgAdapter.getOriginalRoom())) {
						DialogWindow dialog = new DialogWindow(context,
								"Ostrze¿enie", "Czy chcesz"
										+ " zmieniæ lokalizacjê obiektu? Nast¹pi zmiana pomieszczenia z \""
										+ "Gabinet Doktora Housa"
										+ "\" na \"" + "Sala operacji chirurgicznych" + "\"",
								svgAdapter, true);
						dialog.showDialog();

						/*
						 * komentarz pozostawiæ dopóki nie bêd¹ zrealizowane
						 * operacje na bazie !!!!!!
						 * if(dialog.getAnswer().equals("YES")){ // TODO zmiana
						 * pomieszczenia //zapisanie polozenia w bazie g³ównej
						 * oraz lokalnej odnoœnie //po³o¿enia oraz nowego
						 * pomieszczenia } else{
						 * svgAdapter.setElementOriginalPosition(); }
						 */
					}
					float [] coordinates = svgAdapter.getObjectCoordinates(svgAdapter.getChosenObject());
					String roomId = svgAdapter.getElementName(coordinates[0], coordinates[1], "room");
					OBJECTS objectInfo = new OBJECTS();
					objectInfo.OB_ID = Integer.valueOf(svgAdapter.getChosenObject());
					objectInfo.OB_PL_ID = Integer.valueOf(roomId);
					objectInfo.OB_POSX = coordinates[0];
					objectInfo.OB_POSY = coordinates[1];
					DbAdapter db = new DbAdapter(context);
					db.open();
					HashMap<String, Object> map = db.getObject(Integer.valueOf(svgAdapter.getChosenObject()));
					objectInfo.OB_NAME = (String) map.get("name");
					objectInfo.OB_OT_ID = (Integer) map.get("type");
					db.close();
					ServiceHandler handler = new ServiceHandler(context);
					handler.updateObject(objectInfo);
					// zwolnienie blokady
					beginMove = false;
					moving = false;
				}

				break;
			}
			case MotionEvent.ACTION_POINTER_UP: {
				activePointerId = INVALID_POINTER_ID;
				break;
			}
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			return true;
		}

		return true;
	}
	
	private boolean movable(String objectId){
		ServiceHandler handler = new ServiceHandler(context);
		String realDate = handler.getObjectUpdDate(Integer.valueOf(objectId), user, password);
		DbAdapter db = new DbAdapter(context);
		db.open();
		Long localDate = db.getObjectUpdDate(Integer.valueOf(objectId));
		db.close();
		//Toast.makeText(context, realDate + " " + String.valueOf(localDate), Toast.LENGTH_LONG).show();
		if(!realDate.equals(String.valueOf(localDate)))
			return false;
		else
			return true;
	}

	private void markObject(Integer objId) {
		svgAdapter.setMarkedObject(objId);
		svgAdapter.markObject(String.valueOf(objId));
		createNew = true;
		float[] coordinates = svgAdapter.getObjectCoordinates(String
				.valueOf(objId));
		translateX = (screenWidth - layoutLocation[0]) / 2 - coordinates[0]
				* scale;
		translateY = (screenHeight - layoutLocation[1]) / 2 - coordinates[1]
				* scale;
		svgAdapter.selectAtPoint(coordinates[0], coordinates[1]);
		svgAdapter.setChoosenObject(null);
	}

	public void getPlaceTypes() {
		DbAdapter db = new DbAdapter(context);
		db.open();
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		map = db.getPlaceTypes(svgAdapter.getMapId());
		placeTypesArray = new String[map.size()];
		Iterator<String> it = map.keySet().iterator();
		int index = 0;
		while (it.hasNext()) {
			placeTypesArray[index] = it.next();
			index++;
		}

		placeTypesArrayValues = new Integer[map.size()];
		for (int i = 0; i < map.size(); i++) {
			placeTypesArrayValues[i] = map.get((String) placeTypesArray[i]);
		}
		chosenPlaceTypes = new boolean[map.size()];
		for (int i = 0; i < map.size(); i++)
			chosenPlaceTypes[i] = true;
		db.close();
	}

	public void getObjectTypes() {
		DbAdapter db = new DbAdapter(context);
		db.open();
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		map = db.getObjectTypes(svgAdapter.getMapId(), user);
		objectTypesArray = new String[map.size()];
		Iterator<String> it = map.keySet().iterator();
		int index = 0;
		while (it.hasNext()) {
			objectTypesArray[index] = it.next();
			index++;
		}

		objectTypesArrayValues = new Integer[map.size()];
		for (int i = 0; i < map.size(); i++) {
			objectTypesArrayValues[i] = map.get((String) objectTypesArray[i]);
		}
		chosenObjectTypes = new boolean[map.size()];
		for (int i = 0; i < map.size(); i++)
			chosenObjectTypes[i] = true;
		db.close();
	}

	public void setSvgMap(Canvas canvas) {
		this.svgMap = canvas;
	}

	public Picture getPicture() {
		if (createNew) {
			createNew = false;
			return svgAdapter.getPicture(true);
		} else
			return svgAdapter.getPicture(false);
	}

	public float getScale() {
		return scale;
	}

	public float getMoveX() {
		return translateX;
	}

	public float getMoveY() {
		return translateY;
	}

	public SVGAdapter getSvgAdapter() {
		return svgAdapter;
	}

	public boolean createNew() {
		return createNew;
	}

	public void setCreateNew(boolean value) {
		createNew = value;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {

		return false;
	}
	
	public String getUser(){
		return user;
	}
	
	public String getPassword(){
		return password;
	}

}
