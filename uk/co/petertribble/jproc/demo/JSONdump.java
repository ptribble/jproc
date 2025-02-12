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

/**
 * Dump out the process list in JSON format.
 *
 * @author Peter Tribble
 */
public final class JSONdump {

    private JSONdump() {
    }

    /**
     * Create the application.
     *
     * @param args Command line arguments, ignored.
     */
    public static void main(String[] args) {
	boolean firstentry = true;
	JProc jproc = new JProc();
	System.out.println("[");
	for (JProcess jp : jproc.getProcesses()) {
	    if (firstentry) {
		firstentry = false;
	    } else {
		System.out.println(",");
	    }
	    System.out.println(jproc.getInfo(jp).toJSON());
	}
	System.out.println("]");
    }
}
