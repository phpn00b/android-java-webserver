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

package com.foxhorn.foxyserver.web.api;

import java.security.MessageDigest;

/**
 * This class helps with crypto tasks
 * Created by Matt Van Horn on 11/14/14.
 */
public interface ICryptoHandler {

	/**
	 * @return the active message digest in use
	 */
	@SuppressWarnings("unused")
	MessageDigest getMessageDigest();

	/**
	 * Used to get a hash of an input string
	 *
	 * @param input the string to hash
	 * @return the hash of the target
	 */
	String getHashOfString(String input);
}