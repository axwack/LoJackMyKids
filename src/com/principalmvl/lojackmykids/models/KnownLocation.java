package com.principalmvl.lojackmykids.models;

public class KnownLocation {

	private String Address, CityState;
	public String getCityState() {
		return CityState;
	}


	public void setCityState(String cityState) {
		CityState = cityState;
	}


	public double getTimestamp() {
		return Timestamp;
	}


	public void setTimestamp(double timestamp) {
		Timestamp = timestamp;
	}


	private double Lat,Lng, Timestamp;
	private int id;
	private boolean read = false;
	
	

	public KnownLocation(int id, String address, String cityState, double lat, double lng, double timestamp) {
		super();
		this.Address = address;
		this.Lat = lat;
		this.Lng = lng;
		this.id = id;
		this.Timestamp=timestamp;
		this.CityState=cityState;
	}


	public String getAddress() {
		return Address;
	}


	public void setAddress(String address) {
		Address = address;
	}


	public double getLat() {
		return Lat;
	}


	public void setLat(double lat) {
		Lat = lat;
	}


	public double getLng() {
		return Lng;
	}


	public void setLng(double lng) {
		Lng = lng;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public boolean isRead() {
		return read;
	}


	public void setRead(boolean read) {
		this.read = read;
	}
	



}
