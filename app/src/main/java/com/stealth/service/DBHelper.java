package com.stealth.service;

import java.util.Vector;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.stealth.hushkbd.Const2;

public class DBHelper extends SQLiteOpenHelper
{
	public final static String DB_NAME = "hush"+ Const2.appKind+".db"; // DB
	public final static int DB_VERSION = 1;
	    // 2016.1.7   Version=1

	private Vector< SQLiteDatabase > dispose = new Vector< SQLiteDatabase >( );
	private final SQLiteDatabase sd;

	public DBHelper(Context context)
	{
	super( context , DB_NAME , null , DB_VERSION );
	sd = this.getWritableDatabase( );
	dispose.add( sd );
	}

	public void SQLiteDatabaseClose( )
	{
	   while ( dispose.size( ) > 0 )
	     {
	     SQLiteDatabase sqlLiteDatabase = dispose.remove( 0 );
	     if ( sqlLiteDatabase != null )
	        {
	        if ( sqlLiteDatabase.isOpen( ) )
	           {
	           sqlLiteDatabase.close( );
	       	   Const.MyLog("DBHelper", "SQLiteDatabaseClose");
	           }
	        }
	    }
	}

	public void addSQLiteDatabase( SQLiteDatabase sqlLiteDatabase )
	{
	dispose.add( sqlLiteDatabase );
	}

	@Override
	public void onCreate( SQLiteDatabase db )
	{
	Const.MyLog( TAG , "onCreate( SQLiteDatabase db )" );
	db.execSQL( "CREATE TABLE IF NOT EXISTS tb_group (_id integer primary key autoincrement, "
			+ "GroupSid INTEGER,"
			+ "GroupId TEXT)"
			);
	
	try{
	db.execSQL("CREATE INDEX IF NOT EXISTS group_GroupId ON tb_group(GroupId)");
	}    catch ( SQLException e ) {}

		// create tb_user
		db.execSQL( "CREATE TABLE IF NOT EXISTS tb_user (_id integer primary key autoincrement, "+
				"UserSid INTEGER, " +
				"GroupSid INTEGER, " +
				"UserName TEXT, " +
				"UserLevel INTEGER, " +
				"PartsCnt INTEGER, " +
				"BlockId INTEGER "  +
				")" );
		try{
			db.execSQL("CREATE INDEX key_UserSid ON tb_user(UserSid)");
		}    catch ( SQLException e ) {}


		// create tb_key
	db.execSQL( "CREATE TABLE IF NOT EXISTS tb_key (_id integer primary key autoincrement, "+
	        "GroupSid INTEGER, " +
			"KeySeq INTEGER, " +
			"sValidDt TEXT, " +
			"sKey TEXT "  +
			")" );
	try{
	db.execSQL("CREATE INDEX key_GroupSid ON tb_key(GroupSid)");
	}    catch ( SQLException e ) {}
	

    }
	
	@Override
	public void onUpgrade( SQLiteDatabase db , int oldVersion , int newVersion )
	{
		Log.d( TAG , "onUpgrade( SQLiteDatabase db , int oldVersion , int newVersion )" );
		onCreate( db );

		/*
		try{
			db.execSQL( "ALTER TABLE tb_group"+
			            " ADD COLUMN " +
			            "MgrYN INTEGER"
					 );
			}    catch ( SQLException e ) {}
		try{
			db.execSQL( "ALTER TABLE tb_group"+
			            " ADD COLUMN " +
			            "DecryptCopyYN INTEGER"
					 );
			}    catch ( SQLException e ) {}
		
		try{
			db.execSQL( "ALTER TABLE tb_group"+
			            " ADD COLUMN " +
			            "MyState INTEGER"
					 );
			}    catch ( SQLException e ) {}
		*/
	}

	private final static String TAG = DBHelper.class.getSimpleName( );
}
