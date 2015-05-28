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

/**
 * This is our default auth credentials implementation
 * Created by Matt Van Horn on 5/27/15.
 */
public class InternalAuthCredentials implements IAuthCredentials {
	private final String logonName;
	private final String passwordHash;

	public InternalAuthCredentials(String logonName, String passwordHash) {
		this.logonName = logonName;
		this.passwordHash = passwordHash;
	}

	/**
	 * @return the id of the user
	 */
	@Override
	public Long getId() {
		return 0L;
	}

	/**
	 * @return the logon name
	 */
	@Override
	public String getLogonName() {
		return logonName;
	}

	/**
	 * @return the hashed password
	 */
	@Override
	public String getPasswordHash() {
		return passwordHash;
	}

	/**
	 * @return true if the account is active and should be allowed to be used
	 */
	@Override
	public Boolean getIsActive() {
		return false;
	}

	/**
	 * @return the number of times that a login attempt has failed
	 */
	@Override
	public short getFailedLoginAttempts() {
		return 0;
	}
}