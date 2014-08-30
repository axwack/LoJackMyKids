package com.principalmvl.lojackmykids;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.principalmvl.lojackmykids.Database.Database;
import com.principalmvl.lojackmykids.fragments.SaveAddressDialog;

public class KnownLocationsActivity extends Activity implements
		SaveAddressDialog.NoticeDialogListener {

	GoogleMap map;
	MarkerOptions markerOptions;
	LatLng latLng;
	private PointCollector pointCollector = new PointCollector();
	private Database db;
	private List<Address> address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_known_locations);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new KnownLocationFragment()).commit();
			db = new Database(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate
		// the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.known_locations, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//
		// Handle action bar item clicks here. The action bar will //
		// automatically handle clicks on the Home/Up button, so long // as you
		// specify a parent activity in AndroidManifest.xml.
		int id =

		item.getItemId();
		if (id == R.id.delete_db_settings) {
			Log.i(MainActivity.DEBUGTAG, "Delete Database...");
			Log.i(MainActivity.DEBUGTAG,
					"Count of Rows Deleted:" + db.deleteDB());
			Toast.makeText(this, "Database deleted.", Toast.LENGTH_LONG).show();
			return true;
		} else if (id == R.id.see_known_locations_menu) {
			Log.i(MainActivity.DEBUGTAG,
					"Going to Known Locations List Activity...");
			Intent intent = new Intent(this, KnownListActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	// The dialog fragment receives a reference to this Activity through the
	// Fragment.onAttach() callback, which it uses to call the following methods
	// defined by the NoticeDialogFragment.NoticeDialogListener interface
	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// User touched the dialog's positive button
		Log.i(MainActivity.DEBUGTAG, "Observer Positive Click");

		if (db.getKnownLocation(address.get(0).getLatitude(), address.get(0)
				.getLongitude()) == "") {
			Toast.makeText(this, "Address already exists", Toast.LENGTH_LONG)
					.show();
		} else {
			// TODO Need code to collapse keybaord

			if (db.insertKnownLocation(address.get(0))) {
				Toast.makeText(this, "Address has been saved.",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Error with Database", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// User touched the dialog's negative button
		Log.i(MainActivity.DEBUGTAG, "Observer Negative Click");
	}

	private void callBackFromAddressFind(List<Address> addresses) {

		Log.i(MainActivity.DEBUGTAG, "Addresses in callback :" + addresses);
		// Do we want to save this addresss?

		DialogFragment dialog = new SaveAddressDialog();
		dialog.show(getFragmentManager(), "Dialog");
		address = addresses;
	}

	/**
	 * A Known Location fragment containing a simple view.
	 */
	public class KnownLocationFragment extends Fragment {

		public KnownLocationFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_known_locations,
					container, false);
			return rootView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

			super.onActivityCreated(savedInstanceState);
			addFindBtn();
			Log.i(MainActivity.DEBUGTAG, "Back to onActivityCreated");
		}

		private void addFindBtn() {
			Button btn_find = (Button) findViewById(R.id.btn_find);
			final EditText edit_text = (EditText) findViewById(R.id.et_location);

			btn_find.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Log.i(MainActivity.DEBUGTAG,
							"Find_Btn clicked. Address is: "
									+ edit_text.getText());
					// Getting user input location
					// String location = edit_text.getText().toString();

					String location = "97 Lincoln Ave. Cliffside Park, nj";
					if (location != null && !location.equals("")) {
						new GeocoderTask().execute(location);
					}

				}
			});
		}
	}

	// An AsyncTask class for accessing the GeoCoding Web Service
	public class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

		private static final int zoomLevel = 10;

		@Override
		protected List<Address> doInBackground(String... locationName) {
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(getBaseContext());

			List<Address> addresses = null;

			try {
				// Getting a maximum of 3 Address that matches the input text
				addresses = (List<Address>) geocoder.getFromLocationName(
						locationName[0], 3);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return addresses;
		}

		@Override
		protected void onPostExecute(List<Address> addresses) {

			if (addresses == null) {
				Toast.makeText(getBaseContext(), "No Location found",
						Toast.LENGTH_SHORT).show();
			}
			setUpMapIfNeeded();
			// Clears all the existing markers on the map
			map.clear();

			// Adding Markers on Google Map for each matching address

			final List<Address> address = addresses;

			for (int i = 0; i < address.size(); i++) {
				// Creating an instance of GeoPoint, to display in Google Map
				latLng = new LatLng(address.get(i).getLatitude(), address
						.get(i).getLongitude());
				Log.i(MainActivity.DEBUGTAG, "Address at: " + latLng.toString());

				String addressText = String.format("%s, %s", address.get(i)
						.getMaxAddressLineIndex() > 0 ? address.get(i)
						.getAddressLine(0) : "", address.get(i)
						.getCountryName());

				markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				markerOptions.title(addressText);

				Log.i(MainActivity.DEBUGTAG, "Adding Marker...");
				map.addMarker(markerOptions);
			}
			// Locate the first location

			map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
					this.zoomLevel));

			callBackFromAddressFind(address);
			/*
			 * map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
			 * this.zoomLevel), new CancelableCallback() {
			 * 
			 * @Override public void onFinish() { DialogFragment newFragment =
			 * new SaveAddressDialog(); newFragment.show(getFragmentManager(),
			 * "saveAddress"); callBackFromAddressFind(address); }
			 * 
			 * @Override public void onCancel() {
			 * Toast.makeText(getBaseContext(), "Animation to Sydney canceled",
			 * Toast.LENGTH_SHORT) .show(); } });
			 */
			Log.i(MainActivity.DEBUGTAG, "Zooming to " + zoomLevel);
		}

		private void setUpMapIfNeeded() {
			// Do a null check to confirm that we have not already instantiated
			// the map.
			if (map == null) {
				map = ((MapFragment) getFragmentManager().findFragmentById(
						R.id.known_locations_map)).getMap();

				// Check if we were successful in obtaining the map.
				if (map != null) {
					// The Map is verified. It is now safe to manipulate the
					// map.
					map.setMyLocationEnabled(true);
				}
			}
		}

	}

}
