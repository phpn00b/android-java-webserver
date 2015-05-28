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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is used to handle requests and resperent all the useful data from the request
 * Created by Matt Van Horn on 9/24/14.
 */
public class HttpRequest {

	private static final String HEADER_SPLIT_ON = ":";
	public static final byte REQUEST_VERB_GET = 1;
	public static final byte REQUEST_VERB_POST = 2;
	@SuppressWarnings("unused")
	public static final byte REQUEST_VERB_HEAD = 3;
	@SuppressWarnings("unused")
	public static final byte REQUEST_VERB_PUT = 4;
	@SuppressWarnings("unused")
	public static final byte REQUEST_VERB_DELETE = 5;
	@SuppressWarnings("unused")
	public static final byte REQUEST_VERB_TRACE = 6;
	@SuppressWarnings("unused")
	public static final byte REQUEST_VERB_OPTIONS = 7;
	@SuppressWarnings("unused")
	public static final byte REQUEST_VERB_CONNECT = 8;
	@SuppressWarnings("unused")
	public static final byte REQUEST_VERB_PATCH = 9;
	public static final String REQUEST_VERB_GET_NAME = "GET";
	public static final String REQUEST_VERB_POST_NAME = "POST";
	@SuppressWarnings("unused")
	public static final String REQUEST_VERB_HEAD_NAME = "HEAD";
	@SuppressWarnings("unused")
	public static final String REQUEST_VERB_PUT_NAME = "PUT";
	@SuppressWarnings("unused")
	public static final String REQUEST_VERB_DELETE_NAME = "DELETE";
	@SuppressWarnings("unused")
	public static final String REQUEST_VERB_TRACE_NAME = "TRACE";
	@SuppressWarnings("unused")
	public static final String REQUEST_VERB_OPTIONS_NAME = "OPTIONS";
	@SuppressWarnings("unused")
	public static final String REQUEST_VERB_CONNECT_NAME = "CONNECT";
	@SuppressWarnings("unused")
	public static final String REQUEST_VERB_PATCH_NAME = "PATCH";


	private List<Integer> requiredPermissions;
	private byte requestVerb;
	private String accepts;
	private String userAgent;
	private String requestedLanguage;
	private String remoteHost;
	private String requestedDocument;
	private int contentLength;
	private String requestBody;
	private String authToken;
	private String queryString;
	private Map<String, String> queryParams;
	private final HttpContext httpContext;
	private Map<String, String> cookies;

	public HttpRequest(HttpContext context) {
		httpContext = context;
	}

	/**
	 * This handles dealing with the request and making sense of its various parts
	 */
	@SuppressWarnings("ConstantConditions")
	public void process() throws IOException {
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(httpContext.getClientSocket().getInputStream()));
		String document;
		boolean hasRequestType = false;
		boolean hasRemoteHost = false;
		boolean hasUserAgent = false;
		boolean hasAccepts = false;
		boolean hasLanguage = false;
		boolean hasContentLength = false;
		boolean hasCookies = false;
		String[] parts;
		char[] charBuffer = new char[128];
		int bytesRead;
		StringBuilder stringBuilder = new StringBuilder();
		boolean moreToRead = true;
		while (moreToRead && (bytesRead = inputReader.read(charBuffer)) > 0) {
			stringBuilder.append(charBuffer, 0, bytesRead);
			moreToRead = bytesRead == charBuffer.length;
		}
		String total = stringBuilder.toString();
		String[] requestLines = total.split("\n");
		//	System.err.println(total);
		for (String requestHeaderLine : requestLines) {
			//	System.out.printf(parts1[i]);
			if (StringUtils.isNullEmptyOrWhiteSpace(requestHeaderLine)) {
				break;
			}

			if (!hasRequestType) {
				byte startIndex = 0;
				boolean isRequestTypeAndPath = false;
				if (requestHeaderLine.substring(0, 3).equals(REQUEST_VERB_GET_NAME)) {
					requestVerb = REQUEST_VERB_GET;
					startIndex = 4;
					isRequestTypeAndPath = true;
				} else if (requestHeaderLine.substring(0, 4).equals(REQUEST_VERB_POST_NAME)) {
					requestVerb = REQUEST_VERB_POST;
					startIndex = 5;
					isRequestTypeAndPath = true;
				} else {
					Log.w(Constants.Tag, String.format("Have a request type of '%s' that is not a GET or a POST and is not supported. You can add support for it in the HttpRequest.process method.", requestHeaderLine));
				}
				if (isRequestTypeAndPath) {
					hasRequestType = true;
					int httpIndex = requestHeaderLine.indexOf(" HTTP/");
					document = requestHeaderLine.substring(startIndex, httpIndex);
					requestedDocument = document.replaceAll("[/]+", "/");
					if (requestedDocument.contains("?")) {
						// we have a query string handle it
						parts = requestedDocument.split("\\?");
						requestedDocument = parts[0];
						if (parts.length > 1) {
							queryString = parts[1];
							queryParams = new HashMap<>();
							parts = queryString.split("&");
							for (String queryStringParam : parts) {
								String[] paramParts = queryStringParam.split("=");
								if (paramParts.length == 2)
									queryParams.put(paramParts[0], paramParts[1]);
							}
						}
					}
				}
			} else {
				// we have already gotten the first line
				if (!hasRemoteHost && requestHeaderLine.length() >= 5 && requestHeaderLine.substring(0, 5).equals("Host:")) {
					// this is the remote address
					parts = requestHeaderLine.split(HEADER_SPLIT_ON);
					// 0 = Host | 1 = ip | 2 = port
					remoteHost = parts[1].substring(1);
					hasRemoteHost = true;
				}
				if (!hasAccepts && requestHeaderLine.length() >= 7 && requestHeaderLine.substring(0, 7).equals("Accept:")) {
					accepts = requestHeaderLine.split(HEADER_SPLIT_ON)[1].substring(1);
					hasAccepts = true;
				}
				if (!hasUserAgent && requestHeaderLine.length() >= 11 && requestHeaderLine.substring(0, 11).equals("User-Agent:")) {
					userAgent = requestHeaderLine.split(HEADER_SPLIT_ON)[1];
					hasUserAgent = true;
				}
				if (!hasLanguage && requestHeaderLine.length() >= 16 && requestHeaderLine.substring(0, 16).equals("Accept-Language:")) {
					requestedLanguage = requestHeaderLine.split(HEADER_SPLIT_ON)[1];
					hasLanguage = true;
				}
				if (!hasContentLength && requestHeaderLine.length() >= 15 && requestHeaderLine.substring(0, 15).equals("Content-Length:")) {
					contentLength = Integer.valueOf(requestHeaderLine.split(HEADER_SPLIT_ON)[1].substring(1).trim());
					hasContentLength = true;
				}
				if (!hasCookies && requestHeaderLine.length() >= 7 && requestHeaderLine.substring(0, 7).equals("Cookie:")) {
					Log.v("test", "have cookie");
					hasCookies = true;
					String rawCookie = requestHeaderLine.split(HEADER_SPLIT_ON)[1];
					cookies = CookieUtil.parseCookieString(rawCookie);
					if (cookies.containsKey(httpContext.getHttpAuthHandler().getAuthTokenName())) {
						Log.v("test", "have auth token");
						authToken = cookies.get(httpContext.getHttpAuthHandler().getAuthTokenName());
					}
				}
			}
		}
		if (StringUtils.areEqual(requestedDocument, "/")) {
			requestedDocument = "/index.html";
		}
		if (requestVerb == REQUEST_VERB_POST && contentLength > 0) {
			requestBody = total.substring(total.length() - contentLength).trim();
		}
		//System.err.println("done reading request");
		//	*/
	}

	/**
	 * @return The cookies that came with the request
	 */
	@SuppressWarnings("unused")
	public Map<String, String> getCookies() {
		return cookies;
	}

	/**
	 * @return the document that was requested
	 */
	public String getRequestedDocument() {
		return requestedDocument;
	}

	/**
	 * @return ip address of the remote host
	 */
	@SuppressWarnings("unused")
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * @return language the browser requested
	 */
	@SuppressWarnings("unused")
	public String getRequestedLanguage() {
		return requestedLanguage;
	}

	/**
	 * @return user agent string the browser sent
	 */
	@SuppressWarnings("unused")
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * @return type of response the browsers says it will accept for this request
	 */
	@SuppressWarnings("unused")
	public String getAccepts() {
		return accepts;
	}

	/**
	 * @return the verb used for the request
	 */
	@SuppressWarnings("unused")
	public byte getRequestVerb() {
		return requestVerb;
	}

	/**
	 * @return length in bytes of the request
	 */
	@SuppressWarnings("unused")
	public int getContentLength() {
		return contentLength;
	}

	/**
	 * @return the data in the body of the request
	 */
	@SuppressWarnings("unused")
	public String getRequestBody() {
		return requestBody;
	}

	/**
	 * @return a useful (for debugging) clean readable selection of details about the request
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s performed a %S for %s\r\n", remoteHost, requestVerb == REQUEST_VERB_GET ? REQUEST_VERB_GET_NAME : REQUEST_VERB_POST_NAME, requestedDocument));
		sb.append(String.format("languate: %s\r\n", requestedLanguage));
		sb.append(String.format("user-agent: %s\r\n", userAgent));
		sb.append(String.format("accepts: %s\r\n", accepts));
		if (contentLength > 0) {
			sb.append(String.format("body: %s\r\n", requestBody));
		}
		if (!StringUtils.isNullEmptyOrWhiteSpace(queryString)) {
			sb.append(String.format("query string: %s parsed as:\r\n", queryString));
		}
		if (queryParams != null) {
			for (Map.Entry<String, String> param : queryParams.entrySet()) {
				sb.append(String.format("|__%s: %s\r\n", param.getKey(), param.getValue()));
			}
		}
		return sb.toString();
	}

	/**
	 * @return true if this request used the GET verb
	 */
	@SuppressWarnings("unused")
	public boolean isGet() {
		return requestVerb == REQUEST_VERB_GET;
	}

	/**
	 * @return true if this request used the POST verb
	 */
	@SuppressWarnings("unused")
	public boolean isPost() {
		return requestVerb == REQUEST_VERB_POST;
	}

	/**
	 * @return the auth token for this request
	 */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * Used to add a permissions that is required for this request to process
	 *
	 * @param permissionToAdd the permission to require for the request to complete
	 */
	@SuppressWarnings("unused")
	public void addPermissionRequirement(int permissionToAdd) {
		if (requiredPermissions == null)
			requiredPermissions = new ArrayList<>();
		if (!requiredPermissions.contains(permissionToAdd))
			requiredPermissions.add(permissionToAdd);
	}

	/**
	 * Used to remove a permission from the required permission list for a request to process
	 *
	 * @param permissionToRemove the permission to remove
	 */
	@SuppressWarnings("unused")
	public void removedPermissionRequirement(int permissionToRemove) {
		if (requiredPermissions != null)
			requiredPermissions.remove((Integer) permissionToRemove);
	}

	/**
	 * @return list of permissions that are required to perform this request
	 */
	@SuppressWarnings("unused")
	public List<Integer> getRequiredPermissions() {
		return requiredPermissions;
	}
}