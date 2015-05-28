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

import com.foxhorn.foxyserver.web.HttpContext;

/**
 * This class is a starting point for making path handlers that perform basic crud activities
 * Created by Matt Van Horn on 9/26/14.
 */
@SuppressWarnings("unused")
public abstract class BaseCRUDPathHandler extends BaseFoxyPathHandler {

	protected static final String ACTION_CREATE = "create";
	protected static final String ACTION_MODIFY = "modify";
	protected static final String ACTION_VIEW = "view";
	protected static final String ACTION_REMOVE = "remove";

	@SuppressWarnings("unused")
	protected BaseCRUDPathHandler(String pathHandled) {
		super(pathHandled);
	}

	@Override
	public void onRequest(HttpContext context, String action) {
		switch (action) {
			case ACTION_LIST:
				onList(context);
				break;
			case ACTION_VIEW:
				onView(context);
				break;
			case ACTION_CREATE:
				onCreate(context);
				break;
			case ACTION_MODIFY:
				onModify(context);
				break;
			case ACTION_REMOVE:
				onRemove(context);
				break;
			default:
				onNonCRUDAction(context);
				break;
		}
	}

	/**
	 * this is invoked when we have a list request
	 *
	 * @param context the http context
	 */
	protected abstract void onList(HttpContext context);

	/**
	 * this is invoked when we have a create request
	 *
	 * @param context the http context
	 */
	protected abstract void onCreate(HttpContext context);

	/**
	 * this is invoked when we have a modify request
	 *
	 * @param context the http context
	 */
	protected abstract void onModify(HttpContext context);

	/**
	 * this is invoked when we have a remove request
	 *
	 * @param context the http context
	 */
	protected abstract void onRemove(HttpContext context);

	/**
	 * this is invoked when we have a view request
	 *
	 * @param context the http context
	 */
	protected abstract void onView(HttpContext context);

	/**
	 * this is invoked when we have a list request
	 *
	 * @param context the http context
	 */
	protected void onNonCRUDAction(HttpContext context) {
		context.getResponse().send404();
	}
}