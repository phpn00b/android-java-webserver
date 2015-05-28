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

import com.foxhorn.foxyserver.Constants;
import com.foxhorn.foxyserver.text.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This handles interaction with our responses
 * Created by Matt Van Horn on 9/24/14.
 */
public class HttpResponse {

	public static final int HTTP_STATUS_OK = 200;
	public static final int HTTP_STATUS_MOVED_PERMANENTLY = 301;
	public static final int HTTP_STATUS_FOUND = 302;
	public static final int HTTP_STATUS_SEE_OTHER = 303;
	public static final int HTTP_STATUS_TEMPORY_REDIREC = 307;

	@SuppressWarnings("unused")
	public static final int HTTP_STATUS_BAD_REQUEST = 400;
	@SuppressWarnings("unused")
	public static final int HTTP_STATUS_UNAUTHORIZED = 401;
	@SuppressWarnings("unused")
	public static final int HTTP_STATUS_FORBIDDEN = 403;
	@SuppressWarnings("unused")
	public static final int HTTP_STATUS_NOT_FOUND = 404;
	@SuppressWarnings("unused")
	public static final int HTTP_STATUS_METHOD_NOT_ALLOWED = 405;
	@SuppressWarnings("unused")
	public static final int HTTP_STATUS_LENGTH_REQUIRED = 411;
	@SuppressWarnings("unused")
	public static final int HTTP_STATUS_TEAPOT = 418;
	public static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;
	public static final int HTTP_STATUS_NOT_IMPLEMENTED = 401;


	private static final boolean DEBUG_AUTH = true;
	private static final String HEADER_Location = "Location";

	private Map<String, String> headers;
	private int httpStatus;
	private static final String MIME_JSON = "application/json";
	private String contentType;
	private String body;
	private boolean bodyIsFilePath;
	private byte[] rawReply;
	private int contentLength;
	private final HttpContext httpContext;
	private boolean wasProcessed;
	private byte[] extraDataForReply;
	private String authToken;

	/**
	 * Creates our response object for the context that will process it
	 *
	 * @param httpContext the context that this response will process under
	 */
	public HttpResponse(HttpContext httpContext) {
		this.httpContext = httpContext;
		headers = new HashMap<>();
		headers.put("Server", "Foxy Android Webserver/0.9");
		headers.put("Connection", "close");
	}

	/**
	 * This will try to set the content type based on the file extension
	 */
	private void setContentTypeFromExtension() {
		int index = body.lastIndexOf(".");
		if (index == -1) {
			contentType = "text/html";
			return;
		}

		String extension = body.substring(index + 1);
		Log.v(Constants.Tag, String.format("Requesting file %s with extension of %s", body, extension));
		switch (extension) {
			case "js":
				contentType = "text/javascript";
				break;
			case "css":
				contentType = "text/css";
				break;
			case "png":
				contentType = "image/png";
				break;
			case "svg":
				contentType = "image/svg+xml";
				break;
			case "gif":
				contentType = "image/gif";
				break;
			case "jpg":
			case "jpeg":
				contentType = "image/jpeg";
				break;
			case "woff":
				contentType = "application/font-woff";
				break;
			case "html":
				contentType = "text/html";
				break;
			case "map":
				contentType = "application/json";
				break;
			case "ttf":
				contentType = "application/x-font-ttf";
				break;
			case "otf":
				contentType = "application/x-font-opentype";
				break;
			case "eot":
				contentType = "application/vnd.ms-fontobject";
				break;
			default:
				contentType = "text/plain";
				break;
		}
	}

	/**
	 * This runs through the state for the response and does what it should to get the client the data you wantl
	 */
	public void process() {
		if (wasProcessed)
			return;
		wasProcessed = true;
		// if no status code has been set yet default to status code 200 ie all good
		if (httpStatus == 0) {
			httpStatus = HTTP_STATUS_OK;
		}
		if (rawReply != null) {
			// this will be a binary response
			if (StringUtils.isNullEmptyOrWhiteSpace(contentType)) {
				contentType = "raw";
			}
			try {
				OutputStream outputStream = httpContext.getClientSocket().getOutputStream();
				contentLength = rawReply.length;
				outputStream.write(getHeaders().getBytes());
				outputStream.write(rawReply);
				outputStream.flush();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (bodyIsFilePath) { // body is a file path
			// we will write a file to the output stream
			if (StringUtils.isNullEmptyOrWhiteSpace(contentType)) {
				setContentTypeFromExtension();
			}
			BufferedInputStream fileInputStream;
			// verify that the file we are looking for exists
			if (httpContext.getHttpApplication().getFileResolver().exists(body, httpContext.getResponseLanguage())) {
				try {
					fileInputStream = new BufferedInputStream(httpContext.getHttpApplication().getFileResolver().getFileStreamFor(body, httpContext.getRequest().getRequestedLanguage()));
					// our file resolver was able to find the file all good
				} catch (IOException e) {
					// had getting the file stream
					e.printStackTrace();
					send404();
					return;
				}
			} else {
				// file did not exists
				send404();
				return;
			}

			//contentLength = fileInputStream.available()
			ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
			byte[] buf = new byte[2048];
			int count;
			try {
				while ((count = fileInputStream.read(buf)) != -1) {
					tempOut.write(buf, 0, count);
				}
				tempOut.flush();
				OutputStream outputStream = httpContext.getClientSocket().getOutputStream();
				contentLength = tempOut.size() + (extraDataForReply != null ? extraDataForReply.length : 0);
				outputStream.write(getHeaders().getBytes());
				outputStream.write(tempOut.toByteArray());
				if (extraDataForReply != null)
					outputStream.write(extraDataForReply);
				outputStream.flush();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (body != null) { // body is not a file path
			// we will send the string as the reply body
			try {
				OutputStream outputStream = httpContext.getClientSocket().getOutputStream();
				contentLength = body.length();
				outputStream.write(getHeaders().getBytes());
				outputStream.write(body.getBytes());
				outputStream.flush();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			OutputStream outputStream = httpContext.getClientSocket().getOutputStream();
			contentLength = 0;
			outputStream.write(getHeaders().getBytes());
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This reconfigures the response to send the 404 data
	 */
	public void send404() {
		Log.d(Constants.Tag, String.format("going to 404 for body: %s", body));
		body = "not found";
		contentType = "text/plain";
		httpStatus = HTTP_STATUS_NOT_FOUND;
		bodyIsFilePath = false;
		rawReply = null;
		try {
			OutputStream outputStream = httpContext.getClientSocket().getOutputStream();
			contentLength = body.length();
			outputStream.write(getHeaders().getBytes());
			outputStream.write(body.getBytes());
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param path the path to a file that will be returned as the response
	 */
	public void setReplyFile(String path) {
		body = path;
		bodyIsFilePath = true;
	}

	/**
	 * @param data a string that will be returned as the reply
	 */
	public void setReplyString(String data) {
		bodyIsFilePath = false;
		body = data;
	}

	/**
	 * @param data allows directly setting the reply in binary
	 */
	@SuppressWarnings("unused")
	public void setBinaryReply(byte[] data) {
		rawReply = data;
	}

	/**
	 * Convience method to set the mime type to JSON cause I do that a lot
	 */
	@SuppressWarnings("unused")
	public void setMimeTypeJson() {
		contentType = MIME_JSON;
	}

	/**
	 * Used to add a header key value pair
	 *
	 * @param key   header key
	 * @param value header value
	 */
	public void setHeader(String key, String value) {
		if (headers == null) {
			headers = new HashMap<>();
		}
		headers.put(key, value);
	}

	private String getHeaders() {
		authToken = httpContext.getHttpSession().getAuthToken();
		headers.put("Content-Length", String.valueOf(contentLength));
		headers.put("Content-Type", contentType);
		if (httpContext.getHttpAuthHandler().enableAuthCookie()) {
			headers.put("Set-Cookie", String.format("%s=%s; path=/; HttpOnly", httpContext.getHttpAuthHandler().getAuthTokenName(), authToken));
		}
		if (DEBUG_AUTH) {
			headers.put("X-DEBUG-AuthUser-UserId", String.valueOf(httpContext.getHttpSession().getAuthContext().getUser().getUserId()));
			headers.put("X-DEBUG-AuthUser-Name", httpContext.getHttpSession().getAuthContext().getUsername());
		}
		StringBuilder sb = new StringBuilder();
		switch (httpStatus) {
			case HTTP_STATUS_MOVED_PERMANENTLY:
				sb.append(String.format("HTTP/1.1 %s Moved Permanently\n", httpStatus));
				break;
			case HTTP_STATUS_FOUND:
				sb.append(String.format("HTTP/1.1 %s Found\n", httpStatus));
				break;
			case HTTP_STATUS_SEE_OTHER:
				sb.append(String.format("HTTP/1.1 %s See Other\n", httpStatus));
				break;
			case HTTP_STATUS_TEMPORY_REDIREC:
				sb.append(String.format("HTTP/1.1 %s Temporary Redirect\n", httpStatus));
				break;
			case HTTP_STATUS_INTERNAL_SERVER_ERROR:
				sb.append(String.format("HTTP/1.1 %s Internal Server Error\n", httpStatus));
				break;
			case HTTP_STATUS_NOT_IMPLEMENTED:
				sb.append(String.format("HTTP/1.1 %s Not Implemented\n", httpStatus));
				break;
			default:
				sb.append(String.format("HTTP/1.1 %s\n", httpStatus));
				break;
		}

		for (Map.Entry<String, String> pair : headers.entrySet()) {
			if (pair.getKey().equals("Set-Cookie")) {
				sb.append(String.format("Set-Cookie: %s=; expires=Tue, 12-Oct-1999 06:00:00 GMT; path=/; HttpOnly\n", httpContext.getHttpAuthHandler().getAuthTokenName()));
			}
			sb.append(String.format("%s: %s\n", pair.getKey(), pair.getValue()));
		}
		sb.append("\n");
		return sb.toString();
	}

	/**
	 * @return our content type to return
	 */
	@SuppressWarnings("unused")
	public String getContentType() {
		return contentType;
	}

	/**
	 * Allows manually setting the reply content type
	 *
	 * @param contentType the content type to return
	 */
	@SuppressWarnings("unused")
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Extra data apart from the main return
	 *
	 * @return extra data
	 */
	@SuppressWarnings("unused")
	public byte[] getExtraDataForReply() {
		return extraDataForReply;
	}

	/**
	 * This is useful for appending data to the end of a request
	 *
	 * @param extraDataForReply sets extra data to send after the primary reply
	 */
	@SuppressWarnings("unused")
	public void setExtraDataForReply(byte[] extraDataForReply) {
		this.extraDataForReply = extraDataForReply;
	}

	/**
	 * @param httpStatusCode allows us to set the http response code
	 */
	public void setHttpStatus(int httpStatusCode) {
		httpStatus = httpStatusCode;
	}

	/**
	 * @return the auth token for this response
	 */
	@SuppressWarnings("unused")
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * Used to send a redirect
	 *
	 * @param location the location to redirect the request to
	 */
	@SuppressWarnings("unused")
	public void setRedirect(String location) {
		httpStatus = HTTP_STATUS_FOUND;
		setHeader(HEADER_Location, location);
	}
}