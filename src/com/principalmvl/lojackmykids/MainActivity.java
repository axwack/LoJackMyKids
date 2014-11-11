package com.principalmvl.lojackmykids;

import static com.principalmvl.lojackmykids.Helpers.CommonUtilities.SENDER_ID;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.principalmvl.lojackmykids.Adapters.TabsPagerAdapter;
import com.principalmvl.lojackmykids.Helpers.ServerUtilities;

public class MainActivity extends FragmentActivity implements TabListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener // ,PointCollectorListener
{
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9001;
	private final static int KNOWN_LOCATIONS_RESULT = 1000;
	private final static int SETTINGS_RESULT = 1001;
	public static final String DEBUGTAG = "VJL";

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "22298386436";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private SharedPreferences sharedPref;
	boolean device_is_child, password_set;
	public final static String PREFS_NAME = "LJKIDSPrefs";

	private String[] tabNames = { "Map", "Reporting" };
	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	// String SENDER_ID =
	// com.principalmvl.lojackmykids.Helpers.CommonUtilities.SENDER_ID; //
	// PROJECT
	// ID:
	// https://console.developers.google.com/project/apps~metal-complex-658
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	Context context;
	String regId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = this.getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (String tab_name : tabNames) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));

			// Get Values passed in the intent object

			Intent intename = getIntent();
			
			// Get the Values passed in the intent. Looking for two flags:
			// DEVICE_IS_CHILD and Password_SET
			password_set = intename.getBooleanExtra(
					getString(R.string.password_set), false);
			
			device_is_child = intename.getBooleanExtra(
					getString(R.string.device_is_child), false);

			Log.i(DEBUGTAG, "Password flag passed is " + password_set);
			Log.i(DEBUGTAG, "Device is child flag passed is " + device_is_child);
			Log.i(DEBUGTAG, "Intent is  " + getIntent());

		}
		/*
		 * Check the shared preference to see if the system is admin or child.
		 * Also check to see if password is set.
		 */

		// addTouchListener();
		// this.pointCollector.setListener(this);

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int tabPosition) {
				actionBar.setSelectedNavigationItem(tabPosition);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		context = getApplicationContext();

		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices()) {
			// gcm = GoogleCloudMessaging.getInstance(this);
			regId = getRegistrationId(context);

			// if (regId.isEmpty()) {
			registerInBackground();
			// }

		} else {
			Log.i(DEBUGTAG, "No valid Google Play Services APK found.");
		}
	}

	@Override
	protected void onStart() {

		super.onStart();
		// Connect the client.
		// mLocationClient.connect();

	}

	@Override
	protected void onStop() {

		super.onStop();
		// Disconnecting the client invalidates it.
		// mLocationClient.disconnect();
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
		Log.i(MainActivity.DEBUGTAG, "[getRegistrationId] Registration Id: "
				+ registrationId);

		if (registrationId.isEmpty()) {
			Log.i(DEBUGTAG,
					"Existing Registration not found. We need a new one from the server...");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(DEBUGTAG, "App version changed.");
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
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void registerInBackground() {
		new AsyncTask() {
			@SuppressWarnings("unused")
			protected void onPostExecute(String regId) {
				Log.i(DEBUGTAG, "Got RegID: " + regId);
			}

			@Override
			protected String doInBackground(Object... params) {
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(SENDER_ID);
					Log.i(DEBUGTAG, "Device registered, registration ID="
							+ regId);
					// You should send the registration ID to your server over
					// HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your
					// app.
					// The request to your server should be authenticated if
					// your app
					// is using accounts.
					sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the
					// device
					// will send upstream messages to a server that echo back
					// the
					// message using the 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					Log.i(MainActivity.DEBUGTAG, "Error :" + ex.getMessage());
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return regId;

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
		Log.i(DEBUGTAG, "Saving regId on app version " + appVersion);
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
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void sendRegistrationIdToBackend()
			throws UnsupportedEncodingException {
		ServerUtilities.register(context, regId);
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
				Log.i(DEBUGTAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			Intent i = new Intent(this, SettingsActivity.class);
			startActivityForResult(i, SETTINGS_RESULT);

		} else if (id == R.id.set_known_locations) {
			Log.i(DEBUGTAG, "Going to Known Locations Activity...");
			this.goToKnownLocations();
		} else if (id == R.id.set_client_only_view) {
			// Check to see if this view is going to be admin or a Child
			sharedPref = this.getPreferences(Context.MODE_PRIVATE);
			device_is_child = sharedPref.getBoolean(
					getString(R.string.device_is_child), false);

			item.setChecked(false);

			if (item.isChecked()) {
				item.setChecked(true);
				device_is_child = true;
				SharedPreferences customSharedPreference = getSharedPreferences(
						PREFS_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = customSharedPreference.edit();

				editor.putBoolean(getString(R.string.device_is_child),
						device_is_child);
				Log.i(MainActivity.DEBUGTAG, "[MainActivity] Device is child "+ device_is_child);
				Log.i(MainActivity.DEBUGTAG, "Setting Device is child to Shared Preferences");
				editor.putBoolean(getString(R.string.segue_from_child), true);
				editor.putBoolean(getString(R.string.password_set), true);
				editor.commit();
				Intent i = new Intent(this, ChildActivity.class);
				Log.i(MainActivity.DEBUGTAG, "MAINACTIVITY => CHILDACTIVITY");
				startActivity(i);
			} else
				item.setChecked(true);
		}
		return super.onOptionsItemSelected(item);
	}

	private void goToKnownLocations() {
		Log.i(DEBUGTAG, "...starting Known Locations Activity");
		Intent i = new Intent(this, KnownLocationsActivity.class);
		this.startActivityForResult(i, KNOWN_LOCATIONS_RESULT);
		Log.i(DEBUGTAG, "Returned from Known Locations...");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		switch (requestCode) {

		case KNOWN_LOCATIONS_RESULT:
			Log.i(DEBUGTAG, "Back From Known Locations Maps...");
			break;
		}
		super.onActivityResult(resultCode, requestCode, intent);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// Set the tab when the user sits on it. Also need to
		// change the view
		viewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			Toast.makeText(this, connectionResult.getErrorCode(),
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}
}
