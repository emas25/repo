package com.example.eskulap.localization.mobile.app;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ObjectActivity extends ActionBarActivity {

	private String[] objectTypes;
	private Integer[] objectTypesValues;
	private String[] places;
	private Integer[] placesValues;
	private Spinner placesSpinner;
	private Spinner objectTypesSpinner;
	private HashMap<Integer, EditText> parameters;
	private int operationType;
	private EditText nameEditText;
	private Button addObjectButton;
	private Button backButton;
	private boolean dataChanged = false;
	private String user;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_object);
		user = getIntent().getExtras().getString("user");
		password = getIntent().getExtras().getString("password");
		getPlaces(getIntent().getExtras().getInt("mapId"));
		getObjectTypes();
		placesSpinner = (Spinner) findViewById(R.id.placeSpinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, places);
		placesSpinner.setAdapter(adapter);
		objectTypesSpinner = (Spinner) findViewById(R.id.ObjectTypeSpinner);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, objectTypes);
		objectTypesSpinner.setAdapter(adapter);
		nameEditText = (EditText)findViewById(R.id.nameEditText);
		addObjectButton = (Button)findViewById(R.id.addObjectButton); 
		addObjectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(operationType == MapActivity.SHOW_OBJECT){
					dataChanged = true;
					
				}
				else if(operationType == MapActivity.ADD_IN_POINT){
					Integer newObjectId = -1;
					newObjectId = 135;
					DbAdapter db = new DbAdapter(ObjectActivity.this);
					db.open();
					db.insertObject(newObjectId,objectTypesValues[objectTypesSpinner.getSelectedItemPosition()],nameEditText.getText().toString(),placesValues[placesSpinner.getSelectedItemPosition()],"123452",getIntent().getExtras().getFloat("X"),getIntent().getExtras().getFloat("Y"));
					db.close();
					Intent returnIntent = new Intent();
					returnIntent.putExtra("objectId", newObjectId);
					setResult(MapActivity.OBJECT_ADDED,returnIntent);
					finish();
					/*if((newObjectId = addObject()) != -1){   //dodac dodawanie obiektu na lokalnej mapie poprzez czytanei z serwera info o obiekcie
						ServiceHandler handler = new ServiceHandler(ObjectActivity.this);
						String upd_date = handler.getObjectUpdDate(newObjectId, user, password);
						DbAdapter db = new DbAdapter(ObjectActivity.this);
						db.open();
						db.insertObject(newObjectId,objectTypesValues[objectTypesSpinner.getSelectedItemPosition()],nameEditText.getText().toString(),placesValues[placesSpinner.getSelectedItemPosition()],upd_date,getIntent().getExtras().getFloat("X"),getIntent().getExtras().getFloat("Y"));
						db.close();
						Intent returnIntent = new Intent();
						returnIntent.putExtra("objectId", newObjectId);
						setResult(MapActivity.OBJECT_ADDED,returnIntent);
						finish();
					}
					else{
						Toast.makeText(ObjectActivity.this, "Nie uda³o siê dodaæ obiektu!",Toast.LENGTH_LONG).show();
					}*/
				}
			}
		});
		backButton = (Button)findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent returnIntent = new Intent();
				setResult(MapActivity.NORMAL_RETURN);
				finish();
			}
		});
		createFields();
		getSupportActionBar().setTitle("Dodawanie obiektu");
		operationType = getIntent().getExtras().getInt("requestCode");
		if (operationType == MapActivity.ADD_IN_POINT) {
			placesSpinner.setSelection(getIndex(placesValues,
							Integer.parseInt(getIntent().getExtras().getString(
									"room"))));
			placesSpinner.setClickable(false);
			
		}
		else if(operationType == MapActivity.SHOW_OBJECT){
			readData(Integer.parseInt(getIntent().getExtras().getString("object")));
			getSupportActionBar().setTitle("Edycja Obiektu");
			addObjectButton.setText("Zapisz zmiany");
			backButton.setText("Wróæ");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.object, menu);
		return true;
	}

	private int getIndex(Integer[] tab, Integer value) {
		for (int i = 0; i < tab.length; i++) {
			if (tab[i] == value)
				return i;
		}
		return -999;
	}
	
	private void readData(int objectId){
		DbAdapter db = new DbAdapter(this);
		db.open();
		HashMap<String, Object> values = db.getObject(objectId);
		nameEditText.setText((String)values.get("name"));
		placesSpinner.setSelection(getIndex(placesValues,
				(Integer)values.get("place")));
		objectTypesSpinner.setSelection(getIndex(objectTypesValues,
				(Integer)values.get("type")));
		
		//String ti = (String)values.get("date");
		//Long mil = Long.valueOf(ti);
		//Date date = new Date(mil.longValue());
		//Toast.makeText(this, date.toString(), Toast.LENGTH_LONG).show();
		values.clear();
		HashMap<Integer, String> infoValues = new HashMap<Integer, String>();
		infoValues = db.getObjectInfo(objectId);
		db.close();
		Iterator<Integer> it = infoValues.keySet().iterator();
		Integer key;
		while(it.hasNext()){
			key = it.next();
			parameters.get(key).setText(infoValues.get(key));
		}
	}
	
	private Integer addObject(){
		ServiceHandler handler = new ServiceHandler(this);
		return handler.addObject(objectTypesValues[objectTypesSpinner.getSelectedItemPosition()],nameEditText.getText().toString(),placesValues[placesSpinner.getSelectedItemPosition()],new Date(),getIntent().getExtras().getFloat("X"),getIntent().getExtras().getFloat("Y"));
	}

	private void getObjectTypes() {
		DbAdapter db = new DbAdapter(this);
		db.open();
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		map = db.getAllObjectTypes(user);
		objectTypes = new String[map.size()];
		Iterator<String> it = map.keySet().iterator();
		int index = 0;
		while (it.hasNext()) {
			objectTypes[index] = it.next();
			index++;
		}

		objectTypesValues = new Integer[map.size()];
		for (int i = 0; i < map.size(); i++) {
			objectTypesValues[i] = map.get((String) objectTypes[i]);
		}
		db.close();
	}

	private void getPlaces(Integer mapId) {
		DbAdapter db = new DbAdapter(this);
		db.open();
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		map = db.getPlaces(mapId);
		places = new String[map.size()];
		Iterator<String> it = map.keySet().iterator();
		int index = 0;
		while (it.hasNext()) {
			places[index] = it.next();
			index++;
		}

		placesValues = new Integer[map.size()];
		for (int i = 0; i < map.size(); i++) {
			placesValues[i] = map.get((String) places[i]);
		}
		db.close();
	}

	private void createFields() {
		DbAdapter db = new DbAdapter(this);
		db.open();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map = db.getParameters();
		Iterator<String> it = map.keySet().iterator();
		
		// RelativeLayout layout = (RelativeLayout)View.inflate(this,
		// R.layout.activity_object, null);

		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		parameters = new HashMap<Integer, EditText>();
		while (it.hasNext()) {
			TableRow tableRow = new TableRow(this);
			TextView textView = new TextView(this);
			textView.setText(it.next());
			textView.setId((int) System.currentTimeMillis());
			textView.setTextAppearance(this, android.R.attr.textAppearanceLarge);
			// lp.addRule(RelativeLayout.BELOW,R.id.ObjectTypeSpinner);
			/*
			 * if(index == 0){
			 * lp.addRule(RelativeLayout.BELOW,R.id.ObjectTypeSpinner); } else{
			 * lp.addRule(RelativeLayout.BELOW,parametersList[index -
			 * 1].getId()); }
			 */
			// lp.setMargins(0, index * 100 + 100, 0, 0);
			// lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
			// textView.setLayoutParams(lp);
			// layout.addView(textView);
			tableRow.addView(textView);
			EditText editText = new EditText(this);
			editText.setId((int) System.currentTimeMillis());
			// lp = new
			// RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			tableLayout.addView(tableRow);
			// lp.addRule(RelativeLayout.BELOW,textView.getId());
			/*
			 * if(index > 0){
			 * lp.addRule(RelativeLayout.BELOW,R.id.ObjectTypeSpinner);
			 * lp.setMargins(0, index * 100 + 130, 0, 0); } else{
			 * lp.addRule(RelativeLayout.BELOW,R.id.ObjectTypeTextView);
			 * //lp.setMargins(0, 10, 0, 0); }
			 */
			tableRow = new TableRow(this);
			// lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
			// editText.setLayoutParams(lp);
			parameters.put(map.get(textView.getText()), editText);
			tableRow.addView(editText);
			tableLayout.addView(tableRow);
			// layout.addView(editText);
			//parametersList[index] = editText;
			// places[index] = it.next();
			
		}
		db.close();
	}

}
