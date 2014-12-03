package com.principalmvl.lojackmykids;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.principalmvl.lojackmykids.listeners.PointCollectorListener;

public class PointCollector implements OnTouchListener {

	private PointCollectorListener listener;
	private List<Point> points = new ArrayList<Point>();

	public PointCollector() {
	}
	
	public PointCollectorListener getListener() {
		return listener;
	}

	public void setListener(PointCollectorListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		int x = Math.round(event.getX());
		int y = Math.round(event.getY());
		
		points.add(new Point(x,y));
		
		if (points.size()==2){ //This sets the number of touches before a GPS coordinate goes out
			if(listener != null){
				//listener.pointsCollected(points); //This is set on the Child Activity. May need to change on the Child Activity
				//listener.onLocationChanged(location); //we need to pass a location to this
			}
		}	
		return false;
	}
/*
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	*/
}
