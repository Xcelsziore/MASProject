package com.example.masproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;

import android.os.StrictMode;

public class HTTPInteraction {

	InputStream is;

	public HTTPInteraction() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.penaltyDialog() // show a dialog
				.permitNetwork() // permit Network access
				.build());
		is = null;
	}

	public String parseResponse(HttpResponse response) {
		 BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        try {
			while ((line = in.readLine()) != null)
			    sb.append(line + NL);
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        String result = sb.toString();
        return result;
	}

}

