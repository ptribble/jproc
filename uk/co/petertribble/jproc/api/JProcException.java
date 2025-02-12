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

package uk.co.petertribble.jproc.api;

/**
 * A JProc exception, so we can identify whether it's one of ours or thrown by
 * java itself.
 *
 * @author Peter Tribble
 */
public class JProcException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new JProcException, with no message text.
     */
    public JProcException() {
	super();
    }

    /**
     * Create a new JProcException, with the given detail message.
     *
     * @param s  the detail message
     */
    public JProcException(String s) {
	super(s);
    }
    /**
     * Construct a new JProcException with the given detail message and cause.
     *
     * @param s  the detail message
     * @param cause  the underlying cause for this JProcException
     */
    public JProcException(String s, Throwable cause) {
	super(s, cause);
    }
}
