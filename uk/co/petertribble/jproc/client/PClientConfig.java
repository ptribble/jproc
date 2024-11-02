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

import uk.co.petertribble.jumble.JumbleUtils;
import java.io.File;
import java.util.Map;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * A class to hold the configuration details a JProc client needs to
 * connect to a JProc server.
 *
 * @author Peter Tribble
 */
public class PClientConfig {

    /**
     * Represents an XML-RPC client/server configuration.
     */
    public static final int CLIENT_XMLRPC = 0;

    /**
     * Represents a RESTful client/server configuration.
     */
    public static final int CLIENT_REST = 1;

    /*
     * The entries in the array below MUST match the available protocols above,
     * as PClientDialog uses it as an array, and the index then refers back
     * to the selected protocol.
     */

    /**
     * An array of the names of available client-server protocols.
     */
    public static final String[] PROTOCOLS = { "XML-RPC", "REST" };

    private String url_string;
    private String username;
    private String userpass;
    private int protocol;

    /**
     * Create an empty PClientConfig.
     */
    public PClientConfig() {
    }

    /**
     * Create a PClientConfig that is configured to connect to a given url.
     *
     * @param s  the textual url of the server to connect to
     */
    public PClientConfig(String s) {
	this(s, CLIENT_XMLRPC);
    }

    /**
     * Create a PClientConfig that is configured to connect to a given url.
     *
     * @param s  the textual url of the server to connect to
     * @param protocol  specifies the communication protocol
     */
    public PClientConfig(String s, int protocol) {
	url_string = s;
	this.protocol = protocol;
    }

    /**
     * Create a PClientConfig that reads its configuration from a file.
     * The configuration file contains key-value pairs, separated by an
     * = sign, one pair per line.
     *
     * Valid configuration keys:
     * URL the url to connect to
     * User a username to use for authentication
     * Pass a password to use for authentication
     * Protocol an integer representing the communication protocol
     *
     * @param f  the File to read the configuration from.
     */
    public PClientConfig(File f) {
	if (f.exists()) {
	    Map<String, String> m = JumbleUtils.fileToPropMap(f);
	    url_string = m.get("URL");
	    username = m.get("User");
	    userpass = m.get("Pass");
	    String sproto = m.get("Protocol");
	    if (sproto != null) {
		protocol = Integer.parseInt(sproto);
	    }
	}
    }

    /**
     * Returns whether this PClientConfig has enough configuration to be
     * useful.
     *
     * @return whether this PClientConfig has enough configuration to be
     * useful.
     */
    public boolean isConfigured() {
	return url_string != null && !"".equals(url_string);
    }

    /**
     * Return the url to connect to, as a String. If the url has been
     * explicitly passed, use that, else try and get it from the configuration
     * file.
     *
     * @return the url to connect to, as a String
     */
    public String remoteURL() {
	return url_string;
    }

    /**
     * Set the user to connect as.
     *
     * @see #getUser
     *
     * @param username  the username to connect as.
     */
    public void setUser(String username) {
	this.username = username;
    }

    /**
     * Get the user to connect as.
     *
     * @see #setUser
     *
     * @return  the username to connect as.
     */
    public String getUser() {
	return username;
    }

    /**
     * Set the password to use for authentication.
     *
     * @see #getPass
     *
     * @param userpass  the password to use for authentication.
     */
    public void setPass(String userpass) {
	this.userpass = userpass;
    }

    /**
     * Get the password to use for authentication.
     *
     * @see #setPass
     *
     * @return  the password to use for authentication.
     */
    public String getPass() {
	return userpass;
    }

    /**
     * Set the server to connect to, expressed as a String.
     *
     * @see #getServerURL
     *
     * @param s  the server to connect to.
     */
    public void setServerURL(String s) {
	url_string = s;
    }

    /**
     * Set the communication protocol.
     *
     * @see #getProtocol
     *
     * @param protocol  an integer representing the communication protocol
     */
    public void setProtocol(int protocol) {
	this.protocol = protocol;
    }

    /**
     * Get the communication protocol.
     *
     * @see #setProtocol
     *
     * @return an integer representing the communication protocol
     */
    public int getProtocol() {
	return protocol;
    }

    /**
     * Get the server to connect to.
     *
     * @see #setServerURL
     *
     * @throws MalformedURLException if the URL is invalid
     *
     * @return  the URL of the server to connect to.
     */
    public URL getServerURL() throws MalformedURLException {
	return new URL(remoteURL());
    }
}
