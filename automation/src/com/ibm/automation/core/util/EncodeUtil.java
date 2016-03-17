package com.ibm.automation.core.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import sun.misc.BASE64Encoder;

public class EncodeUtil {
	
	public static String b64encode(String raw, String charset) {
		try {
			return new BASE64Encoder().encode(raw.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public static String encode(String str) {
		sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		return new String(encoder.encodeBuffer(str.getBytes())).trim();
	}
	
	public static String decode(String str) {
		try {
			sun.misc.BASE64Decoder decode = new sun.misc.BASE64Decoder();
			return new String(decode.decodeBuffer(str));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}
}