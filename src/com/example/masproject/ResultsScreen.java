package com.example.masproject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.achartengine.GraphicalView;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultsScreen extends Activity {

	 Button nextB;
	 Button prevB; 		 
	 TextView txtWeek;	 
	 Calendar cal; 
	 Calendar cal2;
	 SimpleDateFormat dateFormat = new SimpleDateFormat("MM/d/yy");
	 SimpleDateFormat urlFormat = new SimpleDateFormat("y-MM-d");		 
	 AlertDialog alert;
	 AlertDialog.Builder builder;
	 AlertDialog.Builder menubuilder;	 
	 String jSessionid;
	 String reportUrl;
	 LineGraph myGraph;
	 String startdateTitle;
	 String enddateTitle;
	 String startdateUrl;
	 String enddateUrl;
	 JSONArray jweek;
	 double[] physPts;
	 double[] mentalPts;
	 double[] socialPts;
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.i("Activity Start","Results Screen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_screen);
        //init arrays
        physPts = new double[7];
        mentalPts = new double[7];
        socialPts = new double[7];
        nextB = (Button) findViewById(R.id.nextW);
        prevB = (Button) findViewById(R.id.prevW);		
        txtWeek = (TextView) findViewById(R.id.weekof); 
        // Prev week button
        prevB.setOnClickListener(new View.OnClickListener() {	 
            public void onClick(View arg0) {
            	cal.add(Calendar.DATE, -13);  
                startdateUrl = urlFormat.format(cal.getTime()); 
                startdateTitle = dateFormat.format(cal.getTime()); 
            	cal.add(Calendar.DATE, 6); 
                enddateUrl = urlFormat.format(cal.getTime()); 
                enddateTitle = dateFormat.format(cal.getTime());   
                txtWeek.setText("Week of "+startdateTitle+" - "+enddateTitle);
            	updateGraph();
            }
        });	 
        // Next week button
        nextB.setOnClickListener(new View.OnClickListener() {	 
            public void onClick(View arg0) {
            	if (cal.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR)) {
	            	cal.add(Calendar.DATE, 1); 
	                startdateUrl = urlFormat.format(cal.getTime()); 
	                startdateTitle = dateFormat.format(cal.getTime()); 
	            	cal.add(Calendar.DATE, 6); 
	                enddateUrl = urlFormat.format(cal.getTime()); 
	                enddateTitle = dateFormat.format(cal.getTime());
	                txtWeek.setText("Week of "+startdateTitle+" - "+enddateTitle);
	            	updateGraph();
            	}
            }
        });
        // Time and date init     
        cal = Calendar.getInstance(); 
        cal2 = Calendar.getInstance();     
        cal.setTime(new Date());   
        cal2.setTime(new Date());         
        enddateUrl = urlFormat.format(cal.getTime()); 
        enddateTitle = dateFormat.format(cal.getTime());      
    	cal.add(Calendar.DATE, -6);  
        startdateUrl = urlFormat.format(cal.getTime()); 
        startdateTitle = dateFormat.format(cal.getTime()); 
        txtWeek.setText("Week of "+startdateTitle+" - "+enddateTitle);
    	cal.add(Calendar.DATE, 6);          
        // Getting session cookie from last screen 
        jSessionid = "blank";
        Intent mI = getIntent();
        jSessionid = mI.getStringExtra("sess");   
        reportUrl = "http://dev.m.gatech.edu/developer/pconner3/widget/4261/c/api/reports?startDate=";
        
        builder = new AlertDialog.Builder(this);
        menubuilder = new AlertDialog.Builder(this);
		@SuppressWarnings("unused")
		HTTPInteraction httpobj= new HTTPInteraction();  
		//init graph
		updateGraph();
    }
	private void updateGraph() {
		getMyWeekData();		
		for (int i=0;i<jweek.length();i++) {
        	try {
        		physPts[i] = Double.valueOf(jweek.getJSONObject(i).getString("PhysicalPoints"));
        		mentalPts[i] = Double.valueOf(jweek.getJSONObject(i).getString("MentalPoints"));
        		socialPts[i] = Double.valueOf(jweek.getJSONObject(i).getString("SocialPoints"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
		myGraph = new LineGraph();
    	GraphicalView gView = myGraph.getView(this, physPts, mentalPts, socialPts);        
    	LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
    	layout.removeAllViews();
    	layout.addView(gView);
	}
	// Get data for specific date
    private void getMyWeekData() {
        HTTPInteraction httpobj= new HTTPInteraction();
		try {
			HttpGet request = new HttpGet(reportUrl+startdateUrl+"&endDate="+enddateUrl);
			request.setHeader("Cookie", "PHPSESSID=" + jSessionid);
			DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
				HttpResponse resp = httpclient.execute(request);
				String src = httpobj.parseResponse(resp);
				jweek = new JSONArray(src);
                Log.i("My Week Data ",jweek.toString()+jweek.length());				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ( keyCode == KeyEvent.KEYCODE_MENU ) {
	        Log.i("Key", "MENU pressed");
	    	menubuilder.setTitle("Menu")
	    	.setItems(R.array.menu_array, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int pos) {
	                   if (pos == 0) { // My Activities
	                       Intent nextScreen = new Intent(getApplicationContext(), ActivityScreen.class);
	                       nextScreen.putExtra("sess", jSessionid);
	                       startActivity(nextScreen);
	                       finish();
	                   } else if (pos == 1) { // My Teams
	                       Intent nextScreen = new Intent(getApplicationContext(), TeamScreen.class);
	                       nextScreen.putExtra("sess", jSessionid);
	                       startActivity(nextScreen);
	                       finish();
	                   } else if (pos == 2) { // My Results 
	                	   alert.dismiss();
	                   } else if (pos == 3) { // Log Out  
	                       Intent nextScreen = new Intent(getApplicationContext(), LoginScreen.class);
	                       startActivity(nextScreen);
	                       finish();
	                   }
	                   return;
	               }
	        })
	    	.setNegativeButton("Close", new DialogInterface.OnClickListener() {
	           	public void onClick(DialogInterface dialog, int id) {
		        	alert.dismiss();
	           	};
			});
	    	alert = menubuilder.create();
	    	alert.show();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}       
}

