package com.example.masproject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	String dateTitle;
	String dateUrl;
	SimpleDateFormat titleFormat = new SimpleDateFormat("EEEE, MM/d/yy");	
	SimpleDateFormat urlFormat = new SimpleDateFormat("y-MM-d");	 
	String infoUrl = "http://dev.m.gatech.edu/developer/pconner3/widget/4261/c/api/events?date=";
	String actUrl = "http://dev.m.gatech.edu/developer/pconner3/widget/4261/c/api/activities";
	String jSessionid;
	String msg;
	List<Map<String, String>> eventList;
	List<Map<String, String>> possList;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    
    JSONArray jadd;
    JSONArray jday;
    JSONObject jobj;
    
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
        Log.i("Activity Screen", jSessionid);        
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
            	getDateData();           
            }
        });	 
        // Next day button
        btnNext.setOnClickListener(new View.OnClickListener() {	 
            public void onClick(View arg0) {
            	cal.add(Calendar.DATE, 1);  
            	dateTitle = titleFormat.format(cal.getTime()); 
    	        txtDate.setText(dateTitle); 
            	dateUrl = urlFormat.format(cal.getTime());   
            	getDateData();
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
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });	 
        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {	 
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();
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
    }
    @SuppressWarnings("unchecked")
	public void mapReply(String src) throws IOException {   
    	//convert string reply to list of mapped event data
    	eventList = new ArrayList<Map<String, String>>();
    	String[] events = src.split("\\},\\{");
    	for (int i = 0;i<events.length;i++) {
    		if (i == 0) {
    	    	events[i] = events[i].substring(1) + "}";    			
    		} else if (i==events.length-1) {
    			events[i] = "{" + events[i].substring(0,events[i].length()-1);    			
    		} else {
    			events[i] = "{" + events[i] + "}";    			
    		}
    		eventList.add((Map<String,String>) new ObjectMapper().readValue(events[i], new TypeReference<Map<String,String>>() {}));
            System.out.println("Got " + eventList.get(i)); 
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
				msg = httpobj.parseResponse(resp);
		        mapReply(msg);
				//jarr = new JSONArray(msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /*Preparing the list data*/
    private void prepareListData() {
    	int i = 0;
    	getDateData();
    	getActData();
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
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
        	//newactivity.add(possList.get(i).get("Name"));
        }    
        List<String> activitylist = new ArrayList<String>();
        for (i=0;i<eventList.size();i++) {
        	activitylist.add(eventList.get(i).get("Name"));
        }
        // putting data in list
        listDataChild.put(listDataHeader.get(0), newactivity);
        listDataChild.put(listDataHeader.get(1), activitylist);
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
				//convert string reply to list of mapped event data
//		    	possList = new ArrayList<Map<String, String>>();
//		    	String[] events = src.split("\\},\\{");
//		    	for (int i = 0;i<events.length;i++) {
//		    		if (i == 0) {
//		    	    	events[i] = events[i].substring(1) + "}";    			
//		    		} else if (i==events.length-1) {
//		    			events[i] = "{" + events[i].substring(0,events[i].length()-1);    			
//		    		} else {
//		    			events[i] = "{" + events[i] + "}";    			
//		    		}
//		    		possList.add((Map<String,String>) new ObjectMapper().readValue(events[i], new TypeReference<Map<String,String>>() {}));
//		            System.out.println("Got " + possList.get(i)); 
//		    	}
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
