package com.example.eskulap.localization.mobile.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

public class ServiceHandler {
	private String serviceUrl = "http://192.168.112.1:19075/Rest.svc/";
	private Context context;
	
	public ServiceHandler(Context context){
		this.context = context;
	}
	
	public boolean login(String name, String password){
		String url  = serviceUrl + "login/" + name + "," + password;
		JSONObject response = executeGET(url);
		
		DbAdapter db = new DbAdapter(context);
		db.open();
		//db.insertMap();
		db.close();
		
		try {
			if(response.getString("userLoginResult").equals("Zalogowano siê pomyœlnie!"))
				return true;
			else
				return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public void update(String name, String password){
		DbAdapter db = new DbAdapter(context);
		db.open();
		db.cleanData();
		String url = serviceUrl + "actualization/Colors/" + name + "," + password;
		JSONObject response = executeGET(url);
		updateColors(response, db);
		url = serviceUrl + "actualization/Maps/" + name + "," + password;
		//response = executeGET(url);
		//updateMaps(response, db);
		url = serviceUrl + "actualization/Roles/" + name + "," + password;
		response = executeGET(url);
		updateRoles(response, db);
		url = serviceUrl + "actualization/Object_types/" + name + "," + password;
		response = executeGET(url);
		updateObjectTypes(response, db);
		url = serviceUrl + "actualization/Place_types/" + name + "," + password;
		response = executeGET(url);
		updatePlaceTypes(response, db);
		url = serviceUrl + "actualization/Places/" + name + "," + password;
		response = executeGET(url);
		updatePlaces(response, db);
		url = serviceUrl + "actualization/Objects/" + name + "," + password;
		response = executeGET(url);
		updateObjects("setting", response, db);
		url = serviceUrl + "actualization/Object_info/" + name + "," + password;
		response = executeGET(url);
		updateObjectInfo(response, db);
		url = serviceUrl + "actualization/Users/" + name + "," + password;
		response = executeGET(url);
		updateUsers(response, db);
		url = serviceUrl + "actualization/Parameters/" + name + "," + password;
		response = executeGET(url);
		updateParameters(response, db);
		db.close();
	}
	
	
private void updateParameters(JSONObject data, DbAdapter dbAdapter){
		
		//JSONObject obj = new JSONObject("{\"actualizationResult\":\"[{\"ob_id\\\":1,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":112.31838798522944,\\\"ob_posy\\\":126.76764678955084},{\\\"ob_id\\\":2,\\\"ob_name\\\":\\\"Szafka\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":30.9757633209227950,\\\"ob_posy\\\":147.69158124923703},{\\\"ob_id\\\":3,\\\"ob_name\\\":\\\"USG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":88.49744033813478,\\\"ob_posy\\\":12.6050729751586910},{\\\"ob_id\\\":4,\\\"ob_name\\\":\\\"RTG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":2,\\\"ob_posx\\\":247.75143432617142,\\\"ob_posy\\\":47.96934211254137},{\\\"ob_id\\\":6,\\\"ob_name\\\":\\\"MRI\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.56256484985330,\\\"ob_posy\\\":13.16710540652285},{\\\"ob_id\\\":7,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.88244438171375,\\\"ob_posy\\\":49.04076361656196},{\\\"ob_id\\\":35,\\\"ob_name\\\":\\\"Wiaderko\\\",\\\"ob_ot_id\\\":5,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":267.51873397827150,\\\"ob_posy\\\":146.97936749458313},{\\\"ob_id\\\":33,\\\"ob_name\\\":\\\"Laptop DELL\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":29.1196022033691120,\\\"ob_posy\\\":12.3155574798582280},{\\\"ob_id\\\":32,\\\"ob_name\\\":\\\"Apple MacBook Pro 15\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":30.23218154907220,\\\"ob_posy\\\":126.69961357116699}]\"}".replace("\\", ""));
		//Log.d("debug",obj.getJSONArray("actualizationResult").getJSONObject(0).getString("ob_name"));
	ContentValues values  = new ContentValues();
	
	
	try {
		JSONArray results = data.getJSONArray("actualizationParamsResult");
		for(int i = 0; i < results.length(); i++){
			values.put("pr_id", results.getJSONObject(i).getInt("pr_id"));
			values.put("pr_name", results.getJSONObject(i).getString("pr_name"));
			dbAdapter.updateTable(values,"parameters");
			values.clear();
		}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		Log.d("tagg",e.getMessage());
	}
	
}

private void updateRoles(JSONObject data, DbAdapter dbAdapter){
	
	//JSONObject obj = new JSONObject("{\"actualizationResult\":\"[{\"ob_id\\\":1,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":112.31838798522944,\\\"ob_posy\\\":126.76764678955084},{\\\"ob_id\\\":2,\\\"ob_name\\\":\\\"Szafka\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":30.9757633209227950,\\\"ob_posy\\\":147.69158124923703},{\\\"ob_id\\\":3,\\\"ob_name\\\":\\\"USG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":88.49744033813478,\\\"ob_posy\\\":12.6050729751586910},{\\\"ob_id\\\":4,\\\"ob_name\\\":\\\"RTG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":2,\\\"ob_posx\\\":247.75143432617142,\\\"ob_posy\\\":47.96934211254137},{\\\"ob_id\\\":6,\\\"ob_name\\\":\\\"MRI\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.56256484985330,\\\"ob_posy\\\":13.16710540652285},{\\\"ob_id\\\":7,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.88244438171375,\\\"ob_posy\\\":49.04076361656196},{\\\"ob_id\\\":35,\\\"ob_name\\\":\\\"Wiaderko\\\",\\\"ob_ot_id\\\":5,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":267.51873397827150,\\\"ob_posy\\\":146.97936749458313},{\\\"ob_id\\\":33,\\\"ob_name\\\":\\\"Laptop DELL\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":29.1196022033691120,\\\"ob_posy\\\":12.3155574798582280},{\\\"ob_id\\\":32,\\\"ob_name\\\":\\\"Apple MacBook Pro 15\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":30.23218154907220,\\\"ob_posy\\\":126.69961357116699}]\"}".replace("\\", ""));
	//Log.d("debug",obj.getJSONArray("actualizationResult").getJSONObject(0).getString("ob_name"));
ContentValues values  = new ContentValues();


try {
	JSONArray results = data.getJSONArray("actualizationRolesResult");
	for(int i = 0; i < results.length(); i++){
		values.put("rl_id", results.getJSONObject(i).getInt("rl_id"));
		values.put("rl_level", results.getJSONObject(i).getInt("rl_level"));
		values.put("rl_name", results.getJSONObject(i).getString("rl_name"));
		dbAdapter.updateTable(values,"roles");
		values.clear();
	}
} catch (JSONException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

}
	
private void updateUsers(JSONObject data, DbAdapter dbAdapter){
		
		//JSONObject obj = new JSONObject("{\"actualizationResult\":\"[{\"ob_id\\\":1,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":112.31838798522944,\\\"ob_posy\\\":126.76764678955084},{\\\"ob_id\\\":2,\\\"ob_name\\\":\\\"Szafka\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":30.9757633209227950,\\\"ob_posy\\\":147.69158124923703},{\\\"ob_id\\\":3,\\\"ob_name\\\":\\\"USG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":88.49744033813478,\\\"ob_posy\\\":12.6050729751586910},{\\\"ob_id\\\":4,\\\"ob_name\\\":\\\"RTG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":2,\\\"ob_posx\\\":247.75143432617142,\\\"ob_posy\\\":47.96934211254137},{\\\"ob_id\\\":6,\\\"ob_name\\\":\\\"MRI\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.56256484985330,\\\"ob_posy\\\":13.16710540652285},{\\\"ob_id\\\":7,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.88244438171375,\\\"ob_posy\\\":49.04076361656196},{\\\"ob_id\\\":35,\\\"ob_name\\\":\\\"Wiaderko\\\",\\\"ob_ot_id\\\":5,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":267.51873397827150,\\\"ob_posy\\\":146.97936749458313},{\\\"ob_id\\\":33,\\\"ob_name\\\":\\\"Laptop DELL\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":29.1196022033691120,\\\"ob_posy\\\":12.3155574798582280},{\\\"ob_id\\\":32,\\\"ob_name\\\":\\\"Apple MacBook Pro 15\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":30.23218154907220,\\\"ob_posy\\\":126.69961357116699}]\"}".replace("\\", ""));
		//Log.d("debug",obj.getJSONArray("actualizationResult").getJSONObject(0).getString("ob_name"));
	ContentValues values  = new ContentValues();
	
	
	try {
		JSONArray results = data.getJSONArray("actualizationUsersResult");
		for(int i = 0; i < results.length(); i++){
			values.put("us_first_name", results.getJSONObject(i).getString("us_first_name"));
			values.put("us_id", results.getJSONObject(i).getInt("us_id"));
			values.put("us_last_name", results.getJSONObject(i).getString("us_last_name"));
			values.put("us_name", results.getJSONObject(i).getString("us_name"));
			values.put("us_password", results.getJSONObject(i).getString("us_password"));
			values.put("us_rl_id", results.getJSONObject(i).getInt("us_rl_id"));
			dbAdapter.updateTable(values,"users");
			values.clear();
		}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
	
private void updateObjectInfo(JSONObject data, DbAdapter dbAdapter){
		
		//JSONObject obj = new JSONObject("{\"actualizationResult\":\"[{\"ob_id\\\":1,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":112.31838798522944,\\\"ob_posy\\\":126.76764678955084},{\\\"ob_id\\\":2,\\\"ob_name\\\":\\\"Szafka\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":30.9757633209227950,\\\"ob_posy\\\":147.69158124923703},{\\\"ob_id\\\":3,\\\"ob_name\\\":\\\"USG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":88.49744033813478,\\\"ob_posy\\\":12.6050729751586910},{\\\"ob_id\\\":4,\\\"ob_name\\\":\\\"RTG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":2,\\\"ob_posx\\\":247.75143432617142,\\\"ob_posy\\\":47.96934211254137},{\\\"ob_id\\\":6,\\\"ob_name\\\":\\\"MRI\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.56256484985330,\\\"ob_posy\\\":13.16710540652285},{\\\"ob_id\\\":7,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.88244438171375,\\\"ob_posy\\\":49.04076361656196},{\\\"ob_id\\\":35,\\\"ob_name\\\":\\\"Wiaderko\\\",\\\"ob_ot_id\\\":5,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":267.51873397827150,\\\"ob_posy\\\":146.97936749458313},{\\\"ob_id\\\":33,\\\"ob_name\\\":\\\"Laptop DELL\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":29.1196022033691120,\\\"ob_posy\\\":12.3155574798582280},{\\\"ob_id\\\":32,\\\"ob_name\\\":\\\"Apple MacBook Pro 15\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":30.23218154907220,\\\"ob_posy\\\":126.69961357116699}]\"}".replace("\\", ""));
		//Log.d("debug",obj.getJSONArray("actualizationResult").getJSONObject(0).getString("ob_name"));
	ContentValues values  = new ContentValues();
	
	
	try {
		JSONArray results = data.getJSONArray("actualizationOInfoResult");
		for(int i = 0; i < results.length(); i++){
			values.put("oi_ob_id", results.getJSONObject(i).getInt("oi_ob_id"));
			values.put("oi_pr_id", results.getJSONObject(i).getInt("oi_pr_id"));
			values.put("oi_value", results.getJSONObject(i).getString("oi_value"));
			dbAdapter.updateTable(values,"object_info");
			values.clear();
		}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
	
private void updatePlaces(JSONObject data, DbAdapter dbAdapter){
		
		//JSONObject obj = new JSONObject("{\"actualizationResult\":\"[{\"ob_id\\\":1,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":112.31838798522944,\\\"ob_posy\\\":126.76764678955084},{\\\"ob_id\\\":2,\\\"ob_name\\\":\\\"Szafka\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":30.9757633209227950,\\\"ob_posy\\\":147.69158124923703},{\\\"ob_id\\\":3,\\\"ob_name\\\":\\\"USG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":88.49744033813478,\\\"ob_posy\\\":12.6050729751586910},{\\\"ob_id\\\":4,\\\"ob_name\\\":\\\"RTG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":2,\\\"ob_posx\\\":247.75143432617142,\\\"ob_posy\\\":47.96934211254137},{\\\"ob_id\\\":6,\\\"ob_name\\\":\\\"MRI\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.56256484985330,\\\"ob_posy\\\":13.16710540652285},{\\\"ob_id\\\":7,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.88244438171375,\\\"ob_posy\\\":49.04076361656196},{\\\"ob_id\\\":35,\\\"ob_name\\\":\\\"Wiaderko\\\",\\\"ob_ot_id\\\":5,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":267.51873397827150,\\\"ob_posy\\\":146.97936749458313},{\\\"ob_id\\\":33,\\\"ob_name\\\":\\\"Laptop DELL\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":29.1196022033691120,\\\"ob_posy\\\":12.3155574798582280},{\\\"ob_id\\\":32,\\\"ob_name\\\":\\\"Apple MacBook Pro 15\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":30.23218154907220,\\\"ob_posy\\\":126.69961357116699}]\"}".replace("\\", ""));
		//Log.d("debug",obj.getJSONArray("actualizationResult").getJSONObject(0).getString("ob_name"));
	ContentValues values  = new ContentValues();
	
	
	try {
		JSONArray results = data.getJSONArray("actualizationPlacesResult");
		for(int i = 0; i < results.length(); i++){
			values.put("pl_id", results.getJSONObject(i).getInt("pl_id"));
			values.put("pl_mp_id", results.getJSONObject(i).getInt("pl_mp_id"));
			values.put("pl_name", results.getJSONObject(i).getString("pl_name"));
			values.put("pl_pt_id", results.getJSONObject(i).getInt("pl_pt_id"));
			values.put("pl_posx", results.getJSONObject(i).getDouble("pl_posx"));
			values.put("pl_posy", results.getJSONObject(i).getDouble("pl_posy"));
			dbAdapter.updateTable(values,"places");
			values.clear();
		}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
	
	
private void updatePlaceTypes(JSONObject data, DbAdapter dbAdapter){
		
		//JSONObject obj = new JSONObject("{\"actualizationResult\":\"[{\"ob_id\\\":1,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":112.31838798522944,\\\"ob_posy\\\":126.76764678955084},{\\\"ob_id\\\":2,\\\"ob_name\\\":\\\"Szafka\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":30.9757633209227950,\\\"ob_posy\\\":147.69158124923703},{\\\"ob_id\\\":3,\\\"ob_name\\\":\\\"USG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":88.49744033813478,\\\"ob_posy\\\":12.6050729751586910},{\\\"ob_id\\\":4,\\\"ob_name\\\":\\\"RTG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":2,\\\"ob_posx\\\":247.75143432617142,\\\"ob_posy\\\":47.96934211254137},{\\\"ob_id\\\":6,\\\"ob_name\\\":\\\"MRI\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.56256484985330,\\\"ob_posy\\\":13.16710540652285},{\\\"ob_id\\\":7,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.88244438171375,\\\"ob_posy\\\":49.04076361656196},{\\\"ob_id\\\":35,\\\"ob_name\\\":\\\"Wiaderko\\\",\\\"ob_ot_id\\\":5,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":267.51873397827150,\\\"ob_posy\\\":146.97936749458313},{\\\"ob_id\\\":33,\\\"ob_name\\\":\\\"Laptop DELL\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":29.1196022033691120,\\\"ob_posy\\\":12.3155574798582280},{\\\"ob_id\\\":32,\\\"ob_name\\\":\\\"Apple MacBook Pro 15\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":30.23218154907220,\\\"ob_posy\\\":126.69961357116699}]\"}".replace("\\", ""));
		//Log.d("debug",obj.getJSONArray("actualizationResult").getJSONObject(0).getString("ob_name"));
	ContentValues values  = new ContentValues();
	
	
	try {
		JSONArray results = data.getJSONArray("actualizationPTypesResult");
		for(int i = 0; i < results.length(); i++){
			values.put("pt_id", results.getJSONObject(i).getInt("pt_id"));
			values.put("pt_type", results.getJSONObject(i).getString("pt_type"));
			dbAdapter.updateTable(values,"place_types");
			values.clear();
		}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
	
private void updateObjectTypes(JSONObject data, DbAdapter dbAdapter){
		
		//JSONObject obj = new JSONObject("{\"actualizationResult\":\"[{\"ob_id\\\":1,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":112.31838798522944,\\\"ob_posy\\\":126.76764678955084},{\\\"ob_id\\\":2,\\\"ob_name\\\":\\\"Szafka\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":30.9757633209227950,\\\"ob_posy\\\":147.69158124923703},{\\\"ob_id\\\":3,\\\"ob_name\\\":\\\"USG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":88.49744033813478,\\\"ob_posy\\\":12.6050729751586910},{\\\"ob_id\\\":4,\\\"ob_name\\\":\\\"RTG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":2,\\\"ob_posx\\\":247.75143432617142,\\\"ob_posy\\\":47.96934211254137},{\\\"ob_id\\\":6,\\\"ob_name\\\":\\\"MRI\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.56256484985330,\\\"ob_posy\\\":13.16710540652285},{\\\"ob_id\\\":7,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.88244438171375,\\\"ob_posy\\\":49.04076361656196},{\\\"ob_id\\\":35,\\\"ob_name\\\":\\\"Wiaderko\\\",\\\"ob_ot_id\\\":5,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":267.51873397827150,\\\"ob_posy\\\":146.97936749458313},{\\\"ob_id\\\":33,\\\"ob_name\\\":\\\"Laptop DELL\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":29.1196022033691120,\\\"ob_posy\\\":12.3155574798582280},{\\\"ob_id\\\":32,\\\"ob_name\\\":\\\"Apple MacBook Pro 15\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":30.23218154907220,\\\"ob_posy\\\":126.69961357116699}]\"}".replace("\\", ""));
		//Log.d("debug",obj.getJSONArray("actualizationResult").getJSONObject(0).getString("ob_name"));
	ContentValues values  = new ContentValues();
	
	
	try {
		JSONArray results = data.getJSONArray("actualizationOTypesResult");
		for(int i = 0; i < results.length(); i++){
			values.put("ot_cl_id", results.getJSONObject(i).getInt("ot_cl_id"));
			values.put("ot_id", results.getJSONObject(i).getInt("ot_id"));
			values.put("ot_type", results.getJSONObject(i).getString("ot_type"));
			values.put("ot_rl_id", results.getJSONObject(i).getInt("ot_rl_id"));
			dbAdapter.updateTable(values,"object_types");
			values.clear();
		}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
	
	private void updateColors(JSONObject data, DbAdapter dbAdapter){
		
		//JSONObject obj = new JSONObject("{\"actualizationResult\":\"[{\"ob_id\\\":1,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":112.31838798522944,\\\"ob_posy\\\":126.76764678955084},{\\\"ob_id\\\":2,\\\"ob_name\\\":\\\"Szafka\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":30.9757633209227950,\\\"ob_posy\\\":147.69158124923703},{\\\"ob_id\\\":3,\\\"ob_name\\\":\\\"USG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":88.49744033813478,\\\"ob_posy\\\":12.6050729751586910},{\\\"ob_id\\\":4,\\\"ob_name\\\":\\\"RTG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":2,\\\"ob_posx\\\":247.75143432617142,\\\"ob_posy\\\":47.96934211254137},{\\\"ob_id\\\":6,\\\"ob_name\\\":\\\"MRI\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.56256484985330,\\\"ob_posy\\\":13.16710540652285},{\\\"ob_id\\\":7,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.88244438171375,\\\"ob_posy\\\":49.04076361656196},{\\\"ob_id\\\":35,\\\"ob_name\\\":\\\"Wiaderko\\\",\\\"ob_ot_id\\\":5,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":267.51873397827150,\\\"ob_posy\\\":146.97936749458313},{\\\"ob_id\\\":33,\\\"ob_name\\\":\\\"Laptop DELL\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":29.1196022033691120,\\\"ob_posy\\\":12.3155574798582280},{\\\"ob_id\\\":32,\\\"ob_name\\\":\\\"Apple MacBook Pro 15\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":30.23218154907220,\\\"ob_posy\\\":126.69961357116699}]\"}".replace("\\", ""));
		//Log.d("debug",obj.getJSONArray("actualizationResult").getJSONObject(0).getString("ob_name"));
	ContentValues values  = new ContentValues();
	
	
	try {
		JSONArray results = data.getJSONArray("actualizationColorsResult");
		for(int i = 0; i < results.length(); i++){
			values.put("cl_id", results.getJSONObject(i).getInt("cl_id"));
			values.put("cl_name", results.getJSONObject(i).getString("cl_name"));
			values.put("cl_value", results.getJSONObject(i).getString("cl_value"));
			dbAdapter.updateTable(values,"colors");
			values.clear();
		}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
	
	private void updateObjects(String tableName, JSONObject data, DbAdapter dbAdapter){
	
			//JSONObject obj = new JSONObject("{\"actualizationResult\":\"[{\"ob_id\\\":1,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":112.31838798522944,\\\"ob_posy\\\":126.76764678955084},{\\\"ob_id\\\":2,\\\"ob_name\\\":\\\"Szafka\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":30.9757633209227950,\\\"ob_posy\\\":147.69158124923703},{\\\"ob_id\\\":3,\\\"ob_name\\\":\\\"USG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":88.49744033813478,\\\"ob_posy\\\":12.6050729751586910},{\\\"ob_id\\\":4,\\\"ob_name\\\":\\\"RTG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":2,\\\"ob_posx\\\":247.75143432617142,\\\"ob_posy\\\":47.96934211254137},{\\\"ob_id\\\":6,\\\"ob_name\\\":\\\"MRI\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.56256484985330,\\\"ob_posy\\\":13.16710540652285},{\\\"ob_id\\\":7,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.88244438171375,\\\"ob_posy\\\":49.04076361656196},{\\\"ob_id\\\":35,\\\"ob_name\\\":\\\"Wiaderko\\\",\\\"ob_ot_id\\\":5,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":267.51873397827150,\\\"ob_posy\\\":146.97936749458313},{\\\"ob_id\\\":33,\\\"ob_name\\\":\\\"Laptop DELL\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":29.1196022033691120,\\\"ob_posy\\\":12.3155574798582280},{\\\"ob_id\\\":32,\\\"ob_name\\\":\\\"Apple MacBook Pro 15\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":30.23218154907220,\\\"ob_posy\\\":126.69961357116699}]\"}".replace("\\", ""));
			//Log.d("debug",obj.getJSONArray("actualizationResult").getJSONObject(0).getString("ob_name"));
		ContentValues values  = new ContentValues();
		
		
		try {
			JSONArray results = data.getJSONArray("actualizationObjectsResult");
			for(int i = 0; i < results.length(); i++){
				values.put("ob_id", results.getJSONObject(i).getInt("ob_id"));
				values.put("ob_name", results.getJSONObject(i).getString("ob_name"));
				values.put("ob_ot_id", results.getJSONObject(i).getInt("ob_ot_id"));
				values.put("ob_pl_id", results.getJSONObject(i).getInt("ob_pl_id"));
				values.put("ob_posx", results.getJSONObject(i).getDouble("ob_posx"));
				values.put("ob_posy", results.getJSONObject(i).getDouble("ob_posy"));
				String timeString = results.getJSONObject(i).getString("ob_upd_date");
				timeString = timeString.substring(timeString.indexOf("(") + 1,timeString.indexOf(")"));
				String[] timeSegments = timeString.split("\\+");
				Long timeZoneOffset = Long.valueOf(timeSegments[1]) * 36000;
				//Toast.makeText(context, timeSegments[0], Toast.LENGTH_LONG).show();
				Long millis = Long.valueOf(timeSegments[0]); //+ timeZoneOffset;
				values.put("ob_upd_date", Long.valueOf(millis));
				dbAdapter.updateTable(values,"objects");
				values.clear();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void updateMaps(JSONObject data, DbAdapter dbAdapter){
		
		//JSONObject obj = new JSONObject("{\"actualizationResult\":\"[{\"ob_id\\\":1,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":112.31838798522944,\\\"ob_posy\\\":126.76764678955084},{\\\"ob_id\\\":2,\\\"ob_name\\\":\\\"Szafka\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":30.9757633209227950,\\\"ob_posy\\\":147.69158124923703},{\\\"ob_id\\\":3,\\\"ob_name\\\":\\\"USG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":88.49744033813478,\\\"ob_posy\\\":12.6050729751586910},{\\\"ob_id\\\":4,\\\"ob_name\\\":\\\"RTG\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":2,\\\"ob_posx\\\":247.75143432617142,\\\"ob_posy\\\":47.96934211254137},{\\\"ob_id\\\":6,\\\"ob_name\\\":\\\"MRI\\\",\\\"ob_ot_id\\\":2,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.56256484985330,\\\"ob_posy\\\":13.16710540652285},{\\\"ob_id\\\":7,\\\"ob_name\\\":\\\"Biurko\\\",\\\"ob_ot_id\\\":1,\\\"ob_pl_id\\\":4,\\\"ob_posx\\\":148.88244438171375,\\\"ob_posy\\\":49.04076361656196},{\\\"ob_id\\\":35,\\\"ob_name\\\":\\\"Wiaderko\\\",\\\"ob_ot_id\\\":5,\\\"ob_pl_id\\\":3,\\\"ob_posx\\\":267.51873397827150,\\\"ob_posy\\\":146.97936749458313},{\\\"ob_id\\\":33,\\\"ob_name\\\":\\\"Laptop DELL\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":29.1196022033691120,\\\"ob_posy\\\":12.3155574798582280},{\\\"ob_id\\\":32,\\\"ob_name\\\":\\\"Apple MacBook Pro 15\\\",\\\"ob_ot_id\\\":3,\\\"ob_pl_id\\\":1,\\\"ob_posx\\\":30.23218154907220,\\\"ob_posy\\\":126.69961357116699}]\"}".replace("\\", ""));
		//Log.d("debug",obj.getJSONArray("actualizationResult").getJSONObject(0).getString("ob_name"));
	ContentValues values  = new ContentValues();
	
	
	try {
		JSONArray results = data.getJSONArray("actualizationMapsResult");
		for(int i = 0; i < results.length(); i++){
			values.put("mp_id", results.getJSONObject(i).getInt("mp_id"));
			values.put("mp_name", results.getJSONObject(i).getString("mp_name"));
			values.put("mp_building", results.getJSONObject(i).getString("mp_building"));
			values.put("mp_floor", results.getJSONObject(i).getInt("mp_floor"));
			values.put("mp_path", results.getJSONObject(i).getString("mp_path"));
			dbAdapter.updateTable(values,"maps");
			values.clear();
		}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
	
	public boolean updateObject(OBJECTS object){
		
		Gson gson = new Gson();
		String json = gson.toJson(object);
		
		Integer objId = -1;
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPut postRequest = new HttpPut(serviceUrl + "modify/Objects/admin,admin");

		StringEntity input;
		try {
			input = new StringEntity(json);
			
			input.setContentType("application/json; charset=UTF-8");
			postRequest.setEntity(input);
			HttpResponse response = httpClient.execute(postRequest);
			
			StatusLine status = response.getStatusLine();
			//Toast.makeText(context, status.getStatusCode(), Toast.LENGTH_LONG).show();
			//if(status.getStatusCode() == HttpStatus.SC_OK){
				//ByteArrayOutputStream out = new ByteArrayOutputStream();
				//response.getEntity().writeTo(out);
				//out.close();
				//Toast.makeText(context, out.toString(), Toast.LENGTH_LONG).show();
				//ActionBarDrawerToggle f = new ActionBarDrawerToggle(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes)
				//JSONObject jsonObject = new JSONObject(out.toString());
				
				//objId = Integer.parseInt(out.toString());
			//}
			//else
				//Toast.makeText(context, "status line cos nei tak", Toast.LENGTH_LONG).show();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return true;
		
	}
	
	public Integer addObject(Integer type, String name, Integer place, Date date, float x, float y){
		OBJECTS object = new OBJECTS();
		object.OB_OT_ID = type;
		object.OB_NAME = name;
		object.OB_PL_ID = place;
		object.OB_POSX = x;
		object.OB_POSY = y;
		//String jsonDate = "\\/Date(";
		//jsonDate += Long.toString(date.getTime());
		//jsonDate += "+0000)\\/";
		//jsonDate = jsonDate.replaceAll("\\\\", "\\");
		//object.OB_UPD_DATE = jsonDate;
		Gson gson = new Gson();
		String json = gson.toJson(object);
		
		Integer objId = -1;
		
		DefaultHttpClient httpClient = new DefaultHttpClient();

		HttpPut putRequest = new HttpPut(serviceUrl + "add/Objects/admin,admin");

		StringEntity input;
		try {
			input = new StringEntity(json);
			input.setContentType("application/json; charset=UTF-8");
			putRequest.setEntity(input);
			HttpResponse response = httpClient.execute(putRequest);
			
			StatusLine status = response.getStatusLine();
			if(status.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();			
				//ActionBarDrawerToggle f = new ActionBarDrawerToggle(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes)
				//JSONObject jsonObject = new JSONObject(out.toString());
				
				objId = Integer.parseInt(out.toString());
			}
			else
				Toast.makeText(context, "status line cos nei tak", Toast.LENGTH_LONG).show();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return objId;
		
	}
	
	public String getObjectUpdDate(Integer objectId, String name, String password){
		OBJECTS objectInfo = getObjectInfo(objectId, name, password);
		return objectInfo.OB_UPD_DATE;
	}
	
	public OBJECTS getObjectInfo(Integer objectId, String name, String password){
		String url = serviceUrl + "object/" + name + "," + password + "," + String.valueOf(objectId);
		JSONObject response = executeGET(url);
		OBJECTS objectInfo = new OBJECTS();
		try {
			JSONObject results = response.getJSONObject("getObjectResult");
			objectInfo.OB_ID = results.getInt("ob_id");
			//objectInfo.OB_OT_ID = results.getJSONObject(0).getInt("ob_ot_id");
			objectInfo.OB_NAME = results.getString("ob_name");
			//objectInfo.OB_PL_ID = results.getJSONObject(0).getInt("ob_pl_id");
			objectInfo.OB_POSX = (float) results.getDouble("ob_posx");
			objectInfo.OB_POSY = (float) results.getDouble("ob_posy");
			String timeString = results.getString("ob_upd_date");
			timeString = timeString.substring(timeString.indexOf("(") + 1,timeString.indexOf(")"));
			String[] timeSegments = timeString.split("\\+");
			objectInfo.OB_UPD_DATE = timeSegments[0];
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objectInfo;
	}
	
	private JSONObject executeGET(String url){
		HttpClient service = new DefaultHttpClient();
		JSONObject object = null;
		try {
			HttpResponse response = service.execute(new HttpGet(url));
			
			StatusLine status = response.getStatusLine();
			if(status.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();			
				//ActionBarDrawerToggle f = new ActionBarDrawerToggle(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes)
				object = new JSONObject(out.toString());
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	
}
