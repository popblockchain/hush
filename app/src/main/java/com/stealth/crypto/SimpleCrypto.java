package com.stealth.crypto;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Usage:
 * <pre>
 * String crypto = SimpleCrypto.encrypt(masterpassword, cleartext)
 * ...
 * String cleartext = SimpleCrypto.decrypt(masterpassword, crypto)
 * </pre>
 * @author ferenc.hechler
 */
public class SimpleCrypto {

	
    public static String encrypt(String seed, String cleartext) {
    	try{
        byte[] rawKey = getRawKey(seed.getBytes("UTF-8"));
        byte[] result = encrypt(rawKey, cleartext.getBytes("UTF-8"));

        byte[] encoded = Base64.encodeBase64(result);
        
        String str = new String(encoded, "UTF-8") ;
        return str ;
    	}
    	catch (Exception e)
    	{
    	return "" ;
    	}
    	
    }

    public static String decrypt(String seed, String encrypted)  {
    	try{
        byte[] rawKey = getRawKey(seed.getBytes("UTF-8"));

        
        byte[] decoded = Base64.decodeBase64(encrypted.getBytes("UTF-8"));
        
        byte[] decVal = decrypt(rawKey, decoded);
        
        String str = new String(decVal, "UTF-8");
        return str;
    	}
    	catch (Exception e)
    	{
    		return "" ;
    	}
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }
}