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
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityScreen extends Activity {
	TextView txtDate;
	TextView txtDesc;
	Calendar cal;
	SimpleDateFormat titleFormat = new SimpleDateFormat("EEEE, MM/d/yy");	
	SimpleDateFormat urlFormat = new SimpleDateFormat("y-MM-d");	 
	String dateTitle;
	String dateUrl;
	String eventUrl = "http://dev.m.gatech.edu/developer/pconner3/widget/4261/c/api/events";
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
    ArrayList<String> hourslist;
    StableArrayAdapter adapter;
    Map<String, String> activityIDs;
    double mHours;
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
        Intent mI = getIntent();
        jSessionid = mI.getStringExtra("sess");     
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
    	// Init hours array
    	String[] hourvals = new String[] { "0.5", "1.0", "1.5",
                "2.0", "2.5", "3.0", "3.5", "4.0",
                "4.5", "5.0", "5.5", "6.0", "6.5", "7.0",
                "7.5", "8.0", "8.5", "9.0", "9.5", "10.0"};
        hourslist = new ArrayList<String>();
        for (int i = 0; i < hourvals.length; ++i) {
        	hourslist.add(hourvals[i]);
        }
        adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, hourslist);
    }
    private void addActivity(final int childpos) {
    	addbuilder.setView(inflater.inflate(R.layout.alert_view, null))
    	.setTitle("Add Activity: " + listDataChild.get(listDataHeader.get(0)).get(childpos))
    	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           	public void onClick(DialogInterface dialog, int id) {
        	   alert.dismiss();
           	};
		})
    	.setPositiveButton("Save", new DialogInterface.OnClickListener() {
           	public void onClick(DialogInterface dialog, int id) {
           		addDateData(listDataChild.get(listDataHeader.get(0)).get(childpos),
  	    			  mHours, txtDesc.getText().toString());
        		updateList();
	        	alert.dismiss();
           	};
		});
    	alert = addbuilder.create();
    	alert.show();
    	txtDesc = (TextView) alert.findViewById(R.id.desc);
    	txtDesc.setText("SelfReport-Android");
		ListView lv = (ListView) alert.findViewById(R.id.hours);
	    lv.setAdapter(adapter);
	    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	      @Override
	      public void onItemClick(AdapterView<?> p,View v,int pos,long id) {
	    	  mHours = 0.5*(pos+1);
	      }
	    });
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
        activityIDs = new HashMap<String, String>();
        for (i=0;i<jadd.length();i++) {
        	try {
				newactivity.add(jadd.getJSONObject(i).getString("Name"));
				activityIDs.put(jadd.getJSONObject(i).getString("Name"),jadd.getJSONObject(i).getString("ActivityID"));
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
                //Log.i("Act Data ",jadd.toString());						
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
			HttpGet request = new HttpGet(eventUrl+"?date="+dateUrl);
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
	// Add New Activity via POST
	void addDateData(String actname, double numhours, String mynote) {
		String myID = activityIDs.get(actname);
        //Log.i("Add Date Data ", actname+" "+myID+" "+numhours+" "+mynote);
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost(eventUrl);
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    nameValuePairs.add(new BasicNameValuePair("date", dateUrl));
		    nameValuePairs.add(new BasicNameValuePair("activityID", myID));
		    nameValuePairs.add(new BasicNameValuePair("note", mynote));
		    nameValuePairs.add(new BasicNameValuePair("hours", ""+numhours));
		    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			post.setHeader("Cookie", "PHPSESSID=" + jSessionid);
            try {
				httpclient.execute(post);		
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    private class StableArrayAdapter extends ArrayAdapter<String> {
	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	    public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
	      super(context, textViewResourceId, objects);
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }
    }
}
