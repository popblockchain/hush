package com.stealth.crypto;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import com.stealth.service.Const;

import android.os.Environment;

public class CreateKeyAndCert 
{
	public static void CreateRSA() 
	{
        KeyPairGenerator kpg;
        try {
            // Create a 1024 bit RSA private key
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);// 1024);
            KeyPair kp = kpg.genKeyPair();
            Key publicKey = kp.getPublic();
            Key privateKey = kp.getPrivate();
 
            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pub = (RSAPublicKeySpec) fact.getKeySpec(publicKey, RSAPublicKeySpec.class);
            RSAPrivateKeySpec priv = (RSAPrivateKeySpec) fact.getKeySpec(privateKey, RSAPrivateKeySpec.class);
 
            // Save the file to local drive
            String PATH = Environment.getExternalStorageDirectory() + "/" + "testdir";
	        File file = new File(PATH);
	        file.mkdirs();

			String sPublic = PATH+"/"+ "public.key";
			String sPrivate = PATH+"/"+ "private.key";

			saveToFile(sPublic, pub.getModulus(), pub.getPublicExponent());
            saveToFile(sPrivate, priv.getModulus(), priv.getPrivateExponent());
 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        	Const.MyLog("exception="+e.toString());
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
     
    public static void saveToFile(String fileName, BigInteger mod, BigInteger exp) throws IOException {
        ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        
        try {
        	
            oout.writeObject(mod);
            oout.writeObject(exp);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            oout.close();
        }
    }
 
}


