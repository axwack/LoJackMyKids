package com.principalmvl.lojackmykids;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class CurrentLocation {

	LocationManager lm;
	LocationResult locationResult;
	boolean gps_enabled = false;
	boolean network_enabled = false;

	public boolean getLocation(Context context, LocationResult result) {
		// I use LocationResult callback class to pass location value from
		// MyLocation to user code.
		locationResult = result;
		if (lm == null)
			lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

		// exceptions will be thrown if provider is not permitted.
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			network_enabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		// don't start listeners if no provider is enabled
		if (!gps_enabled && !network_enabled){
			Toast.makeText(context,"Turn on GPS.", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (gps_enabled)
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
					locationListenerGps);
		if (network_enabled)
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
					locationListenerNetwork);

		LocationResult locationResult = GetLastLocation();
		return true;
	}
	
	public LocationResult GetLastLocation(){
		lm.removeUpdates(locationListenerGps);
		lm.removeUpdates(locationListenerNetwork);

		Location net_loc = null, gps_loc = null;
		if (gps_enabled)
			gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (network_enabled)
			net_loc = lm
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		// if there are both values use the latest one
		if (gps_loc != null && net_loc != null) {
			if (gps_loc.getTime() > net_loc.getTime())
				locationResult.gotLocation(gps_loc);
			else
				locationResult.gotLocation(net_loc);
			return locationResult;
		}

		if (gps_loc != null) {
			locationResult.gotLocation(gps_loc);
			return locationResult;
		}
		if (net_loc != null) {
			locationResult.gotLocation(net_loc);
			return locationResult;
		}
		locationResult.gotLocation(null);
		return locationResult;
	}

	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			locationResult.gotLocation(location);
			/*
			 * Toast.makeText(this,"Location changed : Lat: " +
			 * location.getLatitude()+ " Lng: " + location.getLongitude(),
			 * Toast.LENGTH_SHORT).show();
			 */
			String longitude = "Longitude: " + location.getLongitude();
			Log.v(MainActivity.DEBUGTAG, longitude);
			String latitude = "Latitude: " + location.getLatitude();
			Log.v(MainActivity.DEBUGTAG, latitude);

			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerNetwork);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerGps);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}