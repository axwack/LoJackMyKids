package com.principalmvl.lojackmykids.listeners;

import java.util.List;

import android.graphics.Point;
import android.location.Location;

public interface PointCollectorListener {

	public void pointsCollected(List<Point> points);
	public void onLocationChanged(Location location);
}
