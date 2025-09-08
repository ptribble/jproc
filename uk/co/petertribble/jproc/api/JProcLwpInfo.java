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
 * An class for representing information on an lwp in a Solaris process,
 * matching the lwpsinfo_t structure.
 *
 * @author Peter Tribble
 */
public class JProcLwpInfo {

    private int pid;
    private int lwpid;
    private long stime;
    private long etime;
    private long ntime;

    /**
     * Populate this object with data. This routine should never be called
     * by clients, and is only for the JNI layer to interface with.
     *
     * @param pid  the process id
     * @param lwpid  the lwp id
     * @param stime  start time
     * @param etime  execution time
     * @param ntime  execution time, nanosecond part
     */
    public void insert(final int pid, final int lwpid,
		       final long stime, final long etime, final long ntime) {
	this.pid = pid;
	this.lwpid = lwpid;
	this.stime = stime;
	this.etime = etime;
	this.ntime = ntime;
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
     * Return the start time of the lwp. This is measured in seconds
     * since the epoch.
     *
     * @return the start time of the lwp
     */
    public long getstime() {
	return stime;
    }

    /**
     * Return the execution time of the process. This is measured in seconds
     * and includes usr+sys cpu time.
     *
     * @return the execution time of the lwp
     */
    public double gettime() {
	return etime + ntime / 1000000000.0;
    }

    /**
     * Generate a JSON representation of this {@code JProcLwpInfo}.
     *
     * @return A String containing a JSON representation of this
     * {@code JProcLwpInfo}.
     */
    public String toJSON() {
	StringBuilder sb = new StringBuilder(64);
	sb.append("{\"pid\":").append(pid)
	    .append(",\"lwpid\":").append(lwpid)
	    .append(",\"stime\":").append(stime)
	    .append(",\"etime\":").append(etime)
	    .append(",\"ntime\":").append(ntime)
	    .append('}');
	return sb.toString();
    }
}
