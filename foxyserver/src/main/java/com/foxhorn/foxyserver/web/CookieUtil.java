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

package com.foxhorn.foxyserver.web;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used to aid in parsing cookies
 */
public class CookieUtil {

	private static final Pattern cookiePattern = Pattern.compile("([^=]+)=([^;]*);?\\s?");

	public static Map<String, String> parseCookieString(String cookies) {
		Map<String, String> cookieList = new HashMap<>();
		Matcher matcher = cookiePattern.matcher(cookies);
		while (matcher.find()) {
			String cookieKey = matcher.group(1).trim();
			String cookieValue = matcher.group(2).trim();
			cookieList.put(cookieKey, cookieValue);
		}
		return cookieList;
	}
}