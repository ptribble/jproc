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
 * Copyright 2026 Peter Tribble
 *
 */

package uk.co.petertribble.jproc.api;

/**
 * A class for representing a Solaris lwp.
 *
 * @author Peter Tribble
 */
public final class JLwp {

    private final int pid;
    private final int lwpid;

    /**
     * Create a new JLwp object, representing a Solaris lwp.
     *
     * @param npid the process id of the containing process.
     * @param nlwpid the lwpid of the lwp.
     */
    public JLwp(final int npid, final int nlwpid) {
	pid = npid;
	lwpid = nlwpid;
    }

    /**
     * Return the pid of this process.
     *
     * @return the pid of the process containing this JLwp.
     */
    public int getPid() {
	return pid;
    }

    /**
     * Return the lwpid of this lwp.
     *
     * @return the lwpid of this JLwp.
     */
    public int getLWPid() {
	return lwpid;
    }

    /**
     * Generate a JSON representation of this {@code JLwp}.
     *
     * @return A String containing a JSON representation of this
     * {@code JLwp}.
     */
    public String toJSON() {
	StringBuilder sb = new StringBuilder(28);
	sb.append("{\"pid\":").append(pid)
	    .append(",\"lwpid\":").append(lwpid)
	    .append('}');
	return sb.toString();
    }

    /**
     * Returns whether the requested Object is equal to this JLwp. Equality
     * implies that the Object is a JLwp and has the same pid and lwpid.
     *
     * @param o The object to be tested for equality.
     *
     * @return true if the object is a {@code JLwp} with the same pid
     * and lwpid as this {@code JLwp}.
     */
    @Override
    public boolean equals(final Object o) {
	if (o instanceof JLwp) {
	    JLwp jlwp = (JLwp) o;
	    return pid == jlwp.getPid() && lwpid == jlwp.getLWPid();
        }
        return false;
    }

    /*
     * We need to override hashCode() to be consistent with equals().
     * The characteristics of JLwp are that most processes have similar pids,
     * most processes only have a few (or 1) lwps, and that we're primarily
     * interested in differentiating lwps from the same process. Consistency
     * with equals requires that we only use pid and lwpid.
     */
    @Override
    public int hashCode() {
	return lwpid + 31 * pid;
    }
}
