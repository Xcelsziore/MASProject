package com.example.masproject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityScreen extends Activity {
	TextView txtDate;
	Calendar cal;
	SimpleDateFormat titleFormat = new SimpleDateFormat("EEEE, MM/d/yy");	
	SimpleDateFormat urlFormat = new SimpleDateFormat("y-MM-d");	 
	String dateTitle;
	String dateUrl;
	String infoUrl = "http://dev.m.gatech.edu/developer/pconner3/widget/4261/c/api/events?date=";
	String actUrl = "http://dev.m.gatech.edu/developer/pconner3/widget/4261/c/api/activities";
	String jSessionid;
    JSONArray jadd;
    JSONArray jday;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_screen);
        super.onCreate(savedInstanceState);
        // Time and date init
        txtDate = (TextView) findViewById(R.id.txtDate);            
        dateTitle = titleFormat.format(new Date());
        dateUrl = urlFormat.format(new Date());     
        txtDate.setText(dateTitle);
        cal = Calendar.getInstance();   
        cal.setTime(new Date());         
        // Getting session cookie from login screen 
        Intent i = getIntent();
        jSessionid = i.getStringExtra("sess");     
        // Buttons
        Button btnPrev = (Button) findViewById(R.id.btnPrev);
        Button btnNext = (Button) findViewById(R.id.btnNext);
        // Prev day button
        btnPrev.setOnClickListener(new View.OnClickListener() {	 
            public void onClick(View arg0) {
            	cal.add(Calendar.DATE, -1);  
            	dateTitle = titleFormat.format(cal.getTime());  
    	        txtDate.setText(dateTitle);
            	dateUrl = urlFormat.format(cal.getTime());       
            	updateListData();
            }
        });	 
        // Next day button
        btnNext.setOnClickListener(new View.OnClickListener() {	 
            public void onClick(View arg0) {
            	cal.add(Calendar.DATE, 1);  
            	dateTitle = titleFormat.format(cal.getTime()); 
    	        txtDate.setText(dateTitle); 
            	dateUrl = urlFormat.format(cal.getTime());   
            	updateListData();
            }
        });	 
        // Get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);	 
        // Preparing list data
        prepareListData();	 
        // setting list adapter
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);	 
        expListView.setAdapter(listAdapter);	 
        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {	 
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                    int groupPosition, long id) {
                 Toast.makeText(getApplicationContext(),
                 "Group Clicked " + listDataHeader.get(groupPosition),
                 Toast.LENGTH_SHORT).show();
                return false;
            }
        });	 
        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {	 
            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        listDataHeader.get(groupPosition) + " Expanded",
//                        Toast.LENGTH_SHORT).show();
            }
        });	 
        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {	 
            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        listDataHeader.get(groupPosition) + " Collapsed",
//                        Toast.LENGTH_SHORT).show();
            }
        });	 
        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {	 
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(),
                listDataHeader.get(groupPosition) + " : "
                + listDataChild.get(listDataHeader.get(groupPosition)).get(
                childPosition), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        // Expand Activity List by default
        expListView.expandGroup(1);
    }
    // Update data when day changes
    private void updateListData() {  
        expListView.collapseGroup(0); 	
        expListView.collapseGroup(1);  
    	int i = 0;
        List<String> activitylist = new ArrayList<String>();
    	getDateData();
        for (i=0;i<jday.length();i++) {
        	try {
				activitylist.add(jday.getJSONObject(i).getString("Name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        listDataChild.put(listDataHeader.get(1), activitylist); 
        expListView.expandGroup(1); 	
    }
    // List data init
    private void prepareListData() {
    	int i = 0;
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        // Getting data from REST
    	getDateData();
    	getActData();
        // Adding header data
        listDataHeader.add("Add New Activity");
        listDataHeader.add("Activity List"); 
        // Adding child data
        List<String> newactivity = new ArrayList<String>();
        for (i=0;i<jadd.length();i++) {
        	try {
				newactivity.add(jadd.getJSONObject(i).getString("Name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }    
        List<String> activitylist = new ArrayList<String>();
        for (i=0;i<jday.length();i++) {
        	try {
				activitylist.add(jday.getJSONObject(i).getString("Name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        // putting data in list
        listDataChild.put(listDataHeader.get(0), newactivity);
        listDataChild.put(listDataHeader.get(1), activitylist);
    }
    // Get data for specific date
    void getDateData() {
        HTTPInteraction httpobj= new HTTPInteraction();
		try {
			HttpGet request = new HttpGet(infoUrl + dateUrl);
			request.setHeader("Cookie", "PHPSESSID=" + jSessionid);
			DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
				HttpResponse resp = httpclient.execute(request);
				String src = httpobj.parseResponse(resp);
				jday = new JSONArray(src);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	// Get all possible activities
	void getActData() {
        HTTPInteraction httpobj= new HTTPInteraction();
		try {
			HttpGet request = new HttpGet(actUrl);
			request.setHeader("Cookie", "PHPSESSID=" + jSessionid);
			DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
				HttpResponse resp = httpclient.execute(request);
				String src = httpobj.parseResponse(resp);
				jadd = new JSONArray(src);				
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
