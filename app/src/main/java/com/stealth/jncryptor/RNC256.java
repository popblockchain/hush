package com.stealth.jncryptor;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by kim on 2016-09-19.
 */
public class RNC256
{
        private static AES256JNCryptor cryptor = new AES256JNCryptor() ;
        private static String password = "";

        private static void setKey(String key) {
                password = key ;
        }

        private static String encrypt(String src)
        {
                String encryptedString = "" ;

                try {
                        byte[] ciphertext = cryptor.encryptData(src.getBytes(), password.toCharArray());
                        byte[] encoded = Base64.encodeBase64(ciphertext);

                        encryptedString = new String(encoded, "UTF-8") ;
                } catch (Exception e)
                {
                    System.out.println("Error while encrypting: "+e.toString());
                }
                return encryptedString ;
        }

    private static String decrypt(String strToDecrypt)
    {
        String decryptedString = "" ;

        try
        {
            byte[] decoded = Base64.decodeBase64(strToDecrypt.getBytes("UTF-8"));
            byte[] decVal = cryptor.decryptData(decoded, password.toCharArray());

            decryptedString = new String(decVal, "UTF-8");
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
