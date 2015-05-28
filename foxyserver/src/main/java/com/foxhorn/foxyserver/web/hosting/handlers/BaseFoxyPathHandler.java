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

package com.foxhorn.foxyserver.web.hosting.handlers;

import com.foxhorn.foxyserver.text.StringUtils;
import com.foxhorn.foxyserver.web.HttpContext;
import com.foxhorn.foxyserver.web.HttpResponse;

/**
 * This is the base class that handles url request paths for our simple little web server
 * Created by Matt Van Horn on 9/26/14.
 */
public abstract class BaseFoxyPathHandler {

	protected static final String ACTION_LIST = "list";
	protected static final byte ACTION_INDEX = 0;
	protected static final byte ID_INDEX = 1;

	public static final String PATH_PART_SEPARATOR = "/";

	private final String pathHandled;

	/**
	 * ctor
	 *
	 * @param pathHandled the path that a request must start with to get to this path handler
	 */
	protected BaseFoxyPathHandler(String pathHandled) {
		this.pathHandled = pathHandled;
	}

	/**
	 * This is used to handle a given request
	 *
	 * @param context the context of the request
	 */
	public void handleRequest(HttpContext context) {
		String action = context.getPathPart(ACTION_INDEX);
		if (StringUtils.isNullEmptyOrWhiteSpace(action))
			action = ACTION_LIST;
		// invoke the logic that sets needed permissions
		setPermissions(context, action);
		if (verifyPermissions(context))
			onRequest(context, action);
	}

	/**
	 * This is called to process a request on classes that inherit from this
	 *
	 * @param context the context of the request
	 * @param action  the name of the action requested
	 */
	public abstract void onRequest(HttpContext context, String action);

	/**
	 * This is the path that will be routed to this handler
	 *
	 * @return
	 */
	public String getPathHandled() {
		return pathHandled;
	}

	protected void handlePermissionDenied(HttpContext httpContext, String message) {
		httpContext.getResponse().setHttpStatus(HttpResponse.HTTP_STATUS_FORBIDDEN);
		httpContext.getResponse().setReplyString(
				String.format(
						"Failed request %s due to lack ofpermissions.\n%s",
						httpContext.getRequest().getRequestedDocument(),
						message
				)
		);
	}

	/**
	 * Used to allow a path handler to set permissions before a onRequest is called
	 *
	 * @param context the the context of the request
	 * @param action  the name of the action requested
	 */
	protected abstract void setPermissions(HttpContext context, String action);

	/**
	 * Used to verify that a given action should be allowed to run
	 *
	 * @param context the the context of the request
	 * @return true will allow the request false will block it
	 */
	protected boolean verifyPermissions(HttpContext context) {
		if (!context.getHttpAuthHandler().shouldAllowRequest(context)) {
			handlePermissionDenied(context, "Missing permissions");
			return false;
		}
		return true;
	}
}