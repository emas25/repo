package com.example.eskulap.localization.mobile.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.eskulap.localization.mobile.app.exceptions.ObjectNotFoundException;

public class DbAdapter {
	private static final int DB_VERSION = 4;
	private static final String DB_NAME = "lokalizacjaObiektow.db";

	private static String SETTINGS_TABLE;
	private static String PARAMETRES_TABLE;
	private static String MAPS_TABLE;
	private static String COLORS_TABLE;
	private static String OBJECT_TYPES_TABLE;
	private static String PLACE_TYPES_TABLE;
	private static String PLACES_TABLE;
	private static String OBJECTS_TABLE;
	private static String OBJECT_INFO_TABLE;
	private static String USERS_TABLE;
	private static String USER_LOGS_TABLE;
	private static String ROLES_TABLE;

	public SQLiteDatabase db;
	private Context context;
	private DataBaseHelper dbHelper;

	public DbAdapter(Context context) {
		this.context = context;
	}

	private static class DataBaseHelper extends SQLiteOpenHelper {

		private Context context;

		public DataBaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			AssetManager am = this.context.getResources().getAssets();
			Properties properties = new Properties();
			InputStream input;

			try {
				input = am.open("database.properties");
				properties.load(input);
				// DB_VERSION =
				// Integer.parseInt(properties.getProperty("version"));
				SETTINGS_TABLE = properties.getProperty("settings_table");
				PARAMETRES_TABLE = properties.getProperty("parametres_table");
				ROLES_TABLE = properties.getProperty("roles_table");
				MAPS_TABLE = properties.getProperty("maps_table");
				COLORS_TABLE = properties.getProperty("colors_table");
				OBJECT_TYPES_TABLE = properties
						.getProperty("object_types_table");
				PLACE_TYPES_TABLE = properties.getProperty("place_types_table");
				PLACES_TABLE = properties.getProperty("places_table");
				OBJECTS_TABLE = properties.getProperty("objects_table");
				OBJECT_INFO_TABLE = properties.getProperty("object_info_table");
				USERS_TABLE = properties.getProperty("users_table");
				USER_LOGS_TABLE = properties.getProperty("user_logs_table");

				input.close();
				db.execSQL(SETTINGS_TABLE);
				db.execSQL(PARAMETRES_TABLE);
				db.execSQL(ROLES_TABLE);
				db.execSQL(COLORS_TABLE);
				db.execSQL(MAPS_TABLE);
				db.execSQL(OBJECT_TYPES_TABLE);
				db.execSQL(PLACE_TYPES_TABLE);
				db.execSQL(PLACES_TABLE);
				db.execSQL(OBJECTS_TABLE);
				db.execSQL(OBJECT_INFO_TABLE);
				db.execSQL(USERS_TABLE);
				db.execSQL(USER_LOGS_TABLE);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS settings");
			db.execSQL("DROP TABLE IF EXISTS parameters");
			db.execSQL("DROP TABLE IF EXISTS colors");
			db.execSQL("DROP TABLE IF EXISTS maps");
			db.execSQL("DROP TABLE IF EXISTS object_types");
			db.execSQL("DROP TABLE IF EXISTS place_types");
			db.execSQL("DROP TABLE IF EXISTS places");
			db.execSQL("DROP TABLE IF EXISTS objects");
			db.execSQL("DROP TABLE IF EXISTS object_info");
			db.execSQL("DROP TABLE IF EXISTS users");
			db.execSQL("DROP TABLE IF EXISTS user_logs");
			db.execSQL("DROP TABLE IF EXISTS roles");
			onCreate(db);
		}
	}

	public DbAdapter open() {
		dbHelper = new DataBaseHelper(context, DB_NAME, null, DB_VERSION);
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLException e) {
			db = dbHelper.getReadableDatabase();
		}
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public void insert() {
		ContentValues newTodoValues = new ContentValues();
		newTodoValues.put("us_id", "1");
		newTodoValues.put("us_name", "admin");
		newTodoValues.put("us_password", "admin");
		db.insert("users", null, newTodoValues);

	}

	public void insertMap() {
		ContentValues newTodoValues = new ContentValues();
		newTodoValues.put("mp_id", 1);
		newTodoValues.put("mp_name", "Pierwsza mapa");
		newTodoValues.put("mp_building", "Budynek A");
		newTodoValues.put("mp_floor", 1);
		db.insert("maps", null, newTodoValues);

	}

	public void updateTable(ContentValues values, String tableName) {
		db.insert(tableName, null, values);
	}

	public void cleanData() {
		db.delete("parameters", null, null);
		db.delete("user_logs", null, null);
		db.delete("users", null, null);
		db.delete("object_info", null, null);
		db.delete("objects", null, null);
		db.delete("places", null, null);
		db.delete("place_types", null, null);
		db.delete("object_types", null, null);
		db.delete("colors", null, null);
		db.delete("roles", null, null);
		// db.delete("maps", null, null);
		db.delete("settings", null, null);
	}

	public boolean login(String name, String password) {

		Cursor result;
		result = db.query("users", new String[] { "us_password" },
				"us_name = ?", new String[] { name }, null, null, null);

		if (result.getCount() == 0) {
			result.close();
			return false;
		} else {
			result.moveToFirst();
			if (result.getString(0).equals(password)) {
				result.close();
				return true;
			} else {
				result.close();
				return false;
			}
		}
	}

	public HashMap<String, Integer> getPlaceTypes(Integer mapId) {
		HashMap<String, Integer> placeTypes = new HashMap<String, Integer>();
		String sqlQuery = "select pt.pt_type, pt.pt_id from place_types pt where exists( "
				+ "select 1 from objects, places where ob_pl_id = pl_id and pl_pt_id = pt.pt_id "
				+ "and pl_mp_id = " + mapId + ")";
		// String sqlQuery = "select ot_type, ot_id from object_types";
		Cursor result = db.rawQuery(sqlQuery, null);
		if (result.getCount() > 0) {
			do {
				result.moveToNext();
				placeTypes.put(result.getString(0), result.getInt(1));
			} while (!result.isLast());
		}
		result.close();
		return placeTypes;
	}

	public HashMap<String, Integer> getPlaces(int mapId) {
		HashMap<String, Integer> places = new HashMap<String, Integer>();
		String sqlQuery = "select pl_name, pl_id from places where pl_mp_id = "
				+ mapId;
		Cursor result = db.rawQuery(sqlQuery, null);
		if (result.getCount() > 0) {
			do {
				result.moveToNext();
				places.put(result.getString(0), result.getInt(1));
			} while (!result.isLast());
		}
		result.close();
		return places;
	}
	
	public HashMap<String, Integer> getAllObjectTypes(String user) {
		HashMap<String, Integer> places = new HashMap<String, Integer>();
		String sqlQuery = "select ot_type, ot_id from object_types where" + 
		"(select rl_level from roles, users " + 
				"where rl_id = us_rl_id and us_name = \"" + user + "\") <=" + 
				"(select rl_level from roles where rl_id = ot_rl_id)";
		Cursor result = db.rawQuery(sqlQuery, null);
		if (result.getCount() > 0) {
			do {
				result.moveToNext();
				places.put(result.getString(0), result.getInt(1));
			} while (!result.isLast());
		}
		result.close();
		return places;
	}
	
	public HashMap<String, Integer> getParameters(){
		HashMap<String, Integer> parameters = new HashMap<String, Integer>();
		String sqlQuery = "select pr_name, pr_id from parameters";
		Cursor result = db.rawQuery(sqlQuery, null);
		if (result.getCount() > 0) {
			do {
				result.moveToNext();
				parameters.put(result.getString(0), result.getInt(1));
			} while (!result.isLast());
		}
		result.close();
		return parameters;
	}
	
	public HashMap<Integer, String> getObjectInfo(Integer objectId){
		HashMap<Integer, String> objectInfo = new HashMap<Integer, String>();
		String sqlQuery = "select oi_pr_id, oi_value from object_info where oi_ob_id = "  +
						  objectId;
		Cursor result = db.rawQuery(sqlQuery, null);
		if(result.getCount() > 0){
			do{
				result.moveToNext();
				objectInfo.put(result.getInt(0), result.getString(1));
			} while(!result.isLast());
		}
		result.close();
		return objectInfo;
	}
	
	public boolean insertObject(Integer objId, Integer type, String name, Integer place, String date, float x, float y){
		ContentValues content = new ContentValues();
		content.put("ob_id", objId);
		content.put("ob_ot_id", type);
		content.put("ob_name", name);
		content.put("ob_pl_id", place);
		content.put("ob_upd_date", Long.valueOf(date));
		content.put("ob_posx", x);
		content.put("ob_posy", y);
		db.insert("objects", null, content);
		return true;
	}
	
	public boolean updateObject(OBJECTS objectInfo){
		ContentValues content = new ContentValues();
		content.put("ob_posx", objectInfo.OB_POSX);
		content.put("ob_posy", objectInfo.OB_POSY);
		content.put("ob_pl_id", objectInfo.OB_PL_ID);
		content.put("ob_upd_date", Long.valueOf(objectInfo.OB_UPD_DATE));
		db.update("objects", content, "ob_id = " + objectInfo.OB_ID, null);
		return true;
	}
	
	public Long getObjectUpdDate(Integer objectId){
		HashMap<String, Object> objectInfo = getObject(objectId);
		return (Long) objectInfo.get("date");
	}
	
	public HashMap<String, Object> getObject(Integer objectId){
		HashMap<String, Object> object = new HashMap<String, Object>();
		String sqlQuery = "select ob_name, ob_ot_id, ob_pl_id, ob_posx, ob_posy, " +
						  "ob_upd_date from objects where ob_id = " + objectId;
		Cursor result = db.rawQuery(sqlQuery, null);
		if(result.getCount() > 0){
				result.moveToNext();
				object.put("name", result.getString(0));
				object.put("type", result.getInt(1));
				object.put("place", result.getInt(2));
				object.put("x", result.getFloat(3));
				object.put("y", result.getFloat(4));
				object.put("date", result.getLong(5));
		}
		result.close();
		return object;
	}
	

	public HashMap<String, Integer> getObjectTypes(Integer mapId, String user) {
		HashMap<String, Integer> objectTypes = new HashMap<String, Integer>();
		String sqlQuery = "select ot.ot_type, ot.ot_id from object_types ot where exists( "
				+ "select 1 from objects, places where ob_pl_id = pl_id and ob_ot_id = ot.ot_id "
				+ "and pl_mp_id = " + mapId + ") and (select rl_level from roles, users " + 
				"where rl_id = us_rl_id and us_name = \"" + user + "\") <=" + 
				"(select rl_level from roles where rl_id = ot.ot_rl_id)";
		// String sqlQuery = "select ot_type, ot_id from object_types";
		Cursor result = db.rawQuery(sqlQuery, null);
		if (result.getCount() > 0) {
			do {
				result.moveToNext();
				objectTypes.put(result.getString(0), result.getInt(1));
			} while (!result.isLast());
		}
		result.close();
		return objectTypes;
	}
	
	public boolean isVisibleForUser(Integer objectId, String user) throws ObjectNotFoundException{
		String sqlQuery = "select 1 from objects where ob_id = " + objectId;
		Cursor result = db.rawQuery(sqlQuery, null);
		if(result.getCount() < 1)
			throw new ObjectNotFoundException();
		sqlQuery = "select 1 from objects, object_types, roles where " + 
				"ob_ot_id = ot_id and ot_rl_id = rl_id " + 
				"and ob_id = " + objectId + " and rl_level >= (select rl_level " + 
				"from roles, users where rl_id = us_rl_id and us_name = \"" + 
				user + "\")";
		result = db.rawQuery(sqlQuery, null);
		if(result.getCount() > 0){
			result.close();
			return true;
		}
		else{ 
			result.close();
			return false;
		}
	}
	
	public Integer getObjectMapId(Integer objectId){
		String sqlQuery = "select pl_mp_id from objects, places where " + 
				"ob_pl_id = pl_id and ob_id = " + objectId;
		Integer mapId = null;
		Cursor result = db.rawQuery(sqlQuery, null);
		if(result.getCount() > 0){
			result.moveToNext();
			mapId = result.getInt(0);
		}
		result.close();
		return mapId;
	}

	public List<Element> getFilteredObjects(int mapId, Document svgDocument,
			String objects, String places, String user) throws XPathExpressionException {
		List<Element> nodeList = new ArrayList<Element>();
		String sqlQuery = "select ob_id, ob_name, ob_posx, ob_posy, cl_value from objects, "
				+ "object_types, colors,  places where ob_ot_id = ot_id and ot_cl_id = cl_id "
				+ "and ob_pl_id = pl_id and pl_mp_id = " + mapId +
				" and (select rl_level from roles, users where rl_id = us_rl_id and us_name = \"" +
				user + "\") <= (select rl_level from roles where rl_id = ot_rl_id)";
		if (objects.length() > 0)
			sqlQuery += " and ob_ot_id in (" + objects + ")";
		if (places.length() > 0)
			sqlQuery += " and pl_pt_id in (" + places + ")";
		Cursor result = db.rawQuery(sqlQuery, null);
		if (result.getCount() > 0) {
			// result.moveToFirst();
			do {
				result.moveToNext();
				Element elem = svgDocument.createElement("circle");
				elem.setAttribute("class", "object");
				elem.setAttribute("id", Integer.toString(result.getInt(0)));
				elem.setAttribute("cx", Float.toString(result.getFloat(2)));
				elem.setAttribute("cy", Float.toString(result.getFloat(3)));
				elem.setAttribute("r", "7");
				elem.setAttribute("stroke", "#000000");
				elem.setAttribute("stroke-width", "1");
				elem.setAttribute("fill", result.getString(4));
				nodeList.add(elem);

			} while (!result.isLast());
		}
		result.close();
		return nodeList;
	}
	
	public String getObjectColor(String objectId){
		String sqlQuery = "select cl_value from colors, object_types, objects " + 
				"where cl_id = ot_cl_id and ot_id = ob_ot_id and ob_id = " + Integer.parseInt(objectId);
		//Paint paint = new Paint();
		
		Cursor result = db.rawQuery(sqlQuery, null);
		String colorValue = null;
		if(result.getCount() > 0){
			result.moveToFirst();
			colorValue = result.getString(0);
			//paint.setColor(Color.parseColor(colorValue));
		}
		//else
			//paint.setColor(Color.WHITE);
		//return paint;
		return colorValue;
	}

}
