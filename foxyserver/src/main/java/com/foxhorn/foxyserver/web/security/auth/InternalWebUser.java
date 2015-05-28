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

import com.foxhorn.foxyserver.web.api.IAuthCredentials;
import com.foxhorn.foxyserver.web.api.IWebUser;

import java.util.List;

/**
 * This is our built in user implementation it is very basic
 * Created by Matt Van Horn on 5/27/15.
 */
public class InternalWebUser implements IWebUser {

	private final long userId;
	private final String name;
	private final boolean isGuest;
	private final boolean isAuthenticated;
	private final List<Integer> permissions;

	public InternalWebUser(IAuthCredentials authCredentials, List<Integer> permissions) {
		if (authCredentials != null) {
			userId = authCredentials.getId();
			if (userId >= 0 && authCredentials.getIsActive())
				isAuthenticated = true;
			else
				isAuthenticated = false;
			this.permissions = permissions;

			if (userId == 0)
				isGuest = true;
			else
				isGuest = false;
			name = authCredentials.getLogonName();
		} else {
			// auth credentials are null this is a failed login
			userId = -1;
			name = "failed login";
			this.permissions = null;
			isGuest = false;
			isAuthenticated = false;
		}
	}

	/**
	 * @return the id of the user
	 */
	@Override
	public long getUserId() {
		return userId;
	}

	/**
	 * @return the name of the user
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return true if not a known user
	 */
	@Override
	public boolean isGuest() {
		return isGuest;
	}

	/**
	 * @return true if a real user false if not
	 */
	@Override
	public boolean isAuthenticated() {
		return isAuthenticated;
	}

	/**
	 * Used to check if a given user has a given permission
	 *
	 * @param permissionId the id of the permission to check for
	 * @return true if allowed false if not
	 */
	@Override
	public boolean hasPermission(int permissionId) {
		return permissions.contains((Integer) permissionId);
	}
}