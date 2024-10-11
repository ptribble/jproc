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

package uk.co.petertribble.jproc.demo;

import uk.co.petertribble.jproc.api.*;
import uk.co.petertribble.jproc.client.*;

/**
 * An example of the use of the JProc api to give a graphical display of the
 * process tree.
 *
 * @author Peter Tribble
 */
public class JPTree extends JPdemo {

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
