package com.stealth.crypto;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import com.stealth.service.Const;

//import android.util.Base64;

public class RSA
{
	
    public String pub_key = "RSA.pub";
	public String pri_key = "RSA.pri";
	//public static  PrivateKey priv ;
	//public static  PublicKey pub ;

	public RSA()
	{
		//makeKeyPair() ;
	}
	
	public void makeKeyPair()
    {
    	
    	try
    	{
    		  System.out.println("Seeding random number generator...");
    		  SecureRandom rand = new SecureRandom();
    		  rand.nextInt();
    		  
    		  KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA"); //RSA�� ���
    		  kpg.initialize(1024,rand); // ���̴� 512, rand�� ����� �ʱ�ȭ
    		  
    		  System.out.println("Generating keys ...");
    		  KeyPair kp = kpg.genKeyPair();
    		  PrivateKey priv = kp.getPrivate();
    		  PublicKey pub = kp.getPublic();
    		  
    		   KeyFactory fact = KeyFactory.getInstance("RSA");
    		   RSAPublicKeySpec clsPublicKeySpec = fact.getKeySpec( pub, RSAPublicKeySpec.class);
    		   RSAPrivateKeySpec clsPrivateKeySpec = fact.getKeySpec( priv, RSAPrivateKeySpec.class);
    		  
    		  //pub_key = Base64.encodeToString(pub.getEncoded(), Base64.DEFAULT);
          	  byte[] encData64 = Base64.encodeBase64(pub.getEncoded());
              pub_key = new String(encData64, "UTF-8") ;

//              pri_key = Base64.encodeToString(priv.getEncoded(), Base64.DEFAULT);
          	  byte[] encData64priv = Base64.encodeBase64(priv.getEncoded());
              pri_key = new String(encData64priv, "UTF-8") ;
    		  
    	 }
         catch(Exception e)
    	 {
              e.printStackTrace();
         }
    }
    
    public String encrypt(String pub_key1, String text)
    {
    	String str = "" ;
    	try
    	{
        	Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

//        	byte[] keyBytes = Base64.decode(pub_key.getBytes("UTF-8"), Base64.DEFAULT) ;
        	byte[] keyBytes = Base64.decodeBase64(pub_key1.getBytes("UTF-8"));
        	
        	
        	X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(keyBytes);
       	    KeyFactory keyFact = KeyFactory.getInstance("RSA");
        	PublicKey pub = keyFact.generatePublic(pubKeySpec);
        	
        	rsaCipher.init(Cipher.ENCRYPT_MODE, pub);
        	   
        	byte[] data = text.getBytes("UTF-8") ;
        	byte[] encData = rsaCipher.doFinal(data);
        	byte[] encData64 = Base64.encodeBase64(encData);
//        	str = Base64.encodeToString(encData, Base64.DEFAULT);
            str = new String(encData64, "UTF-8") ;
    	}
    	catch(Exception e)
    	{
    		String str1 = e.toString() ;
    		Const.MyLog("RSA encrypt","exception="+str1);
    	}
    	
    	return str ;
    }
    
    public String decrypt(String pri_key1, String enc_text)
    {
    	String str = "" ;
    	try
    	{
        	Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        	//byte[] key_bytes = Base64.decode(pri_key.getBytes(), Base64.DEFAULT) ;
        	byte[] key_bytes = Base64.decodeBase64(pri_key1.getBytes("UTF-8"));
        	
        	PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(key_bytes);
            KeyFactory keyFact = KeyFactory.getInstance("RSA");
            PrivateKey priv = keyFact.generatePrivate(privKeySpec);
            
            rsaCipher.init(Cipher.DECRYPT_MODE, priv);

            //byte[] encData = Base64.decode(enc_text.getBytes(), Base64.DEFAULT);
        	byte[] encData = Base64.decodeBase64(enc_text.getBytes("UTF-8"));
            byte[] decData = rsaCipher.doFinal(encData);
            str = new String(decData,"UTF8");
    	}
    	catch (Exception e)
    	{}
    	
        return str ;
    }
}
