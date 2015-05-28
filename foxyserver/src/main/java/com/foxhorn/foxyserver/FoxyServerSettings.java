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

package com.foxhorn.foxyserver;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.foxhorn.foxyserver.text.StringUtils;
import com.foxhorn.foxyserver.web.api.ICryptoHandler;
import com.foxhorn.foxyserver.web.security.crypto.DefaultCryptoHandler;

import java.security.NoSuchAlgorithmException;

/**
 * This is a high level settings interface for servers
 * Note this is by default not thread safe as typically you will only set your settings once then the server will run.
 * you can easily use a read write lock to make it thread safe if you wish
 */
@SuppressWarnings("unused")
public class FoxyServerSettings {
	private static final String BUILT_IN_SALT = "7y8Mg8eMAA8ji8imGksySnhk8jadq6mkS4kaF0Cgsx2xYPLT0FMK8kOQTrkRnr8";
	private static final String HARDWARE_ID_NOT_SET = "UNKNOWN";
	private static final String DEFAULT_EXTERNAL_FILESYSTEM_PATH_FORMAT = "/sdcard/foxy-server/html/%s";
	private static final int SESSION_INACTIVITY_TIMEOUT_SECONDS = 3600;
	private int getSessionInactivityTimeoutSeconds = SESSION_INACTIVITY_TIMEOUT_SECONDS;
	private String appPrivateCryptoSalt = BUILT_IN_SALT;
	private ICryptoHandler cryptoHandler;
	private String deviceHardwareId = HARDWARE_ID_NOT_SET;
	private boolean noTeaForYou;
	private boolean useDeviceFileSystem = false;
	private String deviceFileSystemAppPath = DEFAULT_EXTERNAL_FILESYSTEM_PATH_FORMAT;
	private String defaultLanguage = "en-US";

	private FoxyServerSettings() {
		try {
			cryptoHandler = new DefaultCryptoHandler();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Log.e(Constants.Tag, "Could not use the default crypto handler. This is because the device does not have SHA-256... how odd that is pretty standard!");
		}
	}

	private static FoxyServerSettings instance;

	/**
	 * @return singleton instance
	 */
	public static FoxyServerSettings getInstance() {
		if (instance == null)
			instance = new FoxyServerSettings();
		return instance;
	}

	/**
	 * Used for salting the known inputs to prevent the ability to externally generate auth tokens
	 *
	 * @return the value of the private salt
	 */
	public String getAppPrivateCryptoSalt() {
		if (BUILT_IN_SALT.equals(appPrivateCryptoSalt))
			Log.w(Constants.Tag, "Warning you have not set a different value for the app private salt do so by calling FoxyServerSettings.setAppPrivateCryptoSalt(\"your super salty string here\"); till this is done your app is way less secure");
		return appPrivateCryptoSalt;
	}

	/**
	 * @param appPrivateCryptoSalt the value to salt our auth values (default username and time of session start) with so that the auth token can not be easily guessed
	 */
	public void setAppPrivateCryptoSalt(String appPrivateCryptoSalt) {
		this.appPrivateCryptoSalt = appPrivateCryptoSalt;
	}

	/**
	 * @return the crypto handler in use
	 */
	public ICryptoHandler getCryptoHandler() {
		return cryptoHandler;
	}

	/**
	 * @param cryptoHandler the crypto handler to use
	 */
	public void setCryptoHandler(ICryptoHandler cryptoHandler) {
		this.cryptoHandler = cryptoHandler;
	}

	/**
	 * @return how long to wait before a session is timed out expressed in seconds default is 60 minutes (3600 seconds)
	 */
	public int getGetSessionInactivityTimeoutSeconds() {
		return getSessionInactivityTimeoutSeconds;
	}

	/**
	 * @param getSessionInactivityTimeoutSeconds set how long to wait before a session is timed out expressed in seconds default is 60 minutes (3600 seconds)
	 */
	public void setGetSessionInactivityTimeoutSeconds(int getSessionInactivityTimeoutSeconds) {
		this.getSessionInactivityTimeoutSeconds = getSessionInactivityTimeoutSeconds;
	}

	/**
	 * @return this is the hardware id of the device (
	 */
	public String getDeviceHardwareId() {
		if (StringUtils.areEqual(HARDWARE_ID_NOT_SET, deviceHardwareId))
			Log.w(Constants.Tag, "Hardware id not set add FoxyServerSettings.setDeviceHardwareId(this); to the the activity or service that you are starting the web server from. Make sure to do it before you start the server");
		return deviceHardwareId;
	}

	/**
	 * Used to automatically set the value by reading it from the device via Settings.Secure.ANDROID_ID
	 * See the note on Settings.Secure.ANDROID_ID about how on multi user devices past android 4.2 each user will get a different value for this. if your wondering why different values are showing up and have multiple users (android device users not users in the servers db)
	 *
	 * @param androidContext pass in a android Context object (Service or Activity instance will do nicely)
	 */
	public void setDeviceHardwareId(Context androidContext) {
		this.deviceHardwareId = StringUtils.padLeft(Settings.Secure.getString(androidContext.getContentResolver(), Settings.Secure.ANDROID_ID), 16, '0');
	}

	/**
	 * @return true disables supporting http://tools.ietf.org/html/rfc2324
	 */
	public boolean isNoTeaForYou() {
		return noTeaForYou;
	}

	/**
	 * @param noTeaForYou true will disable /got-tea from supporting status 418 http://tools.ietf.org/html/rfc2324
	 */
	public void setNoTeaForYou(boolean noTeaForYou) {
		this.noTeaForYou = noTeaForYou;
	}

	/**
	 * @return this is the path on the device fs to use as the app root
	 */
	public String getDeviceFileSystemAppPath() {
		return deviceFileSystemAppPath;
	}

	/**
	 * @param deviceFileSystemAppPath this is the path on the device fs to use as the app root. default is /sdcard/foxy-server/html/%s note that this should end with %s as it is used with a string format
	 */
	public void setDeviceFileSystemAppPath(String deviceFileSystemAppPath) {
		this.deviceFileSystemAppPath = deviceFileSystemAppPath;
	}

	/**
	 * @return true means we are not compiling our files into resources embedded in the apk but are reading them from the devices fs
	 */
	public boolean isUsingDeviceFileSystem() {
		return useDeviceFileSystem;
	}

	/**
	 * @param useDeviceFileSystem true means we are not compiling our files into resources embedded in the apk but are reading them from the devices fs
	 */
	public void setUseDeviceFileSystem(boolean useDeviceFileSystem) {
		this.useDeviceFileSystem = useDeviceFileSystem;
	}

	/**
	 * @return the language to return all response in unless other language is explicitly requested
	 */
	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	/**
	 * @param defaultLanguage sets the language to return all response in unless other language is explicitly requested
	 */
	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
}