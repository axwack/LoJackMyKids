package com.principalmvl.lojackmykids.Adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.principalmvl.lojackmykids.fragments.MapViewFragment;
import com.principalmvl.lojackmykids.fragments.KnownLocationMapFragment;
import com.principalmvl.lojackmykids.fragments.ReportFragment;


public class TabsPagerAdapter extends FragmentPagerAdapter {


	
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int indexOfTab) {
		
		switch(indexOfTab){
		case 0:	
			return new MapViewFragment();

		case 1:
			return new ReportFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

}
