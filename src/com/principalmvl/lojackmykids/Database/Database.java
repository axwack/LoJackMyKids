package com.principalmvl.lojackmykids.Database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.util.Log;

import com.principalmvl.lojackmykids.MainActivity;
import com.principalmvl.lojackmykids.models.KnownLocation;

public class Database extends SQLiteOpenHelper {

	private static final String name = "LOJACKMYKIDS.db";
	private static final CursorFactory factory = null;
	private static final int version = 1;
	private static final String table = "KNOWNLOCATIONS";
	private static final String KEY_ID = "ID";
	private static final String ADDRESS = "ADDRESS";
	private static final String CITYSTATE = "CITYSTATE";

	private static final String LATITUDE = "LATITUDE";
	private static final String LONGITUDE = "LONGITUDE";
	private static final String AT = "AT";

	private static final String createTables = String
			.format("create table %s (%s integer primary key autoincrement, %s text, %s text, %s double, %s double, %s datetime default current_timestamp);",
					table, KEY_ID, ADDRESS, CITYSTATE, LATITUDE, LONGITUDE, AT);

	// private static final String createTables2 =
	// "create table profile (_id integer primary key autoincrement, name text, email text unique, count integer default 0);";

	public Database(Context context) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(MainActivity.DEBUGTAG, createTables);
		db.execSQL(createTables);
		Log.i(MainActivity.DEBUGTAG, "Creating tables...");
		// db.execSQL(createTables2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	public boolean insertKnownLocation(Address address) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		try {
			Log.i(MainActivity.DEBUGTAG, "In Insert...");
			values.put(ADDRESS, address.getAddressLine(0));// need to get
			values.put(CITYSTATE, address.getAddressLine(1));
			values.put(LONGITUDE, address.getLongitude());
			values.put(LATITUDE, address.getLatitude());
			long i=db.insert(table, null, values);
			Log.i(MainActivity.DEBUGTAG, "Inserted record with id: "+i);
		} catch (Exception e) {
			db.close();
			Log.i(MainActivity.DEBUGTAG, "Database insert excepiton");
			return false;
		}

		return true;
	}

	public String getKnownLocation(double Lat, double Lng) {
		String _address = null;
		int id = 0;
		SQLiteDatabase db = getReadableDatabase();

		String sql = String.format(
				"select %s, %s from %s where %s=%s and %s=%s", KEY_ID, ADDRESS,
				table, LATITUDE, Lat, LONGITUDE, Lng);

		Log.i(MainActivity.DEBUGTAG, sql);

		Cursor c = db.rawQuery(sql, null);

		/*
		 * Cursor c = db.query(table, new String[] { KEY_ID, ADDRESS, LATITUDE,
		 * LONGITUDE, AT}, "latitude=? or longitude=?", null, null, null, null);
		 */

		while (c.moveToNext()) {
			_address = c.getString(1);
			id = c.getInt(0);
			Log.i(MainActivity.DEBUGTAG, "Record retrieved:" + _address
					+ ": ID: " + id);
		}
		this.count();
		Log.i(MainActivity.DEBUGTAG, "Select finished.");
		db.close();
		return _address;
	}

	public List<KnownLocation> getAllLocations() {

		List<KnownLocation> list = new ArrayList<KnownLocation>();

		String _address = null;

		SQLiteDatabase db = getReadableDatabase();

		String sql = String.format("select * from %s;", table);

		Log.i(MainActivity.DEBUGTAG, sql);

		Cursor c = db.rawQuery(sql, null);

		while (c.moveToNext()) {
			KnownLocation k = new KnownLocation(c.getInt(0), c.getString(1),
					c.getString(2), c.getDouble(3), c.getDouble(4),
					c.getDouble(5));
			Log.i(MainActivity.DEBUGTAG, "KnownLocation Retrieved:" + k.getAddress());
			list.add(k);
			Log.i(MainActivity.DEBUGTAG, "Record retrieved:" +c.getString(1)
					+ ": ID: " + c.getInt(0));
		}
		this.count();
		Log.i(MainActivity.DEBUGTAG, "Select All finished.");
		db.close();
		return list;
	}

	public int deleteDB() {
		SQLiteDatabase db = getWritableDatabase();

		int count = db.delete(table, "1", null);
		db.close();
		return count;
	}

	public int count() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = String.format("select * from %s;", table);
		Cursor c = db.rawQuery(sql, null);
		Log.i(MainActivity.DEBUGTAG,
				"Number of Records in Table: " + c.getCount());
		db.close();
		return c.getCount();
	}
}
