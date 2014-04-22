package com.example.masproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class TeamScreen extends Activity {

	AlertDialog alert;
	AlertDialog.Builder builder;
	AlertDialog.Builder menubuilder;
	LayoutInflater inflater;
	    
	String jSessionid;
	String teamUrl;
	TextView txtName;
	 
	JSONArray jteam;
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;  
	HashMap<String, String> teamIDs;	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.i("Activity Start","Team Screen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_screen);        
        // Getting session cookie from last screen 
        jSessionid = "blank";
        Intent mI = getIntent();
        jSessionid = mI.getStringExtra("sess");    
        teamUrl = "http://dev.m.gatech.edu/developer/pconner3/widget/4261/c/api/teams";
        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        // Add Team button
        btnAdd.setOnClickListener(new View.OnClickListener() {	 
            public void onClick(View arg0) {
            	addTeamDialog();
            }
        });	 
        // Init builders/inflater
        builder = new AlertDialog.Builder(this);
        menubuilder = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();
		// Get the listview
        expListView = (ExpandableListView) findViewById(R.id.teamexp);	         
        // Preparing list data
        prepareListData();	 
        // Setting list adapter
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);	 
        expListView.setAdapter(listAdapter);	  
        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {	 
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int grouppos, int childpos, long id) {
            	viewTeam(listDataChild.get(listDataHeader.get(grouppos)).get(childpos), grouppos);
                return false;
            }
        });
        //Expand by default
    	if (listDataChild.get(listDataHeader.get(0)).size() > 0)
            expListView.expandGroup(0); 	
    	if (listDataChild.get(listDataHeader.get(1)).size() > 0)
            expListView.expandGroup(1); 
    }

	private void prepareListData() {
    	int i = 0;
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        // Getting data from REST
    	getTeamData();
        // Adding header data
        listDataHeader.add("Teams I Lead"); 
        listDataHeader.add("Teams I've Joined"); 
        // Adding child data
        List<String> newteam = new ArrayList<String>();
        teamIDs = new HashMap<String, String>();
        for (i=0;i<jteam.length();i++) {
        	try {
				newteam.add(jteam.getJSONObject(i).getString("Name"));
				teamIDs.put(jteam.getJSONObject(i).getString("Name"),jteam.getJSONObject(i).getString("TeamID"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }    
        // Set init team data
        setTeamData();
	}
	private void updateList() {  
        expListView.collapseGroup(0); 	
        expListView.collapseGroup(1); 
        getTeamData();
    	setTeamData();
    	if (listDataChild.get(listDataHeader.get(0)).size() > 0)
            expListView.expandGroup(0); 	
    	if (listDataChild.get(listDataHeader.get(1)).size() > 0)
            expListView.expandGroup(1); 
    }
	
	private void setTeamData() {
		List<String> leadlist = new ArrayList<String>();
        List<String> joinlist = new ArrayList<String>();
        for (int i=0;i<jteam.length();i++) {
        	try {
        		if (jteam.getJSONObject(i).getString("Owner").equals("1")) {
        			leadlist.add(jteam.getJSONObject(i).getString("Name"));
        		} else { 
        			joinlist.add(jteam.getJSONObject(i).getString("Name"));
        		}
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        // putting data in list
        listDataChild.put(listDataHeader.get(0), leadlist);
        listDataChild.put(listDataHeader.get(1), joinlist);
	}
	// Get current user's team data
	private void getTeamData() {
		HTTPInteraction httpobj= new HTTPInteraction();
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
		HttpGet request = new HttpGet(teamUrl);
		request.setHeader("Cookie", "PHPSESSID=" + jSessionid);
			HttpResponse resp = httpclient.execute(request);
			String src = httpobj.parseResponse(resp);
			try {
				jteam = new JSONArray(src);
			} catch (JSONException e) {
				e.printStackTrace();
			}		
            Log.i("Team Data ",jteam.toString());						
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

    
	protected void addTeamDialog() {
		Log.i("Add Team", "Add team dialog opened");
		builder.setView(inflater.inflate(R.layout.team_add, null))
		.setTitle("Create New Team")
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	       	public void onClick(DialogInterface dialog, int id) {
	    	   alert.dismiss();
	       	};
		})
		.setPositiveButton("Create", new DialogInterface.OnClickListener() {
           	public void onClick(DialogInterface dialog, int id) {
           		createTeam(txtName.getText().toString().trim());
           	};
		});
		alert = builder.create();
		alert.show();		
    	txtName = (TextView) alert.findViewById(R.id.tname);
	}
	// Create new team via post
    private void createTeam(String teamname) {
        Log.i("Creating new team ", teamname);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost post = new HttpPost(teamUrl);
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    nameValuePairs.add(new BasicNameValuePair("name", teamname));
		    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			post.setHeader("Cookie", "PHPSESSID=" + jSessionid);
			httpclient.execute(post);		
		} catch (Exception e) {
			e.printStackTrace();
		}		
		updateList();
	}    
	private void viewTeam(final String teamname, int group) {
        Log.i("View Team", teamname);
    	builder.setView(inflater.inflate(R.layout.team_view, null))
    	.setTitle("View Team: " + teamname)
    	.setNegativeButton("Close", new DialogInterface.OnClickListener() {
           	public void onClick(DialogInterface dialog, int id) {
        	   alert.dismiss();
           	};
		});
    	if (group == 0) { //If I am the team leader, give delete option 
    		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
	           	public void onClick(DialogInterface dialog, int id) {
	           		deleteTeam(teamname);
	           	};
    		});
    	} else { //If I am not the team leader, give leave option 
    		builder.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
	           	public void onClick(DialogInterface dialog, int id) {
	           		deleteTeam(teamname);
	           	};
    		});
    	}
    	alert = builder.create();
    	alert.show();
    	TextView txtLeader = (TextView) alert.findViewById(R.id.leader);
    	TextView txtmem = (TextView) alert.findViewById(R.id.members);
	    String leader = "blank";
	    String members = "[blank]";
	    for (int i=0;i<jteam.length();i++) {
        	try {
        		if (jteam.getJSONObject(i).getString("Name").equals(teamname)) {
        			leader = jteam.getJSONObject(i).getString("TeamLeader");
        			members = jteam.getJSONObject(i).getString("Members");
        			break;
        		}
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
	    members = members.substring(1, members.length()-1);
	    members = members.replace(",","\n");
	    members = members.replace("\"","");
    	txtLeader.setText(leader);
    	txtmem.setText(members);
	}	
	private void deleteTeam(String teamname) {
		String teamID = teamIDs.get(teamname);
        Log.i("Delete/Leave Team ", teamname+" "+teamID);
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpDelete del = new HttpDelete(teamUrl+"/"+teamID);
			del.setHeader("Cookie", "PHPSESSID=" + jSessionid);
            try {
				httpclient.execute(del);		
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		updateList();
	}

	@Override
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  	    if ( keyCode == KeyEvent.KEYCODE_MENU ) {
  	        Log.i("Key", "MENU pressed");
	    	menubuilder.setTitle("Menu")
	    	.setItems(R.array.menu_array, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int pos) {
	                   if (pos == 0) { // Activity Log	
	                	   Intent nextScreen = new Intent(getApplicationContext(), ActivityScreen.class);
	                       nextScreen.putExtra("sess", jSessionid);
	                       startActivity(nextScreen);
	                       finish();
	                   } else if (pos == 1) { // My Teams
	                	   alert.dismiss();
	                   } else if (pos == 2) { // My Results 
	                       Intent nextScreen = new Intent(getApplicationContext(), ResultsScreen.class);
	                       nextScreen.putExtra("sess", jSessionid);
	                       startActivity(nextScreen);
	                       finish();
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

