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

import java.io.IOException;
import java.io.InputStream;

/**
 * This defines the structure of a file resolver which is how the server will get file streams for files that requests ask for
 * Created by Matt Van Horn on 9/27/14.
 */
public interface IFileResolver {

	/**
	 * Used to check if a file exists
	 *
	 * @param file     the file with path info to check for
	 * @param language the language of the request
	 * @return true if the file is there false if not
	 */
	boolean exists(String file, String language);

	/**
	 * Called to get a input stream for the file that you want to get
	 *
	 * @param file     the file to request
	 * @param language the language of the request
	 * @return the input stream or null
	 * @throws IOException
	 */
	InputStream getFileStreamFor(String file, String language) throws IOException;
}
