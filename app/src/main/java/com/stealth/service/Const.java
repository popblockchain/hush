package com.stealth.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import com.stealth.crypto.AES256;
import com.stealth.crypto.RSA;
import com.stealth.hushkbd.Const2;
import com.stealth.hushkbd.R;
import com.stealth.hushkbd.hush_kbd;
import com.stealth.jncryptor.RNC256;
import com.stealth.util.FileFunc;
import com.stealth.util.ReqParser;
import com.stealth.util.Util;
import com.stealth.hushkbd.Const1;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

public class Const
{
	public static String getStealthIme()
	{
			return Const2.StealthIme ;
	}

	public static boolean isStealthDefault()
	{
		String curIme = Settings.Secure.getString(Const.getService().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
		Const.MyLog("isStealthDefault", "currentKbd="+curIme);
		if (curIme.equals(getStealthIme()))
			return true ;
		else
			return false ;
	}

	public static final int APP_LITE = 1 ;
	public static final int APP_COUPLE = 2 ;
	public static final int APP_COMPANY = 3 ;
	public static final int APP_COMPANY_MGR = 4 ;

	public static final int  SETTINGS_RESULT = 10 ;
	public static int timerCnt = 0 ;

//	public static GroupInfo inviteGroup = null ;

	public static final int DspPureText = 1 ;
	public static final int DspDecryptedText = 2 ;
	public static final int DspSettings = 3 ;
	public static final int DspPasswordAsk = 4 ;

	public static final String COMMON_KEY = "NBgkqhkiG9w0BAQEFAAOCAQ8AM" ;
	public static final String COMMUNI_KEY = "FluN2b5K0BWnklbfRmUajeKt" ;
    public static final String SAVE_KEY = "hdehHUYUiuhsuuYVurTRDuiTREEob3b0FGFD";

	public static String targetString = "" ; // target String after change group

	public static String curClip = "" ;
    //public static final int MAX_SYMBOLS_LITE = 10 ;

    public static final int EC_PASSED_24HOURS = 1 ;

    public static boolean needDspLoginHelp = false ;
    public static boolean needKbdOn = false ;
    public static boolean needAskLogin = false ;
    public static boolean needGotoRegistPhone = false ;
    public static boolean needGotoRegistPass = false ;
    public static boolean needGotoPuzzle = false ;
    public static boolean needGotoLogin = false ;
    public static boolean needExit = false ;
	//public static boolean needCheckPermission = false ;

    public static int errCode = 0 ;
//    public static int recvedRandom = 0 ;
    public static int remainedMinutes = 0 ;
	public static int senderSid = 0 ;

	//ysk20161127 ------
	public static int MAX_GRADE = 9 ; // 0..9
	public static String CryptoSymbolsLite[] =
			{"①", "②","③","④","⑤",
			"⑥","⑦","⑧","⑨","★"};

	public static String CryptoStartLite[] =
			{
					"<" + CryptoSymbolsLite[0], "<" + CryptoSymbolsLite[1],
					"<" + CryptoSymbolsLite[2], "<" + CryptoSymbolsLite[3],
					"<" + CryptoSymbolsLite[4], "<" + CryptoSymbolsLite[5],
					"<" + CryptoSymbolsLite[6], "<" + CryptoSymbolsLite[7],
					"<" + CryptoSymbolsLite[8], "<" + CryptoSymbolsLite[9]
			};


	public static String CryptoEndLite[] =
			{
					CryptoSymbolsLite[0]+ ">", CryptoSymbolsLite[1]+ ">",
					CryptoSymbolsLite[2]+ ">", CryptoSymbolsLite[3]+ ">",
					CryptoSymbolsLite[4]+ ">", CryptoSymbolsLite[5]+ ">",
					CryptoSymbolsLite[6]+ ">", CryptoSymbolsLite[7]+ ">",
					CryptoSymbolsLite[8]+ ">", CryptoSymbolsLite[9]+ ">"
			};

    public static String getCryptoSymbolFromLevel(int level)
    {
		int grade = (level / 7)  ;
        if (grade >= MAX_GRADE) {
            return Const.CryptoSymbolsLite[MAX_GRADE]  ;
        } else {
            return Const.CryptoSymbolsLite[grade] ;
        }
    }

	//---------------ysk20161127


    /*
    public static final int KK_USER_KEY = 0 ;
    public static final int KK_COMMON_KEY = 1 ;
    public static final int KK_GROUP_KEY = 2 ;

    public static int keyKind = KK_COMMON_KEY ;
    */
//	public static String CryptoStart = "<��";
//	public static String CryptoEnd = "��>";

	static public String myPhoneNumber  = "";

	public static long copyTime = 0 ; // from popup copied time

	//public static String[] key_pair = null ; //RSA.makeKeyPair() ;

	public static android.content.ClipboardManager mCM = null;

	public static ClipboardMonitor service1 = null ;
	public static hush_kbd hush_kbd = null ;
	
	//public static MainActivity mainActivity = null ;
	public static EnvSettings envSettings = null ;
	public static GroupList groupList = null ;
	public static GroupSelect groupSelect = null ;
	public static PhoneNumber phoneNumber = null ;
    public static RegistPhone registPhone = null ;
    public static RegistName registName = null ;
    public static RegistEtc registEtc = null ;
	public static RegistPass registPass = null ;
	public static String targetPhone = "" ;
    public static String targetName = "" ;
	public static PopupDecrypt popupDecrypt= null ;
	public static PopupEncrypt popupEncrypt = null ;
	public static Puzzle puzzle = null ;

	public static boolean isUserChecked = false ;
	public static boolean mustUpgrade = false ;

	// GroupReg modes
	public static final int GRM_Regist = 0 ;
	public static final int GRM_Modify = 1 ;

	public static int groupRegMode = GRM_Regist ;

	public static Context getService()
	{
		if (hush_kbd != null)
			return hush_kbd ;
		else
			return service1 ;
	}

	public static long FINISH_HANGUL_TIME = 1500l ; //mSec
	public static long DOUBLE_CLICK_TIME = 300l ; //mSec

	public static int socket_time_out = 5 ;

	public static boolean isReqProcess = false ;
	public static long TimeOutGetGroupInfos = 1*60*1000 ; // 1 minute
	public static boolean bFromKbd = false ; // encrypt/decrypt from keyboard

	// for group make
	public static int myGcnt = 0 ; // group cnt made by me

	public static boolean checkPauseTime()
	{
		long startTime = getStartPauseTime() ;
		if (startTime <= 0)
			return true ;
		else
		{
			long cTime = Util.getCurTime() ;
			if ((cTime - startTime) >= 5*60*1000)
			{
				saveStartPauseTime(0);
				return true ;
			}
		}
		return false ;
	}

	public static void saveStartPauseTime(long lTime)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong("StartPauseTime", lTime);
		editor.commit();
	}

	public static long getStartPauseTime()
	{
		//if (1==1) return 0 ;
		if (getService() == null)
			return 0 ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		long n = prefs.getLong("StartPauseTime", 0);
		return (n) ;
	}

	public static void saveLogin(boolean state)
	{
		// true = loginned
		// false = logoffed
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("Login", state);
		editor.commit();
	}

	public static boolean isLogin() {
		if (getService() == null)
			return false;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		boolean p = prefs.getBoolean("Login", false);
		return p ;
	}

	public static void savePassword(String p)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("Password", p);
		editor.commit();
	}

	public static String getPassword() {
		if (getService() == null)
			return "";

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		String p = prefs.getString("Password", "");
		return p ;
	}

	public static void saveLocalPhoneNumber(String phone)
	{
		// phone numer is '01... '
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("LocalPhoneNumber", phone);
		editor.commit();
	}

	public static String getGlobalPhoneNumber()
	{
		String str = getLocalPhoneNumber() ;
		if (str.equals(""))
			return "" ;
		if (str.startsWith("+"))
			return str ;

		if (str.startsWith("0")) {
			String ccode = Util.getCountryZipCode0();
			return "+" + ccode + str.substring(1);
		}
		else
		{
			String ccode = Util.getCountryZipCode0();
			return "+" + ccode + str;
		}
	}

	private static String getLocalPhoneNumber()
	{
		//if (1==1) return 256 ;
		if (getService() == null)
			return "" ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		String phone = prefs.getString("LocalPhoneNumber", "");

		return (phone) ;
	}

	public static String puzzleImagePath = "hushPuzzle";

	public static String getPuzzleUrl(int level)
	{
		// level == 0 ...
		String url = getYoutubeUrl(level) ;
        if (url.equals(""))
            return "https://youtu.be/dp0F18FFCTE" ; // missA Hush
        /*
		switch (level) {
			case 0: url = "https://youtu.be/dp0F18FFCTE"; break ;
			case 1: url = "https://youtu.be/9bZkp7q19f0"; break ;
		}
		*/
		return url ;
	}

	//ysk20161107
    public static String getYoutubeId(String youtubeUrl)
    {
        String strs[] = youtubeUrl.split("/") ;
        int cnt = strs.length ;
        if (cnt > 1)
            return strs[cnt-1] ;
        else
            return "" ;
    }

	public static String getYoutubeImageUrl(String youtubeUrl)
	{
		String sId = getYoutubeId(youtubeUrl);
		return "http://img.youtube.com/vi/"+sId+"/mqdefault.jpg" ;
	}

    //ysk20161206
    public static String getYoutubeImageUrl(int level)
    {
		String str = getPuzzleUrl(level);
        if (str.contains("youtu.be"))
		   return getYoutubeImageUrl(str) ;
        else
            return getImageUrl(level) ;
    }

	/*
	public static int getPuzzleImageId(int level)
	{
		if (level == 0) return R.drawable.level_1 ;

		return R.drawable.level_1 ;
	}
	*/

	public static int getMyOrgLevel()
	{
		// 0 .. 9,  0=level 1, 9=level 10
        if (getService() == null)
            return 0 ;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
        int level = prefs.getInt("MyLevel",0);

        return level ;
	}

	//ysk20161127
	public static int getMyGradeIndex() // 0..9
	{
		// 0 .. MAX_GRADE
		if (getService() == null)
			return 0 ;

		int grade = getMyOrgLevel() / 7;
		if (grade >= MAX_GRADE)
			return MAX_GRADE ;
		else
		    return grade ;

	}

	public static void upgradeMyOrgLevel()
	{
		int level = getMyOrgLevel() + 1 ;
		saveMyOrgLevel(level);
		for (int i=0; i < MAX_PUZZLES; i++)
			Const.puzzles[i] = 0 ;
		savePuzzles(level);
	}

	public static void saveMYGC(String str)
	{
		myGcnt = Integer.parseInt(str);
	}
    public static int touchPuzzleNum = -1 ;

	public static void saveMyOrgLevel(int level)
	{
		// level == 0 .. 9
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("MyLevel", level);
		editor.commit();
	}

	public static DBHelper dbHelper = null;
	public static DBDao dbDao = null;
	public static Context dbContext = null ;

	public static void openDB(Context context)
	{
		if (Const.dbHelper == null)
		{
			Const.dbContext = context ;
			Const.dbHelper = new DBHelper( dbContext );

			Const.dbDao = new DBDao( dbContext , Const.dbHelper );
		}
	}

	public static void closeDB()
	{
		if (dbHelper != null )
		{
			dbHelper.SQLiteDatabaseClose( );
			dbHelper = null ;
		}
	}

	// selected group, keyinfo
	public static GroupInfo curGroupInfo = null ;

	public static void setCurGroupInfo(GroupInfo info)
	{
		curGroupInfo = info ;
	}

	// my group list
	public static ArrayList<GroupInfo> groupArrs = new ArrayList<GroupInfo> () ;

	// my friends list
	public static ArrayList<UserInfo> userArrs = new ArrayList<UserInfo> () ;


    //ysk20161127
	public static int levelEncryptedText(String str)
	{
        // 0 for encrypt for save text
        if (str.contains(Const.CryptoStartSave) && str.contains(Const.CryptoEndSave))
            return -1 ;

		// return -1 if not encrypted text
		for (int i=0; i <= MAX_GRADE; i++)
		{
			if (str.contains(Const.CryptoStartLite[i]) && str.contains(Const.CryptoEndLite[i])) //ysk20171127
				return i ;
		}

		return -2 ;
	}


	public static String getClipText()
	{
		ClipData cd = mCM.getPrimaryClip();
		Item item = cd.getItemAt(0);

		if (item.getText() == null)
			return "" ;
		else
			return (item.getText().toString().trim()) ;
	}


	public static void setClipText(String str)
	{
		android.content.ClipData clipData = android.content.ClipData
				.newPlainText("text", str);
		mCM.setPrimaryClip(clipData);
	}


	//ysk20161127
	public static String encrypt(String newClip)
	{
		// if hush_lite send only userSid & encrypted with common key
		StringBuilder sb = new StringBuilder() ;

		String key ;

        if (Const2.appKind == Const.APP_LITE)
        {
            //ysk20161031
            if ((!Const.bFromKbd) && (Const.getEncryptMethod() == Const.MethodSave))
            {
                return encryptForSave(newClip) ;
            }
            int grade = Const.getMyGradeIndex() ; //0..9 value //20161129

            sb.append(CryptoStartLite[grade]);
		    sb.append(Util.getStr62FromInt(getMySid())+":");
            key = Util.getMix3String(Const.COMMON_KEY, getMySid()+"", (grade+1)+"", 32);

            String content = Util.getStdDateString("yyyyMMddHHmm") + ";" +
                    "1" + ":" + Const.getTodayRandomNumber() + ";" +
                    newClip ;

		    sb.append(AES256.encrypt(key, content));
            sb.append(CryptoEndLite[grade]);
		}

        // attach DecryptMethodHelp String
        if (Const.isAtachOk())
            sb.append(Const.getAttachMessage());

		return (sb.toString()) ;
	}

	//public static int nDspPopup = 0 ; // 1=pure text, 2=decrypted text
	public static void startClipboardMonitor(Context context)
	{
		if (Const.getService() == null)
		{
			ComponentName service = context.startService(
					new Intent(context, ClipboardMonitor.class));
			if (getService() == null)
			{
				Const.MyLog("Can't start service "
						+ ClipboardMonitor.class.getName());
			}
		}
	}

	public static void saveEncryptWhenCopy(boolean bOn)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("EncrypyWhenCopy", bOn);
		editor.commit();
	}

	public static boolean isEncryptWhenCopy()
	{
		if (getService() == null)
			return true ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		boolean b = prefs.getBoolean("EncrypyWhenCopy", true);
		return (b) ;
	}

    public static boolean isEncryptByKbd() {
        if (Const1.isStealthSelected() && Const.isStealthDefault())
            return true ;
        else
            return false ;
    }

    /*
	public static final int EM_NOTHING = 0 ;
	public static final int EM_KBD = 1 ;
	public static final int EM_COPY = 2 ;
	public static final int EM_ALL = 3 ;

	public static void saveEncryptMethod(int m)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("EncrypyMethod", m);
		editor.commit();
	}

	public static int getDefaultEncryptMethod()
	{
		if (!Util.isKoreanLanguage())
			return EM_COPY ;
		else
			return EM_NOTHING;
	}

	public static boolean isEncryptMethodAskOk()
	{
		if (getService() == null) {
			return true;
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		boolean m = prefs.getBoolean("EncrypyMethodAskOk", true);
		return (m) ;
	}

	public static void saveEncryptMethodAskOk(boolean bOk)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("EncrypyMethodAskOk", bOk);
		editor.commit();
	}

	public static int getEncryptMethod()
	{
		if (getService() == null) {
			return getDefaultEncryptMethod();
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		int m = prefs.getInt("EncrypyMethod", getDefaultEncryptMethod());
		if (m != Const.EM_COPY)
		{
			if (!Const1.isStealthSelected() || !Const.isStealthDefault()) {
				Const.saveEncryptMethod(Const.EM_COPY);
				m = Const.EM_COPY ;
			}
		}


		return (m) ;
	}
    */

	public static void saveDspPopup(int n)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("DspPopup", n);
		editor.commit();
	}

	public static int getDspPopup()
	{
		if (getService() == null)
			return 0 ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		int n = prefs.getInt("DspPopup", 0);
		return (n) ;
	}

	public static void saveSecondUse()
	{
		Context context = envSettings ;
		if (context == null)
			context = getService() ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("SecondUse", true);
		editor.commit();
	}

	public static boolean isSecondUse()
	{
        Context context = envSettings ;
		if (context == null)
			context = getService() ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean n = prefs.getBoolean("SecondUse", false);
		return (n) ;
	}

	public static void saveCheckDay(int day)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("CheckDay", day);
		editor.commit();
	}

	public static int getCheckDay()
	{
		if (getService() == null)
			return 0 ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		int n = prefs.getInt("CheckDay", 0);
		return (n) ;
	}

	public static GroupInfo findGroup(int groupSid)
	{
		for (GroupInfo info : groupArrs)
		{
			if (info.GroupSid == groupSid)
				return info ;
		}
		return null ;
	}

	public static void parseGroupVersList(String str)
	{
		// str =
		// 그룹1 SID.그룹1 version,그룹2 SID. ...
		// compare with (Const.groupArrs)
		//  and update Group Info or delete group info from db
		String strs[] = str.split(",");
		for (int i=0; i < strs.length; i++)
		{
			String ss[] = strs[i].split("\\|") ;
			if (ss.length < 2)
				ss = strs[i].split("\\.");

			if (ss.length >= 2)
			{
				int groupSid = Integer.parseInt(ss[0]);
				int groupVers = Integer.parseInt(ss[1]);
				GroupInfo info = Const.findGroup(groupSid); // find from Const.groupArrs
				if (info == null)
				{
					info = new GroupInfo() ;
					info.GroupSid = groupSid ;
					info.GroupVers = groupVers ;
					info.bNeedUpdate = true ;
					info.bNeedDelete = false ;
					Const.groupArrs.add(info);
					Const.dbDao.insertGroup(info);
				}
				else
				{
					info.bNeedDelete = false ;
					if (info.MgrSid == 0 || groupVers > info.GroupVers)
					{
						info.bNeedUpdate = true ;
						info.GroupVers = groupVers ;
						Const.dbDao.updateGroup(info);
					}
				}

			}
		}
	}

	public static void arrangeGroupList()
	{
		// update or delete groups in db
		// this part should be inside task
		for (int i=groupArrs.size()-1; i >=0; i--)
		{
			GroupInfo info = groupArrs.get(i);
			if (info.bNeedDelete)
			{
				Const.dbDao.deleteGroup(info.GroupSid);
				groupArrs.remove(info);
			}
			else if (info.bNeedUpdate)
			{
				ReqParser.reqGroupInfo1(info.GroupSid);
			}
		}
	}

	//ysk20161127
	public static String decrypt(String newClip)
	{
        Const.errCode = 0 ;
        Const.savePuzzleNum(-1) ;
        Const.remainedMinutes = 0 ;

		StringBuilder sb = new StringBuilder() ;

		// decrypt every text between CryptoStart ... CryptoEnd ...
		int pt = 0 ;
		int ptAbs = 0 ;
        boolean bForSave = false ;

		while (pt < newClip.length() && Const.errCode == 0)
		{
			int ptRelSt = -1 ;
			int grade_seq = -1 ;

			for (grade_seq =0; grade_seq <= MAX_GRADE && ptRelSt < 0; grade_seq++)
			{
				ptRelSt = newClip.substring(pt).indexOf(CryptoStartLite[grade_seq]) ;
				if (ptRelSt >= 0)
					break ;
			}

			if (ptRelSt < 0) // can't find
            {
                //ysk20161031 check for encrypt for save
                ptRelSt = newClip.substring(pt).indexOf(CryptoStartSave);

                if (ptRelSt < 0)
                {
                    sb.append(newClip.substring(pt));
                    break;
                }
                else
                {
                    bForSave = true ;
                }
			}

            if (ptRelSt >= 0)
			{
				ptAbs = pt + ptRelSt  ;

				sb.append(newClip.substring(pt, ptAbs));

                if (bForSave)
                    ptAbs += CryptoStartSave.length() ;
                else
				    ptAbs += CryptoStartLite[grade_seq].length();

                int ptRelEnd ;
                if (bForSave)
                    ptRelEnd = newClip.substring(ptAbs).indexOf(CryptoEndSave) ;
                else
				    ptRelEnd = newClip.substring(ptAbs).indexOf(CryptoEndLite[grade_seq]) ;

				if (ptRelEnd < 0)
				{
					sb.append(newClip.substring(ptAbs)) ;
					break ;
				}
				else
				{
					String str2 = newClip.substring(ptAbs, ptAbs+ptRelEnd);
					String str1 = decrypt_part(grade_seq, str2, bForSave) ;
					sb.append(str1);

                    if (bForSave)
                        pt = ptAbs + ptRelEnd + CryptoEndSave.length() ;
                    else
					    pt = ptAbs + ptRelEnd + CryptoEndLite[grade_seq].length() ;
				}
			}
		}

		if (Const.errCode > 0) return "" ;
		String str = sb.toString() ;
		String strDecryptMethod = Const.getAttachMessage() ;
		if (str.contains(strDecryptMethod))
			str = str.replace(strDecryptMethod, "");
		return str ;
	}


    //ysk20161031
	private static String decrypt_part(int grade_seq, String str, boolean bForSave)
	{
        // format
        // for save
        // SAVE_KEY + mysid + password
        // 발신자SID(62진수):공통키*발신자SID*X(1..10) 암호화된 문장(yyyyMMddHHmm(발신날짜 표준);추가정보;원래문장 )
       if (bForSave)
       {
           String key = Util.getMix3String(Const.SAVE_KEY, getMySid()+"", Const.PasswordSave, 32);
           return (AES256.decrypt(key, str));
       }

        String uSid ;
        int userSid ;
        int upt = 0 ;
        String decryptedStr = "" ;

        try {
            upt = str.indexOf(":");
            if (upt > 0) {
                uSid = str.substring(0, upt);
                if (!Util.isAlphaNumeric(uSid))
                    return str;

                userSid = Util.getIntFrom62(uSid);
                upt++; // skip : ;
            } else
                return str;

            String key = Util.getMix3String(Const.COMMON_KEY, userSid + "", (grade_seq+1) + "", 32);
            String str1 = AES256.decrypt(key, str.substring(upt));

            // get yyyyMMddHHmm and random number
            String strs[] = Util.divideString(str1, ";") ;
            String ymd = strs[0] ;
            if (Util.isPassed24Hours(ymd))
            {
                Const.errCode = Const.EC_PASSED_24HOURS ;
                return "" ;
            }

			String strs1[] = Util.divideString(strs[1], ";") ;
			decryptedStr = strs1[1] ;
			Const.savePuzzleNum(-1) ;
            Const.senderSid = userSid ;


            Const.remainedMinutes = 24*60 - Util.getDiffMinutesFromNow(ymd);

            if (userSid != Const.getMySid())
            {
				Const.saveFriend(userSid); //ysk20161027

                // get random number
                // strs1[0] = infos, strs1[1] = decrypted text

                String strs2[] = strs1[0].split(",") ;
                // 1st info = strs2[0]
                String strs3[] = strs2[0].split(":") ;
                // strs3[0] = "1" -> strs3[1]=random 1..100
                if (strs3[0].equals("1"))
                {
                    Const.savePuzzleNum(Integer.parseInt(strs3[1])) ;
                }
            }

        }
        catch (Exception e)
        {
            return "" ;
        }

        return decryptedStr ;
    }

	public static boolean checkRegGroup(Context context)
	{
		return true ;
	}

	public static int myGSid = 0 ; // just registered group sid

	public static void saveGKey(int gSid, String gkey)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("GroupKey-"+gSid, gkey);
		editor.commit();
	}

	public static String getGKey(int gSid)
	{
		if (getService() == null)
			return "" ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		String s = prefs.getString("GroupKey-"+gSid, "abcde");
		return (s) ;
	}
	
    /*
    public static void saveCurGroup(int groupSid, String groupId) 
    {
    	// groupSid == 0, groupId="" for common key
    	if (getService() == null)
    		return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("CurGroupSid", groupSid);	
		editor.putString("CurGroupId", groupId);	
		editor.commit();
    }
    */

	public static String getCurGroupId()
	{
		if (Const.curGroupInfo != null)
			return Const.curGroupInfo.GroupId ;
		else
			return "" ;
	}

	public static int getCurGroupSid()
	{
		if (Const.curGroupInfo != null)
			return Const.curGroupInfo.GroupSid ;
		else
			return 0 ;
	}



	public static void saveNoHelpForNoGroup()
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("NoHelpForNoGroup", true);
		editor.commit();
	}

	public static boolean isNoHelpForNoGroup()
	{
		// dsp help or not if no group to select
		if (getService() == null)
			return false ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		boolean b = prefs.getBoolean("NoHelpForNoGroup", false);
		return (b) ;
	}

	public static void saveMySid(int sid)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("MySid", sid);
		editor.commit();
	}

	public static int getMySid()
	{
        //if (1==1) return 333; //for test

		if (getService() == null)
			return 0 ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		int s = prefs.getInt("MySid", 0);
		return (s) ;
	}

    public static void saveBirthYear(int n)
    {
        if (getService() == null)
            return  ;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("BirthYear", n);
        editor.commit();
    }

    public static int getBirthYear()
    {
        if (getService() == null)
            return 0 ;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
        int s = prefs.getInt("BirthYear", 0);
        return (s) ;
    }

    public static void saveSexy(int n)
    {
        if (getService() == null)
            return  ;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Sexy", n);
        editor.commit();
    }

    public static int getSexy()
    {
        if (getService() == null)
            return 0 ;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
        int s = prefs.getInt("Sexy", 0);
        return (s) ;
    }

	public static void saveMyName(String name)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("MyName", name);
		editor.commit();
	}


	public static String getMyName()
	{
		if (getService() == null)
			return "" ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		String s = prefs.getString("MyName", "");
		return (s) ;
	}

	public static void saveGroupCnt(int n)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("GroupCnt", n);
		editor.commit();
	}
    
	public static String strR(Context context, int id)
	{
		return(context.getResources().getString(id));
	}


	public static void MyLog(String sTag, String sText)
	{
        Context context = getService() ;
        if (context == null)
            context = envSettings ;

        boolean isDebuggable = // true ;
              ( 0 != ( context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );
        if (isDebuggable)
		    Log.d(sTag, sText);
	}

	public static void MyLog(String sText)
	{
		MyLog("Hush", sText);
	}

	private static ContactsDao contactsDao = null;
	public static ContactsDao getContactsDao()
	{
		if (contactsDao == null)
			contactsDao = new ContactsDao() ;
		return contactsDao ;
	}

	public static ContactsList contactsList = null ;
	public static String searchWord = "" ;
	public static List<ContactGroup> groups = new ArrayList<ContactGroup>();
	public static List<ContactUser> selectedContacts = null;
	public static ContentResolver cr;
//	static String messageOrg ;

	//public static int sendLimitCount = 20 ;
	// public static int sentCountToday = 0 ;
	// public static int sentCountSaveDay = -1;
	public static ContactGroup notAssignedGroup = null ; // 占쏙옙占쏙옙占쏙옙 占쌓뤄옙
	public static boolean bGetGroupContacts = false ;

	public static int curGroupIx ; // currently selected group ix from groups
	public static boolean isMakingContacts = false ;
	public static boolean isReadingContacts = false ;
	public static boolean isSavingContacs = false ;

	public static class ContactGroup
	{
		public Set<String> groupPhones = new HashSet<String>();
		public long _id ;
		public int GroupId;
		public String GroupName;
		public boolean isMyGroup = false ;
		public boolean bSelectAllUsers = false ;
		public List<ContactUser> contacts = new ArrayList<ContactUser>();
		public List<ContactUser> contactsFound = new ArrayList<ContactUser>(); //from searchWord
		public int contactsCount = -1 ; // undefined

		public List<ContactUser> getContacts()
		{
			if (Const.searchWord.equals(""))
				return contacts ;
			else
				return contactsFound ;
		}
	}

	public static class ContactUser
	{
		public long _id ;
		public int GroupId = 0 ;
		public String UserName = "" ;
		public String UserPhone = "" ;
		public boolean bSelected = false ; // exclude in DB
	}

	public static void searchContacts()
	{
		// make all groups contactsFound
		for (ContactGroup grp : groups)
		{
			grp.contactsFound = new ArrayList<ContactUser>();
			if (!searchWord.equals(""))
			{
				for (ContactUser usr : grp.contacts)
				{
					if ((usr.UserName.contains(searchWord)) ||
							(usr.UserPhone.contains(searchWord)))
						grp.contactsFound.add(usr);
				}
			}
		}
	}

	public static void addContactUser(ContactGroup grp, String sUserName, String sUserPhone)
	{
		// bGroupMember占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쌓룹에 占쏙옙占쏙옙 占쏙옙호 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쌓뤄옙占쏙옙 占쏙옙占쏙옙처 占쏙옙占쏙옙占싼댐옙.
		// get rid of '-'
		//Const.MyLog("addContactUser", "grp name="+grp.GroupName+"userName="+sUserName);
		String sPhone = sUserPhone.replaceAll("-", "");

		// 1. validate check of phone (starting with "+821", "01" == OK)
		if ((sPhone==null) || (sPhone.length() < 8))
			return ;

		if (sPhone.length() >= 10)
		{
			grp.groupPhones.add(sPhone);

			ContactUser usr1 = new ContactUser();
			usr1.UserName = sUserName ;
			usr1.UserPhone = sPhone ;
			usr1.GroupId = grp.GroupId ;

			grp.contacts.add(usr1);
		}
	}

	public static int getSelectedCount(ContactGroup grp)
	{
		int nSelected = 0 ;

		if (grp.contacts == null)
			return 0;

		//for(KeywordObj obj : keywords) {
		//for (int i=0; i < grp.contacts.size(); i++)
		for (ContactUser usr : grp.contacts)
		{
			if (usr.bSelected)
				nSelected++;
		}
		return nSelected ;
	}

	public static void addSelectedGroup(ContactGroup grp)
	{
		for (ContactUser usr : grp.contacts)
		{
//			ContactUser usr = grp.contacts.get(i);
			usr.bSelected = true ;

			if (!existContactUser(usr.UserPhone))
				selectedContacts.add(usr);
		}
	}

	private static void refreshSelected1()
	{
		for (ContactGroup grp : getGroupList())
		{
//			ContactGroup grp = groups.get(i);
			grp.bSelectAllUsers = false ;
			if (grp.contacts != null)
			{
				for (ContactUser usr : grp.contacts)
				{
//				    ContactUser usr = grp.contacts.get(j);
					if (usr.bSelected)
					{
						grp.bSelectAllUsers = true ;
						if (!existContactUser(usr.UserPhone))
							selectedContacts.add(usr);
					}
				}
			}
		}
	}

	public static void refreshSelected()
	{
		selectedContacts = new ArrayList<ContactUser>();

		refreshSelected1();
	}

	public static void clearSelected()
	{
		for (ContactGroup grp : getGroupList())
		{
			grp.bSelectAllUsers = false ;
			if (grp.contacts != null)
			{
				for (ContactUser usr : grp.contacts)
				{
					usr.bSelected = false ;
				}
			}
		}
	}

	public static void deleteServerGroup(Const.ContactGroup grp1)
	{
		for (ContactGroup grp : getGroupList())
		{
			if (grp1.GroupId == grp.GroupId)
			{
				getGroupList().remove(grp);
				return ;
			}
		}
	}

	public static void deleteSelectedGroup(int nGroupId)
	{
		for (int i = selectedContacts.size()-1; i >= 0; i--)
		{
			ContactUser usr = selectedContacts.get(i);
			usr.bSelected = false ;
			if (usr.GroupId == nGroupId)
				selectedContacts.remove(usr);
		}
	}

	public static void SelectUser(ContactUser usr, boolean isChecked)
	{
		usr.bSelected = isChecked ;
	}

	public static void SelectGroup(ContactGroup grp, boolean isChecked)
	{
		grp.bSelectAllUsers = isChecked ;

		if (isChecked)
		{
			addSelectedGroup(grp);
		}
		else
		{
			deleteSelectedGroup(grp.GroupId);
		}
	}

	public static void changeSelectAll(boolean bSelected)
	{
		ContactGroup g = getGroupList().get(curGroupIx);
		g.bSelectAllUsers = bSelected ;

		for (ContactUser u : g.contacts)
		{
			u.bSelected = bSelected ;
		}
	}

	public static int getSelectCount()
	{
		int n = 0 ;
		ContactGroup g = getGroupList().get(curGroupIx);

		for (ContactUser u : g.contacts)
		{
			if (u.bSelected)
				n++;
		}
		return n ;
	}

	public static void changeSelectUser(int ix, boolean bSelected)
	{
		ContactGroup g = getGroupList().get(curGroupIx);
		ContactUser u = g.getContacts().get(ix);
		u.bSelected = bSelected ;
	}

	public static boolean existContactUser(String sUserPhone)
	{
		// check if contactcAll include phone already
		for (ContactUser usr : selectedContacts)
		{
			if (usr.UserPhone.equals(sUserPhone))
				return true ;
		}
		return false ;
	}

	public static List<ContactGroup> getGroupList()
	{
		List<ContactGroup> plist ;

		plist = groups ;

		return plist ;
	}

	public static ContactGroup findContactGroup(int nGroupOrg, int nGroupId)
	{
		for (ContactGroup grp : getGroupList())
		{
			if (grp.GroupId == nGroupId)
				return grp ;
		}
		return null ;
	}


	public static void excludeContactIfExist(ContactGroup grp, String sPhone)
	{
//		for (int i=grp.contacts.size()-1; i >= 0; i--)
		for (ContactUser usr : grp.contacts)
		{
//			ContactUser usr = grp.contacts.get(i);
			if (usr.UserPhone.equals(sPhone))
			{
				grp.contacts.remove(usr);
				return ;
			}
		}
	}

	/*
	public static boolean existGroupUser(ContactGroup grp, String userPhone)
	{
		for (int i=0; i < grp.contacts.size(); i++)
		{
			ContactUser usr = grp.contacts.get(i);
			if (usr.UserPhone.equals(userPhone))
				return true ;
		}
		return false ;
    }
    */
	public static void checkNotAssignedGroup(String sPhone)
	{
		if (notAssignedGroup == null)
			return ;

//		for (int i=notAssignedGroup.contacts.size()-1; i >= 0; i--)
		for (ContactUser usr :  notAssignedGroup.contacts)
		{
			if (usr.UserPhone.equals(sPhone))
			{
				notAssignedGroup.contacts.remove(usr);
				return ;
			}
		}
	}

	public static int getMakePhoneState()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		int nCnt = prefs.getInt("MakePhoneState", 0);
		return nCnt ;
	}

	public static void saveMakePhoneState(int nState)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("MakePhoneState", nState);
		editor.commit();
	}

	private static void startPopup(Context context, int nPopup) {
		// send message to alarmreceiver and alarmreceiver dsp popup

		if (nPopup == Const.DspDecryptedText ||
				nPopup == Const.DspPureText) {
			if (Const.curGroupInfo != null) {
				if (!Const.curGroupInfo.isUseOK()) {
					Const.saveDspPopup(0);
					return;
				}
			}
		}

		switch (nPopup) {
			case Const.DspPureText:
				Intent intent1 = new Intent(context, PopupEncrypt.class);
				intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Const.getService().startActivity(intent1);
				break;
			case Const.DspDecryptedText:
				Intent intent2 = new Intent(context, PopupDecrypt.class);
				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Const.getService().startActivity(intent2);
				break;
			case Const.DspSettings:
				Intent intent3 = new Intent(context, EnvSettings.class);
				intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Const.getService().startActivity(intent3);
				break;
			case Const.DspPasswordAsk:
				Intent intent4 = new Intent(context, PasswordAsk.class);
				intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Const.getService().startActivity(intent4);
				break;
		}
	}

	public static void startPopup(Context context)
	{
			if (!Const.isReqProcess && Const.getDspPopup() > 0) {
				int n = Const.getDspPopup();
				saveDspPopup(0);
				startPopup(context, n);
			}
	}

	public static void dspMustUpgrade(final Context context)
	{
		new AlertDialog.Builder(context).setMessage(R.string.upgrade_must)
				.setCancelable(false)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getResources().getString(R.string.app_install_url))));
					}
				})
				.show();
	}

	private static UserInfo findUserInfo(int uSid)
	{
		for (UserInfo info : userArrs)
		{
			if (info.UserSid == uSid)
				return info ;
		}
		return null ;
	}

	public static void saveUserNames(String sids, String names)
	{
		String uSids[] = sids.split(",") ;
		String uNames[] = names.split(",") ;

		if (uSids.length == uNames.length)
		{
			for (int i=0; i < uSids.length; i++)
			{
				int uSid = Integer.parseInt(uSids[i]) ;
				UserInfo info = findUserInfo(uSid) ;
				if (info != null)
				{
					info.UserName = uNames[i] ;
					dbDao.updateUserName(info);
				}
			}
		}
	}

	public static void saveLevelsParts(String sids, String levels, String parts)
	{
		String uSids[] = sids.split(",") ;
		String uLevels[] = levels.split(",") ;
		String uParts[] = parts.split(",") ;

		if (uSids.length == uLevels.length &&
				uSids.length == uParts.length)
		{
			for (int i=0; i < uSids.length; i++)
			{
				int uSid = Integer.parseInt(uSids[i]) ;
				UserInfo info = findUserInfo(uSid) ;
				if (info != null)
				{
					info.UserLevel = Integer.parseInt(uLevels[i]) ;
					info.PartsCnt = Integer.parseInt(uParts[i]) ;
					dbDao.updateUserLevel(info);
				}
			}
		}
	}

	//ysk20161103
	public static boolean mustUpgrade()
	{
		String sMust = getMustVersion() ;
		if (Util.getAppVersion().compareTo(sMust) < 0)
			return true ;
		else
			return false ;
	}

	public static  String getMustVersion()
	{
		String s = getPrepString("MustVersion", "");
		return (s) ;
	}

	public static void saveMustVersion(String vers)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("MustVersion", vers);
		editor.commit();
	}

	//ysk20161127
	public static final int MIN_PUZZLE_CNT = 2 ; // 2x2 matrix
	public static final int MAX_PUZZLE_CNT = 8 ; // 8x8 matrix, 9=2x2 matrix, 10=3x3 ... 9이상=별 표시
    public static final int MAX_PUZZLES = (MAX_PUZZLE_CNT)*(MAX_PUZZLE_CNT) ;
    public static final int MAX_PIECESROW = MAX_PUZZLE_CNT ;
	//public static final int MAX_LEVEL_FRIENDS = 100 ;

    //ysk20161107
    public static int registeredLevel = 0 ;

    public static int puzzles[] = new int[MAX_PUZZLES] ;
	//public static int rcvdPuzzleNum = -1 ; // 0..99
	public static int curLevel = 0 ; // for puzzle disp
	public static boolean prevPuzzleState = false ;

	//ysk20171127
	public static int getGrade(int level)
	{
		return (level / 7) ;
	}

	public static int getGradeIndex(int level)
	{
		int grade = (level / 7) ;
		if (grade > MAX_GRADE)
			return MAX_GRADE ;
		else
			return grade ;
	}

	public static void savePuzzleNum(int n)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("PuzzleNum", n);
		editor.commit();
	}


	public static int getPuzzleNum()
	{
		if (getService() == null)
			return -1 ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		int n = prefs.getInt("PuzzleNum", -1);

        //ysk20161114
        int m = getUsedPuzzleNum(senderSid) ;
        if (m == n)
            n = -1 ;
		return (n) ;
	}

	public static String getFriendSids()
	{
		String str = "" ;
		for (UserInfo info : userArrs)
		{
			if (!str.equals(""))
				str += "," ;

			str += info.UserSid+"" ;
		}
		return str ;
	}

	//ysk20161028
    public static String getPuzzleHex()
    {
        Context context = getService() ;
        if (context == null)
            context = envSettings ;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String s = prefs.getString("Puzzles", "");

        return (s) ;
    }

	//ysk20161028
    public static void initPuzzles()
    {
        for (int i=0; i < MAX_PUZZLES; i++)
            puzzles[i] = 0 ;
        String str = getPuzzleHex() ;
		str = Util.convHexToBinStr(str) ;

        for (int i=0; i < str.length(); i++)
        {
			if (str.charAt(i) == '1')
               puzzles[i] = 1 ;
        }
    }

    //ysk20161127
    public static int getPiecesRow(int level)
    {
        int n = (level) % 7 ;
        int piecesRow = n + 2 ;
        return piecesRow ;
    }

	//ysk20161028
    public static void savePuzzles(int level)
    {
        String val = "" ;

        int piecesRow = getPiecesRow(level) ;

            for (int i=0; i < piecesRow*piecesRow; i++)
            {
                if (puzzles[i] == 1)
                {
					val += "1" ;
                }
				else
				{
					val += "0" ;
				}
            }

		val = Util.convBinToHexStr(val) ;

		savePuzzleHex(val);
    }

	public static void savePuzzleHex(String val) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("Puzzles", val);
		editor.commit();
	}

    public static int countPuzzles(int level)
    {
        int cnt = 0 ;
        int piecesRow = Const.getPiecesRow(level) ;

            for (int i=0; i < piecesRow*piecesRow; i++)
            {
                if (puzzles[i] == 1)
                {
                    cnt++;
                }
            }
        return cnt ;
    }

	public static int getMyPartsCnt(int level)
	{
		//ysk20161127
        return countPuzzles(level);
    }



	//ysk20161113
	public static int remainedPuzzles()
	{
		int cnt = 0 ;
		int piecesRow = Const.getPiecesRow(getMyOrgLevel()) ;

		for (int i=0; i < piecesRow*piecesRow; i++)
		{
			if (puzzles[i] == 0)
			{
				cnt++;
			}
		}
		return cnt ;
	}


	public static void saveBlocksFromMe(String sids)
	{
		String uSids[] = sids.split(",") ;

		for (int i=0; i < uSids.length; i++)
		{
			int uSid = Integer.parseInt(uSids[i]) ;
			UserInfo info = findUserInfo(uSid) ;
			if (info != null)
			{
				if (info.BlockId != 1) {
					info.BlockId = 1;
					dbDao.updateBlockId(info);
				}
			}
			else // not exist
			{
				UserInfo info1 = new UserInfo();
				info1.UserSid = uSid ;
				info1.BlockId = 1 ;
				dbDao.insertUser(info1);
			}
		}
	}

	public static void saveBlocksAboutMe(String sids)
	{
		String uSids[] = sids.split(",") ;

		for (int i=0; i < uSids.length; i++)
		{
			int uSid = Integer.parseInt(uSids[i]) ;
			UserInfo info = findUserInfo(uSid) ;
			if (info != null)
			{
				if (info.BlockId == 0) {
					info.BlockId = 2;
					dbDao.updateBlockId(info);
				}
			}
			else // not exist
			{
				UserInfo info1 = new UserInfo();
				info1.UserSid = uSid ;
				info1.BlockId = 2 ;
				dbDao.insertUser(info1);
			}
		}
	}

	public static void saveNotice(String notice)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("Notice", notice);
		editor.commit();
	}

    //ysk20161103
    public static String getNotice()
    {
        String s= getPrepString("Notice", "") ;
        return s ;
    }

    public static void saveViewedNotice(String notice)
    {
        savePrepString("ViewedNotice", notice);
    }

    public static String getViewedNotice()
    {
        String s= getPrepString("ViewedNotice", "") ;
        return s ;
    }


	public static void saveRecomSid(int recomSid)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("RecomSid", recomSid);
		editor.commit();
	}


	public static int getRecomSid()
	{
		if (getService() == null)
			return 0 ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		int id = prefs.getInt("RecomSid", 0);
		return (id) ;
	}

	public static void saveHP(int hp)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("HushPower", hp);
		editor.commit();
	}


	public static int getHP()
	{
		if (getService() == null)
			return 0 ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		int id = prefs.getInt("HushPower", 0);
		return (id) ;
	}

	public static void saveLock(boolean state)
	{
		// true = locked
		// false = unlocked
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("Lock", state);
		editor.commit();
	}

	public static boolean getLock() {
		if (getService() == null)
			return false;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		boolean p = prefs.getBoolean("Lock", true);
		return p ;
	}

	public static boolean isLock() {
		if (getService() == null)
			return false;

		if (Const.isLockTimeOut())
		{
			return true ;
		}

		return getLock() ;
	}

    public static void saveKbdModeAsked(boolean state)
    {
        // true = locked
        // false = unlocked
        if (getService() == null)
            return  ;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("KbdModeAsked", state);
        editor.commit();
    }

    public static boolean isKbdModeAsked() {
        if (getService() == null)
            return false;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
        boolean p = prefs.getBoolean("KbdModeAsked", false);
        return p ;
    }

	public static void saveLockAsked(boolean state)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("LockAsked", state);
		editor.commit();
	}

	public static boolean isLockAsked() {
		if (getService() == null)
			return false;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		boolean p = prefs.getBoolean("LockAsked", false);
		return p ;
	}

	public static void saveAttachOk(boolean state)
	{
		// true = attach decrypt help ok
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("AttachOk", state);
		editor.commit();
	}

	public static boolean isAtachOk() {
		if (getService() == null)
			return false;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		boolean p = prefs.getBoolean("AttachOk", true);
		return p ;
	}

	public static String getAttachMessage()
	{
		String str1 = strR(getService(), R.string.decrypt_method);
		String str2 = strR(getService(), R.string.app_install_url);
		String str = String.format(str1, str2) ;
		return str ;
	}

	//for time out and lock
	public static long defaultTimeOut = 24*60*60*1000 ;

	public static void saveLockTimeOut(long val)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong("LockTimeOut", val);
		editor.commit();
	}

	public static long getLockTimeOut() {
		if (getService() == null)
			return defaultTimeOut;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		long p = prefs.getLong("LockTimeOut", defaultTimeOut);
		return p ;
	}

    public static void saveTimeOutText(String val)
    {
        if (getService() == null)
            return  ;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TimeOutText", val);
        editor.commit();
    }

    public static String getTimeOutText() {
        if (getService() == null)
            return strR(envSettings, R.string.time_out_default);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
        String p = prefs.getString("TimeOutText", strR(getService(), R.string.time_out_default));
        return p ;
    }

	public static void saveLastUsedTime()
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong("LastUsedTime", Util.getCurTime());
		editor.commit();
	}

	public static long getLastUsedTime() {
		long cTime = Util.getCurTime() ;
		if (getService() == null)
			return cTime;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		long p = prefs.getLong("LastUsedTime", cTime);
		return p ;
	}

	public static boolean isLockTimeOut()
	{
		long curTime = Util.getCurTime() ;
		long timeOut = getLockTimeOut() ;
		long lastUsedTime = getLastUsedTime() ;
		if (curTime >= lastUsedTime+timeOut)
			return true ;
		else
			return false ;
	}

    public static long timeOuts[] =
    {
			60*1000 ,
			5*60*1000 ,
			10*60*1000 ,
            30*60*1000 ,
            60*60*1000 ,
			2*60*60*1000 ,
            4*60*60*1000 ,
            8*60*60*1000 ,
            12*60*60*1000 ,
            24*60*60*1000 ,
            7*24*60*60*1000
    } ;

	public static int getIndexFromTimeOut()
	{
        long timeOut = getLockTimeOut() ;
		Const.MyLog("timeout="+timeOut);
        for (int i=1; i <= 10; i++) {
            if (timeOut < timeOuts[i]) return i - 1;
        }
        return timeOuts.length-1 ;
	}

	public static long getTimeOutFromIndex(int index)
	{
        return timeOuts[index];
	}

	private static String getYoutubeKey(int level)
	{
		return "YoutubeUrl"+"-"+level ;
	}
	//ysk20161206
	private static String getImageKey(int level)
	{
		return "ImageUrl"+"-"+level ;
	}
	public static void saveImageUrl(int level, String url)
	{
		// save key = "Youtube" + "-" + level
		if (getService() == null)
			return  ;
		String key = getImageKey(level) ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, url);
		editor.commit();
	}
	public static String getImageUrl(int level)
	{
		if (getService() == null)
			return "" ;

		String key = getImageKey(level) ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		String s = prefs.getString(key, "");
		return (s) ;
	}


	public static void saveYoutubeUrl(int level, String url)
	{
		// save key = "Youtube" + "-" + level
		if (getService() == null)
			return  ;
		String key = getYoutubeKey(level) ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, url);
		editor.commit();
	}

	public static String getYoutubeUrl(int level)
	{
		if (getService() == null)
			return "" ;

		String key = getYoutubeKey(level) ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		String s = prefs.getString(key, "");
		return (s) ;
	}

	public static int getPrepInt(String key, int default_v)
	{
		if (getService() == null)
			return default_v ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		int n = prefs.getInt(key, default_v);
		return (n) ;
	}

	public static void savePrepInt(String key, int val)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, val);
		editor.commit();
	}

	public static String getPrepString(String key, String default_v)
	{
		if (getService() == null)
			return default_v ;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		String n = prefs.getString(key, default_v);
		return (n) ;
	}

	public static void savePrepString(String key, String val)
	{
		if (getService() == null)
			return  ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, val);
		editor.commit();
	}

	public static final int MAX_LUCKY_NUM = 99 ; // 0..99

	static int[] luckyNums = new int[MAX_LUCKY_NUM+1]  ;

    static String luckyVals = null ;

    public static void getLuckyVals()
    {
        luckyVals = getPrepString("LuckyVals", "") ;
        Const.MyLog("ran getLuckyVals="+luckyVals);

        String vals[] = luckyVals.split(",") ;
        for (int i=0; i <=  MAX_LUCKY_NUM; i++)
        {
            if ((vals != null) && (i < vals.length))
                luckyNums[i] = (vals[i].equals("1"))?1:0 ;
            else
                luckyNums[i] = 0 ;
        }
    }

    public static void saveLuckyVals()
    {
        String s= "" ;
        for (int i=0; i < luckyNums.length; i++)
        {
            if (!s.equals("")) s += "," ;
            s += (luckyNums[i]==1)?"1":"0" ;
        }
        Const.MyLog("ran saveLuckyVals="+s);
        savePrepString("LuckyVals", s);
    }

    public static int getRemainedNum(int valth)
    {
        // val th num from remained numbers
        if (luckyVals == null)
        {
            getLuckyVals() ;
        }

        int v = 0 ;
        for (int i=0; i <= MAX_LUCKY_NUM ; i++)
        {
            if (luckyNums[i] == 0) {
                if (v >= valth)
                {
                    luckyNums[i] = 1 ;
                    saveLuckyVals();
                    return i ;
                }
                else
                    v++;
            }
        }
        return -1 ; // error case
    }

	public static void saveLuckyCount(int n)
	{
		// 0..99 save
		savePrepInt("LuckyCount", n) ;
	}

	public static int getLuckyCount()
	{
		// 0..99 get
		return getPrepInt("LuckyCount", 0) ;
	}

	public static void saveLuckyValue(int n)
	{
        luckyNums[n] = 1 ;
        saveLuckyVals();
	}

	public static void saveTodayRandomNumber(int num)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		String today = Util.getDateString("yyyyMMdd");

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("SaveDate", today);
		editor.putInt("RandomNumber", num);
		editor.commit();
	}

	public static int getTodayRandomNumber()
	{
		// get savedDate
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
		String sDate = prefs.getString("SaveDate", "");

		String today = Util.getDateString("yyyyMMdd");
		if (!sDate.equals(today))
		{
			// new date
            int nCnt = getLuckyCount() ;
            if (nCnt > MAX_LUCKY_NUM)
            {
                MyLog("ran nCnt > MAX_LUCKY_NUM");
                nCnt = 0 ;
                saveLuckyCount(0);
                luckyVals = null ;
                savePrepString("LuckyVals", "");
            }

            saveLuckyCount(nCnt+1);

            MyLog("ran nCnt="+nCnt);

			int val = Util.getRandomInt(MAX_LUCKY_NUM+1-nCnt); //0..some

            MyLog("ran number="+val);
            int val1 = getRemainedNum(val) ; // val th num from remained numbers
			Const.saveTodayRandomNumber(val1);

            MyLog("today ran number="+val1);
			return val1 ;
		}
		else
		{
			int val = prefs.getInt("RandomNumber", 1);
			return val ;
		}
    }

    //ysk20161031
    public static void saveDspEncryptPopSend(boolean bOk)
    {
        savePrepInt("DspEncryptPopSend", (bOk)?1:0);
    }

    public static boolean canDspEncryptPopSend()
    {
        int n = getPrepInt("DspEncryptPopSend", 1);
        return (n == 1) ;
    }

    public static void saveDspEncryptPopSave(boolean bOk)
    {
        savePrepInt("DspEncryptPopSave", (bOk)?1:0);
    }

    public static boolean canDspEncryptPopSave()
    {
        int n = getPrepInt("DspEncryptPopSave", 1);
        return (n == 1) ;
    }


    public static void saveDspDecryptPop(boolean bOk)
    {
        savePrepInt("DspDecryptPop", (bOk)?1:0);
    }

    public static boolean canDspDecryptPop()
    {
        int n = getPrepInt("DspDecryptPop", 1);
        return (n == 1) ;
    }

	public static void saveFriend(int  uSid)
	{
		UserInfo info = findUserInfo(uSid) ;
		if (info == null) // not exist
		{
			UserInfo info1 = new UserInfo();
			info1.UserSid = uSid ;
			info1.BlockId = 0 ;
			dbDao.insertUser(info1);
			Const.userArrs.add(info1);
		}
	}

	public static String getFriendSidsNoName()
	{
		String str = "" ;
		for (UserInfo info : userArrs)
		{
			if (info.UserName.equals("")) {
				if (!str.equals(""))
					str += ",";

				str += info.UserSid + "";
			}
		}
		return str ;
	}

    //ysk20161031
    public static String CryptoSymbolSave = "□" ;
    public static String CryptoStartSave = "<□" ;
    public static String CryptoEndSave = "□>" ;

    public static String PasswordSave = "" ;

    public static final int MethodSend = 0 ;
    public static final int MethodSave = 1 ;

    public static int curDecryptMethod = MethodSend ;

    public static int getEncryptMethod()
    {
        int n = getPrepInt("EncryptMethod", MethodSend);
        return (n) ;
    }

    public static void saveEncryptMethod(int method)
    {
        savePrepInt("EncryptMethod", method);
    }

    public static String encryptForSave(String newClip)
    {
        StringBuilder sb = new StringBuilder() ;
        sb.append(Const.CryptoStartSave);
        String key = Util.getMix3String(Const.SAVE_KEY, getMySid()+"", getPassword(), 32);
        sb.append(AES256.encrypt(key, newClip));
        sb.append(Const.CryptoEndSave);
        return (sb.toString()) ;
    }

    //ysk20161103
    public static boolean bAllFriends = false ;

    public static void saveReqLevelDate()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
        String today = Util.getDateString("yyyyMMdd");

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ReqLevelDate", today);
        editor.commit();
    }

    public static boolean isReqLevelOk()
    {
        // get savedDate
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getService());
        String sDate = prefs.getString("ReqLevelDate", "");

        String today = Util.getDateString("yyyyMMdd");
        if (!sDate.equals(today))
        {
            saveReqLevelDate();
            return true ;
        }
        else
        {
            return false ;
        }
    }

	//ysk20161113
	public static boolean isAskMatchYes()
	{
		int n = getPrepInt("AskMatchYes", 1) ;
		return (n == 1) ;
	}

	public static void saveAskMatchYes(boolean bYes)
	{
		savePrepInt("AskMatchYes", (bYes)?1:0 );
	}

	public static boolean isDspDup()
	{
		int n = getPrepInt("DspDup", 1) ;
		return (n == 1) ;
	}

	public static void saveDspDup(boolean bYes)
	{
		savePrepInt("DspDup", (bYes)?1:0 );
	}

    public static int getUsedPuzzleNum(int uSid)
    {
        String key = "UsedPuzzleNum"+uSid ;
        return getPrepInt(key, -1) ;
    }

    public static void saveUsedPuzzleNum(int uSid, int num)
    {
        if (num < 0)
            return ;

        String key = "UsedPuzzleNum"+uSid ;
        savePrepInt(key, num);
    }

}

