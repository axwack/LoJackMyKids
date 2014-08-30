package com.principalmvl.lojackmykids.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.principalmvl.lojackmykids.MainActivity;
import com.principalmvl.lojackmykids.MyLocation;
import com.principalmvl.lojackmykids.MyLocation.LocationResult;
import com.principalmvl.lojackmykids.R;

public class MapViewFragment extends Fragment {
	GoogleMap map;
	LatLng latLng;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.map_view_fragment, container, false);
		// Gets the MapView from the XML layout and creates it
		
		this.setUpMapIfNeeded();
		// Gets to GoogleMap from the MapView and does initialization stuff
		// map = mapView.getMap();
		// map.getUiSettings().setMyLocationButtonEnabled(false);
		// map.setMyLocationEnabled(true);
		LocationResult locationResult = new LocationResult() {

			@Override
			public void gotLocation(Location location) {
				// Got the location!
				String lng = "Longitude: " + location.getLongitude();
				Log.v(MainActivity.DEBUGTAG, lng);
				String lat = "Latitude: " + location.getLatitude();
				Log.v(MainActivity.DEBUGTAG, lat);
				// Creating a LatLng object for the current location
				// Getting latitude of the current location
			    double latitude = location.getLatitude();

			    // Getting longitude of the current location
			    double longitude = location.getLongitude();
				
				latLng = new LatLng(latitude, longitude);

				// Showing the current location in Google Map
				map.animateCamera(CameraUpdateFactory.newCameraPosition(
				           new CameraPosition.Builder().target(new LatLng(latitude, longitude))
				        .zoom(15.5f)
				        .bearing(0)
				        .tilt(25)
				        .build()
				), new GoogleMap.CancelableCallback() {
				    @Override
				    public void onFinish() {
				        // Your code here to do something after the Map is rendered
				    }

				    @Override
				    public void onCancel() {
				        // Your code here to do something after the Map rendering is cancelled
				    }
				});
				
			    //map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		 
			    // Zoom in the Google Map
			    //map.animateCamera(CameraUpdateFactory.zoomTo(15));
			}
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(getActivity(), locationResult);

		return v;
	}

	/*
	 * // Gets the MapView from the XML layout and creates it mapView =
	 * (MapView) v.findViewById(R.id.map); mapView.onCreate(savedInstanceState);
	 * 
	 * // Gets to GoogleMap from the MapView and does initialization stuff map =
	 * mapView.getMap(); map.getUiSettings().setMyLocationButtonEnabled(false);
	 * map.setMyLocationEnabled(true); map.addMarker(new
	 * MarkerOptions().position(new LatLng(50.167003,19.383262)));
	 * 
	 * 
	 * // Needs to call MapsInitializer before doing any CameraUpdateFactory
	 * calls try { MapsInitializer.initialize(this.getActivity()); } catch
	 * (Exception e) { e.printStackTrace(); }
	 * 
	 * // Updates the location and zoom of the MapView CameraUpdate cameraUpdate
	 * = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
	 * map.animateCamera(cameraUpdate);
	 */
	private void setUpMapIfNeeded() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (map == null) {
	        map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
	                            .getMap();
	        // Check if we were successful in obtaining the map.
	        if (map != null) {
	            // The Map is verified. It is now safe to manipulate the map.
	        	map.setMyLocationEnabled(true);
	        }
	    }
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	/*
	 * @Override public void onResume() { mapView.onResume(); super.onResume();
	 * }
	 * 
	 * @Override public void onDestroy() { super.onDestroy();
	 * mapView.onDestroy(); }
	 * 
	 * @Override public void onLowMemory() { super.onLowMemory();
	 * mapView.onLowMemory(); }
	 */
}
