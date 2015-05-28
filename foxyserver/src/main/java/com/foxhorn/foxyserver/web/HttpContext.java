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

import android.util.Log;

import com.foxhorn.foxyserver.FoxyServerSettings;
import com.foxhorn.foxyserver.text.StringUtils;
import com.foxhorn.foxyserver.web.api.IHttpAuthHandler;
import com.foxhorn.foxyserver.web.api.IHttpAuthSession;
import com.foxhorn.foxyserver.web.hosting.HttpApplication;
import com.foxhorn.foxyserver.web.hosting.handlers.BaseFoxyPathHandler;

import java.net.Socket;

/**
 * This is encapsulation for a single HTTP request
 * Created by Matt Van Horn on 9/27/14.
 */
public class HttpContext implements Runnable {

	private static final String Tag = "foxy-http-server";

	private final HttpRequest request;
	private final HttpResponse response;
	private final Socket clientSocket;
	private final HttpApplication httpApplication;

	private String handlerLocalPath;
	private String[] handlerLocalPathValues;
	private final IHttpAuthHandler httpAuthHandler;
	private IHttpAuthSession httpSession;

	public HttpContext(Socket socket, HttpApplication application, IHttpAuthHandler httpAuthHandler) {
		clientSocket = socket;
		this.httpAuthHandler = httpAuthHandler;
		request = new HttpRequest(this);
		response = new HttpResponse(this);
		this.httpApplication = application;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public HttpRequest getRequest() {
		return request;
	}

	Socket getClientSocket() {
		return clientSocket;
	}

	public HttpApplication getHttpApplication() {
		return httpApplication;
	}

	/**
	 * This is the part of the path that is local to this handler... ie offset from pathHandled
	 *
	 * @return the local path
	 */
	@SuppressWarnings("unused")
	public String getLocalPath() {
		return handlerLocalPath;
	}

	/**
	 * Used to get a path part locally for this handler
	 *
	 * @param pathPart the index of the path part
	 * @return the value or null
	 * for example if the full request is /example/widget/update/2 and the path handled for this handler is /example/widget/ than calling this will work as follows
	 * getPathPart(0) returns update
	 * getPathPart(1) returns 1
	 */
	public String getPathPart(int pathPart) {
		if (handlerLocalPathValues != null && pathPart < handlerLocalPathValues.length)
			return handlerLocalPathValues[pathPart];
		return null;
	}

	@Override
	public void run() {
		try {
			request.process();
			//	Log.i(Tag, request.toString());
			String authToken = request.getAuthToken();
			//	Log.v(Tag, String.format("using request token:%s", authToken));
			boolean needsSessionStart = false;
			if (!StringUtils.isNullEmptyOrWhiteSpace(authToken)) {
				httpSession = httpAuthHandler.getSessionForAuthToken(authToken);
				if (httpSession == null)
					needsSessionStart = true;
			} else {
				needsSessionStart = true;
			}
			if (needsSessionStart) {
				httpSession = httpAuthHandler.createGuestAuthSession(this);
			}

			BaseFoxyPathHandler handler = httpApplication.findHandlerForPath(request.getRequestedDocument());
			if (handler != null) {
				// let the handler handle it
				Log.d(Tag, String.format("Using custom request processor {%s} for path: %s", handler.getClass().getName(), request.getRequestedDocument()));

				handlerLocalPath = request.getRequestedDocument().substring(handler.getPathHandled().length());
				handlerLocalPathValues = handlerLocalPath.split(BaseFoxyPathHandler.PATH_PART_SEPARATOR);
				try {
					handler.handleRequest(this);
				} catch (Exception appError) {
					appError.printStackTrace();
					response.setReplyString(appError.toString());
					response.setHttpStatus(500);
				}
			} else {
				// lets treat this as a normal file request
				Log.d(Tag, "Using default request handler");
				response.setReplyFile(request.getRequestedDocument());
			}
			response.process();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * Used for standard crud controllers to easily get the requested entity id
	 *
	 * @return the id or -1  = error
	 */
	@SuppressWarnings("unused")
	public long getRequestedEntityId() {
		String idStr = getPathPart(1);
		if (StringUtils.isNullEmptyOrWhiteSpace(idStr))
			return -1;
		try {
			return Long.valueOf(idStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public IHttpAuthHandler getHttpAuthHandler() {
		return httpAuthHandler;
	}

	@SuppressWarnings("unused")
	public void setHttpSession(IHttpAuthSession httpAuthSession) {
		httpSession = httpAuthSession;
	}

	public IHttpAuthSession getHttpSession() {
		return httpSession;
	}


	/**
	 * @return gets the language to use to respond to requests with
	 * todo: make sure that this parses in a good way to just be the base language so if en-US comes in we just use en for working with translations and resource files easier¬
	 */
	public String getResponseLanguage() {
		//TODO: make this allow users to set the response language
		if (StringUtils.isNullEmptyOrWhiteSpace(request.getRequestedLanguage()))
			return FoxyServerSettings.getInstance().getDefaultLanguage();
		return request.getRequestedLanguage();
	}
}