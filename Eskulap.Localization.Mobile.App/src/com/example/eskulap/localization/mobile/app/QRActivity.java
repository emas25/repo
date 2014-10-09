package com.example.eskulap.localization.mobile.app;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class QRActivity extends Activity {
	Button b;
	TextView text;
	QRActivity activity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qr_activity);
		activity = this;
		b = (Button)findViewById(R.id.button1);
		text = (TextView)findViewById(R.id.textView1);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IntentIntegrator integrator = new IntentIntegrator(activity);
				integrator.initiateScan();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null) {
		    text.setText(scanResult.getContents());
		  }
		  // else continue with any other code you need in the method
		  
		}

}
