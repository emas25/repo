package com.example.eskulap.localization.mobile.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends ActionBarActivity {

	ActionBar ab;
	Button qrCodeButton;
	Button mapButton;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		ab = getSupportActionBar();
		qrCodeButton = (Button)findViewById(R.id.qrCodeButton);
		mapButton = (Button)findViewById(R.id.mapButton);
		
		qrCodeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				scanQRCode();
			}
		});
		
		mapButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openMap();
			}
		});
		context = this;
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	// @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.qr_code:
			scanQRCode();
			return true;
		case R.id.map:
			openMap();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class TabListener implements ActionBar.TabListener {
		private final Activity myActivity;
		private final String myTag;

		public TabListener(Activity activity, String tag) {
			myActivity = activity;
			myTag = tag;

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {

			// Toast.makeText(myActivity, ft.toString(),
			// Toast.LENGTH_LONG).show();

		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {

		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}
	}
	
	private void scanQRCode(){
		Intent intent = new Intent(context,QRActivity.class); //!!!!!!!!!!!
		startActivity(intent);
	}
	
	private void openMap(){
		Intent intent = new Intent(context,MapActivity.class); //!!!!!!!!!!!
		startActivity(intent);
	}

}
