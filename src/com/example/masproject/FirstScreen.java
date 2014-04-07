package com.example.masproject;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

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

public class FirstScreen extends Activity {

	 Button loginButton;
	 Button signupButton;
	 TextView inputName;
	 TextView inputPassword;
	 AlertDialog alert;
	 AlertDialog.Builder builder;
	 String jSessionid;
	 String msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
		inputName = (EditText) findViewById(R.id.et_un);
		inputPassword = (EditText) findViewById(R.id.et_pw);
        
        loginButton = (Button) findViewById(R.id.button1);
        signupButton = (Button) findViewById(R.id.button2);        
        loginButton.setOnClickListener(myhandler1);
        signupButton.setOnClickListener(myhandler2);
        
        jSessionid = "blank";
        builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                alert.dismiss();
	           }
		});
		@SuppressWarnings("unused")
		HTTPInteraction httpobj= new HTTPInteraction();
    }
	
	//Login Button     
    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {       	
            //Attempting Login            
            HttpGet httget = new HttpGet("http://dev.m.gatech.edu/developer/pconner3/widget/4261/c/api/login?username=test1&password=test");
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
				HttpResponse response = httpclient.execute(httget);
                System.out.println(response.toString());
	            List<Cookie> cookies = httpclient.getCookieStore().getCookies();
	            if (cookies.isEmpty()) {
	                System.out.println("no cookie found");
	    			builder.setMessage("no cookie found - invalid login");
					alert = builder.create();				
					alert.show();	
					return;
	            } else {
	                for (int i = 0; i < cookies.size(); i++) {
	                    Cookie cookie = cookies.get(i);
                        Log.i("Cookies","- " + cookies.get(i).toString());
	                    if(cookie.getName().equals("PHPSESSID")){
	                        jSessionid = cookie.getValue();
	                        break;
	                    }
	                }
	            }
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	

        	//Creating a new Intent
            Intent nextScreen = new Intent(getApplicationContext(), SecondScreen.class);
            //Sending data to another Activity
            nextScreen.putExtra("name", inputName.getText().toString());
            nextScreen.putExtra("sess", jSessionid);
        	//Starting new Intent
            startActivity(nextScreen);
        }
      };
	  // New Account Button
	  View.OnClickListener myhandler2 = new View.OnClickListener() {
	    public void onClick(View v) {
        	//Creating a new Intent
            //Intent nextScreen = new Intent(getApplicationContext(), SignupScreen.class);
        	//Starting new Intent
            //startActivity(nextScreen);
	        
	    }
	  };       
}

