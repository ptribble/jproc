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

package uk.co.petertribble.jproc.server;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import java.io.IOException;
import java.io.File;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

/**
 * A simple jproc server using the xml-rpc WebServer class. Taken straight
 * from the Apache XML-RPC examples.
 *
 * @author Peter Tribble
 */
public class PServer1 {

    // global so can be called at shutdown
    private JmDNS jmdns;

    /**
     * Constructs a PServer1 object.
     *
     * @param psc the configuration to be applied
     */
    public PServer1(PServerConfig psc) {
	try {
	    WebServer webServer = new WebServer(psc.getPort());
	    XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
	    PropertyHandlerMapping phm = new PropertyHandlerMapping();
	    phm.load(Thread.currentThread().getContextClassLoader(),
                   "properties/PServer1.properties");
	    xmlRpcServer.setHandlerMapping(phm);
	    XmlRpcServerConfigImpl serverConfig =
		(XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
	    serverConfig.setContentLengthOptional(false);

	    webServer.start();
	    if (psc.shouldRegister()) {
		registerService(psc);
	    }
	} catch (Exception e) {
	    System.err.println("Server failed to start!");
	}
    }

    /*
     * Register this server in mdns, with the type "_jproc._tcp"
     */
    private void registerService(PServerConfig psc) {
	try {
	    jmdns = JmDNS.create(psc.getInetAddress());
	    ServiceInfo serviceInfo = ServiceInfo.create("_jproc._tcp.local.",
		    "JProc/" + psc.getHostname(),
		    psc.getPort(),
		    "path=/");
            jmdns.registerService(serviceInfo);
	    Thread exitHook = new Thread(() -> this.unRegisterService());
	    Runtime.getRuntime().addShutdownHook(exitHook);
	    System.out.println("Service registered on " + psc.getInetAddress());
	} catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /*
     * Called as a shutdown hook.
     */
    private void unRegisterService() {
	jmdns.unregisterAllServices();
    }

    private static void usage() {
	System.err.println("Usage: server [-m] [-p port | -f config_file]");
	System.exit(1);
    }

    /**
     * Start the server. A -p argument specifies a listener port, default
     * 8080. A -f argument specifies a configuration file. A -m argument
     * causes the server to be registered in mdns.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
	PServerConfig psc = new PServerConfig();
	int i = 0;
	while (i < args.length) {
	    if ("-m".equals(args[i])) {
		psc.setRegister(true);
	    } else if ("-p".equals(args[i])) {
		if (i + 1 < args.length) {
		    i++;
		    try {
			psc.setPort(Integer.parseInt(args[i]));
		    } catch (NumberFormatException nfe) {
			usage();
		    }
		} else {
		    usage();
		}
	    } else if ("-f".equals(args[i])) {
		if (i + 1 < args.length) {
		    i++;
		    File f = new File(args[i]);
		    if (f.exists()) {
			psc.parseConfig(f);
		    } else {
			usage();
		    }
		} else {
		    usage();
		}
	    } else {
		usage();
	    }
	    i++;
	}
	new PServer1(psc);
    }
}
