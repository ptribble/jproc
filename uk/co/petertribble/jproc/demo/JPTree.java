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

package uk.co.petertribble.jproc.demo;

import uk.co.petertribble.jproc.api.*;
import uk.co.petertribble.jproc.client.PClientConfig;

/**
 * An example of the use of the JProc api to give a graphical display of the
 * process tree.
 *
 * @author Peter Tribble
 */
public final class JPTree extends JPdemo {

    private static final long serialVersionUID = 1L;

    private JPTreePanel jpt;

    private static final String VERSION = "JPTree version 1.0";

    /**
     * Run the JPTree demo, printing a tree of all processes.
     */
    public JPTree() {
	this(new JProc(), true);
    }

    /**
     * Run the JPTree demo, printing a tree of all processes.
     *
     * @param pcc a PClientConfig containing client configuration
     */
    public JPTree(PClientConfig pcc) {
	this(new JProc(pcc), true);
    }

    /**
     * Run the JPTree demo, printing a tree of all processes.
     *
     * @param jproc a JProc object
     * @param standalone whether this is a standalone application
     */
    public JPTree(JProc jproc, boolean standalone) {
	super("JPTree", standalone);

        jpt = new JPTreePanel(jproc, new JProcessFilter(new JProcessSet(jproc)),
			DEFAULT_INTERVAL);

	setContentPane(jpt);

	addInfoPanel(jpt, VERSION);

	setSize(720, 560);
	validate();
	setVisible(true);
    }

    @Override
    public void stopLoop() {
	jpt.stopLoop();
    }

    @Override
    public void setDelay(int i) {
	jpt.setDelay(i);
	setLabelDelay(i);
    }

    /**
     * Invoke the JPTree demo from the command line.
     *
     * @param args  Command line arguments
     */
    public static void main(String[] args) {
	if (args.length == 0) {
	    new JPTree();
	} else if (args.length == 2) {
	    if ("-s".equals(args[0])) {
		new JPTree(
		    new PClientConfig(args[1], PClientConfig.CLIENT_XMLRPC));
	    } else if ("-S".equals(args[0])) {
		new JPTree(
		    new PClientConfig(args[1], PClientConfig.CLIENT_REST));
	    } else {
		System.err.println("Usage: jptree [-s|-S server]");
	    }
	} else {
	    System.err.println("Usage: jptree [-s|-S server]");
	}
    }
}
