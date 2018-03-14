package com.stealth.crypto;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
Aes encryption
*/
public class AES128
{

    private static SecretKeySpec secretKey ;
    private static byte[] key ;
    private static byte[] iv;
    private static String decryptedString;
    private static String encryptedString;

    public static void setKey(String myKey){


        MessageDigest sha = null;
        try {
            /* test ----- */
            for (int i=myKey.length(); i < 16; i++)
                myKey += "1" ;
            /* ----- test */
            key = myKey.getBytes("UTF-8");
            System.out.println(key.length);

            /* test
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            */

            key = Arrays.copyOf(key, 16); // use only first 128 bit
            secretKey = new SecretKeySpec(key, "AES");
            iv = new byte[]{11,53,63,87,11,69,63,28,0,9,18,99,95,23,45,8};

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } /*test  catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } */



    }

    public static String getDecryptedString() {
        return decryptedString;
    }
    public static void setDecryptedString(String decryptedString) {
        AES128.decryptedString = decryptedString;
    }
    public static String getEncryptedString() {
        return encryptedString;
    }
    public static void setEncryptedString(String encryptedString) {
        AES128.encryptedString = encryptedString;
    }
    public static String encrypt(String strToEncrypt)
    {
    	encryptedString = "" ;
        try
        {
            Cipher cipher = Cipher.getInstance("AES"); //("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] encVal = cipher.doFinal(strToEncrypt.getBytes("UTF-8")) ;
            byte[] encoded = Base64.encodeBase64(encVal);
            
            String str = new String(encoded, "UTF-8") ;

            setEncryptedString(str);

        }
        catch (Exception e)
        {

            System.out.println("Error while encrypting: "+e.toString());
        }
        return encryptedString ;
    }
    public static String decrypt(String strToDecrypt)
    {
    	decryptedString = "" ;
    	
        try
        {
            Cipher cipher = Cipher.getInstance("AES") ;  //("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            
            byte[] decoded = Base64.decodeBase64(strToDecrypt.getBytes("UTF-8"));
            byte[] decVal = cipher.doFinal(decoded) ;
            
            String str = new String(decVal, "UTF-8");
            setDecryptedString(str);

        }
        catch (Exception e)
        {

            System.out.println("Error while decrypting: "+e.toString());
        }
        return decryptedString;
    }
    
    
    public static String encrypt(String key, String src)
    {
    	setKey(key);
    	return (encrypt(src)) ;
    }

    public static String decrypt(String key, String src)
    {
    	setKey(key);
    	return (decrypt(src)) ;
    }
    
}
