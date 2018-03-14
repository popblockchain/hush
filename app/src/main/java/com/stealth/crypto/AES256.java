package com.stealth.crypto;


import android.util.Base64;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
Aes encryption
*/
public class AES256
{

    private static SecretKeySpec secretKey ;
    private static byte[] key ;
    private static byte[] iv;
    private static String decryptedString;
    private static String encryptedString;

    public static void setKey(String password){

        iv = new byte[16] ;
        Arrays.fill(iv, (byte)0x00);

        MessageDigest sha = null;
        try {
            int keyLength = 256 ;
            byte[] keyBytes = new byte[keyLength / 8] ;
            Arrays.fill(keyBytes, (byte)0x00);

            // if password is shorter than key length, it will be zero-padded
            // to key length
            byte[] passwordBytes = password.getBytes("UTF-8") ;
            int length = passwordBytes.length < keyBytes.length ?
                    passwordBytes.length : keyBytes.length ;
            System.arraycopy(passwordBytes, 0, keyBytes, 0, length);

            secretKey = new SecretKeySpec(keyBytes, "AES");

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
        AES256.decryptedString = decryptedString;
    }
    public static String getEncryptedString() {
        return encryptedString;
    }
    public static void setEncryptedString(String encryptedString) {
        AES256.encryptedString = encryptedString;
    }
    public static String encrypt(String strToEncrypt)
    {
    	encryptedString = "" ;
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding"); //("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

            byte[] encVal = cipher.doFinal(strToEncrypt.getBytes("UTF-8")) ;
            byte[] encoded = Base64.encode(encVal, Base64.DEFAULT);

            encryptedString = new String(encoded, "UTF-8") ;

            setEncryptedString(encryptedString);

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
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding") ;  //("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            byte[] decoded = Base64.decode(strToDecrypt.getBytes("UTF-8"), Base64.DEFAULT);
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
