package com.example.masproject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

public class ActivityScreen extends Activity {
	TextView txtDate;
	TextView txtName;
	Calendar cal;
	String dateTitle;
	String dateUrl;
	SimpleDateFormat titleFormat = new SimpleDateFormat("EEEE, MM/d/yy");	
	SimpleDateFormat urlFormat = new SimpleDateFormat("y-MM-d");	 
	String infoUrl = "http://dev.m.gatech.edu/developer/pconner3/widget/4261/c/api/events?date=";
	String jSessionid;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_screen);
        super.onCreate(savedInstanceState);
        
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtName = (TextView) findViewById(R.id.txtName);                
        dateTitle = titleFormat.format(new Date());
        dateUrl = urlFormat.format(new Date());     
        txtDate.setText(dateTitle);
        cal = Calendar.getInstance();   
        cal.setTime(new Date()); 
        
        // Getting session cookie from login screen 
        Intent i = getIntent();
        jSessionid = i.getStringExtra("sess");
        Log.i("Activity Screen", jSessionid);
        getData();
 
        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        Button btnPrev = (Button) findViewById(R.id.btnPrev);
        Button btnNext = (Button) findViewById(R.id.btnNext);
        // Add activity button
        btnAdd.setOnClickListener(new View.OnClickListener() {	 
            public void onClick(View arg0) {
            	//Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), ActivityList.class);
                startActivity(nextScreen);	               
            }
        });	 
     // Prev day button
        btnPrev.setOnClickListener(new View.OnClickListener() {	 
            public void onClick(View arg0) {
            	cal.add(Calendar.DATE, -1);  
            	dateTitle = titleFormat.format(cal.getTime());  
    	        txtDate.setText(dateTitle);
            	dateUrl = urlFormat.format(cal.getTime());     
            	getData();           
            }
        });	 
     // Next day button
        btnNext.setOnClickListener(new View.OnClickListener() {	 
            public void onClick(View arg0) {
            	cal.add(Calendar.DATE, 1);  
            	dateTitle = titleFormat.format(cal.getTime()); 
    	        txtDate.setText(dateTitle); 
            	dateUrl = urlFormat.format(cal.getTime());   
            	getData();
            }
        });	 
        
     }
    
    void getData() {
    	// Displaying Received data
        HTTPInteraction httpobj= new HTTPInteraction();
		try {
			HttpGet request = new HttpGet(infoUrl + dateUrl);
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

    }
}
