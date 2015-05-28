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

/**
 * This is the structure of a web user
 * Created by Matt Van Horn on 11/14/14.
 */
public interface IWebUser {

	/**
	 * @return the id of the user
	 */
	long getUserId();

	/**
	 * @return the name of the user
	 */
	String getName();

	/**
	 * @return true if not a known user
	 */
	@SuppressWarnings("unused")
	boolean isGuest();

	/**
	 * @return true if a real user false if not
	 */
	@SuppressWarnings("unused")
	boolean isAuthenticated();

	/**
	 * Used to check if a given user has a given permission
	 *
	 * @param permissionId the id of the permission to check for
	 * @return true if allowed false if not
	 */
	boolean hasPermission(int permissionId);
}