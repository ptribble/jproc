/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
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
	    throw new JProcException("Malformed URL.");
	}
	initClient(config);
    }

    /**
     * Create a JProc client that communicates with a server using the XML-RPC
     * protocol.
     *
     * @param config  Holds the configuration with details of how to contact
     * the server.
     */
    public JProcClient(XmlRpcClientConfigImpl config) {
	initClient(config);
    }

    private void initClient(XmlRpcClientConfigImpl config) {
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
