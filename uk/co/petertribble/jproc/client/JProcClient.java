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

import uk.co.petertribble.jproc.api.JProcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.XmlRpcException;
import java.net.MalformedURLException;

/**
 * A class providing access to a remote JProc server over XML-RPC.
 *
 * @author Peter Tribble
 */
public class JProcClient {

    private XmlRpcClient client;

    /**
     * Create a JProc client that communicates with a server using the XML-RPC
     * protocol.
     *
     * @param pcc  Holds the configuration with details of how to contact the
     * server.
     */
    public JProcClient(PClientConfig pcc) {
	XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	try {
	    config.setServerURL(pcc.getServerURL());
	} catch (MalformedURLException mue) {
	    throw new JProcException("Malformed URL.", mue);
	}
	config.setEnabledForExtensions(true);
	client = new XmlRpcClient();
	client.setConfig(config);
    }

    /**
     * Execute the given method on a remote JProc server.
     *
     * @param method  The name of the method to execute.
     *
     * @return  The result of the remote method execution. The type depends
     * on the method called, and is defined by the server, although the XML-RPC
     * layer changes the return types.
     *
     * @throws  XmlRpcException An exception, passed up from XML-RPC if the
     * remote procedure call failed.
     */
    public Object execute(String method) throws XmlRpcException {
	return client.execute("JProcServer." + method, new Object[0]);
    }

    /**
     * Execute the given method on a remote JProc server.
     *
     * @param method  The name of the method to execute.
     * @param args  An array of parameters to pass as arguments to the
     *  method call.
     *
     * @return  The result of the remote method execution. The type depends
     * on the method called, and is defined by the server, although the XML-RPC
     * layer changes the return types.
     *
     * @throws  XmlRpcException An exception, passed up from XML-RPC if the
     * remote procedure call failed.
     */
    public Object execute(String method, Object[] args) throws XmlRpcException {
	return client.execute("JProcServer." + method, args);
    }
}
