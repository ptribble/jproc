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
 * An class for representing the status of a Solaris process, matching the
 * pstatus_t structure.
 *
 * @author Peter Tribble
 */
public class JProcStatus {

    private int pid;
    private long utime;
    private long nutime;
    private long stime;
    private long nstime;
    private long cutime;
    private long ncutime;
    private long cstime;
    private long ncstime;

    /**
     * Populate this object with data. This routine should never be called
     * by clients, and is only for the JNI layer to interface with.
     *
     * @param pid  the process id
     * @param utime  process user cpu time
     * @param nutime  process user cpu time, nanosecond part
     * @param stime  process sys cpu time
     * @param nstime  process sys cpu time, nanosecond part
     * @param cutime  sum of child user time
     * @param ncutime  sum of child user time, nanosecond part
     * @param cstime  sum of child sys time
     * @param ncstime  sum of child sys time, nanosecond part
     */
    public void insert(final int pid,
		       final long utime, final long nutime,
		       final long stime, final long nstime,
		       final long cutime, final long ncutime,
		       final long cstime, final long ncstime) {
	this.pid = pid;
	this.utime = utime;
	this.nutime = nutime;
	this.stime = stime;
	this.nstime = nstime;
	this.cutime = cutime;
	this.ncutime = ncutime;
	this.cstime = cstime;
	this.ncstime = ncstime;
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
     * Return the execution time of the process. This is measured in seconds
     * and includes user cpu time.
     *
     * @return the user time consumed by this process
     */
    public double getutime() {
	return utime + nutime / 1000000000.0;
    }

    /**
     * Return the execution time of the process. This is measured in seconds
     * and includes system cpu time.
     *
     * @return the system time consumed by this process
     */
    public double getstime() {
	return stime + nstime / 1000000000.0;
    }

    /**
     * Return the sum of the execution time of the process children. This is
     * measured in seconds and includes user cpu time.
     *
     * @return the user time of this processes children
     */
    public double getcutime() {
	return cutime + ncutime / 1000000000.0;
    }

    /**
     * Return the sum of the execution time of the process children. This is
     * measured in seconds and includes system cpu time.
     *
     * @return the system time of this processes children
     */
    public double getcstime() {
	return cstime + ncstime / 1000000000.0;
    }

    /**
     * Generate a JSON representation of this {@code JProcStatus}.
     *
     * @return A String containing a JSON representation of this
     * {@code JProcStatus}.
     */
    public String toJSON() {
	StringBuilder sb = new StringBuilder(128);
	sb.append("{\"pid\":").append(pid)
	    .append(",\"utime\":").append(utime)
	    .append(",\"nutime\":").append(nutime)
	    .append(",\"stime\":").append(stime)
	    .append(",\"nstime\":").append(nstime)
	    .append(",\"cutime\":").append(cutime)
	    .append(",\"ncutime\":").append(ncutime)
	    .append(",\"cstime\":").append(cstime)
	    .append(",\"ncstime\":").append(ncstime)
	    .append('}');
	return sb.toString();
    }
}
