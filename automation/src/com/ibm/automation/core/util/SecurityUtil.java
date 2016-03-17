package com.ibm.automation.core.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
/**
 * java 加密 nodejs 解密
 * 
 * @author hujin
 * 
 * 
 * */
public class SecurityUtil {
	/**
	 * @param input 输入数据
	 * @param key 加密秘钥 必须16位
	 * */
	public static String encrypt(String input, String key){
	  byte[] crypted = null;
	  try{
	    SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
	      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	      cipher.init(Cipher.ENCRYPT_MODE, skey);
	      crypted = cipher.doFinal(input.getBytes());
	    }catch(Exception e){
	    	System.out.println(e.toString());
	    }
	    return new String(Base64.encodeBase64(crypted));
	}

	public static String decrypt(String input, String key){
	    byte[] output = null;
	    try{
	      SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
	      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	      cipher.init(Cipher.DECRYPT_MODE, skey);
	      output = cipher.doFinal(Base64.decodeBase64(input));
	    }catch(Exception e){
	      System.out.println(e.toString());
	    }
	    return new String(output);
	}
	
	public static void main(String[] args) {
	  String key = "1234567890abcdfg";  //必须16位
	  String data = "passw0rd";
	//  System.out.println(SecurityUtil.decrypt(SecurityUtil.encrypt(data, key), key));
	 // System.out.println(SecurityUtil.encrypt(data, key));	
	 byte[] b = Base64.decodeBase64("cGFzc3cwcmQ=");
	  System.out.println(new String(b));
	}	
	
}
