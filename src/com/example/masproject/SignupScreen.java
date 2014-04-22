package com.example.masproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignupScreen extends Activity {

	 Button okButton;
	 Button cancelButton;
	 TextView inputName;
	 TextView inputDob;
	 TextView inputEmail;
	 TextView inputUname;
	 TextView inputPass1;
	 TextView inputPass2;
	 AlertDialog alert;
	 AlertDialog.Builder builder;
	 String jSessionid;
	 String registerUrl;
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.i("Activity Start","Signup Screen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);
		inputName = (EditText) findViewById(R.id.name);
		inputDob = (EditText) findViewById(R.id.dob);
		inputEmail = (EditText) findViewById(R.id.email);
		inputUname = (EditText) findViewById(R.id.user);
		inputPass1 = (EditText) findViewById(R.id.et_pw1);
		inputPass2 = (EditText) findViewById(R.id.et_pw2);
        
        okButton = (Button) findViewById(R.id.confirm);
        cancelButton = (Button) findViewById(R.id.cancel);        
        okButton.setOnClickListener(okHandler);
        cancelButton.setOnClickListener(cancelHandler);

		@SuppressWarnings("unused")
		HTTPInteraction httpobj= new HTTPInteraction();
        jSessionid = "blank";
        registerUrl = "http://dev.m.gatech.edu/developer/pconner3/widget/4261/c/api/register";
        
        builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                alert.dismiss();
	           }
		});
    }
	
	//Login Button     
    View.OnClickListener okHandler = new View.OnClickListener() {
        public void onClick(View v) {  
        	// Check password match
        	if (inputPass1.getText().toString().compareTo(inputPass2.getText().toString().trim())!=0) {
                System.out.println("Passwords don't match");
    			builder.setMessage("Passwords don't match");
				alert = builder.create();				
				alert.show();	
				return;        		
        	}
            DefaultHttpClient httpclient1 = new DefaultHttpClient();
            HTTPInteraction httpobj= new HTTPInteraction();    
        	//Attempting Signup	
        	try {
    			HttpPost post = new HttpPost(registerUrl);
    		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    		    nameValuePairs.add(new BasicNameValuePair("username", inputUname.getText().toString().trim()));
    		    nameValuePairs.add(new BasicNameValuePair("password", inputPass1.getText().toString().trim()));
    		    nameValuePairs.add(new BasicNameValuePair("name", inputName.getText().toString().trim()));
    		    nameValuePairs.add(new BasicNameValuePair("email", inputEmail.getText().toString().trim()));
    		    nameValuePairs.add(new BasicNameValuePair("dateOfBirth", inputDob.getText().toString().trim()));
    		    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                System.out.println("Attempting signup");
				HttpResponse response = httpclient1.execute(post);
				String msg = httpobj.parseResponse(response);
	            System.out.println(msg);
                if (response.toString().contains("Error")||response.toString().contains("504")) {
                    System.out.println("Registration error");
        			builder.setMessage("Registration error - double check fields or try different username");
    				alert = builder.create();				
    				alert.show();	
    				return;                  	
                }
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	    
            System.out.println("Signup Succesful");
            //Go back to Login   
            Intent nextScreen = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(nextScreen);	
            //Finish to disallow back button access
            finish();
        }
      };
	  // Cancel Button
	  View.OnClickListener cancelHandler = new View.OnClickListener() {
	    public void onClick(View v) {
            Intent nextScreen = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(nextScreen);	        
	    }
	  };       
}

