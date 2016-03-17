package com.ibm.automation.core.util;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UnicodeUtil {
	
	
	public static String unicodeEscape(String json){
		ObjectMapper om = new ObjectMapper();
		try {
			json = om.writer(new HtmlCharacterEscapes()).writeValueAsString(om.readTree(json));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	public static class HtmlCharacterEscapes extends CharacterEscapes {
		
		private static final long serialVersionUID = 1L;
		private final int[] asciiEscapes;

		public HtmlCharacterEscapes() {
			// start with set of characters known to require escaping
			// (double-quote, backslash etc)
			int[] esc = CharacterEscapes.standardAsciiEscapesForJSON();
			// and force escaping of a few others:
			esc['"'] = CharacterEscapes.ESCAPE_STANDARD;
			esc['<'] = CharacterEscapes.ESCAPE_STANDARD;
			esc['>'] = CharacterEscapes.ESCAPE_STANDARD;
			esc['&'] = CharacterEscapes.ESCAPE_STANDARD;
			esc['\''] = CharacterEscapes.ESCAPE_STANDARD;
			asciiEscapes = esc;
		}

		// this method gets called for character codes 0 - 127
		@Override
		public int[] getEscapeCodesForAscii() {
			return Arrays.copyOf(asciiEscapes, asciiEscapes.length);
		}

		@Override
		public SerializableString getEscapeSequence(int ch) {
			return new SerializedString(String.format("\\u%04x", ch));
		}
	}
}
