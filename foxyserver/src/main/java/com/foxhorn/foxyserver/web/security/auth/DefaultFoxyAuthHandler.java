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


import com.foxhorn.foxyserver.web.HttpContext;
import com.foxhorn.foxyserver.web.api.IAuthCredentials;
import com.foxhorn.foxyserver.web.api.IAuthCredentialsProvider;
import com.foxhorn.foxyserver.web.api.IHttpAuthHandler;
import com.foxhorn.foxyserver.web.api.IHttpAuthSession;
import com.foxhorn.foxyserver.web.api.IHttpAuthSessionFactory;
import com.foxhorn.foxyserver.web.security.HttpAuthContext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This handles our default logic for a auth handler
 * Created by Matt Van Horn on 11/16/14.
 */
@SuppressWarnings("unused")
public class DefaultFoxyAuthHandler implements IHttpAuthHandler {

	private final String authCookieName;
	private final IHttpAuthSessionFactory httpAuthSessionFactory;
	private final IAuthCredentialsProvider authCredentialsProvider;
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private final Map<String, IHttpAuthSession> httpAuthSessionMap;

	public DefaultFoxyAuthHandler(String authCookieName, IAuthCredentialsProvider authCredentialsProvider, IHttpAuthSessionFactory httpAuthSessionFactory) {
		this.authCredentialsProvider = authCredentialsProvider;
		this.authCookieName = authCookieName;
		httpAuthSessionMap = new HashMap<>();
		this.httpAuthSessionFactory = httpAuthSessionFactory;
	}

	//<editor-fold desc="IHttpAuthHandler Implementation">

	/**
	 * Used to check if a given request should be allowed
	 *
	 * @param httpContext the context to check if we should allow the request against
	 * @return true if the request should be allowed
	 */
	@Override
	public boolean shouldAllowRequest(HttpContext httpContext) {
		if (httpContext.getRequest().getRequiredPermissions() == null)
			return true;
		List<Integer> requiredPermissions = httpContext.getRequest().getRequiredPermissions();
		for (Integer requiredPermission : requiredPermissions) {
			if (!httpContext.getHttpSession().getAuthContext().getUser().hasPermission(requiredPermission))
				return false;
		}
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
		writeLock.lock();
		try {
			IAuthCredentials credentials = authCredentialsProvider.fetchByLoginCredentials(
					new InternalAuthCredentials(username, password)
			);
			if (credentials != null) {
				IHttpAuthSession authSession = httpAuthSessionFactory.constructSession(new HttpAuthContext(
						new InternalWebUser(credentials, authCredentialsProvider.getPermissionsForUser(credentials.getId())),
						new Date(),
						httpContext.getRequest().getRemoteHost(),
						credentials
				), httpContext);
				httpAuthSessionMap.put(authSession.getAuthToken(), authSession);
				return authSession;
			}
			// credentials are null
			return null;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Used to find a HttpAuthContext for a given token
	 *
	 * @param authToken the auth token to find the session for
	 * @return the session or null if it can't find the session for that token
	 */
	@Override
	public IHttpAuthSession getSessionForAuthToken(String authToken) {
		readLock.lock();
		try {
			if (httpAuthSessionMap.containsKey(authToken)) {
				IHttpAuthSession session = httpAuthSessionMap.get(authToken);
				if (session.getAuthContext().getSessionExpires() < System.currentTimeMillis()) {
					httpAuthSessionMap.remove(authToken);
					return null;
				}
				session.getAuthContext().updateExpiration();
				return session;
			}
			return null;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * This creates a guest session
	 *
	 * @param httpContext the http context of the request to create a guest session for
	 * @return the auth context
	 */
	@Override
	public IHttpAuthSession createGuestAuthSession(HttpContext httpContext) {
		writeLock.lock();
		try {
			IAuthCredentials guestCredentials = authCredentialsProvider.createNewGuestCredentials();
			IHttpAuthSession authSession = new DefaultFoxyAuthSession(new HttpAuthContext(
					new InternalWebUser(
							guestCredentials,
							authCredentialsProvider.getPermissionsForUser(guestCredentials.getId())
					),
					new Date(),
					httpContext.getRequest().getRemoteHost(),
					null
			));
			httpAuthSessionMap.put(authSession.getAuthToken(), authSession);
			return authSession;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * @return the name of the auth token cookie
	 */
	@Override
	public String getAuthTokenName() {
		return authCookieName;
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
		writeLock.lock();
		try {
			httpAuthSessionMap.remove(httpAuthSession.getAuthToken());
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * @return the instance of the IAuthCredentialsProvider being used
	 */
	@Override
	public IAuthCredentialsProvider getAuthCredentialsProvider() {
		return authCredentialsProvider;
	}
	//</editor-fold>
}