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
import com.foxhorn.foxyserver.web.api.IAuthCredentialsProvider;

/**
 * This handles authentication
 * Created by Matt Van Horn on 11/16/14.
 */
@SuppressWarnings("unused")
public abstract class BaseAuthenticationPathHandler extends BaseFoxyPathHandler {
	public static final int PERMISSION_MANAGE_USER = 10;
	public static final int PERMISSION_MANAGE_ROLE = 11;

	private static final String STATUS_OK = "ok";
	private static final String STATUS_FAIL = "fail";
	private static final String PATH = "/auth/";
	@SuppressWarnings("unused")
	protected static final byte ACTION_INDEX = 0;

	private static final byte ENTITY_NAME_INDEX = 1;
	private static final byte ENTITY_ACTION_INDEX = 2;
	private static final byte ENTITY_ID_INDEX = 3;
	private static final String ENTITY_USER_NAME = "user";
	private static final String ENTITY_ROLE_NAME = "role";

	private static final String ACTION_MANAGE = "manage";

	protected static final String ACTION_CREATE = "create";
	protected static final String ACTION_MODIFY = "modify";
	protected static final String ACTION_REMOVE = "remove";

	@SuppressWarnings("unused")
	protected static final String ACTION_EDIT_ROLE_PERMISSIONS = "role-permissions";
	protected static final String ACTION_LOG_ON = "log-on";
	protected static final String ACTION_LOG_OFF = "log-off";
	protected static final String ACTION_LOCK_USER = "lock-user";
	protected static final String ACTION_UNLOCK_USER = "unlock-user";
	private final IAuthCredentialsProvider authCredentialsProvider;

	@SuppressWarnings("unused")
	protected BaseAuthenticationPathHandler(IAuthCredentialsProvider authCredentialsProvider) {
		super(PATH);
		this.authCredentialsProvider = authCredentialsProvider;
	}

	/**
	 * Used to allow a path handler to set permissions before a onRequest is called
	 *
	 * @param context the the context of the request
	 * @param action  the name of the action requested
	 */
	@Override
	protected void setPermissions(HttpContext context, String action) {
		//noinspection unused
		String entityAction = context.getPathPart(ENTITY_ACTION_INDEX);
		String entityName = context.getPathPart(ENTITY_NAME_INDEX);
		if (action.equals(ACTION_MANAGE)) {
			if (entityName.equals(ENTITY_USER_NAME)) {
				context.getRequest().addPermissionRequirement(PERMISSION_MANAGE_USER);
			} else if (entityName.equals(ENTITY_ROLE_NAME)) {
				context.getRequest().addPermissionRequirement(PERMISSION_MANAGE_ROLE);
			}
		}
	}

	@Override
	public void onRequest(HttpContext context, String action) {

		String entityName = context.getPathPart(ENTITY_NAME_INDEX);
		String entityId;
		if (action.equals(ACTION_MANAGE))
			entityId = context.getPathPart(ENTITY_ID_INDEX);
		else
			entityId = context.getPathPart(ID_INDEX);
		long userId = 0;
		if (!StringUtils.isNullEmptyOrWhiteSpace(entityId)) {
			try {
				userId = Long.valueOf(entityId);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		String entityAction = context.getPathPart(ENTITY_ACTION_INDEX);
		if (StringUtils.isNullEmptyOrWhiteSpace(entityAction))
			entityAction = ACTION_LIST;
		switch (action) {
			case ACTION_MANAGE:
				if (entityName.equals(ENTITY_USER_NAME)) {
					switch (entityAction) {
						case ACTION_LIST:
							onListUser(context);
							break;
						case ACTION_MODIFY:
							onModifyUser(context, userId);
							break;
						case ACTION_CREATE:
							onCreateUser(context);
							break;
						case ACTION_REMOVE:
							onRemoveUser(context, userId);
							break;
					}
				} else if (entityName.equals(ENTITY_ROLE_NAME)) {
					switch (entityAction) {
						case ACTION_LIST:
							onListRole(context);
							break;
						case ACTION_MODIFY:
							onModifyRole(context, userId);
							break;
						case ACTION_CREATE:
							onCreateRole(context);
							break;
						case ACTION_REMOVE:
							onRemoveRole(context, userId);
							break;
					}
				}
				break;
			case ACTION_LOG_ON:
				onLogOn(context);
				break;
			case ACTION_LOG_OFF:
				onLogOff(context);
				break;
			case ACTION_LOCK_USER:
				onLockUser(context, userId);
				break;
			case ACTION_UNLOCK_USER:
				onUnlockUser(context, userId);
				break;
			default:
				onUnknownAction(context);
				break;
		}
	}

	protected abstract void onUnknownAction(HttpContext context);

	private void onLockUser(HttpContext context, long userId) {
		try {
			authCredentialsProvider.lockAuthCredentials(userId);
			context.getResponse().setReplyString(STATUS_OK);
		} catch (Exception e) {
			e.printStackTrace();
			context.getResponse().setReplyString(STATUS_FAIL);
		}
	}

	protected void onLogOff(HttpContext context) {
		context.getHttpAuthHandler().logout(context.getHttpSession());
		onLogOn(context);
	}

	protected void onLogOn(HttpContext context) {
		if (context.getRequest().isGet()) {
			context.getResponse().setReplyFile(getLoginView());
			//TODO handle writting out model data
			//	writeModelData(context.getResponse(), getGsonSerializer().getGson().toJson(new Credentials()));
		} else {
			try {
				// todo get this to work with out a dependency on gson
				/*
				//Credentials credentials = getGsonSerializer().getGson().fromJson(context.getRequest().getRequestBody(), Credentials.class);
				IHttpAuthSession authSession = context.getHttpAuthHandler().login(context, credentials.logonName, credentials.passwordHash);
				if (authSession == null) {
					context.getResponse().setReplyString(STATUS_FAIL);
				} else {
					if (authSession.getAuthContext().getUser().isAuthenticated()) {
						context.setHttpSession(authSession);
						context.getResponse().setReplyString(STATUS_OK);
					} else {
						context.getResponse().setReplyString(STATUS_FAIL);
					}
				}
				*/
			} catch (Exception e) {
				e.printStackTrace();
				context.getResponse().setReplyString(STATUS_FAIL);
			}
		}
	}

	private void onUnlockUser(HttpContext context, long userId) {
		try {
			authCredentialsProvider.unlockAuthCredentials(userId);
			context.getResponse().setReplyString(STATUS_OK);
		} catch (Exception e) {
			e.printStackTrace();
			context.getResponse().setReplyString(STATUS_FAIL);
		}
	}

	protected abstract void onModifyUser(HttpContext context, long userId);

	protected abstract void onRemoveUser(HttpContext context, long userId);

	protected abstract void onCreateUser(HttpContext context);

	protected abstract void onListUser(HttpContext context);


	protected abstract void onModifyRole(HttpContext context, long roleId);

	protected abstract void onRemoveRole(HttpContext context, long roleId);

	protected abstract void onCreateRole(HttpContext context);

	protected abstract void onListRole(HttpContext context);

	/**
	 * @return the path to the login view
	 */
	protected abstract String getLoginView();

	@SuppressWarnings("unused")
	protected IAuthCredentialsProvider getAuthCredentialsProvider() {
		return authCredentialsProvider;
	}

	@SuppressWarnings("unused")
	public class Credentials {
		public String logonName;
		public String password;
		public String passwordHash;
	}
}