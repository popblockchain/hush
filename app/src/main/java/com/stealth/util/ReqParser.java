package com.stealth.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import com.stealth.crypto.AES256;
import com.stealth.crypto.RSA;
import com.stealth.hushkbd.Const2;
import com.stealth.jncryptor.RNC256;
import com.stealth.service.Const;
import com.stealth.service.GroupInfo;
import com.stealth.service.MemberInfo;

public class ReqParser 
{
	// send request , receive answer and parse
	public static String request(String str1)
	{
		Const.MyLog("request="+str1);
		SocketClient client = new SocketClient("14.63.227.84", 39776);
		
		if (!client.openSocket())
			return "" ;

		Date dateCrypt = new Date() ;

		//String str1 = AES256.encrypt(Util.getDateString("MMdd")+"keyForProtocol", str) ;
		String str = AES256.encrypt(
				Util.getMix3String(Const.COMMUNI_KEY,
						Util.getStdFormatDate(dateCrypt, "ddMMyyyy"), "", 32),
				str1
		);
		boolean bOk = client.sendStrToServer(str) ;
		
		if (bOk)
		{
			String str2 = client.readServerMessage() ;
			String str3 = AES256.decrypt(
					Util.getMix3String(Const.COMMUNI_KEY,
							Util.getStdFormatDate(dateCrypt, "ddMMyyyy"), "", 32),
					str2
			);
//			String str3 = AES256.decrypt(Util.getDateString("MMdd")+"keyForProtocol", str2) ;
			client.closeSocket(); 
			Const.MyLog("answer="+str3);
			return str3 ;
		}
		
		return "" ;
	}
	

	public static int reqRegUser(String pass)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RRUSR"); 

			sb.append(";UPHO:"+Const.targetPhone); //getGlobalPhoneNumber()) ;
			sb.append(";PASS:"+pass);
			sb.append(";APPV:"+Util.getAppVersion());
			sb.append(";PKND:1");
			sb.append(";AKND:"+ Const2.appKind);
			sb.append(";COCD:"+ Util.getCountryCode());
			sb.append(";LNCD:"+ Util.getLanguageCode());

			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length <= 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("ARUSR"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;

                if (ret == 0)
                    Const.savePassword(pass);
	        	
	        	for (int i=1; i < strs.length; i++)
	        	{
	        	    ss = strs[i].split(":");
	        	    if (ss.length >= 2 && ss[0].equals("USID"))
	        	    {
	        		   Const.saveMySid(Integer.parseInt(ss[1]));
	        	    }
					else if (ss.length >= 2 && ss[0].equals("GCNT"))
					{
						// group count
					}
                    else if (ss.length >= 2 && ss[0].equals("MLVL"))
                    {
                        Const.saveMyOrgLevel(Integer.parseInt(ss[1]));
                    }
                    else if (ss.length >= 2 && ss[0].equals("HPRM"))
                    {
                        // HushPower
                        Const.saveHP(Integer.parseInt(ss[1]));
                    }
	        	}
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqRegRecommender(int recomSid)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;

		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RRSID");
			sb.append(";");
			sb.append("USID:"+Const.getMySid());
			sb.append(";");
			sb.append("RSID:"+recomSid);

			String ans = request(sb.toString());

			// parse ans
			String strs[] = ans.split(";");
			if (strs.length < 1)
				return 99 ;

			String ss[] = strs[0].split(":") ;
			if (ss.length >= 2 && ss[0].equals("ARSID"))
			{
				ret = Integer.parseInt(ss[1]) ;

                for (int i=1; i < strs.length; i++) {
                    ss = strs[i].split(":");
                    if (ss.length >= 2 && ss[0].equals("RSID")) {
                        Const.saveRecomSid(Integer.parseInt(ss[1]));
                    }
                }

				if (ret == 0)
					Const.saveRecomSid(recomSid);

				return ret ;
			}
		}
		catch (Exception e)
		{
		}
		return ret ;
	}

	public static int reqRegEtc(String userName, int bYear, int sexy)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;

		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RRETC");

			sb.append(";USID:"+Const.getMySid()) ;
			sb.append(";UNAM:"+URLEncoder.encode(userName, "UTF-8"));
            sb.append(";SEXY:"+ sexy);
			sb.append(";BYER:"+ bYear);

			String ans = request(sb.toString());

			// parse ans
			String strs[] = ans.split(";");
			if (strs.length <= 0)
				return 99 ;

			String ss[] = strs[0].split(":") ;
			if (ss.length >= 2 && ss[0].equals("ARETC"))
			{
				ret = Integer.parseInt(ss[1]) ;

				if (ret == 0)
				{
					Const.saveMyName(userName);
					Const.saveBirthYear(bYear);
					Const.saveSexy(sexy);
				}

				return ret ;
			}
		}
		catch (Exception e)
		{
		}
		return ret ;
	}

	/*
	public static int reqRegUserName(String userName)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RRNAM"); 
			sb.append(";");
			sb.append("USID:"+Const.getMySid()); 
			sb.append(";");
			sb.append("UNAM:"+URLEncoder.encode(userName, "UTF-8")); 
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length < 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("ARNAM"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	
	        	if (ret == 0)
	        		Const.saveMyName(userName);;
	        	
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}
	*/

	public static int reqPhoneCheck()
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;

		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RPCHK");
			String str = Const.targetPhone ;
			if (str.equals(""))
				return 99 ;

			sb.append(";UPHO:"+str);

			String ans = request(sb.toString());

			// parse ans
			String strs[] = ans.split(";");

			String ss[] = strs[0].split(":") ;
			if (ss.length >= 2 && ss[0].equals("APCHK"))
			{
				ret = Integer.parseInt(ss[1]) ;
				return ret ;
			}
		}
		catch (Exception e)
		{
		}
		return ret ;
	}


    public static int reqBlock(int uSid, int fSid, int OnOff)
    {
        // OnOff = 1 set Block
        // OnOff = 0 reset Block
        // return 0 if success
        // return > 0 if error
        int ret = 99 ;

        try
        {
            StringBuilder sb = new StringBuilder() ;
            sb.append("RNOTF");
            sb.append(";USID:"+uSid);
            sb.append(";FSID:"+fSid);
            sb.append(";BLOK:"+OnOff);

            String ans = request(sb.toString());

            // parse ans
            String strs[] = ans.split(";");
            if (strs.length < 1)
                return 99 ;

            String ss[] = strs[0].split(":") ;
            if (ss.length >= 2 && ss[0].equals("ABLOK"))
            {
                ret = Integer.parseInt(ss[1]) ;
                if (ret == 0)
                {
                }
                return ret ;
            }
        }
        catch (Exception e)
        {
        }
        return ret ;
    }

    public static int reqLevels(String sids)
    {
        // sids = sid1, sid2, ...
        // return 0 if success
        // return > 0 if error
        int ret = 99 ;

        try
        {
            StringBuilder sb = new StringBuilder() ;
            sb.append("RLVLS");
            sb.append(";SIDS:"+sids);

            String ans = request(sb.toString());

            // parse ans
            String strs[] = ans.split(";");
            if (strs.length < 1)
                return 99 ;

            String ss[] = strs[0].split(":") ;
            String levels = "" ;
            String parts = "" ;
			String blks = "" ;

            if (ss.length >= 2 && ss[0].equals("ALVLS"))
            {
                ret = Integer.parseInt(ss[1]) ;
                if (ret != 0)
                    return ret ;

                for (int i=1; i < strs.length; i++)
                {
                    ss = strs[i].split(":");
                    if (ss.length >= 2 && ss[0].equals("LVLS"))
                        levels = ss[1] ;
                    else if (ss.length >= 2 && ss[0].equals("PRTS"))
                        parts = ss[1] ;
					else if (ss.length >= 2 && ss[0].equals("BLKS"))
						blks = ss[1] ;
                }

                Const.saveLevelsParts(sids, levels, parts);

                return ret ;
            }
        }
        catch (Exception e)
        {
        }
        return ret ;
    }

    public static int reqNames(String sids)
    {
        // sids = sid1, sid2, ...
        // return 0 if success
        // return > 0 if error
        int ret = 99 ;

        try
        {
            StringBuilder sb = new StringBuilder() ;
            sb.append("RNAMS");
            sb.append(";SIDS:"+sids);

            String ans = request(sb.toString());

            // parse ans
            String strs[] = ans.split(";");
            if (strs.length < 1)
                return 99 ;

            String ss[] = strs[0].split(":") ;
            if (ss.length >= 2 && ss[0].equals("ANAMS"))
            {
                ret = Integer.parseInt(ss[1]) ;
                if (ret != 0)
                    return ret ;

                for (int i=1; i < strs.length; i++)
                {
                    ss = strs[i].split(":");
                    if (ss.length >= 2 && ss[0].equals("NAMS"))
                        Const.saveUserNames(sids, ss[1]);
                }
                return ret ;
            }
        }
        catch (Exception e)
        {
        }
        return ret ;
    }

    /*
	public static void checkReqUserKey(String sUK_DT12)
	{
		if (Const.dbDao.isUserKeyChanged(sUK_DT12))
		{
			reqUserKey(false) ;
		}
	}
	*/

	public static int reqGroupVersList1(int groupSid)
	{
		// if groupSid == 0 req all group vers
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RGLST");
			sb.append(";");
			sb.append("USID:"+Const.getMySid()); 
			
			if (groupSid > 0)
			{
				sb.append(";");
				sb.append("GSID:"+groupSid); 
			}
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length <= 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("AGLST"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	
	        	for (int i=1; i < strs.length; i++)
	        	{
	        	    ss = strs[i].split(":");
	        	    if (ss.length >= 2 && ss[0].equals("GCNT"))
	        		    Const.saveGroupCnt(Integer.parseInt(ss[1]));
	        	    else if (ss.length >= 2 && ss[0].equals("GINF"))
	        		    Const.parseGroupVersList(ss[1]);
	        	}
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}
    

	public static int reqGroupInfo1(int groupSid)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RGINF1");
			sb.append(";");
			sb.append("GSID:"+groupSid); 
			sb.append(";");
			sb.append("USID:"+Const.getMySid()); 
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length <= 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("AGINF1"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	
	        	if (ret != 0)
	        	{
	        		return ret ;
	        	}

				boolean bGroupCreated = false ;
	        	GroupInfo info = Const.findGroup(groupSid) ;
	        	if (info == null)
				{
					info = new GroupInfo();
					info.GroupSid = groupSid ;
					bGroupCreated = true ;
				}

	        	info.bNeedDelete = false ;
	        	info.bNeedUpdate = false ;
	        	
	        	for (int i=1; i < strs.length; i++)
	        	{
	        	    ss = strs[i].split(":");
	        	    if (ss.length >= 2 && ss[0].equals("GRID"))
	        	    	info.GroupId = URLDecoder.decode(ss[1], "UTF-8");
//	        	    else if (ss.length >= 2 && ss[0].equals("GLVL"))
//	        		    info.Level = Integer.parseInt(ss[1]);
	        	    else if (ss.length >= 2 && ss[0].equals("GMGR"))
	        	    	info.MgrName = URLDecoder.decode(ss[1], "UTF-8");
	        	    else if (ss.length >= 2 && ss[0].equals("GUYN"))
	        	    	info.UseYN = Integer.parseInt(ss[1]); 
	        	    else if (ss.length >= 2 && ss[0].equals("GPYN"))
	        	    	info.PayedYN = Integer.parseInt(ss[1]); 
	        	    else if (ss.length >= 2 && ss[0].equals("MCNT"))
	        	    	info.MemberCnt = Integer.parseInt(ss[1]); 
//	        	    else if (ss.length >= 2 && ss[0].equals("GHSH"))
//	        	    	info.GroupPwHash = ss[1]; 
	        	    else if (ss.length >= 2 && ss[0].equals("MVYN"))
	        	    	info.MemberViewYN = Integer.parseInt(ss[1]); 
	        	    else if (ss.length >= 2 && ss[0].equals("MGYN"))
	        	    	info.MgrYN = Integer.parseInt(ss[1]); 
	        	    else if (ss.length >= 2 && ss[0].equals("DCYN"))
	        	    	info.DecryptCopyYN = Integer.parseInt(ss[1]); 
	        	    else if (ss.length >= 2 && ss[0].equals("MSTT"))
	        	    	info.MyState = Integer.parseInt(ss[1]); 
	        	    else if (ss.length >= 2 && ss[0].equals("SDYN"))
	        	    	info.SdcardMustYN = Integer.parseInt(ss[1]); 
//	        	    else if (ss.length >= 2 && ss[0].equals("GVDT"))
//	        	    	info.KeyValidDt12 = ss[1]; 
	        	    else if (ss.length >= 2 && ss[0].equals("MSID"))
	        	    	info.MgrSid = Integer.parseInt(ss[1]);
	        	}
	        	
				if (bGroupCreated)
				{
					Const.groupArrs.add(info);
					Const.dbDao.insertGroup(info);
				}
				else
                    Const.dbDao.updateGroup(info);
//	        	Const.saveGroupSids(info.GroupSid);
	        	
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqMemberList(int groupSid, int mcnt, String sWord)
	{
		// return 0 if success
		// return > 0 if error
		// save to Const.memberList
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RMLST"); 
			sb.append(";");
			sb.append("GSID:"+groupSid); 
			sb.append(";");
			sb.append("MCNT:"+mcnt); 
			sb.append(";");
			sb.append("SWRD:"+URLEncoder.encode(sWord, "UTF-8"));
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length <= 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("AMLST"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	int rcnt ;

	        	Const.curGroupInfo.memberList = new ArrayList<MemberInfo>() ;
	        	
	        	for (int i=1; i < strs.length; i++)
	        	{
	        	    ss = strs[i].split(":");
	        	    if (ss.length >= 2 && ss[0].equals("MCNT"))
	        	    	rcnt = Integer.parseInt(ss[1]);
	        	    else if (ss.length >= 2 && ss[0].equals("MLST"))
	        	    {
	        	    	String sss[] = ss[1].split(",");
	        	    	for (int j=0; j < sss.length; j++)
	        	    	{
	        	    		MemberInfo info = new MemberInfo() ;
	        	    		String ssss[] = sss[j].split("\\|");
	        	    		info.Name = URLDecoder.decode(ssss[0], "UTF-8");
	        	    		info.Phone = Util.convLocalPhoneNumber(ssss[1]) ;
	        	    		info.MemberSid = Integer.parseInt(ssss[2]) ;
	        	    		info.UseYN = Integer.parseInt(ssss[3]);
	        	    		info.PayedYN = Integer.parseInt(ssss[4]);
	        	    		info.UserState = Integer.parseInt(ssss[5]);
	        	    		info.MgrYN = Integer.parseInt(ssss[6]);
							info.UserLevel = Integer.parseInt(ssss[7]);
							info.ProYN = Integer.parseInt(ssss[8]);
	        	    		Const.curGroupInfo.memberList.add(info);
	        	    	}
	        	    }
	        	}
	        	
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqJoinXList(int groupSid, int xcnt, String sPhone)
	{
		// return 0 if success
		// return > 0 if error
		// save to group joinXList
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RXLST"); 
			sb.append(";");
			sb.append("GSID:"+groupSid); 
			sb.append(";");
			sb.append("XCNT:"+xcnt); 
			sb.append(";");
			sb.append("SPHN:"+sPhone); 
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length <= 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("AXLST"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	int rcnt ;

	        	Const.curGroupInfo.joinXList = new ArrayList<String>() ;
	        	
	        	for (int i=1; i < strs.length; i++)
	        	{
	        	    ss = strs[i].split(":");
	        	    if (ss.length >= 2 && ss[0].equals("XCNT"))
	        	    	rcnt = Integer.parseInt(ss[1]);
	        	    else if (ss.length >= 2 && ss[0].equals("XLST"))
	        	    {
	        	    	String sss[] = ss[1].split(",");
	        	    	for (int j=0; j < sss.length; j++)
	        	    	{
	        	    		Const.curGroupInfo.joinXList.add(sss[j]);
	        	    	}
	        	    }
	        	}
	        	
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqRegGroup1(String gid, String mgrName, int memberViewYN, int decryptCopyYN, int SdcardMustYN)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RRGRP1");
			sb.append(";");
			sb.append("GRID:"+URLEncoder.encode(gid, "UTF-8"));
			//sb.append(";");
			//sb.append("GLVL:"+level);
			sb.append(";");
			sb.append("USID:"+Const.getMySid()); 
			sb.append(";");
			sb.append("GMGR:"+URLEncoder.encode(mgrName, "UTF-8"));
			sb.append(";");
			sb.append("MVYN:"+memberViewYN); 
			sb.append(";");
			sb.append("DCYN:"+decryptCopyYN); 
			sb.append(";");
			sb.append("SDYN:"+SdcardMustYN); 
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length <= 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("ARGRP1"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	
	        	for (int i=1; i < strs.length; i++)
	        	{
	        	    ss = strs[i].split(":");
	        	    if (ret == 0 && ss[0].equals("GSID"))
	        	    {
	        	    	int gSid = Integer.parseInt(ss[1]);
//	        		   Const.saveCurGroup(gSid, gid) ;
	        		   GroupInfo info = new GroupInfo() ;
	        		   info.GroupId = gid ;
	        		   info.GroupSid = gSid ;
	        		   info.bNeedDelete = false ;
	        		   info.bNeedUpdate = true ;
	        		   info.MgrName = mgrName ;
	        		   info.MemberViewYN = memberViewYN ;
	        		   info.MgrYN = 1 ;
	        		   info.SdcardMustYN = SdcardMustYN ;
	        		   
	        		   Const.groupArrs.add(info);
	        		   Const.dbDao.insertGroup(info);
	        		   Const.setCurGroupInfo(info); 
	        	    }
	        	}
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqModifyGroup1(int gSid, String gid, String mgrName, int memberViewYN, int decryptCopyYN, int SdcardMustYN)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RMGRP1");
			sb.append(";");
			sb.append("GSID:"+gSid); 
			sb.append(";");
			sb.append("GRID:"+URLEncoder.encode(gid, "UTF-8"));
			sb.append(";");
//			sb.append("GLVL:"+level);
//			sb.append(";");
			sb.append("USID:"+Const.getMySid()); 
			sb.append(";");
			sb.append("GMGR:"+URLEncoder.encode(mgrName, "UTF-8"));
			sb.append(";");
			sb.append("MVYN:"+memberViewYN); 
			sb.append(";");
			sb.append("DCYN:"+decryptCopyYN); 
			sb.append(";");
			sb.append("SDYN:"+SdcardMustYN); 
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length < 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("AMGRP1"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	if (ret == 0)
	        	{
	        		   GroupInfo info = Const.curGroupInfo ;
	        		   info.GroupId = gid ;
	        		   info.MgrName = mgrName ;
	        		   info.MemberViewYN = memberViewYN ;
	        		   info.DecryptCopyYN = decryptCopyYN ;
	        		   info.SdcardMustYN = SdcardMustYN ;
	        		   //info.Level = level ;
	        		   
	        		   Const.dbDao.updateGroup(info);
	        		
	        	}
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqSubMember(int gSid, String uNames, String uPhones)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RSGMB"); 
			sb.append(";");
			sb.append("GSID:"+gSid); 
			sb.append(";");
//			sb.append("CCOD:"+Util.getCountryZipCode());
//			sb.append(";");
			sb.append("UNAM:"+URLEncoder.encode(uNames, "UTF-8"));
			sb.append(";");
			sb.append("UPHO:"+uPhones);
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length < 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("ASGMB"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}
	
	public static int reqRegMember(String gSid, String uSid, String stat)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RRGMB"); 
			sb.append(";");
			sb.append("GSID:"+gSid); 
			sb.append(";"); 
			sb.append("USID:"+uSid); 
			sb.append(";"); 
			sb.append("MSTT:"+stat); 
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length < 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("ARGMB"))
	        {
	        	// 1= belong to Join refused list 
	        	ret = Integer.parseInt(ss[1]) ;
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqPowerManage(String tPhone, String sdyn, String prik, String pmng)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RPMNG"); 
			sb.append(";");
			sb.append("USID:"+Const.getMySid()); 
			sb.append(";");
//			sb.append("CCOD:"+Util.getCountryZipCode());
//			sb.append(";");
			sb.append("TPNO:"+tPhone); 
			sb.append(";"); 
			sb.append("SDYN:"+sdyn); 
			sb.append(";"); 
			sb.append("PRIK:"+prik); 
			sb.append(";"); 
			sb.append("PMNG:"+pmng); 
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length < 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("APMNG"))
	        {
	        	// 1= belong to Join refused list 
	        	ret = Integer.parseInt(ss[1]) ;
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqDeleteGroup(int gSid)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RDGRP"); 
			sb.append(";");
			sb.append("GSID:"+gSid); 
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length < 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("ADGRP"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	if (ret == 0)
	        	{
		        	Const.dbDao.deleteGroup(Const.curGroupInfo.GroupSid);
	            	for (GroupInfo info : Const.groupArrs)
	            	{
	            		if (info.GroupSid == Const.curGroupInfo.GroupSid)
	            		{
	            			Const.groupArrs.remove(info);
	            		}
	            	}
	        	}
	        		
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqDeleteMember(int gSid, int uSid)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RDGMB"); 
			sb.append(";");
			sb.append("GSID:"+gSid); 
			sb.append(";");
			sb.append("USID:"+uSid); 
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length < 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("ADGMB"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqRegXList(int gSid, ArrayList<String> phones)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RJOIX"); 
			sb.append(";");
			sb.append("GSID:"+gSid); 
			sb.append(";"); 
//			sb.append("CCOD:"+Util.getCountryZipCode());
//			sb.append(";");
			sb.append("UPNS:") ;
			for (int i=0; i < phones.size(); i++)
			{
				if (i==0) sb.append(phones.get(0)) ;
				else sb.append(","+phones.get(i)) ;
			}
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length <= 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("AJOIX"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqRegOList(int gSid, ArrayList<String> phones)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RJOIO"); 
			sb.append(";");
			sb.append("GSID:"+gSid); 
			sb.append(";"); 
			//sb.append("CCOD:"+Util.getCountryZipCode());
			//sb.append(";");
			sb.append("UPNS:") ;
			for (int i=0; i < phones.size(); i++)
			{
				if (i==0) sb.append(phones.get(0)) ;
				else sb.append(","+phones.get(i)) ;
			}
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length <= 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("AJOIO"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqUseOX(int gSid, int uSid, String sUseOk)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RSUSE"); 
			sb.append(";");
			sb.append("GSID:"+gSid); 
			sb.append(";"); 
			sb.append("USID:"+uSid); 
			sb.append(";"); 
			sb.append("GUYN:"+sUseOk); 
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length < 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("ASUSE"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;
	        	if (ret == 0)
	        	{
	        		if (sUseOk.equals("1"))
	        			Const.curGroupInfo.setUse(uSid, 1);
	        		else
	        			Const.curGroupInfo.setUse(uSid, 0);
	        	}
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

	public static int reqGroupKey1(int gSid, int keyLevel, int keySeq)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		
		try
		{
			String kSeq = "" ;
			String gKey = "" ;
			String sDt = "" ;

			RSA rsa = new RSA() ;
			rsa.makeKeyPair();
			
			String pkey = rsa.pub_key ;
					
			StringBuilder sb = new StringBuilder() ;
			sb.append("RGKEY1");
			sb.append(";");
			sb.append("GSID:"+gSid);
			sb.append(";");
			sb.append("KLVL:"+keyLevel);
			sb.append(";");
			sb.append("KSEQ:"+keySeq);
			sb.append(";");
			sb.append("PKEY:"+pkey); 
			
			String ans = request(sb.toString());
			
			// parse ans
			String strs[] = ans.split(";");
	        if (strs.length <= 1)
	        	return 99 ;
	        
        	String ss[] = strs[0].split(":") ;
	        if (ss.length >= 2 && ss[0].equals("AGKEY1"))
	        {
	        	ret = Integer.parseInt(ss[1]) ;

	        	for (int i=1; i < strs.length; i++)
	        	{
	        	    ss = strs[i].split(":");
					/*
	        	    if (ss.length >= 2 && ss[0].equals("KSEQ"))
	        	    {
						ki.KeySeq = Integer.parseInt(ss[1]) ;
	        	    }
	        	    else if (ss.length >= 2 && ss[0].equals("VDAT"))
	        	    {
						ki.sValidDt = ss[1] ;
	        	    }
					else if (ss.length >= 2 && ss[0].equals("GKEY"))
					{
						gKey = rsa.decrypt(rsa.pri_key, ss[1]);
					}
					*/
	        	}

				/*
				ki.setEncryptedKey(gKey) ;
				Const.updateGroupKey(ki);
				Const.dbDao.updateGroupKey(ki);
				Const.MyLog("groupid="+ki.GroupSid+",Group Key="+ki.getSKey());
				*/
	        	return ret ;
	        }
		}
		catch (Exception e)
		{
		}
	return ret ;
	}

    public static int reqUseHP(int uSid, int hp)
    {
        // return 0 if success
        // return > 0 if error
        int ret = 99 ;

        try
        {
            StringBuilder sb = new StringBuilder() ;
            sb.append("RHPUS");
            sb.append(";");
            sb.append("USID:"+uSid);
            sb.append(";");
            sb.append("HPUS:"+hp);

            String ans = request(sb.toString());

            // parse ans
            String strs[] = ans.split(";");
            if (strs.length < 1)
                return 99 ;

            String ss[] = strs[0].split(":") ;
            if (ss.length >= 2 && ss[0].equals("AHPUS"))
            {
                // 1= belong to Join refused list
                ret = Integer.parseInt(ss[1]) ;
                for (int i=1; i < strs.length; i++) {
                    ss = strs[i].split(":");
                    if (ss.length >= 2 && ss[0].equals("HPRM")) {
                        Const.saveHP(Integer.parseInt(ss[1]));
                    }
                }
                return ret ;
            }
        }
        catch (Exception e)
        {
        }
        return ret ;
    }


	public static int reqYoutubeUrl(int level)
	{
		int ret = 99 ;

		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RUTUB");
			sb.append(";USID"+Const.getMySid());
			sb.append(";MLVL:"+(level+1)); //ysk20161127
			sb.append(";SEXY:"+Const.getSexy());
			sb.append(";BYER:"+Const.getBirthYear());
			sb.append(";COCD:"+Util.getCountryCode());
			sb.append(";LNCD:"+Util.getLanguageCode());

			String ans = request(sb.toString());

			// parse ans
			String strs[] = ans.split(";");
			if (strs.length < 1)
				return 99 ;

			String ss[] = strs[0].split(":") ;
			if (ss.length >= 2 && ss[0].equals("AUTUB"))
			{
				ret = Integer.parseInt(ss[1]) ;
				if (ret == 0) {
					for (int i = 1; i < strs.length; i++) {
						ss = strs[i].split(":");
						if (ss.length >= 2 && ss[0].equals("UURL")) {
							String url = URLDecoder.decode(ss[1], "UTF-8");
							Const.saveYoutubeUrl(level, url);
						}
						else if (ss.length >= 2 && ss[0].equals("IURL")) {
							String url = URLDecoder.decode(ss[1], "UTF-8");
							Const.saveImageUrl(level, url);
						}
					}
				}
				return ret ;
			}
		}
		catch (Exception e)
		{
		}
		return ret ;
	}

	//ysk20161113
	public static int reqUserCheck(int plusHP)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;

		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RUCHK");

			int level = Const.getMyOrgLevel();
			sb.append(";USID:"+Const.getMySid()) ;
			sb.append(";LNCD:"+Util.getLanguageCode()) ;
			sb.append(";MLVL:"+level) ;
			sb.append(";PCNT:"+Const.getPiecesRow(level)) ;
			sb.append(";AKND:"+Const2.appKind) ;
			sb.append(";APPV:"+Util.getAppVersion()) ;
			sb.append(";RNUM:"+Const.getTodayRandomNumber()) ;
            sb.append(";PDAT:"+Const.getPuzzleHex()) ;
			sb.append(";HPGV:"+plusHP) ;

			String ans = request(sb.toString());

			// parse ans
			String strs[] = ans.split(";");
			if (strs.length <= 0)
				return 99 ;

			String ss[] = strs[0].split(":") ;
			if (ss.length >= 2 && ss[0].equals("AUCHK"))
			{
				ret = Integer.parseInt(ss[1]) ;

				if (ret != 0)
					return ret ;

				for (int i=1; i < strs.length; i++)
				{
					ss = strs[i].split(":");
					if (ss.length >= 2 && ss[0].equals("BLOK1"))
						Const.saveBlocksFromMe(ss[1]);
					else if (ss.length >= 2 && ss[0].equals("BLOK2"))
						Const.saveBlocksAboutMe(ss[1]);
					else if (ss.length >= 2 && ss[0].equals("NOTC"))
						Const.saveNotice(URLDecoder.decode(ss[1], "UTF-8"));
					else if (ss.length >= 2 && ss[0].equals("MVER"))
						Const.saveMustVersion(ss[1]);
					else if (ss.length >= 2 && ss[0].equals("RSID"))
						Const.saveRecomSid(Integer.parseInt(ss[1]));
					else if (ss.length >= 2 && ss[0].equals("HPRM"))
						Const.saveHP(Integer.parseInt(ss[1]));
				}
				return ret ;
			}
		}
		catch (Exception e)
		{
		}
		return ret ;
	}
	//ysk20161028
	public static int reqUserLogin(String pass)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		String pDat = "" ;
		int level = 2 ;

		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RULOG");

			sb.append(";UPHO:"+Const.targetPhone); //getGlobalPhoneNumber()) ;
			sb.append(";PASS:"+pass);
			sb.append(";APPV:"+Util.getAppVersion());
			sb.append(";PKND:1");
			sb.append(";AKND:"+ Const2.appKind);
			sb.append(";COCD:"+ Util.getCountryCode());
			sb.append(";LNCD:"+ Util.getLanguageCode());

			String ans = request(sb.toString());

			// parse ans
			String strs[] = ans.split(";");
			if (strs.length <= 1)
				return 99 ;

			String ss[] = strs[0].split(":") ;
			if (ss.length >= 2 && ss[0].equals("AULOG"))
			{
				ret = Integer.parseInt(ss[1]) ;

				if (ret == 0)
					Const.savePassword(pass);

				for (int i=1; i < strs.length; i++)
				{
					ss = strs[i].split(":");
					if (ss.length >= 2 && ss[0].equals("USID"))
					{
						Const.saveMySid(Integer.parseInt(ss[1]));
					}
					else if (ss.length >= 2 && ss[0].equals("UNAM"))
					{
						String name = "" ;
						if (!ss[1].equals(""))
							name = URLDecoder.decode(ss[1], "UTF-8") ;
						Const.saveMyName(name);
					}
					else if (ss.length >= 2 && ss[0].equals("BYER"))
					{
						Const.saveBirthYear(Integer.parseInt(ss[1]));
					}
					else if (ss.length >= 2 && ss[0].equals("SEXY"))
					{
						Const.saveSexy(Integer.parseInt(ss[1]));
					}
					else if (ss.length >= 2 && ss[0].equals("GCNT"))
					{
						// group count
					}
					else if (ss.length >= 2 && ss[0].equals("MLVL"))
					{
						level = Integer.parseInt(ss[1]) ;
						Const.saveMyOrgLevel(level);
					}
					else if (ss.length >= 2 && ss[0].equals("MVER"))
					{
						if (ss.length >= 2)
							Const.saveMustVersion(ss[1]);
					}
					else if (ss.length >= 2 && ss[0].equals("RNUM"))
					{
						if (ss.length >= 2) {
							if (!ss[0].equals("0"))
								Const.saveTodayRandomNumber(Integer.parseInt(ss[1]));
						}
					}
					else if (ss.length >= 2 && ss[0].equals("RSID")) {
						if (ss.length >= 2) {
							Const.saveRecomSid(Integer.parseInt(ss[1]));
						}
					}
					else if (ss.length >= 2 && ss[0].equals("HPRM"))
						Const.saveHP(Integer.parseInt(ss[1]));
					else if (ss.length >= 2 && ss[0].equals("PDAT"))
						pDat = ss[1];
				}
				if (ret == 0) {
					Const.savePuzzleHex(pDat);
					Const.initPuzzles();
				}
				return ret ;
			}
		}
		catch (Exception e)
		{
		}
		return ret ;
	}

	//ysk20161103
	public static int reqChangePassword(String newPass)
	{
		// return 0 if success
		// return > 0 if error
		int ret = 99 ;
		String pDat = "" ;
		int level = 2 ;

		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RCHPW");

			sb.append(";USID:"+Const.getMySid()); //getGlobalPhoneNumber()) ;
			sb.append(";PASS:"+Const.getPassword());
			sb.append(";NPAS:"+newPass);

			String ans = request(sb.toString());

			// parse ans
			String strs[] = ans.split(";");
			if (strs.length < 1)
				return 99 ;

			String ss[] = strs[0].split(":") ;
			if (ss.length >= 2 && ss[0].equals("ACHPW"))
			{
				ret = Integer.parseInt(ss[1]) ;

				if (ret == 0)
					Const.savePassword(newPass);

				return ret ;
			}
		}
		catch (Exception e)
		{
		}
		return ret ;
	}

    //ysk20161107
	public static int reqRegYoutube(String utbUrl, String imageUrl, String comment)
	{
		int ret = 99 ;

		try
		{
			StringBuilder sb = new StringBuilder() ;
			sb.append("RRUTB");
            sb.append(";USID:"+Const.getMySid());
			sb.append(";SEXY:"+Const.getSexy());
			sb.append(";BYER:"+Const.getBirthYear());
			sb.append(";COCD:"+Util.getCountryCode());
			sb.append(";LNCD:"+Util.getLanguageCode());
            sb.append(";UURL:"+URLEncoder.encode(utbUrl, "UTF-8"));
			sb.append(";IURL:"+URLEncoder.encode(imageUrl, "UTF-8"));
            sb.append(";UCMT:"+URLEncoder.encode(comment, "UTF-8"));

			String ans = request(sb.toString());

			// parse ans
			String strs[] = ans.split(";");
			if (strs.length < 1)
				return 99 ;

			String ss[] = strs[0].split(":") ;
			if (ss.length >= 2 && ss[0].equals("ARUTB"))
			{
				ret = Integer.parseInt(ss[1]) ;
				if (ret == 0) {
                    Const.saveHP(Const.getHP()+2);
                }

                for (int i=1; i < strs.length; i++) {
                    ss = strs[i].split(":");
                    if (ss.length >= 2 && ss[0].equals("MLVL")) {
                        Const.registeredLevel = Integer.parseInt(ss[1]);
                    }
                }

				return ret ;
			}
		}
		catch (Exception e)
		{
		}
		return ret ;
	}
}
