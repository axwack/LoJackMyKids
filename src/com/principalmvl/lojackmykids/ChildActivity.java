package com.principalmvl.lojackmykids;

import static com.principalmvl.lojackmykids.Helpers.CommonUtilities.SENDER_ID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.principalmvl.lojackmykids.Helpers.CommonUtilities;
import com.principalmvl.lojackmykids.Helpers.ServerUtilities;
import com.principalmvl.lojackmykids.listeners.PointCollectorListener;
import com.principalmvl.lojackmykids.models.ExtendedLatLng;

public class ChildActivity extends Activity implements LocationListener,
		PointCollectorListener {

	private Boolean password_set = false;
	private Boolean segue_from_child = false;
	private Boolean device_is_child = false;
	private ArrayList<ExtendedLatLng> Points;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";

	AtomicInteger msgId = new AtomicInteger();
	Context context;
	String regid;

	public final static String PREFS_NAME = "LJKIDSPrefs";
	
	// Handle to GCM
	GoogleCloudMessaging gcm;

	// Creating the point collector object
	PointCollector pointCollector = new PointCollector();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child);

		SharedPreferences values = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		/*
		 * Register on GCM
		 */
		// TODO: We need to create a singleton because we also do this from the
		// mainactivity.
		context = getApplicationContext();

		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);

			// Register the Child with GCM
			regid = getRegistrationId(context);

			Log.i(MainActivity.DEBUGTAG, "[CHILD ACTIVITY] Device is registered as "+ regid);
			if (regid.isEmpty()) {
				registerInBackground();
			}
		} else {
			Log.i(MainActivity.DEBUGTAG,
					"No valid Google Play Services APK found.");
		}
		// Get the Values passed in the intent. Looking for two flags:
		// DEVICE_IS_CHILD and Password_SET
		password_set = values.getBoolean(getString(R.string.password_set),
				false);
		device_is_child = values.getBoolean(
				getString(R.string.device_is_child), false);
		segue_from_child = values.getBoolean(
				getString(R.string.segue_from_child), false);
		Log.i(MainActivity.DEBUGTAG, "[ChildActivity] Device_is_child: "
				+ device_is_child + " Password_set :" + password_set
				+ " segue_from_child: " + segue_from_child);
		segue_from_child = false;

		/*
		 * There are two ways to get the position of the child. 1) is to tap 2
		 * times on the screen 2) To accumulate points that move in succession
		 * via a list and pass that to the GCM server as a bundle
		 */
		// Helper for GPS-Position
		LocationManager locManager;
		
		// GPS Position
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				this);
		
		Criteria criteria = new Criteria();
		String provider = locManager.getBestProvider(criteria, false);
		Location location = locManager.getLastKnownLocation(provider);
		
		this.onLocationChanged(location);
		
		// Create the ArrayList to store our points
		Points = new ArrayList<ExtendedLatLng>();

		// Handle the Login Button Click
		Button ChildLoginButton = (Button) findViewById(R.id.child_loginBtn);

		this.addTouchListener();
		pointCollector.setListener(this);

		ChildLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intObj = new Intent(ChildActivity.this,
						LoginActivity.class);

				intObj.putExtra(getString(R.string.password_set), password_set);
				intObj.putExtra(getString(R.string.device_is_child),
						device_is_child);
				intObj.putExtra(getString(R.string.segue_from_child), true);
				startActivity(intObj);
			}
		});

	}

	/*
	 * Register the ID in the background
	 */
	private void registerInBackground(){
		new AsyncTask<Void, Void, String>(){
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(SENDER_ID);
	                Log.i(MainActivity.DEBUGTAG, "Device registered, registration ID=" + regid);

	                // You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.
	                // The request to your server should be authenticated if your app
	                // is using accounts.
	                sendRegistrationIdToBackend();

	                // For this demo: we don't need to send it because the device
	                // will send upstream messages to a server that echo back the
	                // message using the 'from' address in the message.

	                // Persist the regID - no need to register again.
	                storeRegistrationId(context, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
	            return msg;
			}		
		}.execute(null,null,null);	
	}
	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(MainActivity.DEBUGTAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(MainActivity.DEBUGTAG, "[CHILDACTIVITY] Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(MainActivity.DEBUGTAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// Save the REGID in the Shared Preferences
		//
		return getSharedPreferences(ChildActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	/*
	 * Create the addTouchListener on the image
	 */
	private void addTouchListener() {
		ImageView image = (ImageView) this
				.findViewById(R.id.child_layout_background_image);
		image.setOnTouchListener(pointCollector);
	}

	/*
 * 
 	* Gets the points on the latitiude longitude on the map
	 */
	@SuppressWarnings("unused")
	private ExtendedLatLng getPoint(Location loc) {
		ExtendedLatLng latLng = new ExtendedLatLng(loc.getLatitude(),
				loc.getLongitude());
		return latLng;

	}

	private ArrayList<ExtendedLatLng> getPoints(Location loc) {

		/*
		 * Start to get the first point After getting the first point check the
		 * points in the ArrayList and see if it already exists If it exists,
		 * discard it If it doesn't exist check to see if the boundaries are
		 * within the threshold If if the point is within the threshold, it
		 * means that the phone moved a slight distance If the point moved
		 * significantly from the last check point, the phone is moving and we
		 * need that point Increase the capture point timing. CAPTURE_INTERVAL =
		 * 5 minutes If the phone moves significantly (> 30%) from the last
		 * point then increase the interval and capture more
		 */

		ExtendedLatLng latLng = new ExtendedLatLng(loc.getLatitude(),
				loc.getLongitude());

		Log.i(MainActivity.DEBUGTAG, "Childs Lat Lng @: " + latLng.getLat()
				+ " " + latLng.getLng());

		if (Points.size() == 0)
			Points.add(latLng);

		if (getPoint(latLng) != null
				&& latLng.getDistance(Points.get(Points.size() - 1)) > 0) {
			Log.i(MainActivity.DEBUGTAG, "LatLng is new: " + latLng.hashCode());
			Points.add(latLng);
			Log.i(MainActivity.DEBUGTAG, "Points Array Size: " + Points.size());
			Log.i(MainActivity.DEBUGTAG,
					"Distance: "
							+ latLng.getDistance(Points.get(Points.size() - 1)));
		}
		return Points;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void pointsCollected(List<ExtendedLatLng> points) {
		Log.i(MainActivity.DEBUGTAG, "[CHILD ACTIVITY] Points collected: "
				+ points.size());

		new AsyncTask() {
			String msg = "";

			@Override
			protected Object doInBackground(Object... params) {
				// Sends the message using an Async Task.
				try {
					Bundle data = new Bundle();
					data.putSerializable("gps",
							Points.lastIndexOf(Points.size()));
					data.putString("my_action", "send_gps_child");
					String id = Integer.toString(msgId.incrementAndGet());
					gcm.send(SENDER_ID + "@gcm.googleapis.com", id, 0, data);
					msg = "Sent message";
					Log.i(MainActivity.DEBUGTAG, msg);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.i(MainActivity.DEBUGTAG, msg);
				}
				return msg;
			}

			@Override
			protected void onPostExecute(Object result) {
				Log.i(MainActivity.DEBUGTAG, "GCM Message: " + msg);
			}
		}.execute(null, null, null);
	}

	private ExtendedLatLng getPoint(ExtendedLatLng latLng) {

		ExtendedLatLng _latLng = null;
		try {
			if (Points.contains(latLng) == true)
				_latLng = Points.get(Points.indexOf(latLng));
		} catch (NullPointerException e) {
			Log.i(MainActivity.DEBUGTAG, "LatLng is null");
			_latLng = latLng;
		}
		return _latLng;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.child, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 * This method provides location of the gps.
	 */
	@Override
	public void onLocationChanged(Location location) {
		
		
		final ExtendedLatLng latLng = new ExtendedLatLng(location.getLatitude(), location.getLongitude());
		Log.i(MainActivity.DEBUGTAG, "[CHILD ACTIVITY] Lat Lng: " + latLng.getLat() + "," + latLng.getLng());
		
		//Points = this.getPoints(location);
		//pointCollector.setLocation(location);

		new AsyncTask<Void, ExtendedLatLng, String>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[]) This
			 * sends the actual GPS coordinate to GCM
			 */
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";			
							
				try {
					List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
					data.add(new BasicNameValuePair("reg_ids", regid)); //TODO: this needs to be able to take an array of Regids..how does RegId get multiple?
					data.add(new BasicNameValuePair("lat", Double.toString(latLng.getLat())));
					data.add(new BasicNameValuePair("lng", Double.toString(latLng.getLng())));
					data.add(new BasicNameValuePair("API_KEY", CommonUtilities.AP_KEY));
					UrlEncodedFormEntity GPSPosition = new UrlEncodedFormEntity(data, "UTF-8");
					
					//String id = Integer.toString(msgId.incrementAndGet());
					
					//gcm.send(SENDER_ID + "@gcm.googleapis.com", id, 0, data);
					ServerUtilities.send(CommonUtilities.SERVER_URL+"sendHttp", GPSPosition);
					msg = "Sent message : ";
					
					Log.i(MainActivity.DEBUGTAG, "[CHILD ACTIVITY] JSON: " + GPSPosition.toString());
					msg+=data.toString();
					
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.i(MainActivity.DEBUGTAG, "[CHILD ACTIVITY] " + msg);
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				
			}

		}.execute(null, null, null);
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(MainActivity.DEBUGTAG, "[ChildActivity] Saving regId on app version "
				+ appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
		Log.i(MainActivity.DEBUGTAG, "[CHILDACTIVITY] sendRegistrationIdToBackend()");
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

}