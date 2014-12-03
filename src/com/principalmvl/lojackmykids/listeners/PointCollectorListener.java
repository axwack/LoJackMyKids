package com.principalmvl.lojackmykids.listeners;

import java.util.List;

import com.principalmvl.lojackmykids.models.ExtendedLatLng;

import android.graphics.Point;
import android.location.Location;

public interface PointCollectorListener {

	public void pointsCollected(List<ExtendedLatLng> points);
	public void onLocationChanged(Location location);
}
