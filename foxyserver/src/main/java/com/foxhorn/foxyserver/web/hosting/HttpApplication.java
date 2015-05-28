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
import com.foxhorn.foxyserver.web.HttpContext;
import com.foxhorn.foxyserver.web.api.IFileResolver;
import com.foxhorn.foxyserver.web.api.IHttpAuthHandler;
import com.foxhorn.foxyserver.web.hosting.handlers.BaseFoxyPathHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is where we have registered path handlers. This is
 * Created by Matt Van Horn on 9/27/14.
 */
public class HttpApplication implements Runnable {

	private final BaseFoxyPathHandler[] activeHandlers;
	private final ExecutorService executorService = Executors.newCachedThreadPool();
	private final int serverPort;
	private final IFileResolver fileResolver;
	private final int handlerCount;
	private final IHttpAuthHandler httpAuthHandler;
	private final boolean useExternalFiles;
	private ServerSocket listener = null;
	private Thread connectionInitializationThread;
	private boolean keepRunning = false;

	/**
	 * Creates our application server
	 *
	 * @param port            the port to run on
	 * @param androidContext  the android application context
	 * @param requestHandlers the path handlers to use
	 * @param fileResolver    the logic to resolve files for the server
	 * @param httpAuthHandler the logic to handle authentication
	 */
	public HttpApplication(int port, Context androidContext, BaseFoxyPathHandler[] requestHandlers, IFileResolver fileResolver, IHttpAuthHandler httpAuthHandler) {
		activeHandlers = requestHandlers;
		this.httpAuthHandler = httpAuthHandler;
		serverPort = port;
		this.fileResolver = fileResolver;
		handlerCount = activeHandlers.length;
		FoxyServerSettings.getInstance().setDeviceHardwareId(androidContext);
		useExternalFiles = FoxyServerSettings.getInstance().isUsingDeviceFileSystem();
	}

	@SuppressWarnings("unused")
	public void start() {
		connectionInitializationThread = new Thread(this);
		keepRunning = true;
		connectionInitializationThread.start();
	}

	@SuppressWarnings("unused")
	public void stop() throws InterruptedException, IOException {
		keepRunning = false;
		listener.close();
		connectionInitializationThread.join();
	}

	@Override
	public void run() {
		try {
			listener = new ServerSocket(serverPort);
		} catch (IOException e) {
			Log.e(Constants.Tag, String.format("Failed to init server for port: %s", serverPort), e);
			keepRunning = false;
			return;
		}
		Log.i(Constants.Tag, String.format("foxy http server up on port: %s", serverPort));
		while (keepRunning) {
			try {
				Socket clientSocket = listener.accept();
				HttpContext httpContext = new HttpContext(clientSocket, this, httpAuthHandler);
				executorService.submit(httpContext);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Used to get the path handler for a given path
	 *
	 * @param requestedPath the path requested
	 * @return the handler or null
	 */
	public BaseFoxyPathHandler findHandlerForPath(String requestedPath) {
		for (int i = 0; i < handlerCount; i++) {
			if (requestedPath.startsWith(activeHandlers[i].getPathHandled())) {
				return activeHandlers[i];
			}
		}
		return null;
	}

	public IFileResolver getFileResolver() {
		return fileResolver;
	}

	@SuppressWarnings("unused")
	public boolean isUsingExternalFiles() {
		return useExternalFiles;
	}
}