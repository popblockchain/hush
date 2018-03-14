package com.stealth.service;


import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stealth.util.Util;

public class DBDao extends SQLiteOpenHelper
{
    public final SQLiteDatabase sd;

	String sqlForGroup = "SELECT GroupSid, GroupId," // GroupPwHash, "
			+ "MgrSid, MgrName, UseYN,"
			+ "PayedYN, MemberCnt, GroupVers, MemberViewYN, MgrYN, DecryptCopyYN, MyState, SdcardMustYN"
			+ " FROM tb_group"  ;

	String sqlForKey = "SELECT GroupSid, "
			+ "KeyLevel, KeySeq, sValidDt, sKey"
			+ " FROM tb_key"  ;

	String sqlForUser = "SELECT UserSid, "
			+ "GroupSid, UserName, UserLevel, PartsCnt, BlockId"
			+ " FROM tb_user ORDER BY UserLevel,UserName"  ;

	public DBDao( Context context , DBHelper dbHelper )
	{
		super( context , DBHelper.DB_NAME , null , DBHelper.DB_VERSION );
		sd = this.getWritableDatabase( );
		dbHelper.addSQLiteDatabase( sd );
	}

public void insertGroup(GroupInfo info)
{
	if ( sd.isOpen( ) )
	   {
	   String sql = "INSERT INTO tb_group (GroupSid, GroupId, " //GroupPwHash, "
	   		+ "MgrSid, MgrName, UseYN,"
	   		+ "PayedYN, MemberCnt, GroupVers, MemberViewYN, MgrYN,"
	   		+ "DecryptCopyYN, MyState, SdcardMustYN) VALUES(" +
			  info.GroupSid + "," +
	          "'" + info.GroupId + "'," +
	          //"'" + info.GroupPwHash + "'," +
	          info.MgrSid  + "," +
	          "'" + Util.changeDBString(info.MgrName) + "'," +
	          info.UseYN  + "," +
	          info.PayedYN  + "," +
	          info.MemberCnt  + "," +
	          info.GroupVers  + "," +
	          info.MemberViewYN + "," +
	          info.MgrYN + "," +
	          info.DecryptCopyYN + "," +
	          info.MyState + "," +
	          info.SdcardMustYN +
	          ")";
	   //Const.MyLog( TAG , sql );
	   try
	      {
	      sd.execSQL( sql );
	      }
	   catch ( SQLException e )
	      {
	      }
	   }
	else
	   {
	   }
}

public void updateGroup(GroupInfo info)
{
	if ( sd.isOpen( ) )
	   {
	   String sql = "UPDATE tb_group set " +
	          "GroupId="+ "'" + Util.changeDBString(info.GroupId) + "'," +
//	          "GroupPwHash="+ "'" + info.GroupPwHash + "'," +
//	          "Level=" + info.Level  + "," +
	          "MgrSid=" + info.MgrSid  + "," +
//	          "KeyValidDt12=" + "'" + info.KeyValidDt12 + "'," +
	          "MgrName=" + "'" + Util.changeDBString(info.MgrName) + "'," +
	          "UseYN=" + info.UseYN  + "," +
	          "PayedYN=" + info.PayedYN  + "," +
	          "MemberCnt=" + info.MemberCnt  + "," +
	          "GroupVers=" + info.GroupVers  + "," +
	          "MemberViewYN=" + info.MemberViewYN + "," +
	          "MgrYN=" + info.MgrYN + "," +
	          "DecryptCopyYN=" + info.DecryptCopyYN + "," +
	          "MyState=" + info.MyState + "," +
	          "SdcardMustYN=" + info.SdcardMustYN +
			  " where GroupSid=" + info.GroupSid ;
	   //Const.MyLog( TAG , sql );
	   try
	      {
	      sd.execSQL( sql );
	      }
	   catch ( SQLException e )
	      {
	      }
	   }
	else
	   {
	   }
}

/*
public void updateValidDt(int groupSid, String valid_dt)
{
	if ( sd.isOpen( ) )
	   {
	   String sql = "UPDATE tb_group set " +
	          "KeyValidDt12=" + "'" + valid_dt + "'" +
			  " where GroupSid=" + groupSid ;
	   //Const.MyLog( TAG , sql );
	   try
	      {
	      sd.execSQL( sql );
	      }
	   catch ( SQLException e )
	      {
	      }
	   }
	else
	   {
	   }
}
*/

public void deleteGroup(int groupSid)
{
	if ( sd.isOpen( ) )
	{
		String sql = "DELETE FROM tb_group WHERE GroupSid =" + groupSid ;
		//Const.MyLog( TAG , sql );
		try
		{
			sd.execSQL( sql );
		}
		catch ( SQLException e )
		{
		}

		sql = "DELETE FROM tb_key WHERE GroupSid =" + groupSid ;
		//Const.MyLog( TAG , sql );
		try
		{
			sd.execSQL( sql );
		}
		catch ( SQLException e )
		{
		}
	}
	else
	{
	}
}

	/*
        "GroupSid INTEGER, " +
                "KeyLevel INTEGER, " +
                "KeySeq INTEGER, " +
                "sValidDt TEXT, " +
                "sKey TEXT "  +
    */

/*
public void saveValidDt(int groupSid, String vdt)
{
	if ( sd.isOpen( ) )
	   {
	   String sql = "UPDATE tb_group set " +
	          "KeyValidDt12=" + "'" + vdt + "'," +
			  " where GroupSid=" + groupSid ;
	   //Const.MyLog( TAG , sql );
	   try
	      {
	      sd.execSQL( sql );
	      }
	   catch ( SQLException e )
	      {
	      }
	   }
	else
	   {
	   }
}

public String getValidDt(int groupSid)
{
	String s = "" ;
	if ( sd.isOpen( ) )
	   {
	    String sql = 
	    		"SELECT KeyValidDt12"
		   		+ " FROM tb_group" 
	    		+ " where GroupSid = " + groupSid ;
	    Cursor c = sd.rawQuery( sql , null );
	    if ( c != null )
	       {
	       c.moveToFirst( );

	       if ( c.isAfterLast( ) == false ) // Ŀ���� �������� �ƴϸ�
	           {
	    	   s = c.getString(0) ;
	          }
	       
	      c.close( );
	      }
	   else
	      {
//	      Const.MyLog( "Main" , "DB OPEN FAIL" );
	      }
	   }

	return s ;
}
*/

public int getGroupCnt()
{
	// return 0 if group not exist
	int s = 0 ;
	if ( sd.isOpen( ) )
	   {
	    String sql = 
	    		"SELECT count(*) as cnt"
		   		+ " FROM tb_group" ;
	    Cursor c = sd.rawQuery( sql , null );
	    if ( c != null )
	       {
	       c.moveToFirst( );

	       if ( c.isAfterLast( ) == false ) // Ŀ���� �������� �ƴϸ�
	           {
	    	   s = c.getInt(0) ;
	          }
	       
	      c.close( );
	      }
	   else
	      {
//	      Const.MyLog( "Main" , "DB OPEN FAIL" );
	      }
	   }

	return s ;

}

public int getGroupVers(int groupSid)
{
	int s = Integer.MAX_VALUE ;
	if ( sd.isOpen( ) )
	   {
	    String sql = 
	    		"SELECT GroupVers"
		   		+ " FROM tb_group" 
	    		+ " where GroupSid = " + groupSid ;
	    Cursor c = sd.rawQuery( sql , null );
	    if ( c != null )
	       {
	       c.moveToFirst( );

	       if ( c.isAfterLast( ) == false ) // Ŀ���� �������� �ƴϸ�
	           {
	    	   s = c.getInt(0) ;
	          }
	       
	      c.close( );
	      }
	   else
	      {
//	      Const.MyLog( "Main" , "DB OPEN FAIL" );
	      }
	   }

	return s ;
}

private void getGroupInfoFromCursor(GroupInfo info, Cursor c)
{
	info.GroupSid = c.getInt(c.getColumnIndex("GroupSid"));
	info.GroupId = c.getString(c.getColumnIndex("GroupId"));
//	info.GroupPwHash = c.getString(c.getColumnIndex("GroupPwHash"));
//	info.Level = c.getInt(c.getColumnIndex("Level"));
	info.MgrSid = c.getInt(c.getColumnIndex("MgrSid"));
//	info.KeyValidDt12 = c.getString(c.getColumnIndex("KeyValidDt12"));
	info.MgrName = c.getString(c.getColumnIndex("MgrName"));
	info.UseYN = c.getInt(c.getColumnIndex("UseYN"));
	info.PayedYN = c.getInt(c.getColumnIndex("PayedYN"));
	info.MemberCnt = c.getInt(c.getColumnIndex("MemberCnt"));
	info.GroupVers = c.getInt(c.getColumnIndex("GroupVers"));
	info.MemberViewYN = c.getInt(c.getColumnIndex("MemberViewYN"));
	info.MgrYN = c.getInt(c.getColumnIndex("MgrYN"));
	info.DecryptCopyYN = c.getInt(c.getColumnIndex("DecryptCopyYN"));
	info.MyState = c.getInt(c.getColumnIndex("MyState"));
	info.SdcardMustYN = c.getInt(c.getColumnIndex("SdcardMustYN"));
}


	private void getUserInfoFromCursor(UserInfo info, Cursor c)
	{
		info.UserSid = c.getInt(c.getColumnIndex("UserSid"));
		info.GroupSid = c.getInt(c.getColumnIndex("GroupSid"));
		info.UserName = c.getString(c.getColumnIndex("UserName"));
		info.UserLevel = c.getInt(c.getColumnIndex("UserLevel"));
		info.PartsCnt = c.getInt(c.getColumnIndex("PartsCnt"));
		info.BlockId = c.getInt(c.getColumnIndex("BlockId"));
	}



public GroupInfo getGroupInfo(int groupSid)
{
	GroupInfo info = null ;
	
	if ( sd.isOpen( ) )
	{
	    String sql = sqlForGroup 
	    		+ " where GroupSid = " + groupSid ;
	    Cursor c = sd.rawQuery( sql , null );
	    if ( c != null )
	    {
	       c.moveToFirst( );

	       if ( c.isAfterLast( ) == false ) 
	       {
	    	   info = new GroupInfo() ;
	    	   getGroupInfoFromCursor(info, c);
	       }
	       
	      c.close( );
	   }
	   else
	   {
//	      Const.MyLog( "Main" , "DB OPEN FAIL" );
	      }
	   }

	return info ;
}

	public UserInfo getUserInfo(int userSid)
	{
		UserInfo info = null ;

		if ( sd.isOpen( ) )
		{
			String sql = sqlForUser
					+ " where UserSid = " + userSid ;
			Cursor c = sd.rawQuery( sql , null );
			if ( c != null )
			{
				c.moveToFirst( );

				if ( c.isAfterLast( ) == false )
				{
					info = new UserInfo() ;
					getUserInfoFromCursor(info, c);
				}

				c.close( );
			}
			else
			{
			}
		}

		return info ;
	}

	public ArrayList<GroupInfo> getGroups()
{
	Const.MyLog("getGroups");
	ArrayList<GroupInfo> groups = new ArrayList<GroupInfo>() ;
	GroupInfo info = null ;
	
	if ( sd.isOpen( ) )
	{
	    String sql = sqlForGroup ;
	    Cursor c = sd.rawQuery( sql , null );
	    if ( c != null )
	    {
	       c.moveToFirst( );

	       while ( c.isAfterLast( ) == false ) 
	       {
	    	   info = new GroupInfo() ;
	    	   getGroupInfoFromCursor(info, c);
	    	   groups.add(info);
	           c.moveToNext( );
	       }
	       
	      c.close( );
	   }
	   else
	   {
//	      Const.MyLog( "Main" , "DB OPEN FAIL" );
	   }
	}

	return groups ;
}

	public ArrayList<UserInfo> getUsers()
	{
		Const.MyLog("getUsers");
		ArrayList<UserInfo> users = new ArrayList<UserInfo>() ;
		UserInfo info = null ;

		if ( sd.isOpen( ) )
		{
			String sql = sqlForUser ;
			Cursor c = sd.rawQuery( sql , null );
			if ( c != null )
			{
				c.moveToFirst( );

				while ( c.isAfterLast( ) == false )
				{
					info = new UserInfo() ;
					getUserInfoFromCursor(info, c);
					users.add(info);
					c.moveToNext( );
				}

				c.close( );
			}
			else
			{
			}
		}

		return users ;
	}


/*
public String getGroupPwHash(int groupSid)
{
	String s = "" ;
	if ( sd.isOpen( ) )
	   {
	    String sql = 
	    		"SELECT GroupPwHash"
		   		+ " FROM tb_group" 
	    		+ " where GroupSid = " + groupSid ;
	    Cursor c = sd.rawQuery( sql , null );
	    if ( c != null )
	       {
	       c.moveToFirst( );

	       if ( c.isAfterLast( ) == false ) // Ŀ���� �������� �ƴϸ�
	           {
	    	   s = c.getString(0) ;
	          }
	       
	      c.close( );
	      }
	   else
	      {
//	      Const.MyLog( "Main" , "DB OPEN FAIL" );
	      }
	   }

	return s ;
}
*/

/*
public int getGroupLevel(int groupSid)
{
	int l = 0 ;
	if ( sd.isOpen( ) )
	   {
	    String sql = 
	    		"SELECT Level"
		   		+ " FROM tb_group" 
	    		+ " where GroupSid = " + groupSid ;
	    Cursor c = sd.rawQuery( sql , null );
	    if ( c != null )
	       {
	       c.moveToFirst( );

	       if ( c.isAfterLast( ) == false ) // Ŀ���� �������� �ƴϸ�
	           {
	    	   l = c.getInt(0) ;
	          }
	       
	      c.close( );
	      }
	   else
	      {
//	      Const.MyLog( "Main" , "DB OPEN FAIL" );
	      }
	   }

	return l ;
}
*/

public void updateUserLevel(UserInfo info)
{
	if ( sd.isOpen( ) )
	{
		String sql = "UPDATE tb_user set " +
				"UserLevel=" + info.UserLevel  + "," +
				"PartsCnt=" + info.PartsCnt  +
				" where UserSid=" + info.UserSid ;
		//Const.MyLog( TAG , sql );
		try
		{
			sd.execSQL( sql );
		}
		catch ( SQLException e )
		{
		}
	}
	else
	{
	}
}

    public void insertUser(UserInfo info)
    {
        if ( sd.isOpen( ) )
        {
            String sql = "INSERT INTO tb_user (UserSid, GroupSid, " //GroupPwHash, "
                    + "UserName, UserLevel, PartsCnt, BlockId) VALUES(" +
                    info.UserSid + "," +
                    info.GroupSid + "," +
                    "'" + Util.changeDBString(info.UserName) + "'," +
                    info.UserLevel  + "," +
                    info.PartsCnt + "," +
                    info.BlockId  +
                    ")";
            //Const.MyLog( TAG , sql );
            try
            {
                sd.execSQL( sql );
            }
            catch ( SQLException e )
            {
            }
        }
        else
        {
        }
    }

	public void updateUserName(UserInfo info)
	{
		if ( sd.isOpen( ) )
		{
			String sql = "UPDATE tb_user set " +
					"UserName='" + Util.changeDBString(info.UserName)  + "'" +
					" where UserSid=" + info.UserSid ;
			//Const.MyLog( TAG , sql );
			try
			{
				sd.execSQL( sql );
			}
			catch ( SQLException e )
			{
			}
		}
		else
		{
		}
	}

    public void updateBlockId(UserInfo info)
    {
        if ( sd.isOpen( ) )
        {
            String sql = "UPDATE tb_user set " +
                    "BlockId=" + info.BlockId  +
                    " where UserSid=" + info.UserSid ;
            //Const.MyLog( TAG , sql );
            try
            {
                sd.execSQL( sql );
            }
            catch ( SQLException e )
            {
            }
        }
        else
        {
        }
    }


	@Override
public void onCreate( SQLiteDatabase db )
{}

@Override
public void onUpgrade( SQLiteDatabase db , int oldVersion , int newVersion )
{}
}

