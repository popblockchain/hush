package com.stealth.crypto;

import java.security.KeyStore;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.stealth.service.Const;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class KeyStoreUtil 
{
/*
	public static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        
        byte[] raw = skey.getEncoded();
        return raw;
    }
*/
	public static String generateAESKeyStr()
	{
		SecretKey sk = generateAESKey() ;
		return(get64StrFromSecretKey(sk));
	}
	
    public static SecretKey generateAESKey()
    {
    	SecretKey sk = null ;
    	
    	try
    	{
    	KeyGenerator kg = KeyGenerator.getInstance("AES");
    	kg.init(256); // 128?
    	sk = kg.generateKey();
    	}
    	catch(Exception e)
    	{
    		
    	}
    	return sk ;
    }
    
    public static String get64StrFromSecretKey(SecretKey sk)
    {
    	byte[] encoded1 = sk.getEncoded() ;
    	byte[] encoded = Base64.encodeBase64(encoded1);
        
    	String str = "" ;
    	try{
        str = new String(encoded, "UTF-8") ;
    	} catch(Exception e) {}
    	
    	return str ;
    }
    
    
    public static SecretKey getSecretKeyFrom64Str(String b64Str)
    {
    	try
    	{
    		byte[] decoded1 = b64Str.getBytes("UTF-8") ;
        	byte[] decodedKey = Base64.decodeBase64(decoded1);
        	
        	// rebuild key using SecretKeySpec
        	SecretKey originalKey = new SecretKeySpec(decodedKey,"AES");  //0, decodedKey.length, "AES");
        	
        	return originalKey ;
    	}
    	catch (Exception e)
    	{
    		String str = "getSecretKeyFrom64Str exception="+e.toString() ;
    		Const.MyLog(str);
    		return null ;
    	}
    }
    
    public static String getHexFromSecretKey(SecretKey sk)
    {
    	return(String.valueOf(Hex.encodeHex(sk.getEncoded())));
    }
    
    /*
    public static String getKeyStrFromStore(String Alias)
    {
    	SecretKey sk = getKeyFromStore(Alias);
    	if (sk == null)
    		return "" ;
    	else
    	{
    		return(get64StrFromSecretKey(sk));
    	}
    }
    */
    public static String getKeyStrFromStore(String Alias)
    {
    	if (Const.getService() == null)
    		return "" ;
    		
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Const.getService());
	    String str = prefs.getString("KeyStore:"+Alias, "");
	    String[] strs = str.split(":");
	    if (strs.length >= 2)
	    	return strs[1] ;
	    return "" ; 
    }

    public static boolean saveKeyStrToStore(String Alias, String keyStr)
    {
    	if (Const.getService() == null)
    		return false ;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Const.getService());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("KeyStore:"+Alias, keyStr);	
		editor.commit();
		return true ;
    }
    /* 
    public static boolean saveKeyStrToStore(String Alias, String keyStr)
    {
        SecretKey sk = getSecretKeyFrom64Str(keyStr);
        if (sk == null)
        	return false ;
        return(saveKeyToStore(Alias, sk));
    }
    */
    
    
    public static SecretKey getKeyFromStore(String Alias)
    {
    	SecretKey mySecretKey = null ;
    	java.io.FileInputStream fis = null;
    	char[] password = "keystorepassword".toCharArray();
    	KeyStore ks = null ;
    	
    	try 
    	{
    		String type = KeyStore.getDefaultType() ;
        	ks = KeyStore.getInstance(type);
        	try{
    	    fis = new java.io.FileInputStream("keyStoreName");
        	} catch (Exception e) {}
        	
    	    ks.load(fis, password);
    	} 
    	catch (Exception e)
    	{}
    	finally 
    	{
    	  if (fis != null) 
    	  {
    		  try {
    	          fis.close();
    		  }
    		  catch (Exception e) {}
    	  }
    	}

    	if (ks == null)
    		return null ;
    	
    	KeyStore.ProtectionParameter protParam = 
    	    new KeyStore.PasswordProtection(password);

    	try
    	{
    	// get my secret key
    	KeyStore. SecretKeyEntry skEntry = (KeyStore. SecretKeyEntry)
    	       ks.getEntry(Alias, protParam);
    	mySecretKey = skEntry.getSecretKey();
    	}
    	catch (Exception e)
    	{}
    	
    	return mySecretKey ;
    }

    public static boolean saveKeyToStore(String Alias, SecretKey key)
    {
    	java.io.FileOutputStream fos = null;
    	char[] password = "keystorepassword".toCharArray();
    	KeyStore ks = null ;
    	
    	try 
    	{
    		String type = KeyStore.getDefaultType() ;
        	ks = KeyStore.getInstance(type);
        	try{
    	    fos = new java.io.FileOutputStream("keyStoreName");
        	} catch (Exception e) {}
        	
    	    ks.store(fos, password);
    	} 
    	catch (Exception e)
    	{}
    	finally 
    	{
    	  if (fos != null) 
    	  {
    		  try {
    	          fos.close();
    		  }
    		  catch (Exception e) {}
    	  }
    	}

    	if (ks == null)
    		return false ;
    	
    	KeyStore.ProtectionParameter protParam = 
    	    new KeyStore.PasswordProtection(password);

    	try
    	{
    		// save my secret key
    		  KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(key);
    		  ks.setEntry(Alias, skEntry, protParam);
    		  return true ;
    	}
    	catch (Exception e)
    	{
    		return false ;
    	}
    }
}
