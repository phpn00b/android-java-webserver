/*
 *
 *  * Copyright (C) 2015. Matt Van Horn (http://www.musingsofacodefiend.com/)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.foxhorn.foxyserver.text;

/**
 * Collection of tools that aid in dealing with text
 *
 * @author matt van horn
 */
public class StringUtils {
	/**
	 * This is used to help with converting byte arrays to strings
	 */
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	/**
	 * Used to right pad a string
	 *
	 * @param str     the string to pad
	 * @param size    the desired length
	 * @param padChar the character used for padding
	 * @return the string padded as requested
	 */
	@SuppressWarnings("unused")
	public static String padRight(String str, int size, char padChar) {
		StringBuilder padded = new StringBuilder(str);
		while (padded.length() < size) {
			padded.append(padChar);
		}
		return padded.toString();
	}

	/**
	 * Used to check if a string is null, empty, or all white space
	 * This is here to mimic the method with the same name from C# which is sooooo useful come on Java get with it!!!!
	 *
	 * @param str the string to check
	 * @return true if it is null white space or empty
	 */
	public static boolean isNullEmptyOrWhiteSpace(String str) {
		return isNullOrEmpty(str) || isWhitespace(str);
	}

	/**
	 * Used to check if a string is null or empty
	 *
	 * @param s the string to test
	 * @return true if is null or length is 0
	 */
	public static boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0;
	}

	/**
	 * Used to check if a string null or white space
	 *
	 * @param s the string to check
	 * @return true if is null or white space
	 */
	@SuppressWarnings("unused")
	public static boolean isNullOrWhitespace(String s) {
		return s == null || isWhitespace(s);
	}

	/**
	 * Used to check if a string is just white space
	 *
	 * @param s the string to check
	 * @return true if it is any white space character (space, tab, CR/NL, etc)
	 */
	public static boolean isWhitespace(String s) {
		int length = s.length();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				if (!Character.isWhitespace(s.charAt(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Used to left pad a string
	 *
	 * @param str     the string to left pad
	 * @param size    the total desired length
	 * @param padChar the character used for padding
	 * @return the string padded as requested
	 */
	public static String padLeft(String str, int size, char padChar) {
		StringBuilder sb = new StringBuilder();
		for (int toprepend = size - str.length(); toprepend > 0; toprepend--) {
			sb.append(padChar);
		}
		sb.append(str);
		return sb.toString();
	}

	/**
	 * Used to take a byte array and make it all pretty hex readable
	 *
	 * @param bytes              the raw bytes
	 * @param interByteSeparator what will be used to separate each byte
	 * @return the clean human readable string
	 */
	@SuppressWarnings("unused")
	public static String byteArrayToHexString(byte[] bytes, char interByteSeparator) {
		if (bytes == null)
			return null;
		char[] hexChars = new char[bytes.length * 3];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 3] = hexArray[v >>> 4];
			hexChars[j * 3 + 1] = hexArray[v & 0x0F];
			hexChars[j * 3 + 2] = interByteSeparator;
		}
		return new String(hexChars);
	}

	/**
	 * Check if two strings are equal null safe on either side
	 *
	 * @param str1 first string
	 * @param str2 second string
	 * @return true if they match (match is both null or str1.equals(str2) anything else is no match)
	 */
	public static boolean areEqual(String str1, String str2) {
		return str1 == null && str2 == null || !(str1 == null || str2 == null) && str1.equals(str2);
	}

	/**
	 * Converts byte[] to a string of hex characters
	 *
	 * @param bytes the bytes to convert
	 * @return a string in hex for all the values of the bytes
	 */
	@SuppressWarnings("unused")
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}