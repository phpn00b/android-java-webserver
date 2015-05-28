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

import java.util.List;

/**
 * This is the structure that handles long term storage and access of credentials for login
 * Created by Matt Van Horn on 11/16/14.
 */
@SuppressWarnings("unused")
public interface IAuthCredentialsProvider {

	/**
	 * Used to get credentials by the username and password hash
	 *
	 * @param credentials the credentials
	 * @return null if can't bet found or the instance fully populated if it can be found
	 */
	IAuthCredentials fetchByLoginCredentials(IAuthCredentials credentials);

	/**
	 * This is used to create a new guest account
	 * Guests will all have a userId of 0 passing in 0 to getPermissionsForUser will return the permissions to assign to guests
	 *
	 * @return credentials for the guest
	 */
	IAuthCredentials createNewGuestCredentials();

	/**
	 * Used to unlock a given users credentials
	 *
	 * @param userId the id of the user to unlock
	 */
	void unlockAuthCredentials(long userId);

	/**
	 * Used to create or update login credentials
	 *
	 * @param credentials the credentials to create or update
	 * @return true if successful false if not
	 */
	boolean createUpdateLoginCredentials(IAuthCredentials credentials);

	/**
	 * Used to lock out credentials to prevent them from being used
	 *
	 * @param userId the id of the user to lock out
	 */
	void lockAuthCredentials(long userId);

	/**
	 * Used to get the permissions that a given uses has
	 * This is not returned with the IAuthCredentials to allow for further abstraction later if needed
	 *
	 * @param userId the id of the user to get permissions for
	 * @return the list of permissions for the user
	 */
	List<Integer> getPermissionsForUser(long userId);

	/**
	 * Used to list all the users
	 *
	 * @return a list of users
	 */
	List<IAuthCredentials> fetchUsers();

	/**
	 * Used to get a user by its id
	 *
	 * @param userId the id of the user
	 * @return the user or null
	 */
	IAuthCredentials fetchUserById(long userId);

	/**
	 * Used to fetch all roles
	 *
	 * @return a list of roles
	 */
	List<IAuthRole> fetchRoles();

	/**
	 * Used to get a role by its id
	 *
	 * @param roleId the id of the role
	 * @return the role or null
	 */
	IAuthRole fetchRoleById(long roleId);

	/**
	 * Used to create or update a new role
	 *
	 * @param role the role to create or update
	 * @return true if successful
	 */
	boolean createUpdateLoginRoles(IAuthRole role);

	/**
	 * Used to add a role to a user
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 */
	void addRoleToUser(long userId, long roleId);

	/**
	 * Used to remove a role from a user
	 *
	 * @param userId the id of the user
	 * @param roleId the id of the role
	 */
	void removeRoleFromUser(long userId, long roleId);

	/**
	 * Used to change a given users password
	 *
	 * @param userId                the user id
	 * @param currentPasswordHash   the current password
	 * @param newPasswordHash       the password that should be changed to
	 * @param forceOldPasswordMatch should we require the current password to be correct before we make the change
	 * @return true if the password was changed false if not
	 */
	boolean changeUserPasswordHash(long userId, String currentPasswordHash, String newPasswordHash, boolean forceOldPasswordMatch);
}