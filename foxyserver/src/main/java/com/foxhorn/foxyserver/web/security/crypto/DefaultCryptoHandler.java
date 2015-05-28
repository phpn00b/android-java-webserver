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

package com.foxhorn.foxyserver.web.security.crypto;

import com.foxhorn.foxyserver.web.api.ICryptoHandler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Basic crypto handler using SHA-256 to hash a string
 * Created by Matt Van Horn on 11/14/14.
 */
public class DefaultCryptoHandler implements ICryptoHandler {

	private final MessageDigest messageDigest;

	public DefaultCryptoHandler() throws NoSuchAlgorithmException {
		messageDigest = MessageDigest.getInstance("SHA-256");
	}

	/**
	 * @return the active message digest in use
	 */
	@Override
	public MessageDigest getMessageDigest() {
		return messageDigest;
	}

	/**
	 * Used to get a hash of an input string
	 *
	 * @param input the string to hash
	 * @return the hash of the target
	 */
	@Override
	public String getHashOfString(String input) {
		messageDigest.reset();
		byte[] byteData = messageDigest.digest(input.getBytes());
		StringBuilder sb = new StringBuilder();

		for (byte aByteData : byteData) {
			sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}