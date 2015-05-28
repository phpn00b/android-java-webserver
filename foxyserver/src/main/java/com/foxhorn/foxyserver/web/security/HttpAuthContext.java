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

package com.foxhorn.foxyserver.web.security;

import com.foxhorn.foxyserver.FoxyServerSettings;
import com.foxhorn.foxyserver.web.api.IWebUser;

import java.util.Calendar;
import java.util.Date;

/**
 * This is a simple auth data structure
 * Created by Matt Van Horn on 11/14/14.
 */
public class HttpAuthContext {

	private final long userId;
	private final String username;
	private final Date sessionStart;
	private long sessionExpires;
	private final String remoteIpAddress;
	private final String authToken;
	private final Object userData;
	private final String mySecret;
	private final IWebUser user;

	/**
	 * Creates our auth context
	 *
	 * @param webUser         the user
	 * @param startDate       the date that the session started
	 * @param remoteIpAddress the ip of the device this request is coming from
	 * @param userData        any user data to associate with the session for later use
	 */
	public HttpAuthContext(IWebUser webUser, Date startDate, String remoteIpAddress, Object userData) {
		user = webUser;
		this.userId = user.getUserId();
		this.username = user.getName();
		this.sessionStart = startDate;
		this.remoteIpAddress = remoteIpAddress;
		this.userData = userData;
		mySecret = String.format("%s|%s|%s", FoxyServerSettings.getInstance().getAppPrivateCryptoSalt(), FoxyServerSettings.getInstance().getDeviceHardwareId(), sessionStart.getTime());
		authToken = FoxyServerSettings.getInstance().getCryptoHandler().getHashOfString(getStringToHash());
		updateExpiration();
	}

	@SuppressWarnings("unused")
	public Date getSessionStart() {
		return sessionStart;
	}

	@SuppressWarnings("unused")
	public Object getUserData() {
		return userData;
	}

	@SuppressWarnings("unused")
	public String getAuthToken() {
		return authToken;
	}

	@SuppressWarnings("unused")
	public String getRemoteIpAddress() {
		return remoteIpAddress;
	}

	@SuppressWarnings("unused")
	public long getSessionExpires() {
		return sessionExpires;
	}

	@SuppressWarnings("unused")
	public String getUsername() {
		return username;
	}

	@SuppressWarnings("unused")
	public long getUserId() {
		return userId;
	}

	@SuppressWarnings("unused")
	public IWebUser getUser() {
		return user;
	}

	private String getStringToHash() {
		return String.format(
				"%s|%s|%s|%s",
				userId,
				username,
				remoteIpAddress,
				mySecret
		);
	}

	/**
	 * Used to slide the expiration date for the session by the amount supplied to session inactivity time out
	 */
	public void updateExpiration() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, FoxyServerSettings.getInstance().getGetSessionInactivityTimeoutSeconds());
		sessionExpires = cal.getTime().getTime();
	}
}