package com.principalmvl.lojackmykids.models;

import com.google.android.gms.maps.model.LatLng;

public class ExtendedLatLng {
	
	private Double lat, lng, distance;

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public ExtendedLatLng(LatLng latlng){
		
		this.lat = latlng.latitude;
		this.lng = latlng.longitude;
	}

	public ExtendedLatLng(double latitude, double longitude) {
		this.lat = latitude;
		this.lng = longitude;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    
	    if (obj == null)
	        return false;
	    
	    if (getClass() != obj.getClass())
	        return false;
	    
	    if (!(obj instanceof ExtendedLatLng)) 
		    return false;
	    
	    ExtendedLatLng other = (ExtendedLatLng) obj;
	    if (lat == null) 
	    {
	        if (other.lat != null)
	            return false;
	    } 
	    else if (lat != other.lat)
	        return false;
	    if (lng == null) 
	    {
	        if (other.lng != null)
	            return false;
	    } 
	    else if (lng != other.lng)
	        return false;
	    return true;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((lat == null) ? 0 : lat.hashCode());
	    result = prime * result + ((lng == null) ? 0 : lng.hashCode());
	    return result;
	}
	
	public double getDistance(ExtendedLatLng latLng){
		
		/* http://www.movable-type.co.uk/scripts/latlong.html
		 * var R = 6371; // km
		   var φ1 = lat1.toRadians();
		   var φ2 = lat2.toRadians();
		   var Δφ = (lat2-lat1).toRadians();
		   var Δλ = (lon2-lon1).toRadians();

		   var a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
        		Math.cos(φ1) * Math.cos(φ2) *
        		Math.sin(Δλ/2) * Math.sin(Δλ/2);
		   var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		   var d = R * c;
		 */
		double a1 = Math.toRadians(latLng.lat);
		double a2 = Math.toRadians(latLng.lng);
		
		double deltaS = Math.toRadians(latLng.lat - this.lat);
		double deltaY = Math.toRadians(latLng.lng - this.lng);
		
		double a = Math.sin(deltaS/2) * Math.sin(deltaY/2) + Math.cos(a1) *Math.cos(a2) * Math.sin(deltaS/2) * Math.sin(deltaY/2);
		
		return 6371 * (2 * Math.atan2((Math.sqrt(a)), Math.sqrt(1-a)));
				
	}
	
	
	
}
