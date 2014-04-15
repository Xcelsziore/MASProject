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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

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
    AlertDialog alert;
    AlertDialog.Builder addbuilder;
    AlertDialog.Builder editbuilder;
    //AlertDialog.Builder viewbuilder;
    LayoutInflater inflater;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_screen);
        super.onCreate(savedInstanceState);
        addbuilder = new AlertDialog.Builder(this);
        editbuilder = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();
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
            	updateList();
            }
        });	 
        // Next day button
        btnNext.setOnClickListener(new View.OnClickListener() {	 
            public void onClick(View arg0) {
            	cal.add(Calendar.DATE, 1);  
            	dateTitle = titleFormat.format(cal.getTime()); 
    	        txtDate.setText(dateTitle); 
            	dateUrl = urlFormat.format(cal.getTime());   
            	updateList();
            }
        });	 
        // Get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);	 
        // Preparing list data
        prepareListData();	 
        // Setting list adapter
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);	 
        expListView.setAdapter(listAdapter);	  
        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {	 
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
            	if (groupPosition == 0) { // Add Activity
            		addActivity(childPosition);
            	} else if (groupPosition == 1) { // Edit/Delete Activity
            		editActivity(childPosition);
            	} else if (groupPosition == 2) { // View Activity           		
            	}
                return false;
            }
        });
        // Expand List by default
    	if (listDataChild.get(listDataHeader.get(1)).size() > 0)
            expListView.expandGroup(1); 	
    	if (listDataChild.get(listDataHeader.get(2)).size() > 0)
            expListView.expandGroup(2); 
    	// Instantiate an AlertDialog.Builder
    }
    private void addActivity(int childpos) {
    	addbuilder.setView(inflater.inflate(R.layout.alert_view, null))
    	.setTitle("Add Activity: " + listDataChild.get(listDataHeader.get(0)).get(childpos))
    	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	           	public void onClick(DialogInterface dialog, int id) {
	        	   alert.dismiss();
	           	};
		})
    	.setPositiveButton("Save", new DialogInterface.OnClickListener() {
	           	public void onClick(DialogInterface dialog, int id) {
	        	   alert.dismiss();
	           	};
		});
    	alert = addbuilder.create();
    	alert.show();
		TextView text = (TextView) alert.findViewById(R.id.desc);
		text.setText("Android custom dialog " + childpos);
    }
    private void editActivity(int childpos) {
    	//listDataChild.get(listDataHeader.get(1)).get(childpos);
    	editbuilder.setTitle("Edit Activity")
    	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	           	public void onClick(DialogInterface dialog, int id) {
	        	   alert.dismiss();
	           	};
		})
    	.setPositiveButton("Save", new DialogInterface.OnClickListener() {
	           	public void onClick(DialogInterface dialog, int id) {
	        	   alert.dismiss();
	           	};
		});
    	alert = editbuilder.create();
    	alert.show();
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
        listDataHeader.add("Self-Reported Activities"); 
        listDataHeader.add("Auto-Reported Activities"); 
        // Adding child data
        List<String> newactivity = new ArrayList<String>();
        for (i=0;i<jadd.length();i++) {
        	try {
				newactivity.add(jadd.getJSONObject(i).getString("Name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }    
        // Putting data in list
        listDataChild.put(listDataHeader.get(0), newactivity);
        // Set init date data
        setDateData();
    }
    void setDateData() {
    	List<String> selfactivitylist = new ArrayList<String>();
        List<String> autoactivitylist = new ArrayList<String>();
        for (int i=0;i<jday.length();i++) {
        	try {
                Log.i("TPE ",jday.getJSONObject(i).getString("ThirdPartyEntry"));
        		if (jday.getJSONObject(i).getString("ThirdPartyEntry").equals("0")) {
        			selfactivitylist.add(jday.getJSONObject(i).getString("Name") + "\n"
        				+ jday.getJSONObject(i).getString("Hours") + " hours");
        		} else { 
        			autoactivitylist.add(jday.getJSONObject(i).getString("Name") + "\n"
            				+ jday.getJSONObject(i).getString("Hours") + " hours");
        		}
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        // putting data in list
        listDataChild.put(listDataHeader.get(1), selfactivitylist);
        listDataChild.put(listDataHeader.get(2), autoactivitylist);
    }
    // Update data when day changes
    private void updateList() {  
        expListView.collapseGroup(0); 	
        expListView.collapseGroup(1); 
        expListView.collapseGroup(2);  
        getDateData();
    	setDateData();
    	if (listDataChild.get(listDataHeader.get(1)).size() > 0)
            expListView.expandGroup(1); 	
    	if (listDataChild.get(listDataHeader.get(2)).size() > 0)
            expListView.expandGroup(2); 
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
                Log.i("Date Data ",jday.toString());
				
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
