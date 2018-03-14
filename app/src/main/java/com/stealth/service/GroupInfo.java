package com.stealth.service;

import java.util.ArrayList;

public class GroupInfo 
{
	public int GroupSid =0;
	public String GroupId ="";
//	public String GroupPwHash="" ;
//	public int Level=2 ;
	public int MgrSid =0;
	//public String KeyValidDt8[] = {"","", "","", "",""} ; // yyyyMMdd format
	public String MgrName = "";
	public int UseYN = 1 ;
	public int PayedYN = 1 ; 
	public int MgrYN = 0 ;
	public int MemberViewYN = 1 ; // can view member list
	public int DecryptCopyYN = 1 ; // can copy decrypted text
	public int SdcardMustYN = 0 ;
	public int MemberCnt = 1 ;
	public int GroupVers = Integer.MAX_VALUE;
	public boolean bNeedUpdate = false ; 
	public boolean bNeedDelete = true ;
	public int MyState = 1 ; // 0=미가입, 1=가입, 2=거절
	public int curLevel = 1 ;
    public static ArrayList<MemberInfo> memberList = null;
    public static ArrayList<String> joinXList = null; //prohibit join phones
    public boolean isGroupMgr()
    {
    	return (Const.getMySid() == MgrSid) || (MgrYN == 1);
    }
    
    public MemberInfo findMember(int uSid)
    {
    	if (memberList == null) return null ;
    	
    	for (MemberInfo info : memberList)
    	{
    		if (info.MemberSid == uSid)
    			return info ;
    	}
    	return null ;
    	
    }
    
    public void setUse(int uSid, int nUse)
    {
    	MemberInfo info = findMember(uSid) ;
    	if (info != null)
    		info.UseYN = nUse ;
    }
	
    public boolean isUseOK()
    {
       return (UseYN == 1 && PayedYN == 1 && MyState != 2);
    }
}
