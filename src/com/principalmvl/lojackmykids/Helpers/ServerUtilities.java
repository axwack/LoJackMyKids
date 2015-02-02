/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.principalmvl.lojackmykids.Helpers;

import static com.principalmvl.lojackmykids.Helpers.CommonUtilities.SERVER_URL;
import static com.principalmvl.lojackmykids.Helpers.CommonUtilities.TAG;
import static com.principalmvl.lojackmykids.Helpers.CommonUtilities.displayMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

import com.principalmvl.lojackmykids.MainActivity;
import com.principalmvl.lojackmykids.R;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	/**
	 * Register this account/device pair within the server.
	 *
	 */
	public static void register(final Context context, final String regId)
			throws UnsupportedEncodingException {
		Log.i(MainActivity.DEBUGTAG, "[SERVERUTILITIES : register] Registering device (regId = " + regId
				+ ")");
		String serverUrl = SERVER_URL + "/register";
		
		String message = "";
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		// Once GCM returns a registration id, we need to register it in the
		// demo server. As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				displayMessage(context, context.getString(
						R.string.server_registering, i, MAX_ATTEMPTS));
				
				String code = post(serverUrl, params, 100);
				
				// GCMRegistrar.setRegisteredOnServer(context, true);
				if (code == "200"){
					message = context.getString(R.string.server_registered); // This does not return the correct message 
				}
				else {
					message = context.getString(R.string.server_register_error);
				}
				CommonUtilities.displayMessage(context, message);
				return;
			} catch (IOException e) {
				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).
				Log.e(MainActivity.DEBUGTAG, "Failed to register on attempt "
						+ i + ":" + e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(MainActivity.DEBUGTAG, "Sleeping for " + backoff
							+ " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(MainActivity.DEBUGTAG,
							"Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}
				// increase backoff exponentially
				backoff *= 2;
			}
		}
		message = context.getString(R.string.server_register_error,
				MAX_ATTEMPTS);
		CommonUtilities.displayMessage(context, message);
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	public static void unregister(final Context context, final String regId) {
		Log.i(MainActivity.DEBUGTAG, "unregistering device (regId = " + regId
				+ ")");
		String serverUrl = SERVER_URL + "/unregister";
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		try {
			post(serverUrl, params, 100);
			// GCMRegistrar.setRegisteredOnServer(context, false);
			String message = context.getString(R.string.server_unregistered);
			CommonUtilities.displayMessage(context, message);
		} catch (IOException e) {
			// At this point the device is unregistered from GCM, but still
			// registered in the server.
			// We could try to unregister again, but it is not necessary:
			// if the server tries to send a message to the device, it will get
			// a "NotRegistered" error message and should unregister the device.
			String message = context.getString(
					R.string.server_unregister_error, e.getMessage());
			CommonUtilities.displayMessage(context, message);
		}
	}

	/**
	 * Send a message.
	 */
	public static String send(String url, UrlEncodedFormEntity GPSPosition) throws IOException {

		return post(url, GPSPosition, MAX_ATTEMPTS);
	}

	/**
	 * Issue a POST request to the server.
	 *
	 * @param endpoint
	 *            POST address.
	 * @param params
	 *            request parameters.
	 * @return response
	 * @throws IOException
	 *             propagated from POST.
	 */
	
	private static String executePost(String endpoint, UrlEncodedFormEntity params)
			throws IOException {
		
		 HttpURLConnection connection;
	       OutputStreamWriter request = null;
	            URL url = null;   
	            String result = null;         

		InputStream inputStream = null;

		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		try {           
			
			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(endpoint);
			
			// 3. build jsonObject but don't need here

			// 4. convert JSONObject to JSON to String
						
			// ** Alternative way to convert Person object to JSON string usin
			// Jackson Lib
			// ObjectMapper mapper = new ObjectMapper();
			// json = mapper.writeValueAsString(person);

			// 5. set json to StringEntity
			//StringEntity se = new StringEntity(json);
			
			// 7. Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			//httpPost.setHeader("Content-type", "application/json");
			httpPost.setHeader("Authorization", "key="+CommonUtilities.AP_KEY);
						
			// 6. set httpPost Entity		
			//httpPost.setEntity(new StringEntity(json));
			httpPost.setEntity(params);
		
			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// 9. receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// 10. convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
		Log.i(MainActivity.DEBUGTAG,"[SERVER UTILIITES] HTTP Response: "+ result);
        return result;
	}
	
	private static String executePost(String endpoint, Map<String,String> params)
			throws IOException {
		
		 HttpURLConnection connection;
	       OutputStreamWriter request = null;
	            URL url = null;   
	            String result = null;         

		InputStream inputStream = null;
		int code = 0;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		try {           
			
			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(endpoint);
			
			// 3. build jsonObject but don't need here
			String regId=params.get("regId");
			
			// 4. convert JSONObject to JSON to String
						
			// ** Alternative way to convert Person object to JSON string usin
			// Jackson Lib
			// ObjectMapper mapper = new ObjectMapper();
			// json = mapper.writeValueAsString(person);

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(regId);
			List<org.apache.http.NameValuePair> nameValuePairs = new ArrayList<org.apache.http.NameValuePair>(1);
		    nameValuePairs.add(new BasicNameValuePair("regId", regId));
		    
			// 7. Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			//httpPost.setHeader("Content-type", "application/json");
			httpPost.setHeader("Authorization", "key="+CommonUtilities.AP_KEY);
						
			// 6. set httpPost Entity		

			// httpPost.setEntity(se); This was the old one. This may be needed if the class doesn't work after it's been registered.
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);
			
			code = httpResponse.getStatusLine().getStatusCode();
			
			// 9. receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();
			
				result +=" Code: "+ code;
		
			// 10. convert inputstream to string
			if (inputStream != null)
				result += convertInputStreamToString(inputStream);
			else
				result += "Did not work!";

		} catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
		Log.i(MainActivity.DEBUGTAG,"[SERVER UTILIITES] HTTP Response: "+ result);
		
	
		// 11. return result
		return Integer.toString(code);
	}
	
	 private static String convertInputStreamToString(InputStream inputStream) throws IOException{
	        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	        String line = "";
	        String result = "";
	        while((line = bufferedReader.readLine()) != null)
	            result += line;
	 
	        inputStream.close();
	        return result;
	 
	    }   

	/** Issue a POST with exponential backoff */
	private static String post(String endpoint, UrlEncodedFormEntity params,
			int maxAttempts) throws IOException {
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		for (int i = 1; i <= maxAttempts; i++) {
			Log.d(TAG, "Attempt #" + i + " to connect");
			try {
				return executePost(endpoint, params);
			} catch (IOException e) {
				Log.e(TAG, "Failed on attempt " + i + ":" + e);
				if (i == maxAttempts) {
					throw e;
				}
				try {
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					return null;
				}
				backoff *= 2;
			} catch (IllegalArgumentException e) {
				throw new IOException(e.getMessage(), e);
			}
		}
		return null;
	}
	/** Issue a POST with exponential backoff */
	private static String post(String endpoint, Map<String,String> params,
			int maxAttempts) throws IOException {
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		for (int i = 1; i <= maxAttempts; i++) {
			Log.d(TAG, "Attempt #" + i + " to connect");
			try {
				return executePost(endpoint, params);
			} catch (IOException e) {
				Log.e(TAG, "Failed on attempt " + i + ":" + e);
				if (i == maxAttempts) {
					throw e;
				}
				try {
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					return null;
				}
				backoff *= 2;
			} catch (IllegalArgumentException e) {
				throw new IOException(e.getMessage(), e);
			}
		}
		return null;
	}
}