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
public final class JProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The pid of this process.
     */
    private int pid;
    /**
     * The underlying JProcInfo for this process.
     */
    private JProcInfo info;

    /**
     * Create a new JProcess object, representing a process.
     *
     * @param npid the process id of the process.
     * @param jpinfo a JprocInfo object with basic information about the
     * process.
     */
    public JProcess(final int npid, final JProcInfo jpinfo) {
	pid = npid;
	info = jpinfo;
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
     * @param jpinfo a new JProcInfo object containing updated information
     * about this process.
     */
    public void updateInfo(final JProcInfo jpinfo) {
	info = jpinfo;
    }

    /**
     * Return Information on this process. This uses cached information, to
     * avoid native code lookups, and is designed for filtering purposes where
     * data like ids are reasonably static.
     *
     * @return a JProcInfo object containing information about this process.
     */
    public JProcInfo getCachedInfo() {
	return info;
    }

    /**
     * Returns whether the requested Object is equal to this JProcess. Equality
     * implies that the Object is a JProcess and has the same pid.
     *
     * @param o the object to be tested for equality.
     *
     * @return true if the object is a {@code JProcess} with the same pid
     * as this {@code JProcess}.
     */
    @Override
    public boolean equals(final Object o) {
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
