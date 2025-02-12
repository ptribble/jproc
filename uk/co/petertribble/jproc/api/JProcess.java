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

import java.io.Serializable;

/**
 * A class for representing a Solaris process.
 *
 * @author Peter Tribble
 */
public class JProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    private int pid;
    private JProcInfo info;

    /**
     * Create a new JProcess object, representing a Solaris process.
     *
     * @param pid  The process id of the process.
     * @param info A JprocInfo object with basic information about the process.
     */
    public JProcess(int pid, JProcInfo info) {
	this.pid = pid;
	this.info = info;
    }

    /**
     * Return the pid of this process.
     *
     * @return the pid of the process represented by this JProcess.
     */
    public int getPid() {
	return pid;
    }

    /**
     * Update the Information on this process.
     *
     * @param info A new JProcInfo object containing updated information
     * about this process.
     */
    public void updateInfo(JProcInfo info) {
	this.info = info;
    }

    /**
     * Return Information on this process. This uses cached information, to
     * avoid native code lookups, and is designed for filtering purposes where
     * data like ids are reasonably static.
     *
     * @return A JProcInfo object containing information about this process.
     */
    public JProcInfo getCachedInfo() {
	return info;
    }

    /**
     * Returns whether the requested Object is equal to this JProcess. Equality
     * implies that the Object is a JProcess and has the same pid.
     *
     * @param o The object to be tested for equality.
     *
     * @return true if the object is a {@code JProcess} with the same pid
     * as this {@code JProcess}.
     */
    @Override
    public boolean equals(Object o) {
	if (o instanceof JProcess) {
	    JProcess jp = (JProcess) o;
	    return pid == jp.getPid();
        }
        return false;
    }

    @Override
    public int hashCode() {
	return pid;
    }
}
