package com.example.masproject;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondScreen extends Activity {
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.add_activity_screen);
	 
	        TextView txtName = (TextView) findViewById(R.id.txtName);
	        Button btnClose = (Button) findViewById(R.id.btnClose);
	 
	        Intent i = getIntent();
	        // Receiving the Data
	        String name = i.getStringExtra("name");
	        String jSessionid = i.getStringExtra("sess");
	        Log.e("Second Screen", name + " - " + jSessionid);
	 
	        // Displaying Received data
	        HTTPInteraction httpobj= new HTTPInteraction();
			try {
				HttpGet request = new HttpGet("http://dev.m.gatech.edu/developer/pconner3/widget/4261/c/api/events?date=2014-03-08");
				request.setHeader("Cookie", "PHPSESSID=" + jSessionid);
				DefaultHttpClient httpclient = new DefaultHttpClient();
	            try {
					HttpResponse resp = httpclient.execute(request);
			        txtName.setText(httpobj.parseResponse(resp));       
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}
	 
	        // Binding Click event to Button
	        btnClose.setOnClickListener(new View.OnClickListener() {	 
	            public void onClick(View arg0) {
	            	//Starting a new Intent
	                Intent nextScreen = new Intent(getApplicationContext(), MainListActivity.class);
	                startActivity(nextScreen);	               
	            }
	        });	 
	    }
	}
