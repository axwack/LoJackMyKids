package com.principalmvl.lojackmykids.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.principalmvl.lojackmykids.R;

public class SettingsFragment extends PreferenceFragment {

	@Override
	 public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	     addPreferencesFromResource(R.xml.settings);
	 }
	
}
