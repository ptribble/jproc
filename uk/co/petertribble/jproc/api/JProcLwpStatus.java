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
 * An class for representing the status of an lwp in a Solaris process,
 * matching the lwpstatus_t structure.
 *
 * @author Peter Tribble
 */
public class JProcLwpStatus {

    private int pid;
    private int lwpid;
    private long utime;
    private long nutime;
    private long stime;
    private long nstime;

    /**
     * Populate this object with data. This routine should never be called
     * by clients, and is only for the JNI layer to interface with.
     *
     * @param ipid the process id
     * @param ilwpid the lwp id
     * @param iutime process user cpu time
     * @param inutime process user cpu time, nanosecond part
     * @param istime process sys cpu time
     * @param instime process sys cpu time, nanosecond part
     */
    public void insert(final int ipid, final int ilwpid,
		       final long iutime, final long inutime,
		       final long istime, final long instime) {
	pid = ipid;
	lwpid = ilwpid;
	utime = iutime;
	nutime = inutime;
	stime = istime;
	nstime = instime;
    }

    /**
     * Return the process id.
     *
     * @return the process id
     */
    public int getpid() {
	return pid;
    }

    /**
     * Return the lwp id.
     *
     * @return the lwp id
     */
    public int getlwpid() {
	return lwpid;
    }

    /**
     * Return the execution time of this lwp. This is measured in seconds
     * and includes user cpu time.
     *
     * @return the user time of this lwp
     */
    public double getutime() {
	return utime + nutime / 1000000000.0;
    }

    /**
     * Return the execution time of this lwp. This is measured in seconds
     * and includes system cpu time.
     *
     * @return the system time of this lwp
     */
    public double getstime() {
	return stime + nstime / 1000000000.0;
    }

    /**
     * Generate a JSON representation of this {@code JProcLwpStatus}.
     *
     * @return A String containing a JSON representation of this
     * {@code JProcLwpStatus}.
     */
    public String toJSON() {
	StringBuilder sb = new StringBuilder(80);
	sb.append("{\"pid\":").append(pid)
	    .append(",\"lwpid\":").append(lwpid)
	    .append(",\"utime\":").append(utime)
	    .append(",\"nutime\":").append(nutime)
	    .append(",\"stime\":").append(stime)
	    .append(",\"nstime\":").append(nstime)
	    .append('}');
	return sb.toString();
    }
}
