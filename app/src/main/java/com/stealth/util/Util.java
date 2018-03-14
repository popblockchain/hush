package com.stealth.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stealth.service.Const;
import com.stealth.hushkbd.R;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Patterns;
import android.util.TypedValue;
import android.widget.EditText;

/*
	   String sYY = sYYMMDDHHMM.substring(0, 4);
	   String sMM = sYYMMDDHHMM.substring(4, 6);
	   String sDD = sYYMMDDHHMM.substring(6, 8);
	   String sHH = sYYMMDDHHMM.substring(8, 10);
	   String smm = sYYMMDDHHMM.substring(10, 12);
	   
	   int nYY = Integer.parseInt(sYY);
	   int nMM = Integer.parseInt(sMM);
	   int nDD = Integer.parseInt(sDD);
	   int nHH = Integer.parseInt(sHH);
	   int nmm = Integer.parseInt(smm);

	   // adjust time zone
	   Calendar cal = new GregorianCalendar(nYY,nMM,nDD,nHH,nmm);
	   return (cal.getTimeInMillis()-32400000L) ;
	   */
public class Util {
	public static String getDateyymmddhhmm(long cTime) {
		// format = yyyy-MM-dd hh:mm
		Date d = new Date(cTime);
		String time = "";

		try {
			SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmm");
			ft.setTimeZone(TimeZone.getTimeZone("GMT+0900"));
			//ft.setTimeZone(TimeZone.getTimeZone("KOREA"));
			time = ft.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (time.length() > 0)
			time = time.substring(2);
		return time;
	}

	// md5 암호화
	private static String encryptMD5(String key) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(key.getBytes("UTF-8"));
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "";
		}
		return result;
	}

	public static String getHash(String str) {
		return encryptMD5(str);
	}

	public static String getCountryZipCode0() {
		String CountryID = "";
		String CountryZipCode = "";

		TelephonyManager manager = (TelephonyManager) Const.getService().getSystemService(Context.TELEPHONY_SERVICE);
		//getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		String[] rl = Const.getService().getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0];
				break;
			}
		}
		return CountryZipCode;
	}

	public static String getCountryZipCode3() {
		String CountryZipCode = getCountryZipCode0();

		String str = "";
		for (int i = 0; i < 3 - CountryZipCode.length(); i++)
			str += "0";
		return str + CountryZipCode;
	}

	public static String getDateString(String format) {
		// format = 'yyyyMMdd' etc.
		// 占쏙옙占� 占쏙옙占쏙옙
		Date date = new Date();
		return getFormatDate(date, format);
	}

	public static String getStdDateString(String format) {
		// format = 'yyyyMMdd' etc.
		// 占쏙옙占� 占쏙옙占쏙옙
		Date date = new Date();
		return getStdFormatDate(date, format);
	}

	public static String getStdFormatDate(Date date, String formatString) {
		// get Greenwich std time String
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		format.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
		String str = format.format(date);
		return str;
	}

	public static String getFormatDate(Date date, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		String str = format.format(date);
		return str;
	}


	public static long convYYTimeToCTime(String sYYMMDDHHMM) {
		long lTime = 0L;
		try {
			java.util.Date date = new SimpleDateFormat("yyyyMMddHHmm").parse(sYYMMDDHHMM);
			lTime = date.getTime();
		} catch (Exception e) {
		}
		;

		return lTime;
	   /*
	   String sYY = sYYMMDDHHMM.substring(0, 4);
	   String sMM = sYYMMDDHHMM.substring(4, 6);
	   String sDD = sYYMMDDHHMM.substring(6, 8);
	   String sHH = sYYMMDDHHMM.substring(8, 10);
	   String smm = sYYMMDDHHMM.substring(10, 12);
	   
	   int nYY = Integer.parseInt(sYY);
	   int nMM = Integer.parseInt(sMM);
	   int nDD = Integer.parseInt(sDD);
	   int nHH = Integer.parseInt(sHH);
	   int nmm = Integer.parseInt(smm);

	   // adjust time zone
	   Calendar cal = new GregorianCalendar(nYY,nMM,nDD,nHH,nmm);
	   return (cal.getTimeInMillis()-32400000L) ;
	   */
	}

	public static long getCurTime() {
/*	   Time today = new Time(Time.getCurrentTimezone());
	   today.setToNow();

		TimeZone tZone = TimeZone.getDefault();
		int offset = tZone.getOffset(date.getTime());
		
	   return (System.currentTimeMillis()+9*60*60*1000L);
	   
	   Calendar cal = Calendar.getInstance(); 

	   int millisecond = cal.get(Calendar.MILLISECOND);
	   int second = cal.get(Calendar.SECOND);
	   int minute = cal.get(Calendar.MINUTE);
	         //12 hour format
	   int hour = cal.get(Calendar.HOUR);
	         //24 hour format
	   int hourofday = cal.get(Calendar.HOUR_OF_DAY);
	 Same goes for the date, as follows:

	 Calendar cal = Calendar.getInstance(); 

	   int dayofyear = cal.get(Calendar.DAY_OF_YEAR);
	   int year = cal.get(Calendar.YEAR);
	   int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
	   int dayofmonth = cal.get(Calendar.DAY_OF_MONTH);
*/

		long l2 = System.currentTimeMillis();

		return l2;
	}

	public static int getCurMinute() {
		Calendar cal = Calendar.getInstance();
		int minute = cal.get(Calendar.MINUTE);
//	   Const.MyLog("getCurDay", "Hour="+hourOfDay);
		return minute;
	}

	public static int getCurHour() {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY); // 0 -- 23
//	   Const.MyLog("getCurDay", "Hour="+hourOfDay);
		return hour;
	}

	public static long getDiffDate(String startYYYYMMDD, String endYYYYMMDD) {
		long diffDays = 0;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Date beginDate = formatter.parse(startYYYYMMDD);
			Date endDate = formatter.parse(endYYYYMMDD);
			// 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
			long diff = endDate.getTime() - beginDate.getTime();
			diffDays = diff / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
		}
		return diffDays;
	}

	public static int getCurDay() {
		Calendar cal = Calendar.getInstance();
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
//	   Const.MyLog("getCurDay", "Hour="+hourOfDay);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		return dayOfMonth;
	}

	public static String convPurePhoneNumber(String phone) {
		String str = phone.trim();
		str = PhoneNumberUtils.stripSeparators(str);
		PhoneNumberUtils.toaFromString(str);
		return str;
	}

	public static String convGlobalPhoneNumber(String phone) {
		if (phone.startsWith("+"))
			return (convPurePhoneNumber(phone));
		else if (phone.startsWith("0")) {
			String str = convPurePhoneNumber(phone);
			return "+" + getCountryZipCode0() + str.substring(1);
		}
		return phone;
	}

	public static String convLocalPhoneNumber(String phone) {
		String str = phone.trim();
		if (phone.startsWith("+")) {
			if (phone.substring(1).startsWith(getCountryZipCode0())) {
				int len = getCountryZipCode0().length();
				return "0" + phone.substring(1 + len);
			} else
				return phone;
		} else
			return phone;
	}


	//ysk 0.6.6
	public static int getCurMonth() {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		return month;
	}

	public static int getCurYear() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		return year;
	}

	public static String convCTimeToHHMM(long cTime) {
		Date d = new Date(cTime);
		if (isYesterday(cTime)) {
//		   return Const.mainActivity.getResources().getString(R.string.yesterday);
			SimpleDateFormat ft = new SimpleDateFormat("M.d");
			ft.setTimeZone(TimeZone.getTimeZone("GMT+0900"));
			//ft.setTimeZone(TimeZone.getTimeZone("KOREA"));
			String time = ft.format(d);

			return time;
		} else {
			SimpleDateFormat ft = new SimpleDateFormat("kk:mm");
			ft.setTimeZone(TimeZone.getTimeZone("GMT+0900"));
			//ft.setTimeZone(TimeZone.getTimeZone("KOREA"));
			String time = ft.format(d);

			return time;
		}
	}

	public static String getDateLong(long cTime) {
		// format = yyyy-MM-dd hh:mm
		Date d = new Date(cTime);
		String time = "";

		try {
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			ft.setTimeZone(TimeZone.getTimeZone("GMT+0900"));
			//ft.setTimeZone(TimeZone.getTimeZone("KOREA"));
			time = ft.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return time;
	}

	public static String getDateShort(long cTime) {
		Date d = new Date(cTime);
		String time = "";

		try {
			SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
			ft.setTimeZone(TimeZone.getTimeZone("GMT+0900"));
			//ft.setTimeZone(TimeZone.getTimeZone("KOREA"));
			time = ft.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return time;
	}

	public static String getTimeShort(long cTime) {
		Date d = new Date(cTime);
		String time = "";

		try {
			SimpleDateFormat ft = new SimpleDateFormat("HHmm");
			ft.setTimeZone(TimeZone.getTimeZone("GMT+0900"));
			//ft.setTimeZone(TimeZone.getTimeZone("KOREA"));
			time = ft.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return time;
	}


	public static boolean isYesterday(long cTime) {
		Date d1 = new Date(cTime);
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		ft.setTimeZone(TimeZone.getTimeZone("GMT+0900"));
		String timeYesterday = ft.format(d1);

		Date d2 = new Date(getCurTime());
		SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd");
		ft2.setTimeZone(TimeZone.getTimeZone("GMT+0900"));
		String timeToday = ft.format(d2);

		if (timeToday.compareTo(timeYesterday) > 0)
			return true;
		else
			return false;
	    /*
	    String timeYesterday= getDateShort(cTime) ;
	    String timeToday= getDateShort(getCurTime());
	    
	    if (timeToday.compareTo(timeYesterday) > 0)
	    	return true ;
	    else
	    	return false ;
        */
	}

	public static String getDeviceNumber() {
		// get USIM serial number
		TelephonyManager telemanager = (TelephonyManager) Const.getService().getSystemService(Context.TELEPHONY_SERVICE);
		String getSimSerialNumber = telemanager.getSimSerialNumber();
		return getSimSerialNumber;
	}

	public static String getDeviceFullPhoneNumber() {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager)
				Const.getService().getSystemService(Context.TELEPHONY_SERVICE);
		String str = mTelephonyMgr.getLine1Number();
		if ((str == null) || (str.equals(""))) {
			return "";
		}
		Const.MyLog("getDevicePhoneNumber phoneNum=" + str);

		if (Util.getCountryCode().equals("JP"))
			str = str.replaceFirst("[+]81", "0");
		else if (Util.getCountryCode().equals("KR"))
			str = str.replaceFirst("[+]82", "0");
		return str;
	}

	public static String getDeviceLocalPhoneNumber() {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager)
				Const.getService().getSystemService(Context.TELEPHONY_SERVICE);
		String str = mTelephonyMgr.getLine1Number();
		if ((str == null) || (str.equals(""))) {
			return "";
		}
		Const.MyLog("getDevicePhoneNumber phoneNum=" + str);

		return str;
	}

	public static String getModelName() {
		return (Build.MODEL);
	}

	public static String getMaker() {
		if (Build.MODEL.endsWith("S"))
			return ("SK");
		else if (Build.MODEL.endsWith("L"))
			return ("LG");
		else if (Build.MODEL.endsWith("K"))
			return ("KT");
		else
			return ("Unknown");
	}

	public static String getAgent() {
		/*
        String str = Build.MODEL ;
        int nLen = str.length();
        String sEnd = str.substring(nLen-1, nLen) ;
        if (sEnd.equals("K"))
        	return "KT" ;
        else if (sEnd.equals("L"))
        	return "LG" ;	
        else //if (sEnd.equals("S"))
        	return "SK" ;
        	*/
		if (Const.getService() == null)
			return "";

		String agent;
		TelephonyManager tm = (TelephonyManager) Const.getService().getSystemService(Context.TELEPHONY_SERVICE);

		int operator = Integer.parseInt(tm.getNetworkOperator());

		switch (operator) {
			case 45002:
			case 45004:
			case 45008:
				agent = "KT";
				break;
			case 45003:
			case 45005:
			case 45011:
				agent = "SK";
				break;
			case 45006:
				agent = "LG";
				break;
			default:
				agent = "Unknown";
				break;
		}
		return agent;
	}

	public static String getAppVersion() {
		if (Const.getService() == null)
			return "0.0";

		PackageManager pm = Const.getService().getPackageManager();
		String app_vername = "0.0";
		try {
			PackageInfo pi = pm.getPackageInfo(Const.getService().getPackageName(), 0);
			app_vername = pi.versionName;
//			APP_VER = Integer.toString(pi.versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return app_vername;
	}

	public static int getAppVersionCode() {
		PackageManager pm = Const.getService().getPackageManager();
		int app_version_code = 0;
		try {
			PackageInfo pi = pm.getPackageInfo(Const.getService().getPackageName(), 0);
			app_version_code = pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return app_version_code;
	}
	/*
	public static int findRecentVersion()
	{
		// 0 = no version
		// 1 = version exist
		// 2 = must upgrade version exist
      try
	  {
		String strVersion = JSONParser.reqTextLine(Const.VERSION_URL);
		Const.versionHelps = strVersion.split("[\r\n]+"); //strVersion.split("\\s+");
		if (Const.versionHelps.length >= 1)
		{
		   String[] strs = Const.versionHelps[0].split("\\s+");
		   String curVersion = getAppVersion();
		   Const.recentVersion = strs[0];
		   if (Const.recentVersion.compareTo(curVersion) > 0)
		   {
			   if ((strs.length >= 2) && 
			       ((strs[1].equals("must")) || (strs[1].equals("MUST"))))
				   return 2 ;
			   else 
				  return 1 ;
		   }
		   else
			  return 0 ;
		}
		return 0 ;
	  }
	  catch (Exception e)
	  {
			return 0 ;
	  }
	}
	*/


	public static String getOSVersion() {
		return (android.os.Build.VERSION.RELEASE);
	}

	public static String getReadablePhoneNumber(String orgPhoneNumber) {
		// attach '-'
		int mlen = 4;

		if (orgPhoneNumber.length() < 10)
			return orgPhoneNumber;

		if (orgPhoneNumber.length() == 11)
			mlen = 4;
		else if (orgPhoneNumber.length() == 10)
			mlen = 3;

		return (String.format("%s-%s-%s", orgPhoneNumber.substring(0, 3),
				orgPhoneNumber.substring(3, 3 + mlen),
				orgPhoneNumber.substring(3 + mlen, 7 + mlen)));
	}

	public static String convUTC2LocalTimeSpan(String utcTimeStr) {
		String localTimeStr = "";

		if (null != utcTimeStr && !"".equals(utcTimeStr)) {
			try {
				java.util.Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(utcTimeStr);

				TimeZone tZone = TimeZone.getDefault();
				int offset = tZone.getOffset(date.getTime());

				localTimeStr = DateUtils.getRelativeTimeSpanString(
						date.getTime() + offset, System.currentTimeMillis(), 0,
						DateUtils.FORMAT_ABBREV_RELATIVE).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return localTimeStr;
	}

	public static int downloadAppMarket() {
		try {
			String sUrl = "market://details?id=" + Const.service1.getPackageName();
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sUrl));
			Const.service1.getBaseContext().startActivity(browserIntent);
		} catch (Exception e) {
			return 0;
		}
		return 1;
	}


	public static int getVersionOS() {
		return android.os.Build.VERSION.SDK_INT;
	}

	public static int getMessageLength(String s) {
		int count = 0;

		for (int i = 0; i < s.length(); i++) {
			Character c = s.charAt(i);
			if (c.charValue() <= 127) {
				count++;
				if (getAgent().equals("KT"))
					count++;
			} else
				count += 2;
		}
		return count;
	}

	public static boolean isLMS(int nCnt) {
		if (getAgent().equals("LG")) {
			if (nCnt > 90)
				return true;
		} else {
			if (nCnt > 140)
				return true;
		}
		return false;
	}

	public static boolean isKitkat() {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			return true;
		else
			return false;
	}

	public static void sleep(int n) {
		// sleep mSec
		SystemClock.sleep(n);
	}


	private static boolean isValidPhoneNumberKorea(String phone) {
		//if (1==1) return true ;
		if (!Util.getCountryCode().equals("KR"))
			return true;
		String start = "01";
		return (phone.startsWith(start));
	}


	public static String getCountryCode() {
		return (Locale.getDefault().getCountry());    // 占쏙옙占쏙옙占쌘듸옙 ISO 3166-1 Alpha-2 code. ex) KR, US, CH, JP, etc.
	}

	public static String getCountryName() {
		return (Locale.getDefault().getDisplayCountry());
	}

	public static String getLanguageCode() {
		return (Locale.getDefault().getLanguage());    // ko, en, etc.
	}

	public static boolean isKoreanLanguage() {
		return (getLanguageCode().equals("ko"));
	}

	public static boolean isKorea() {
		return (Util.getCountryCode().equals("KR"));
	}

	public static boolean isWifiConnected() {
		ConnectivityManager conMan = (ConnectivityManager) Const.getService().getSystemService(Context.CONNECTIVITY_SERVICE);

		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
			//wifi
			return true;
		else
			return false;
	}

	public static boolean isMobileConnected() {
		ConnectivityManager conMan = (ConnectivityManager) Const.getService().getSystemService(Context.CONNECTIVITY_SERVICE);
		//mobile
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

		if (mobile == NetworkInfo.State.CONNECTED)
			//mobile
			return true;
		else
			return false;
	}

	/*
	public static void connectWifi()
	{
		WifiManager wifiManager = (WifiManager) Const.mainActivity.getSystemService(Context.WIFI_SERVICE); 
		wifiManager.setWifiEnabled(true);
	}

	public static void disconnectWifi() 
	{
		WifiManager wifiManager = (WifiManager) Const.mainActivity.getSystemService(Context.WIFI_SERVICE); 
		wifiManager.setWifiEnabled(false);
	}
    */

	/*
	public static boolean WifiState = false ;
	
	public static String getWifiName()
	{
		if (!isWifiConnected())
			return "" ;
		else
		{
			WifiManager wifiMgr = (WifiManager) Const.mainActivity.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
			return(wifiInfo.getSSID());
		}
	}
	*/

	static public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = {MediaStore.Images.Media.DATA};
			cursor = context.getContentResolver().query(contentUri, proj, null, null, null);

			// ERROR ORIGINATING THE LINE BELOW
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public static boolean existFile(String path) {
		File file = new File(path);
		if ((file.exists()) && (file.length() > 0))
			return true;
		else
			return false;
	}


	public static boolean isAlphaNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			int c = (int) str.charAt(i);

			if (((c >= '0') && (c <= '9')) ||
					((c >= 'A') && (c <= 'Z')) ||
					((c >= 'a') && (c <= 'z'))) {
			} else
				return false;
		}
		return true;
	}

	public static boolean isHangulAlphaNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			int c = (int) str.charAt(i);

			if (c > 256) {
			} else if (((c >= '0') && (c <= '9')) ||
					((c >= 'A') && (c <= 'Z')) ||
					((c >= 'a') && (c <= 'z'))) {
			} else
				return false;
		}
		return true;
	}

	public static boolean deleteFile(String path) {
		File fdelete = new File(path);
		if (fdelete.exists()) {
			if (fdelete.delete()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static boolean deleteDirectoryFiles(String path, boolean bAll) {
		File directory = new File(path);
		return (deleteDirectory(directory, bAll));
	}

	public static boolean deleteDirectory(File directory, boolean bAll) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i], bAll);
					} else {
						long lastM = files[i].lastModified();
						long cTime = getCurTime();
						if ((bAll) || (!getDateShort(lastM).equals(getDateShort(cTime)))) {
							files[i].delete();
						}
					}
				}
			}
		}
		if (bAll)
			return (directory.delete());
		else
			return true;
	}

	public static String[] getOneOrTwoPhoneNumbers(String str) {
		String numbers[] = new String[2];
		numbers[0] = "";
		numbers[1] = "";

		int offset1 = str.indexOf("01");

		if (offset1 == -1 || str.length() < offset1 + 11)
			return numbers;

		String Number = (str.substring(offset1, offset1 + 11));

		Pattern pattern = Pattern.compile("\\d");
		Matcher matcher = pattern.matcher(Number);
		while (matcher.find()) {
			numbers[0] += matcher.group(0);
		}

		if (numbers[0].length() < 10)
			return numbers;

		int offset2 = str.indexOf("010", offset1 + 11);

		if (offset2 == -1
				|| str.length() < offset2 + 11)
			return numbers;

		Number = (str.substring(offset2, offset2 + 11));

		matcher = pattern.matcher(Number);
		while (matcher.find()) {
			numbers[1] += matcher.group(0);
		}

		return numbers;
	}

	public static int getNumber(String str) {
		Pattern pattern = Pattern.compile("\\d");
		Matcher matcher = pattern.matcher(str);

		String sNum = "";

		int offset = 0;
		while (matcher.find()) {
			sNum += matcher.group(0);
		}

		if (sNum.equals(""))
			return -1;
		return Integer.parseInt(sNum);
	}

	public static String getNumStr(int n) {
		String strn;
		if (n >= 100000)
			strn = String.format("%dK", n / 1000);
		else
			strn = String.format("%d", n);
		return strn;
	}

	private static int getValue62(char ch) {
		if (ch >= '0' && ch <= '9')
			return ch - '0';
		else if (ch >= 'A' && ch <= 'Z')
			return ch - 'A' + 10;
		else if (ch >= 'a' && ch <= 'z')
			return ch - 'a' + 36;
		else
			return 0;
	}

	public static int getIntFrom62(String str62) {
		int sum = 0;

		for (int i = 0; i < str62.length(); i++) {
			char a = str62.charAt(i);
			sum = sum * 62 + getValue62(a);
		}
		return sum;
	}

	public static char getCh62FromInt(int val) {
		if (val >= 0 && val <= 9)
			return (char) (val + '0');
		else if (val >= 10 && val <= 35)
			return (char) (val - 10 + 'A');
		else if (val >= 36 && val <= 61)
			return (char) (val - 36 + 'a');
		else
			return '0';
	}

	public static String getStr62FromInt(int value) {
		String str = "";
		int quot = value;
		int remain = 0;
		do {
			remain = quot % 62;
			quot = quot / 62;
			str = getCh62FromInt(remain) + str;
		}
		while (quot > 0);
		return str;
	}

	public static boolean isPureNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			int c = (int) str.charAt(i);

			if ((c >= '0') && (c <= '9')) {
			} else
				return false;
		}
		return true;
	}

	public static String removeSpaces(String str) {
		String str1 = "";
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch != ' ')
				str1 += ch;
		}
		return str1;
	}

	public static String getSDFolder(boolean bForWrite) {
		FileFunc f = new FileFunc();

		// start from '/' dir
		int depth = 0;
		String sDir = "/";
		boolean bFind = false;
		ArrayList<String> dirFiles = f.getDirList(sDir);
		for (String sDirFile : dirFiles) {
			sDir = "/" + sDirFile;
			if (f.isDirectory(sDir)) {
				ArrayList<String> dirFiles1 = f.getDirList("/" + sDirFile);
				for (String sDirFile1 : dirFiles1) {
					String sFolder = "/" + sDirFile + "/" + sDirFile1;
					String sUp = sDirFile1.toUpperCase();
					if (sUp.contains("USB") && ((bForWrite) ? f.isWritableDirectory(sFolder) : f.isDirectory(sFolder))) {
						String sFile = sFolder + "/stealth_sd_test.txt";
						if (bForWrite && f.openWrite(sFile)) {
							if (f.writeLine("stealth")) {
								f.close();
								if (f.openRead(sFile)) {
									String str = f.readLine();
									f.close();
									if (str.equals("stealth")) {
										f.deleteFile(sFile);
										return sFolder;
									}
								}
							}
							f.deleteFile(sFile);
						}
						return sFolder;
					}
				}
			}
		}
		return null;
	}


	public static Date addDate(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	public static boolean isPassedDate(String sDt) {
		String sNow = getDateString("yyyyMMdd");
		if (sDt.compareTo(sNow) < 0)
			return true;
		return false;
	}


	public static boolean isHalfPassedDate(String sDt, int level) {
		String sNow = getDateString("yyyyMMdd");
		if (level == 4) {
			if (sDt.compareTo(sNow) <= 0)
				return true;
			else
				return false;
		} else {
			int nDays;
			if (level == 3)
				nDays = 4;
			else
				nDays = 16;

			Date date = new Date();

			date = Util.addDate(date, nDays);

			String sDate = Util.getFormatDate(date, "yyyyMMdd");

			if (sDt.compareTo(sDate) <= 0)
				return true;
			else
				return false;
		}
	}

	public static String getMix3String(String s1, String s2, String s3, int len) {
		// if s3 == "" mix 2 String
		int l1 = s1.length();
		int l2 = s2.length();
		int l3 = s3.length();

		int i1 = 0;
		int i2 = 0;
		int i3 = 0;

		String sb = "";
		int ib = 0;

		while (ib < len) {
			if (i1 >= l1) i1 = 0;
			sb += s1.substring(i1, i1 + 1);
			ib++;
			i1++;

			if (i2 >= l2) i2 = 0;
			if (ib < len) {
				sb += s2.substring(i2, i2 + 1);
				ib++;
				i2++;
			}

			if (l3 > 0) {
				if (i3 >= l3) i3 = 0;
				if (ib < len) {
					sb += s3.substring(i3, i3 + 1);
					ib++;
					i3++;
				}
			}
		}
		return sb;
	}

	public static int getRandomInt(int Max) {
		double d1 = Math.random();
		int val = (int) (d1 * Max) ; //0 .. Max-1
		return val;
	}

	public static String[] divideString(String src, String divide) {
		// divide src by 1st occurrence of divide
		String strs[] = new String[2];

		int len = divide.length();
		int pt = src.indexOf(divide);
		if (pt == -1) {
			strs[0] = src;
			strs[1] = "";
		} else {
			strs[0] = src.substring(0, pt);
			strs[1] = src.substring(pt + len);
		}
		return strs;
	}

	public static boolean isPassed24Hours(String yyyyMMddHH) {
		int hours = getDiffHoursFromNow(yyyyMMddHH);
		if (hours >= 24)
			return true;
		else
			return false;
	}

	public static int getDiffHoursFromNow(String yyyyMMddHHmm) {
		long lTime = 0L;
		long cTime = getCurTime();

		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
			format.setTimeZone(TimeZone.getTimeZone("GMT+0000"));

			java.util.Date date = format.parse(yyyyMMddHHmm);
			lTime = date.getTime();
		} catch (Exception e) {
		}
		;

		int hours = (int) ((cTime - lTime) / (1000 * 60 * 60));
		return hours;
	}

	public static int getDiffMinutesFromNow(String yyyyMMddHHmm) {
		long lTime = 0L;
		long cTime = getCurTime();

		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHm");
			format.setTimeZone(TimeZone.getTimeZone("GMT+0000"));

			java.util.Date date = format.parse(yyyyMMddHHmm);
			lTime = date.getTime();
		} catch (Exception e) {
		}
		;

		int minutes = (int) ((cTime - lTime) / (1000 * 60));
		return minutes;
	}

	public static void setEditTextEditable(EditText et, boolean bEditable) {
		et.setFocusable(bEditable);
		et.setFocusableInTouchMode(bEditable);
		et.setClickable(bEditable);
	}

	private static final float DEFAULT_HDIP_DENSITY_SCALE = 1.5f;
	/**
	 * 픽셀단위를 현재 디스플레이 화면에 비례한 크기로 반환합니다.
	 *
	 * @param pixel 픽셀
	 * @return 변환된 값 (DP)
	 */
	public static int getDPFromPixel(Context context, int pixel)
	{
		float scale = context.getResources().getDisplayMetrics().density;

		return (int)(pixel / DEFAULT_HDIP_DENSITY_SCALE * scale);
	}

	/**
	 * 현재 디스플레이 화면에 비례한 DP단위를 픽셀 크기로 반환합니다.
	 *
	 * @param  DP 픽셀
	 * @return 변환된 값 (pixel)
	 */
	public static int getPixelFromDP(Context context, int DP)
	{

		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, r.getDisplayMetrics());

		return (int)px ;
		/*
		float scale = context.getResources().getDisplayMetrics().density;

		return (int)(DP / scale * DEFAULT_HDIP_DENSITY_SCALE);
		*/
	}

    public static String getDownloadFullPath(String subPath, String localFileName)
    {
        return (Environment.getExternalStorageDirectory() + "/" + subPath + "/" + localFileName);
    }

    public static String getDownloadPurePath(String subPath)
    {
        return (Environment.getExternalStorageDirectory() + "/" + subPath);
    }

	public static Bitmap downloadUrlImage(String requestUrl)
	{
		Bitmap bitmap ;
        FileOutputStream fos ;

        try {
            URL url = new URL(requestUrl);
            URLConnection conn = url.openConnection();
            InputStream is = new URL( requestUrl ).openStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception ex) {
            return null ;
        }
        return bitmap ;
    }

    public static boolean saveBitmapToPngFile(Bitmap bitmap, String subPath, String filename)
    {
        File direct = new File(getDownloadPurePath(subPath));
        if (!direct.exists()) {
            direct.mkdir();
        } // end of if

        File file = new File(direct.getAbsoluteFile(), filename);
        if (file.exists())
            return true;

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Const.MyLog("save Bitmap error="+e.toString());
            return false ;
        }
        return true ;
    }

	public static Bitmap getHushBitmap(String path1)
	{
		File file = new File(path1);
		long length = file.length();
		Const.MyLog("filesize=", "filesize="+length);
		if (length == 0)
			return null ;

		try
		{
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path1, options);
			int srcWidth = options.outWidth;
			int srcHeight = options.outHeight;
			int scale = 1;

			options.inJustDecodeBounds = false;
			options.inSampleSize = scale;
			options.inScaled = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			return(BitmapFactory.decodeFile(path1, options)) ;
		}
		catch (Exception e)
		{
			return null ;
		}

	}

	public static boolean checkEmail(String email)
	{
		Pattern pattern = Patterns.EMAIL_ADDRESS;
		return pattern.matcher(email).matches();
	}

    //ysk20161028
    public static String convHexToBinStr(String src)
    {
        String s = "" ;
        for (int i=0; i < src.length(); i++)
        {
            s += getBinStr(src.charAt(i)) ;
        }
        return s ;
    }

	public static String convBinToHexStr(String src)
	{
		String dest = "" ;
		int hex_val = 0 ;
		int hex_cnt = 0 ;
		for (int i=0; i < src.length(); i++)
		{
			hex_val = hex_val*2 ;
			if (src.charAt(i) == '1')
			{
				hex_val += 1 ;
			}
			hex_cnt++ ;
			if (hex_cnt >= 4)
			{
				dest += getHexStr(hex_val) ;
				hex_cnt = 0 ;
				hex_val = 0 ;
			}
		}
		// remained
		if (hex_val > 0)
		{
            while (hex_cnt < 4)
            {
                hex_val = hex_val * 2 ;
                hex_cnt++;
            }
			dest += getHexStr(hex_val) ;
		}

		return dest ;
	}

	public static String getHexStr(int val)
	{
		// val = 0 .. 15
		char a  ;

		if (val < 10) {
			a = (char) (0x30 + val);
			return a + "";
		}
		else
		{
			a = (char)(val-10+'A') ;
			return a + "" ;
		}
	}

	public static String getBinStr(char Hex)
	{
		int val ;
		String s = "" ;
		if (Hex <= '9')
		    val = Hex-'0' ;
		else
			val = Hex-'A' + 10 ;

		for (int i=0; i < 4; i++)
		{
			int r = val % 2 ;
			val = val / 2 ;
			s = ((r==1)?"1":"0") + s ;
		}
		return s ;
	}

	public static String changeDBString(String src)
	{
		return (src.replaceAll("'", "''"));
	}


} // end of class

    
