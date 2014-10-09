package com.example.eskulap.localization.mobile.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button zalogujButton;
	private EditText name;
	private EditText password;
	private Context context;
	private ServiceHandler serviceHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		zalogujButton = (Button)findViewById(R.id.zalogujButton);
		
		name = (EditText)findViewById(R.id.loginEditText);
		password = (EditText)findViewById(R.id.passwordEditText);
		context = this;
		serviceHandler = new ServiceHandler(context);
		zalogujButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(login()){
					
					
					updateData();
				
					Intent intent = new Intent(context,MapActivity.class);
					intent.putExtra("user", name.getText().toString());
					intent.putExtra("password", password.getText().toString());
					startActivity(intent);
				}
				else{
					Toast.makeText(context, "Logowanie nieudane", Toast.LENGTH_LONG).show();
				}
				
					
					/*DbAdapter db = new DbAdapter(context);
					db.open();
					
					db.insert();
					db.close();*/
				
				
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private boolean login(){
		if(isOnline()){
			boolean result = serviceHandler.login(name.getText().toString(), password.getText().toString());
			return result;
		}
		else{
			DbAdapter db = new DbAdapter(context);
			db.open();
			
			if(db.login(name.getText().toString(), password.getText().toString()))
				return true;
			else
				return false;
		}
	}
	
	private boolean isOnline() {

	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {

	        return true;
	    }
	    return false;
	}
	
	private void updateData(){
		if(isOnline()){
			serviceHandler.update(name.getText().toString(), password.getText().toString());
		}
	}
	

}
