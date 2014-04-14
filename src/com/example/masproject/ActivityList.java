package com.example.masproject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;
 
	 
	public class ActivityList extends Activity {
	 
	    ExpandableListAdapter listAdapter;
	    ExpandableListView expListView;
	    List<String> listDataHeader;
	    HashMap<String, List<String>> listDataChild;
	 
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_adder);
	 
	        // get the listview
	        expListView = (ExpandableListView) findViewById(R.id.lvExp);
	 
	        // preparing list data
	        prepareListData();
	 
	        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
	 
	        // setting list adapter
	        expListView.setAdapter(listAdapter);
	 
	        // Listview Group click listener
	        expListView.setOnGroupClickListener(new OnGroupClickListener() {
	 
	            @Override
	            public boolean onGroupClick(ExpandableListView parent, View v,
	                    int groupPosition, long id) {
	                // Toast.makeText(getApplicationContext(),
	                // "Group Clicked " + listDataHeader.get(groupPosition),
	                // Toast.LENGTH_SHORT).show();
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
	                // TODO Auto-generated method stub
	                Toast.makeText(
	                        getApplicationContext(),
	                        listDataHeader.get(groupPosition)
	                                + " : "
	                                + listDataChild.get(
	                                        listDataHeader.get(groupPosition)).get(
	                                        childPosition), Toast.LENGTH_SHORT)
	                        .show();
	                return false;
	            }
	        });
	    }
	 
	    /*
	     * Preparing the list data
	     */
	    private void prepareListData() {
	        listDataHeader = new ArrayList<String>();
	        listDataChild = new HashMap<String, List<String>>();
	 

        // Adding child data
        listDataHeader.add("Add New Activity");
        listDataHeader.add("Activity List");
       // listDataHeader.add("You may add");
 
        // Adding child data
        List<String> newactivity = new ArrayList<String>();
        newactivity.add("Heavy Exercise");
        newactivity.add("TV/Movie");
        newactivity.add("Playing Games on XBOX");
        newactivity.add("Hit the Gym");
        newactivity.add("Homework");
        newactivity.add("Grocery shopping");
    
 
        List<String> activitylist = new ArrayList<String>();
        activitylist.add("Biking/Cycling");
        activitylist.add("Social/Team Building");
        activitylist.add("Turbo");
       // activitylist.add("Grown Ups 2");
       //  activitylist.add("Red 2");
       //  activitylist.add("The Wolverine");
 
        /*List<String> mayAdd = new ArrayList<String>();
        mayAdd.add("2 Guns");
        mayAdd.add("The Smurfs 2");
        mayAdd.add("The Spectacular Now");
       // mayAdd.add("The Canyons");
       //  mayAdd.add("Europa Report");
*/ 
        listDataChild.put(listDataHeader.get(0), newactivity); // Header, Child data
        listDataChild.put(listDataHeader.get(1), activitylist);
       // listDataChild.put(listDataHeader.get(2), mayAdd);
    }
}