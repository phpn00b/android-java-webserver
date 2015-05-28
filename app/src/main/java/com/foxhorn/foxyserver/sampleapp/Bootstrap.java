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

package com.foxhorn.foxyserver.sampleapp;

import android.content.Context;

import com.foxhorn.foxyserver.sampleapp.auth.ExampleGuestOnlyAuthHandler;
import com.foxhorn.foxyserver.web.api.IFileResolver;
import com.foxhorn.foxyserver.web.api.IHttpAuthHandler;
import com.foxhorn.foxyserver.web.hosting.AndroidContextAssetsFileResolver;
import com.foxhorn.foxyserver.web.hosting.HttpApplication;

import java.io.IOException;

/**
 * This is what will get our server up and going
 * Created by Matt Van Horn on 5/27/15.
 */
public class Bootstrap {

	private HttpApplication httpApplication;
	private final IHttpAuthHandler authHandler;
	private IFileResolver fileResolver;

	public Bootstrap() {
		authHandler = new ExampleGuestOnlyAuthHandler();
	}

	public void start(Context androidContext) {
		fileResolver = new AndroidContextAssetsFileResolver(androidContext);
		httpApplication = new HttpApplication(9321, androidContext, null, fileResolver, authHandler);
		httpApplication.start();
	}

	public void stop() throws IOException, InterruptedException {
		httpApplication.stop();
	}


}
