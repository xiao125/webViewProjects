package com.game.sdk.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.game.sdk.GameSDK;
import com.game.sdk.bean.Data;

public class DBHelper extends SQLiteOpenHelper {

	private static DBHelper instance = null;
	private static Context context = Data.getInstance().getApplicationContex();
	
	private static final String CREATE_USER_TABLE_SQL = "create table if not exists user(_id integer primary key autoincrement ,username text, password text)";
	private static final String DROP_USER_TABLE_SQL = "drop table if exists user";

	private static final String DB_NAME = "kndb";
	private static final int DB_VERSION = 1;
	
	public DBHelper() {
		super(context, DB_NAME, null, DB_VERSION);
//		setContext(context);
	}
	
	public Context getContext() {
		return context;
	}

	
	public static DBHelper getInstance() {
		if (instance == null) {
			instance = new DBHelper();
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_USER_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion != newVersion) {
			db.execSQL(DROP_USER_TABLE_SQL);
			onCreate(db);
		}
	}
	
//	public boolean insertOrUpdateUser(JSONObject obj)
//	{
//		String username = null;
//		String password = null;
//		try {
//			username = obj.getString("username");
//			password = obj.getString("password");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		
//		if( username == null || password == null ){
//			return false;
//		}
//		
//		insertOrUpdateUser(username , password);
//		return true;
//	}
	
	public long insertOrUpdateUser(String userName, String password) {
		boolean isUpdate = false;
		String[] usernames = findAllUserName();
		for (int i = 0; i < usernames.length; ++i) {
			if (userName.equals(usernames[i])) {
				isUpdate = true;
				break;
			}
		}
		long id = -1L;
		SQLiteDatabase db = getWritableDatabase();
		if (db != null) {
			if (isUpdate) {
				deleteUser(userName);
			}
			ContentValues values = new ContentValues();
			values.put("username", userName);
			values.put("password", password);
			id = db.insert("user", null, values);
		}
		return id;
	}
	
	public String findPwdByUsername(String username)
	{
		
		SQLiteDatabase db = getWritableDatabase();
		
		String sql = "select * from user where username = '" + username + "'";
		String password = "";
		Cursor cursor = null;
		
		if(db != null)
			cursor = db.rawQuery(sql, null);
		
		if( cursor != null && cursor.getColumnCount() > 0 )
		{
			cursor.moveToFirst();
			password = cursor.getString(cursor.getColumnIndex("password"));
		}
		
		closeCursor(cursor);
		
		return password;
	}
	
	
	public String[] findAllUserName() {
		SQLiteDatabase db = getWritableDatabase();
		if (db != null) {
			String _orderBy = "_id desc";
			Cursor cursor = db.query("user", null, null, null, null,null, _orderBy);
			int count = cursor.getCount();
			String[] userNames = new String[count];
			if (count > 0) {
				cursor.moveToFirst();
				for (int i = 0; i < count; ++i) {
					userNames[i] = cursor.getString(cursor.getColumnIndex("username"));
					cursor.moveToNext();
				}
			}
			closeCursor(cursor);

			return userNames;
		}
		return new String[0];
	}
	
	public long deleteUser(String userName)
	  {
	    SQLiteDatabase db = getWritableDatabase();
	    long id = db.delete("user", "username = '" + userName + "'", null);

	    return id;
	  }
	
	private void closeCursor(Cursor cursor)
	{
		if( cursor != null )
			cursor.close();
	}

}
