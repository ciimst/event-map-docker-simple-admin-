package com.imst.event.map.admin.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imst.event.map.admin.constants.Statics;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ExportItemEncryptUtils {
	
	private static String secretKey = Statics.exporttEncryptPrivateKey;
	private static String salt = Statics.exporttEncryptPrivateKey;
	private static Cipher cipher = null;
	
	public static <T> String encryptGeneric(T T) throws JsonProcessingException {
		String encrypt = null;
	     		
	     try{
	    	 	ObjectMapper objectMapper = new ObjectMapper();
	    	 	String itemJsonData = objectMapper.writeValueAsString(T);
		        
	    	 	Cipher cipher = getCipher();
		        encrypt = Base64.getEncoder().encodeToString(cipher.doFinal(itemJsonData.getBytes("UTF-8")));
	    	 		    	 	
		    } 
		    catch (Exception e) 
		    {
		        log.error("Error while encrypting: " + e.toString());
		    }
		return encrypt;
	}
	
	public static Cipher getCipher() throws InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException  {
		
			if(cipher != null){
	            return cipher;
	        }
			
			try {
				
				byte[] iv = new byte[16];
		        IvParameterSpec ivspec = new IvParameterSpec(iv);
		         
		        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		        KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
		        SecretKey tmp = factory.generateSecret(spec);
		        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");		        		        		      
		        Cipher tempCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");        
		        tempCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
		        cipher= tempCipher;
				
		        return tempCipher;
				
			} catch (NoSuchAlgorithmException e) {

				log.error(e);
			} catch (NoSuchPaddingException e) {

				log.error(e);
			}
			 
		
		return null;
	}
	
}
