package com.stealth.hushkbd;

import com.stealth.service.Const;
import com.stealth.hushkbd.*;
import com.stealth.hushkbd.Const1;

import android.view.inputmethod.InputConnection;

public class ComKbd 
{
    /**
     * Helper function to commit any text being composed in to the editor.
     */
    public static StringBuilder mComposing = new StringBuilder() ;
	
	public static String mOldText = "" ;
	
	//public static boolean bEncrypted = false ;
	
    public static void commitEncryptDecryptTyped(InputConnection inputConnection) 
    {
    	
    	if (Const.getMySid() == 0)
    	{
//    		if (Const.service != null)
//    		   Const.service.gotoJoin() ;
    		return ;
    	}
    	
    	if (mComposing.length() > 0)
        {
        	//ysk added
    		Const1.curInputConnection = inputConnection ;

			Const.bFromKbd = true ;
    		mOldText = mComposing.toString() ;
        	if (Const.levelEncryptedText(mOldText) >= -1) {
				    mComposing = decrypt(mComposing);
			}
        	else
        	    mComposing  = encrypt(mComposing);
        	//bEncrypted = true ;
        }
    }

    /*
    public static void cancelEncrypt()
    {
    	mComposing.setLength(0);
    	mComposing.append(mOldText);
    }
    */
    
    public static void delete()
    {
    	// delete 1 char
    	String str = mComposing.toString() ;
    	if (str.length() > 0)
    	{
    	   mComposing.setLength(0);
    	   mComposing.append(str.substring(0, str.length()-1));
    	}
    }
    
    public static StringBuilder encrypt(StringBuilder composing)
    {
    	StringBuilder sb = new StringBuilder();
    	String str = "" ;
    	try 
    	{
    		str = Const.encrypt(composing.toString().trim());
    	} 
    	catch (Exception e) 
    	{}
    	
    	sb.append(str);
    	return sb ;
    }
    
    public static StringBuilder decrypt(StringBuilder composing)
    {
    	StringBuilder sb = new StringBuilder();
    	
        String str = Const.decrypt(composing.toString());
    	
    	sb.append(str);
    	return sb ;
    }
    
    /**
     * Helper to determine if a given character code is alphabetic.
     */
    public static boolean isAlphabet(int code) {
        if (Character.isLetter(code)) {
            return true;
        } else {
            return false;
        }
    }

}
