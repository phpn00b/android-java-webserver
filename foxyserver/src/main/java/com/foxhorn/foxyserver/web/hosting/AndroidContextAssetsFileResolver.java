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

package com.foxhorn.foxyserver.web.hosting;

import android.content.Context;
import android.util.Log;

import com.foxhorn.foxyserver.Constants;
import com.foxhorn.foxyserver.FoxyServerSettings;
import com.foxhorn.foxyserver.web.api.IFileResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is a file resolver that finds files on the assets off the android context
 * Please be aware that this does not yet support getting files based on languages
 * Created by Matt Van Horn on 9/27/14.
 */
public class AndroidContextAssetsFileResolver implements IFileResolver {
	private static final String ASSET_FORMAT = "Checking for asset with uri:(your application)/src/main/assets/htdocs/%s";
	private static final String FS_FORMAT = "Checking for file with uri:%s";
	private static final String compiledUrl = "htdocs";
	private final Context myContext;
	private final boolean useExternalFiles;
	private final String externalPathFormat;

	/**
	 * Creates our file resolver
	 *
	 * @param androidContext the android application context
	 */
	public AndroidContextAssetsFileResolver(Context androidContext) {
		myContext = androidContext;
		useExternalFiles = FoxyServerSettings.getInstance().isUsingDeviceFileSystem();
		externalPathFormat = useExternalFiles ? FoxyServerSettings.getInstance().getDeviceFileSystemAppPath() : null;
	}

	/**
	 * Used to check if a file exists
	 *
	 * @param file     the file with path info to check for
	 * @param language the language of the request
	 * @return true if the file is there false if not
	 */
	@Override
	public boolean exists(String file, String language) {
		String path = !useExternalFiles ? String.format("%s%s", compiledUrl, file) : String.format(externalPathFormat, file);
		Log.v(Constants.Tag, String.format(useExternalFiles ? FS_FORMAT : ASSET_FORMAT, path));
		return !useExternalFiles || new File(path).exists();

	}

	/**
	 * Called to get a input stream for the file that you want to get
	 *
	 * @param file     the file to request
	 * @param language the language of the request
	 * @return the input stream or null
	 * @throws IOException
	 */
	@Override
	public InputStream getFileStreamFor(String file, String language) throws IOException {
		if (!useExternalFiles) {
			String fullFile = String.format("%s%s", compiledUrl, file);
			return myContext.getAssets().open(fullFile);
		}
		return new FileInputStream(new File(String.format(externalPathFormat, file)));
	}
}