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

import com.foxhorn.foxyserver.web.security.HttpAuthContext;

/**
 * This defines the basic structure of an auth session that is required for the server to work with any implementation
 * Created by Matt Van Horn on 11/16/14.
 */
public interface IHttpAuthSession {

	/**
	 * @return our auth token for the session this is a means of uniquely identifying the session
	 */
	String getAuthToken();

	/**
	 * @return our auth context for the session
	 */
	HttpAuthContext getAuthContext();

	/**
	 * Used to get a value saved in the session
	 *
	 * @param key the name of the value
	 * @return the value
	 */
	@SuppressWarnings("unused")
	Object getSessionValue(String key);

	/**
	 * Used to save a value to the session
	 *
	 * @param key   the name of the value
	 * @param value the value
	 */
	@SuppressWarnings("unused")
	void saveSessionValue(String key, Object value);
}