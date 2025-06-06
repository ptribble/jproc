/*
 * SPDX-License-Identifier: CDDL-1.0
 *
 * CDDL HEADER START
 *
 * This file and its contents are supplied under the terms of the
 * Common Development and Distribution License ("CDDL"), version 1.0.
 * You may only use this file in accordance with the terms of version
 * 1.0 of the CDDL.
 *
 * A full copy of the text of the CDDL should have accompanied this
 * source. A copy of the CDDL is also available via the Internet at
 * http://www.illumos.org/license/CDDL.
 *
 * CDDL HEADER END
 *
 * Copyright 2025 Peter Tribble
 *
 */

package uk.co.petertribble.jproc.client;

import java.io.IOException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.HttpClients;

/**
 * A class providing access to a remote JProc server via REST.
 *
 * @author Peter Tribble
 */
public class JPhttpClient {

    private String baseURL;
    private HttpClient httpclient;

    /**
     * Create a JProc client that uses REST to communicate with a HTTP server.
     *
     * @param pcc holds the configuration with details of how to contact the
     * server
     */
    public JPhttpClient(PClientConfig pcc) {
	baseURL = pcc.remoteURL();
	if (!baseURL.endsWith("/")) {
	    baseURL = baseURL + "/";
	}
	httpclient = HttpClients.createDefault();
    }

    /**
     * Execute the given method on a remote JProc server.
     *
     * @param method the name of the method to execute
     *
     * @return the result of the remote method execution
     *
     * @throws IOException if there was a problem communicating with the server
     */
    public String execute(String method) throws IOException {
	return doGet(method);
    }

    /**
     * Execute the given method on a remote JProc server.
     *
     * @param method the name of the method to execute
     * @param args an array of parameters to pass as arguments to the
     * method call
     *
     * @return the result of the remote method execution
     *
     * @throws IOException if there was a problem communicating with the server
     */
    public String execute(String method, String[] args) throws IOException {
	StringBuilder sb = new StringBuilder();
	sb.append(method);
	for (String s : args) {
	    sb.append('/').append(s);
	}
	return doGet(sb.toString());
    }

    private String doGet(String request) throws IOException {
	return httpclient.execute(new HttpGet(baseURL + request),
					new BasicHttpClientResponseHandler());
    }
}
