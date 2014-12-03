package com.principalmvl.lojackmykids;

import com.principalmvl.lojackmykids.Database.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DataProvider extends ContentProvider {

	public static final String COL_ID = "_id";

	public static final String TABLE_MESSAGES = "messages";
	public static final String COL_MSG = "msg";
	public static final String COL_FROM = "email";
	public static final String COL_TO = "email2";
	public static final String COL_AT = "at";

	public static final String TABLE_PROFILE = "profile";
	public static final String COL_NAME = "name";
	public static final String COL_EMAIL = "email";
	public static final String COL_COUNT = "count";

	public static final Uri CONTENT_URI_MESSAGES = Uri
			.parse("content://com.principalmvl.lojackmykids.provider/messages");
	public static final Uri CONTENT_URI_PROFILE = Uri
			.parse("content://com.principalmvl.lojackmykids.provider/profile");

	private static final int MESSAGES_ALLROWS = 1;
	private static final int MESSAGES_SINGLE_ROW = 2;
	private static final int PROFILE_ALLROWS = 3;
	private static final int PROFILE_SINGLE_ROW = 4;

	private static final UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.principalmvl.lojackmykids.provider", "messages",
				MESSAGES_ALLROWS);
		uriMatcher.addURI("com.principalmvl.lojackmykids.provider", "messages/#",
				MESSAGES_SINGLE_ROW);
		uriMatcher.addURI("com.principalmvl.lojackmykids.provider", "profile",
				PROFILE_ALLROWS);
		uriMatcher.addURI("com.principalmvl.lojackmykids.provider", "profile/#",
				PROFILE_SINGLE_ROW);
	}

	private Database db;

	public DataProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase _dataBase= db.getWritableDatabase();
		long id;

		switch (uriMatcher.match(uri)){

		case MESSAGES_ALLROWS:
			id =_dataBase.insertOrThrow(TABLE_MESSAGES, null, values);
			if(values.get(COL_TO) == null){
				_dataBase.execSQL("update profile set count=count+1 where email = ?", new Object[]{values.get(COL_FROM)});
			            getContext().getContentResolver().notifyChange(CONTENT_URI_PROFILE, null);
			}
			break;
		case PROFILE_ALLROWS:
			id=_dataBase.insertOrThrow(TABLE_PROFILE, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unsupported uri: " + uri);
		
	}
		Uri insertUri = ContentUris.withAppendedId(uri, id);
		getContext().getContentResolver().notifyChange(insertUri, null);
		return insertUri;
	}

	@Override
	public boolean onCreate() {
		db = new Database(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase _dataBase = db.getReadableDatabase();
		SQLiteQueryBuilder _queryBuilder = new SQLiteQueryBuilder();

		switch (uriMatcher.match(uri)) {

		case MESSAGES_ALLROWS:
		case PROFILE_SINGLE_ROW:
			_queryBuilder.setTables(getTableName(uri));
			_queryBuilder.appendWhere("_id = " + uri.getLastPathSegment());
			break;

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		
		Cursor cursor =_queryBuilder.query(_dataBase, projection, selection, selectionArgs, "", "", sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	private String getTableName(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case MESSAGES_ALLROWS:
		case MESSAGES_SINGLE_ROW:
			return TABLE_MESSAGES;
		case PROFILE_ALLROWS:
		case PROFILE_SINGLE_ROW:
			return TABLE_PROFILE;
		}
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
