package com.example.masproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.StrictMode;
import android.util.Log;

public class HTTPInteraction {

	InputStream is;

	public HTTPInteraction() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.penaltyDialog() // show a dialog
				.permitNetwork() // permit Network access
				.build());
		is = null;
	}

	public InputStream httpRequest(String url,
			ArrayList<NameValuePair> nameValuePairs) {

		// http post
		try {

			System.out.println("test1");
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			System.out.println("test2");
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			System.out.println(is + "response");

		}

		catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
		}
		return is;
	}

	public String parseResponse(HttpResponse response) {
		 BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        try {
			while ((line = in.readLine()) != null) {
			    sb.append(line + NL);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String result = sb.toString();
        return result;
	}

}

