package com.principalmvl.lojackmykids;

import static com.principalmvl.lojackmykids.Helpers.CommonUtilities.SENDER_ID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
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
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.principalmvl.lojackmykids.listeners.PointCollectorListener;
import com.principalmvl.lojackmykids.models.ExtendedLatLng;

public class ChildActivity extends Activity implements LocationListener,
		PointCollectorListener {
	private Boolean password_set = false;
	private Boolean segue_from_child = false;
	private Boolean device_is_child = false;
	private ArrayList<ExtendedLatLng> Points;
	AtomicInteger msgId = new AtomicInteger();
	public final static String PREFS_NAME = "LJKIDSPrefs";

	// Helper for GPS-Position
	private LocationManager locManager;
	
	GoogleCloudMessaging gcm;
	
	//Creating the point collector object
	PointCollector pointCollector = new PointCollector();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child);

		SharedPreferences values = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);

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
		 * There are two ways to get the position of the child. 1) is to tap 4 times on the screen
		 * 2) To accumulate points that move in succession via a list and pass that to the GCM server
		 * as a bundle
		 */
		
		// GPS Position
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				this);
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

	private void addTouchListener() {
		ImageView image = (ImageView) this
				.findViewById(R.id.child_layout_background_image);
		image.setOnTouchListener(pointCollector);
	}
	
	private ExtendedLatLng getPoint(Location loc){
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

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Points = this.getPoints(location);
		pointCollector.setLocation(location);
		
		new AsyncTask<Void, Object, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
                try {
                    Bundle data = new Bundle();
                        data.putSerializable("gps", Points.lastIndexOf(Points.size()));
                        data.putString("my_action",
                                "send_gps_child");
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
			protected void onPostExecute(String msg) {
			      Log.i(MainActivity.DEBUGTAG, msg);	
			}

        }.execute(null, null, null);     
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void pointsCollected(List<Point> points) {
		Log.i(MainActivity.DEBUGTAG, "[CHILD ACTIVITY] Points collected: "+ points.size());
		
		new AsyncTask() {
			String msg = "";
			
			@Override
			protected Object doInBackground(Object... params) {
				 
	                try {
	                	Bundle data = new Bundle();
                        data.putSerializable("gps", Points.lastIndexOf(Points.size()));
                        data.putString("my_action",
                                "send_gps_child");
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

}
