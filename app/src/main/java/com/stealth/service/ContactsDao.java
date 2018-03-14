package com.stealth.service;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.stealth.service.Const.ContactGroup;
import com.stealth.hushkbd.R;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;


public class ContactsDao 
{
String SEPARATOR = "|";


/**
* �����͸� �����ϴ� �޼���
*
* DB -> SharedPreferences change
* PhoneGroup key 
*     "PhoneGroup_"+GroupOrg+"_"+"ids" // group ids
*     "PhoneGroup_"+GroupOrg+"_"+"names" // group names
* PhoneUser key
*     "PhoneUser_"+GroupOrg+"_"+GroupId+"_"+"names" // phone user names
*     "PhoneUser_"+GroupOrg+"_"+GroupId+"_"+"phones" // phone user phone numbers   
*            
* */

public String getKeyGroupIds()
{
	return "PhoneGroup_ids" ; // group ids
}

public String getKeyGroupNames()
{
	return "PhoneGroup_names" ; // group names
}

public String getKeyUserNames(int nGroupId)
{
	return "PhoneUser_"+nGroupId+"_names" ; // phone user names
}

public String getKeyUserPhones(int nGroupId)
{
	return "PhoneUser_"+nGroupId+"_phones" ; // phone user phone numbers   
}

public String[] getGroupIds()
{
	if (Const.getService() == null)
		return null ;
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Const.getService());
	String str = prefs.getString(getKeyGroupIds(), "");
    if ((str == null) || (str.equals("")))
    {
    	return null ;
    }
    else
    {
    	String xx[] = str.split(SEPARATOR);
    	return xx ;
    }
}

public String[] getGroupNames()
{
	if (Const.getService() == null)
		return null ;
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Const.getService());
	String str = prefs.getString(getKeyGroupNames(), "");
    if ((str == null) || (str.equals("")))
    {
    	return null ;
    }
    else
    {
    	String xx[] = str.split(SEPARATOR);
    	return xx ;
    }
}

public String[] getUserNames(int nGroupId)
{
	if (Const.getService() == null)
		return null ;

	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Const.getService());
	String str = prefs.getString(getKeyUserNames(nGroupId), "");
    if ((str == null) || (str.equals("")))
    {
    	return null ;
    }
    else
    {
    	String xx[] = str.split(SEPARATOR);
    	return xx ;
    }
}

public String[] getUserPhones(int nGroupId)
{
	if (Const.getService() == null)
		return null ;

	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Const.getService());
	String str = prefs.getString(getKeyUserPhones(nGroupId), "");
    if ((str == null) || (str.equals("")))
    {
    	return null ;
    }
    else
    {
    	String xx[] = str.split(SEPARATOR);
    	return xx ;
    }
}

public void getGroups()
{
	Const.groups = new ArrayList<ContactGroup>();
	String groupIds[] = getGroupIds() ;
	String groupNames[] = getGroupNames() ;
	
	if ((groupIds == null) ||
	    (groupNames == null))
	   return ;
	   
	for (int i=0; i < groupIds.length; i++)
	{
		Const.ContactGroup grp = new Const.ContactGroup() ;
	    grp.GroupId = Integer.parseInt(groupIds[i]) ;
	    grp.GroupName = groupNames[i] ;
	    
	    // get group contacts
	    String names[] = getUserNames(grp.GroupId);
	    String phones[] = getUserPhones(grp.GroupId);
	    grp.contactsCount = names.length ;
	    grp.isMyGroup = true ;
	    for (int j=0; j < names.length; j++)
	    {
  	        Const.ContactUser usr = new Const.ContactUser();
	  
  	        usr.GroupId = grp.GroupId;
  	        usr.UserName = names[j];
  	        usr.UserPhone = phones[j];
        
            grp.contacts.add(usr);
	    }
	    
	    Const.groups.add(grp);
	}
}

public void saveGroupIds(String str)
{
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Const.getService());
	SharedPreferences.Editor editor = prefs.edit();
	editor.putString(getKeyGroupIds(), str);	
	editor.commit();
}

public void saveGroupNames(String str)
{
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Const.getService());
	SharedPreferences.Editor editor = prefs.edit();
	editor.putString(getKeyGroupNames(), str);	
	editor.commit();
}

public void saveUserNames(int nGroupId, String str)
{
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Const.getService());
	SharedPreferences.Editor editor = prefs.edit();
	editor.putString(getKeyUserNames(nGroupId), str);	
	editor.commit();
}

public void saveUserPhones(int nGroupId, String str)
{
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Const.getService());
	SharedPreferences.Editor editor = prefs.edit();
	editor.putString(getKeyUserPhones(nGroupId), str);	
	editor.commit();
}


public boolean existContacts()
{
	String xx[] = getGroupIds() ;
	if ((xx == null) || xx.length == 0)
		return false ;
	else
		return true ;
}

public void deleteAllContacts()
{
	for (Const.ContactGroup grp:Const.groups)
	{
		saveUserPhones(grp.GroupId, "") ;
		saveUserNames(grp.GroupId, "") ;
	}
    saveGroupIds("");
    saveGroupNames("");
}

public void writeGroups()
{
	// write Const.groups
   	// make userNames, userPhones
	StringBuilder sbIds = new StringBuilder();
	StringBuilder sbNames = new StringBuilder();
	for (Const.ContactGroup grp : Const.groups)
	{
		if (sbIds.toString().equals(""))
			sbIds.append(grp.GroupId+"") ;
		else
			sbIds.append(SEPARATOR).append(grp.GroupId+"") ;

		if (sbNames.toString().equals(""))
			sbNames.append(grp.GroupName) ;
		else
			sbNames.append(SEPARATOR).append(grp.GroupName) ;
	}
	saveGroupNames(sbNames.toString());
	saveGroupIds(sbIds.toString());
}

public void writePhoneGroup(ContactGroup grp)
{
   	// make userNames, userPhones
	StringBuilder sbNames = new StringBuilder();
	StringBuilder sbPhones = new StringBuilder();
	for (Const.ContactUser usr : grp.contacts)
	{
		if (sbNames.toString().equals(""))
			sbNames.append(usr.UserName) ;
		else
			sbNames.append(SEPARATOR).append(usr.UserName) ;
		
		if (sbPhones.toString().equals(""))
			sbPhones.append(usr.UserPhone) ;
		else
			sbPhones.append(SEPARATOR).append(usr.UserPhone) ;
	}
	saveUserNames(grp.GroupId, sbNames.toString());
	saveUserPhones(grp.GroupId, sbPhones.toString());
}


public void getNamePhones(String contactId, ContactGroup grp)
{
	String sUserName ;
	String sUserPhone ;
	
//	Const.MyLog("ContactsDao", "getNamePhones");
	ContentResolver cr = Const.getService().getContentResolver(); 

	Cursor pCur = cr.query(
     ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
     ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
     new String[] { contactId }, null);

   // get name/phone number belong to contact
   while (pCur.moveToNext()) 
   {
    sUserName = pCur
         .getString(pCur
                 .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).trim();

    sUserPhone = pCur
         .getString(pCur
                 .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();

    if ((!sUserName.equals("")) &&
    		(!sUserPhone.equals("")))
       Const.addContactUser(grp, sUserName, sUserPhone);
   } // while pcur

pCur.close();
}

private void addWithSort(ContactGroup grp)
{
	for (int i=0; i < Const.groups.size(); i++)
	{
		ContactGroup grp1 = Const.groups.get(i) ;
		if (grp.GroupName.compareTo(grp1.GroupName) <= 0)
		{
			Const.groups.add(i, grp);
			return ;
		}
	}
	Const.groups.add(grp);
}

public void makePhoneContacts()
{
	Const.saveMakePhoneState(0);
	
	Set<String> groupContactIds = new HashSet<String>();
	Set<String> notAssignedContactIds = new HashSet<String>();
	
	Const.groups = new ArrayList<ContactGroup>();
	ContentResolver cr = Const.getService().getContentResolver(); 

	Const.MyLog("ContactDao", "startMakePhoneContacts");

	// 1. groups
    Cursor groupCur = cr.query(ContactsContract.Groups.CONTENT_SUMMARY_URI, null, null, null, null);

	if (groupCur.getCount() > 0) 
       {
        while (groupCur.moveToNext()) 
           {
        	int nMembers = 0 ;
        	String sGroupId ;
        	String sGroupName ;
        	
        	String sMembers ;
           	sMembers = groupCur.getString(groupCur.getColumnIndex(ContactsContract.Groups.SUMMARY_WITH_PHONES)) ;
           	nMembers = Integer.parseInt(sMembers);
           	
        	if (nMembers > 0)
     	       {
    		   ContactGroup grp = new ContactGroup() ;
        	
               sGroupId = groupCur.getString(groupCur.getColumnIndex(ContactsContract.Groups._ID));
               grp.GroupId = Integer.parseInt(sGroupId);
               grp.GroupName  = groupCur.getString(groupCur.getColumnIndex(ContactsContract.Groups.TITLE));
        	   Const.MyLog("GroupId", "GroupId="+grp.GroupId+",GroupName="+grp.GroupName);
               grp.contactsCount = nMembers ;
               addWithSort(grp);
     	       }
           }
       groupCur.close();
       }

	// 2. get group members contacts ids 
	for (int i=Const.groups.size()-1; i >= 0 ; i--)
	{
	   ContactGroup grp = Const.groups.get(i);
//	   Const.MyLog("get grp members", "grpName="+grp.GroupName);
	   
       Uri groupURI = ContactsContract.Data.CONTENT_URI ;
       String[] projection = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID };

       Cursor c ;

       c = cr.query(
            groupURI,
            projection,
            ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
                    + "=" + grp.GroupId + " and " + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER+"=1", 
                    null, 
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");

       // get contactIds belong to group
       while (c.moveToNext()) 
       {
           String contactId = c
                .getString(c
                        .getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID));
           
           groupContactIds.add(contactId);

           getNamePhones(contactId, grp);
	      } // while c
      c.close();
      
      grp.contactsCount = grp.contacts.size() ;
      if (grp.contactsCount == 0)
    	  Const.groups.remove(i);
	} // for
	
	   Const.MyLog("ContactsDao", "before get not assigned"); //mylog

	// 3. get not assigned group members
	ContactGroup grp = new ContactGroup() ;
	grp.GroupId = -1 ;
	grp.GroupName = Const.getService().getResources().getString(R.string.not_assigned) ;
	ContactGroup notAssignedGroup = grp ;
	
    Cursor gmCursor = cr.query(ContactsContract.Data.CONTENT_URI,
            new String[] { ContactsContract.Data.CONTACT_ID, ContactsContract.Data.DISPLAY_NAME }, null,
            null, "2 ASC");

    // We want a projected matrix cursor that contains the contact ID and
    // group name, rather than the group ID
    while (gmCursor.moveToNext()) 
    {
        String contactId = gmCursor.getString(0);

        if (!groupContactIds.contains(contactId)) 
        {
            notAssignedContactIds.add(contactId);
            getNamePhones(contactId, grp);
        }
    }

    gmCursor.close();
    grp.contactsCount = grp.contacts.size() ;
    if (grp.contactsCount > 0)
    	Const.groups.add(0,grp);

    // write to db
    /*
    for (ContactGroup grp1 : Const.groups)
    {
    	writePhoneGroup(grp1);
    	for (ContactUser usr1 : grp1.contacts)
    	{
    		writePhoneUser(usr1);
    	}
    }
    */
	Const.MyLog("ContactsDao", "end MakePhoneContacts");
	
	// this to after writePhoneTask
	//Const.saveMakePhoneState(1);
}

public void readPhoneContacts()
{
	// return SendInfo which is sending data at save time
	
    if (Const.isReadingContacts)
	    return ;
    
   Const.isReadingContacts = true ;
   
   getGroups();

   Const.isReadingContacts = false ;
}

/*
public long writeSendInfo( Const.SendInfo si)
{
	// return _id
Const.MyLog( TAG , "SendInfo write" );
if ( sd.isOpen( ) )
   {
   String sql = "INSERT INTO SendInfo(orderOrg, SeqNo, reserveStartTime, " +
                "sendInterval, message, siState, "+
                "sentCount, countSuccess, countFail "+
                ") VALUES(" +
                si.orderOrg + ",'" + si.SeqNo+ "'," + si.reserveStartTime + "," +
                si.sendInterval+ ",'" + si.message + "'," +
                si.getSiState() + "," + si.getSentCount()+ "," + si.getCountSuccess() + "," +
                si.getCountFail() +
                ")";
   Const.MyLog( TAG , sql );
   try
      {
      sd.execSQL( sql );
      }
   catch ( SQLException e )
      {
      Log.e( TAG , "" , e );
      }
   }
else
   {
   Log.w( TAG , "DB Open FAIL" );
   }

//get _id
long id = 0 ;

String sql = " select MAX(_id) from SendInfo  " ;
Cursor c = sd.rawQuery( sql , null );
if ( c != null )
{
c.moveToFirst( );
id = c.getLong(0);
c.close();
} 

// write si.contacts
for (ContactUser usr : si.contacts)
{
	usr.sendInfoId = id ;
	usr._id = writeContactUser(usr);
}

//write logs
for (ServerLog log : si.logs)
{
	log.sendInfoId = id ;
	log._id = writeServerLog(log);
}

return id ;
}

public long writeContactUser(Const.ContactUser usr)
{
Const.MyLog( TAG , "ContactUser write" );
if ( sd.isOpen( ) )
   {
   String sql = "INSERT INTO ContactUser(sendInfoId, GroupId, SeqNo, UserName, " +
                "UserPhone, iSentState, reserveTime, sCallBack, "+
                "message "+
                ") VALUES(" +
                usr.sendInfoId + "," +
                usr.GroupId + ",'" +usr.SeqNo+ "','"+ usr.UserName+ "','" + 
                usr.UserPhone + "'," +usr.iSentState + "," + usr.reserveTime+ ",'" + usr.sCallBack + "','" +
                usr.message + "'" +
                ")";
//   Const.MyLog( TAG , sql );
   try
      {
      sd.execSQL( sql );
      }
   catch ( SQLException e )
      {
      Log.e( TAG , "" , e );
      }
   }
else
   {
   Log.w( TAG , "DB Open FAIL" );
   }

//get _id
long id = 0 ;

String sql = " select MAX(_id) from ContactUser  " ;
Cursor c = sd.rawQuery( sql , null );
if ( c != null )
{
c.moveToFirst( );
id = c.getLong(0);
c.close();
} 

return id ;
}

public long writeServerLog(Const.ServerLog log)
{
Const.MyLog( TAG , "ServerLog write" );
if ( sd.isOpen( ) )
   {
   String sql = "INSERT INTO ServerLog(sendInfoId, sLog, logTime "+
                ") VALUES(" +
                log.sendInfoId + ",'" +log.sLog+ "',"+ 
                log.logTime+ 
                ")";
//   Const.MyLog( TAG , sql );
   try
      {
      sd.execSQL( sql );
      }
   catch ( SQLException e )
      {
      Log.e( TAG , "" , e );
      }
   }
else
   {
   Log.w( TAG , "DB Open FAIL" );
   }

//get _id
long id = 0 ;

String sql = " select MAX(_id) from ServerLog  " ;
Cursor c = sd.rawQuery( sql , null );
if ( c != null )
{
c.moveToFirst( );
id = c.getLong(0);
c.close();
} 

return id ;
}

public void updateCounts(long id, int sentCount, int countSuccess, int countFail)
{
	if ( sd.isOpen( ) )
	{
	    String sql = "UPDATE SendInfo SET sentCount=" + sentCount +
	    		     ",countSuccess=" + countSuccess + 
	    		     ",countFail=" + countFail + 
	    		     " WHERE _id=" + id ;
	    Const.MyLog( TAG , sql );
	    try
	    {
	    sd.execSQL( sql );
	    }
	    catch ( SQLException e )
	    {
	    Log.e( TAG , "" , e );
	    }
	}
	else
	{
	    Log.w( TAG , "DB OPEN FAIL" );
	}
}

public void updateUsrState(long id, int iSentState)
{
	if ( sd.isOpen( ) )
	{
	    String sql = "UPDATE ContactUser SET iSentState=" + iSentState +
	    		     " WHERE _id=" + id ;
	    Const.MyLog( TAG , sql );
	    try
	    {
	    sd.execSQL( sql );
	    }
	    catch ( SQLException e )
	    {
	    Log.e( TAG , "" , e );
	    }
	}
	else
	{
	    Log.w( TAG , "DB OPEN FAIL" );
	}
}

public void updateSiState(long siId, int si_state)
{
	if ( sd.isOpen( ) )
	{
	    String sql = "UPDATE SendInfo SET siState=" + si_state +
	    		     " WHERE _id=" + siId ;
	    Const.MyLog( TAG , sql );
	    try
	    {
	    sd.execSQL( sql );
	    }
	    catch ( SQLException e )
	    {
	    Log.e( TAG , "" , e );
	    }
	}
	else
	{
	    Log.w( TAG , "DB OPEN FAIL" );
	}
}


public void deleteDataForId( long sendInfoId )
{
	// delete all data for Seq Number 
    Const.MyLog( TAG , "deleteDataForId" );
    if ( sd.isOpen( ) )
    {
        String sql = "DELETE FROM SendInfo WHERE _id=" + sendInfoId ;
        Const.MyLog( TAG , sql );
        try
        {
            sd.execSQL( sql );
        }
        catch ( SQLException e )
        {
        Log.e( TAG , "" , e );
        }

        sql = "DELETE FROM ContactUser WHERE sendInfoId=" + sendInfoId ;
        Const.MyLog( TAG , sql );
        try
        {
            sd.execSQL( sql );
        }
        catch ( SQLException e )
        {
        Log.e( TAG , "" , e );
        }

        sql = "DELETE FROM ServerLog WHERE sendInfoId=" + sendInfoId ;
        Const.MyLog( TAG , sql );
        try
        {
            sd.execSQL( sql );
        }
        catch ( SQLException e )
        {
        Log.e( TAG , "" , e );
        }
    }
    else
    {
    Log.w( TAG , "DB OPEN FAIL" );
    }
}

public SendInfo readWorkSIs( )
{
	// return SendInfo which is sending data at save time
	SendInfo sendingSI = null ;
Const.SendInfo si = null ;
Const.workSIs = new ArrayList<SendInfo>();

if ( sd.isOpen( ) )
   {
	// 1 read from SendInfo
    String sql = "SELECT _id,orderOrg,SeqNo,reserveStartTime,sendInterval," +
	             "message,siState,sentCount,countSuccess,countFail " +
	             "FROM SendInfo";
	try{
    Cursor c = sd.rawQuery( sql , null );
    if ( c != null )
       {
       c.moveToFirst( );

       // �ݺ����� ���鼭, ���ڵ带 �˻��ؼ�, ȭ�鿡 ���̵���
       while ( c.isAfterLast( ) == false ) // Ŀ���� �������� �ƴϸ�
          {
    	  si = new Const.SendInfo();
    	  
    	  int n = 0 ;
    	  si._id =  c.getLong(n++);
    	  si.orderOrg = c.getInt(n++); 
          si.SeqNo = c.getString(n++);
          si.reserveStartTime = c.getLong(n++);
          si.sendInterval = c.getInt(n++);
          si.message = c.getString(n++);
          int siState = c.getInt(n++) ;
          int sentCount = c.getInt(n++) ;
          int countSuccess = c.getInt(n++) ;
          int countFail = c.getInt(n++) ;
          si.setValues(sentCount, countSuccess, countFail);
          si.setSiState(siState); //mylog
          if (si.getSiState() == Const.SIStateSending)
        	  sendingSI = si ;
        	  
          Const.workSIs.add(si);
          // ���� ���ڵ�� �̵�
          c.moveToNext( );
          }
       
      // Ŀ����ü�� �ݾ���
      c.close( );
      }
   else
      {
      Const.MyLog( "Main" , "DB OPEN FAIL" );
      }
	}
	catch (Exception e) {}
   }

// read ContactUsers / logs
   for (SendInfo si1 : Const.workSIs)
   {
	   readContactUser(si1);
	   readLogs(si1);
   }

   return sendingSI ;
}

public void readLogs(SendInfo si)
{
	Const.ServerLog log ;
	
if ( sd.isOpen( ) )
   {
	// 1 read from SendInfo
    String sql = "SELECT _id,sLog,logTime " +
	             "FROM ServerLog where sendInfoId = " + si._id;
	try{
    Cursor c = sd.rawQuery( sql , null );
    if ( c != null )
       {
       c.moveToFirst( );

       // �ݺ����� ���鼭, ���ڵ带 �˻��ؼ�, ȭ�鿡 ���̵���
       while ( c.isAfterLast( ) == false ) // Ŀ���� �������� �ƴϸ�
          {
    	  log = new Const.ServerLog();
    	  
    	  int n = 0 ;
    	  log._id = c.getLong(n++);
    	  log.sLog = c.getString(n++);
    	  log.logTime = c.getLong(n++);
          
          si.logs.add(log);
          
          // ���� ���ڵ�� �̵�
          c.moveToNext( );
          }
       
      // Ŀ����ü�� �ݾ���
      c.close( );
      }
   else
      {
      Const.MyLog( "Main" , "DB OPEN FAIL" );
      }
	}
	catch (Exception e) {}
   }
}


public void readContactUser(SendInfo si)
{
	Const.ContactUser usr ;
	
if ( sd.isOpen( ) )
   {
	// 1 read from SendInfo
    String sql = "SELECT _id,sendInfoId,GroupId,SeqNo,UserName," +
	             "UserPhone,iSentState,reserveTime,sCallBack,message " +
	             "FROM ContactUser where sendInfoId = " + si._id;
	try{
    Cursor c = sd.rawQuery( sql , null );
    if ( c != null )
       {
       c.moveToFirst( );

       // �ݺ����� ���鼭, ���ڵ带 �˻��ؼ�, ȭ�鿡 ���̵���
       while ( c.isAfterLast( ) == false ) // Ŀ���� �������� �ƴϸ�
          {
    	  usr = new Const.ContactUser();
    	  
    	  int n = 0 ;
    	  usr._id = c.getLong(n++);
    	  usr.sendInfoId = c.getLong(n++);
    	  usr.GroupId = c.getInt(n++);
    	  usr.SeqNo = c.getString(n++);
    	  usr.UserName = c.getString(n++);
    	  usr.UserPhone = c.getString(n++);
    	  usr.iSentState = c.getInt(n++);
    	  usr.reserveTime = c.getLong(n++);
    	  usr.sCallBack = c.getString(n++);
    	  usr.message = c.getString(n++);
          
          si.contacts.add(usr);
          
          // ���� ���ڵ�� �̵�
          c.moveToNext( );
          }
       
      // Ŀ����ü�� �ݾ���
      c.close( );
      }
   else
      {
      Const.MyLog( "Main" , "DB OPEN FAIL" );
      }
	}
	catch (Exception e) {}
   }
}

public byte getMessageBox(int type) 
{
	int UriType;
	String strUri;
	String datas = "";
	strUri = "content://com.sec.mms.provider/message/";
	Cursor c = Const.cr.query(Uri.parse(strUri), null, null,
			null, null);
	UriType = 1;

	if (c == null) {
		strUri = "content://com.btb.ums.provider.MessageProvider/sms/";
		c = Const.cr.query(Uri.parse(strUri), null, null, null,
				null);
		UriType = 2;
	}
	*
	 * if(c == null) { c = getContentResolver().query(Uri.parse(
	 * "content://com.lge.messageprovider/msg/inbox/"), null, null, null,
	 * null); UriType = 3; }
	 *
	if (c == null) {
		strUri = "content://com.btb.sec.mms.provider/message/";
		c = Const.cr.query(Uri.parse(strUri), null, null, null,
				null);
		UriType = 4;
	}

	if (c == null) {
		strUri = "content://sms/inbox/";
		c = Const.cr.query(Uri.parse(strUri), null, null, null,
				null);
		UriType = 5;
	}

	if (c == null) {
		// if(type == 0)
		// sendChangedNumber((byte)2, "", "");
		return 2;
	}

	if (c != null) {
		if (c.moveToFirst()) {
			*
			 * String msg; for (int i = 0; i < c.getColumnCount(); i++) {
			 * msg =
			 * c.getString(c.getColumnIndex(c.getColumnName(i).toString()));
			 * Log.d("DEBUG", "DEBUG : " + c.getColumnName(i).toString() +
			 * " : " + msg); }
			 *
			do {
				String msgbody = "", Number, BeforeNumber = "", AfterNumber = "";
				int offset1, offset2;

				switch (UriType) {
				case 1:
				case 2:
				case 4:
					if (c.getColumnIndex("Title") != -1)
						msgbody = c.getString(c.getColumnIndex("Title"));
					break;
				case 3:
				case 5:
					if (c.getColumnIndex("body") != -1)
						msgbody = c.getString(c.getColumnIndex("body"));
					break;
				}

				{
					if (type == 1) {
						if (msgbody.startsWith("Settings:/")) {
							offset1 = msgbody.indexOf("/");
							offset2 = msgbody.indexOf("/", offset1 + 1);

							if (offset1 == -1 || offset2 == -1)
								continue;

							String url;

							url = msgbody.substring(offset1 + 1, offset2);

							Log.d("DEBUG", "DEBUG : " + url);

							offset1 = msgbody.indexOf("/", offset2 + 1);

							if (offset1 == -1)
								continue;

							SharedPreferences pref =  Const.mainActivity.getSharedPreferences(
									"setting", Context.MODE_PRIVATE);
							Editor editor = pref.edit();
							editor.putString("Server_DOMAIN", url);
							editor.commit();

							if (UriType == 5) {
								String pid = "";
								if (c.getColumnIndex("thread_id") != -1)
									pid = c.getString(c
											.getColumnIndex("thread_id"));

								Const.cr
										.delete(Uri.parse("content://sms/conversations/"
												+ pid), null, null);
							}

							continue;
						}
					}

					boolean changenumber = true;

					if (msgbody.indexOf("�Ƚɹ���") != -1)
						changenumber = false;

					offset1 = msgbody.indexOf("01");

					if (offset1 == -1 || msgbody.length() < offset1 + 11)
						continue;

					Number = (msgbody.substring(offset1, offset1 + 11));

					Pattern pattern = Pattern.compile("\\d");
					Matcher matcher = pattern.matcher(Number);
					while (matcher.find()) {
						BeforeNumber += matcher.group(0);
					}

					if (BeforeNumber.length() < 10)
						continue;

					if (changenumber) {
						offset2 = msgbody.indexOf("010", offset1 + 11);

						if (offset2 == -1
								|| msgbody.length() < offset2 + 11)
							continue;

						Number = (msgbody.substring(offset2, offset2 + 11));

						matcher = pattern.matcher(Number);
						while (matcher.find()) {
							AfterNumber += matcher.group(0);
						}

						if (AfterNumber.length() < 10)
							continue;
					}

					if (type == 0) {
						// sendChangedNumber((byte)0, BeforeNumber,
						// AfterNumber);
						if (!datas.isEmpty())
							datas += "��";
						if (changenumber)
							datas += String.format("U,%s,%s", BeforeNumber,
									AfterNumber);
						else
							datas += String.format("D,%s,x", BeforeNumber);

						if (UriType == 5) {
							String pid = "";
							if (c.getColumnIndex("thread_id") != -1)
								pid = c.getString(c
										.getColumnIndex("thread_id"));

							Const.cr
									.delete(Uri.parse("content://sms/conversations/"
											+ pid), null, null);
						}
					}
				}

			} while (c.moveToNext());
		}
	}

	// if(type == 0 && !datas.isEmpty())
	if (type == 0) 
	{
		if (!datas.isEmpty())
			JSONParser.reqSmsRltGather(Const.myCompanyId, Const.myPhoneNumber, Const.getLastSeq(), datas);
	}

	return 0;
}

public short DeleteMessageBox() 
{
	int UriType;
	short Count = 0;
	String strUri;
	strUri = "content://com.sec.mms.provider/message/";
	Cursor c = Const.cr.query(Uri.parse(strUri), null, null,
			null, null);
	UriType = 1;

	if (c == null) {
		strUri = "content://com.btb.ums.provider.MessageProvider/sms/";
		c = Const.cr.query(Uri.parse(strUri), null, null, null,
				null);
		UriType = 2;
	}
	*
	 * if(c == null) { c = getContentResolver().query(Uri.parse(
	 * "content://com.lge.messageprovider/msg/inbox/"), null, null, null,
	 * null); UriType = 3; }
	 *
	if (c == null) {
		strUri = "content://com.btb.sec.mms.provider/message/";
		c = Const.cr.query(Uri.parse(strUri), null, null, null,
				null);
		UriType = 4;
	}

	if (c == null) {
		strUri = "content://sms/";
		c = Const.cr.query(Uri.parse(strUri), null, null, null,
				null);
		UriType = 5;
	}

	if (c == null) {
		return -1;
	}

	if (c.moveToFirst()) {
		*
		 * String msg; for (int i = 0; i < c.getColumnCount(); i++) { msg =
		 * c.getString(c.getColumnIndex(c.getColumnName(i).toString()));
		 * Log.d("DEBUG", "DEBUG : " + c.getColumnName(i).toString() + " : "
		 * + msg); }
		 *
		do {
			String pid = "";
			if (c.getColumnIndex("thread_id") != -1) {
				pid = c.getString(c.getColumnIndex("thread_id"));
				Const.cr.delete(
						Uri.parse("content://sms/conversations/" + pid),
						null, null);

				Count++;
			}

		} while (c.moveToNext());
	}

	if (UriType == 5) {
		strUri = "content://mms/";
		c = Const.cr.query(Uri.parse(strUri), null, null, null,
				null);

		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String pid = "";
					if (c.getColumnIndex("thread_id") != -1) {
						pid = c.getString(c.getColumnIndex("_id"));

						Const.cr.delete(
								Uri.parse("content://mms/inbox/" + pid),
								null, null);

						Count++;
					}

				} while (c.moveToNext());
			}
		}
	}

	return Count;
}
*/

private final static String TAG = ContactsDao.class.getSimpleName( );

}

/*
 * ' get GroupId
   GROUPID = "" 
   sql = " select MAX(GroupId) as GROUPID  from GroupInfo  " 
   Rs.open sql, db, 1
   If Rs.Eof Or Rs.Bof Then
   Else
      GROUPID  = Trim(Rs("GROUPID"))
   End If
   Rs.close
 * 
 * 
 * */
