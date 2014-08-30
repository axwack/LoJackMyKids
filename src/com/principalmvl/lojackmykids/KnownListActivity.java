package com.principalmvl.lojackmykids;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.principalmvl.lojackmykids.models.KnownLocation;
import com.principalmvl.lojackmykids.Adapters.KnownLocations;
import com.principalmvl.lojackmykids.Database.Database;

public class KnownListActivity extends Activity {
	private Database db = new Database(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.known_locations_listlayout);
		
		setupListFromDatabase(getKnownLocationsFromDatabase());
	}

	private void setupListFromDatabase(List<KnownLocation> knownLocations) {
		ListView listView = (ListView) findViewById(R.id.known_locations_listview);

		KnownLocations messageAdapter = new KnownLocations(this, knownLocations);
		listView.setAdapter(messageAdapter);
	}
	
	private List<KnownLocation> getKnownLocationsFromDatabase(){
		List<KnownLocation> knownLocations = new ArrayList<KnownLocation>();
		knownLocations = db.getAllLocations();
		return knownLocations;
	}
}
