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

import com.foxhorn.foxyserver.web.HttpContext;

/**
 * This is the structure for a Http Auth handler
 * Created by Matt Van Horn  on 11/14/14.
 */
public interface IHttpAuthHandler {

	/**
	 * Used to check if a given request should be allowed
	 *
	 * @param httpContext the context that needs to be checked for auth
	 * @return true if it should be allowed false if not
	 */
	boolean shouldAllowRequest(HttpContext httpContext);

	/**
	 * Used to login a user
	 *
	 * @param httpContext the context
	 * @param username    the username
	 * @param password    the password
	 * @return a user or null
	 */
	@SuppressWarnings("unused")
	IHttpAuthSession login(HttpContext httpContext, String username, String password);

	/**
	 * Used to find a HttpAuthContext for a given token
	 *
	 * @param authToken
	 * @return returns a auth session for a given auth token (auth token is some means of identifying a distinct session ie cookie or shared query param for all request)
	 */
	IHttpAuthSession getSessionForAuthToken(String authToken);

	/**
	 * This creates a guest session
	 *
	 * @param httpContext the http context of the request to create a guest session for
	 * @return the auth context to use for the new guest session
	 */
	IHttpAuthSession createGuestAuthSession(HttpContext httpContext);

	/**
	 * the name of the cookie to use for auth token storage retrival
	 *
	 * @return the name of the auth token cookie
	 */
	String getAuthTokenName();

	/**
	 * should we use an auth cookie
	 *
	 * @return true will force putting in an auth cookie
	 */
	boolean enableAuthCookie();

	/**
	 * Used to log-out a given IHttpAuthSession
	 *
	 * @param httpAuthSession the session to terminate
	 */
	void logout(IHttpAuthSession httpAuthSession);

	/**
	 * @return the instance of the IAuthCredentialsProvider being used this is what is used to verify usename / password
	 */
	@SuppressWarnings("unused")
	IAuthCredentialsProvider getAuthCredentialsProvider();
}