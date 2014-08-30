package com.principalmvl.lojackmykids;

import java.util.ArrayList;
import java.util.List;

import com.principalmvl.lojackmykids.listeners.PointCollectorListener;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PointCollector implements OnTouchListener {

	private PointCollectorListener listener;
	private List<Point> points = new ArrayList<Point>();
	
	public PointCollector() {
		// TODO Auto-generated constructor stub
	}

	public PointCollectorListener getListener() {
		return listener;
	}

	public void setListener(PointCollectorListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		if(listener != null){
			listener.pointsCollected(points);
		}
		return false;
	}


}
