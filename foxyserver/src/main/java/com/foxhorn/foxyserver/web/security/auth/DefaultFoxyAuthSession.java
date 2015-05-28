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

package com.foxhorn.foxyserver.web.security.auth;


import com.foxhorn.foxyserver.web.api.IHttpAuthSession;
import com.foxhorn.foxyserver.web.security.HttpAuthContext;

import java.util.HashMap;
import java.util.Map;

/**
 * This is our session object
 * Created by Matt Van Horn on 11/16/14.
 */
public class DefaultFoxyAuthSession implements IHttpAuthSession {

	private final Object lock = new Object();
	private Map<String, Object> sessionValues;

	private final HttpAuthContext authContext;

	/**
	 * Creates our auth session for the context supplied
	 *
	 * @param authContext the auth context
	 */
	public DefaultFoxyAuthSession(HttpAuthContext authContext) {
		this.authContext = authContext;
	}

	/**
	 * @return our auth token for the session
	 */
	@Override
	public String getAuthToken() {
		return authContext.getAuthToken();
	}

	/**
	 * @return our auth context for the session
	 */
	@Override
	public HttpAuthContext getAuthContext() {
		return authContext;
	}

	/**
	 * Used to get a value saved in the session
	 *
	 * @param key the name of the value
	 * @return the value
	 */
	@Override
	public Object getSessionValue(String key) {
		synchronized (lock) {
			if (sessionValues != null)
				if (sessionValues.containsKey(key))
					return sessionValues.get(key);
			return null;
		}
	}

	/**
	 * Used to save a value to the session
	 *
	 * @param key   the name of the value
	 * @param value the value
	 */
	@Override
	public void saveSessionValue(String key, Object value) {
		synchronized (lock) {
			if (sessionValues != null)
				sessionValues = new HashMap<>();
			assert sessionValues != null;
			sessionValues.put(key, value);
		}
	}
}