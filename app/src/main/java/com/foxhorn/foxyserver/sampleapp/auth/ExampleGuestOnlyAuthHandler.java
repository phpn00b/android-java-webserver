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

package com.foxhorn.foxyserver.sampleapp.auth;

import com.foxhorn.foxyserver.web.HttpContext;
import com.foxhorn.foxyserver.web.api.IAuthCredentialsProvider;
import com.foxhorn.foxyserver.web.api.IHttpAuthHandler;
import com.foxhorn.foxyserver.web.api.IHttpAuthSession;
import com.foxhorn.foxyserver.web.api.IWebUser;
import com.foxhorn.foxyserver.web.security.HttpAuthContext;
import com.foxhorn.foxyserver.web.security.auth.DefaultFoxyAuthSession;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This sample auth handler just makes everyone a guest. but it keeps each guest session distinct
 * Created by Matt Van Horn on 5/27/15.
 */
public class ExampleGuestOnlyAuthHandler implements IHttpAuthHandler {
	private static final String AUTH_TOKEN_NAME = "___exampleFoxyAuthToken";
	private final Object lock = new Object();
	private final Map<String, IHttpAuthSession> httpAuthContextMap = new HashMap<>();
	private AtomicInteger userIndex = new AtomicInteger(1);

	@Override
	public boolean shouldAllowRequest(HttpContext httpContext) {
		return true;
	}

	/**
	 * Used to login a user
	 *
	 * @param httpContext the context
	 * @param username    the username
	 * @param password    the password
	 * @return a user or null
	 */
	@Override
	public IHttpAuthSession login(HttpContext httpContext, String username, String password) {
		// This is not supported
		return null;
	}

	private IWebUser createGuestUser() {
		return new IWebUser() {
			private final String name = String.format("Guest#%s", userIndex.incrementAndGet());
			private final int userId = userIndex.get();

			@Override
			public long getUserId() {
				return userId;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public boolean isGuest() {
				return true;
			}

			@Override
			public boolean isAuthenticated() {
				return false;
			}

			@Override
			public boolean hasPermission(int permissionId) {
				return false;
			}
		};
	}

	@Override
	public IHttpAuthSession getSessionForAuthToken(String authToken) {
		synchronized (lock) {
			if (httpAuthContextMap.containsKey(authToken))
				return httpAuthContextMap.get(authToken);
			return null;
		}
	}

	@Override
	public IHttpAuthSession createGuestAuthSession(HttpContext httpContext) {
		synchronized (lock) {
			IHttpAuthSession authSession = new DefaultFoxyAuthSession(new HttpAuthContext(
					createGuestUser(),
					new Date(),
					httpContext.getRequest().getRemoteHost(),
					null
			));
			httpAuthContextMap.put(authSession.getAuthToken(), authSession);
			return authSession;
		}
	}

	/**
	 * @return the name of the auth token cookie
	 */
	@Override
	public String getAuthTokenName() {
		return AUTH_TOKEN_NAME;
	}

	/**
	 * @return true will force putting in an auth cookie
	 */
	@Override
	public boolean enableAuthCookie() {
		return true;
	}

	/**
	 * Used to log-out a given IHttpAuthSession
	 *
	 * @param httpAuthSession the session to terminate
	 */
	@Override
	public void logout(IHttpAuthSession httpAuthSession) {
		httpAuthContextMap.remove(httpAuthSession.getAuthToken());
	}

	/**
	 * @return the instance of the IAuthCredentialsProvider being used
	 */
	@Override
	public IAuthCredentialsProvider getAuthCredentialsProvider() {
		return null;
	}
}