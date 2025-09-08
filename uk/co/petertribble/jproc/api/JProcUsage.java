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
 * An class for representing usage of a Solaris process, matching the
 * prusage_t structure.
 *
 * @author Peter Tribble
 */
public class JProcUsage {

    private int lwpid;
    private int count;
    private long rtime;
    private long nrtime;
    private long utime;
    private long nutime;
    private long stime;
    private long nstime;
    private long minf;
    private long majf;
    private long nswap;
    private long inblk;
    private long oublk;
    private long msnd;
    private long mrcv;
    private long sigs;
    private long vctx;
    private long ictx;
    private long sysc;
    private long ioch;

    /**
     * Populate this object with data. This routine should never be called
     * by clients, and is only for the JNI layer to interface with.
     *
     * @param lwpid   lwp id.  0: process or defunc
     * @param count   number of contributing lwp
     * @param rtime   total lwp real (elapsed) time - seconds
     * @param nrtime  total lwp real (elapsed) time - nanoseconds
     * @param utime   user level cpu time - seconds
     * @param nutime  user level cpu time - nanoseconds
     * @param stime   system call cpu time - seconds
     * @param nstime  system call cpu time - nanoseconds
     * @param minf    minor page faults
     * @param majf    major page faults
     * @param nswap   swaps
     * @param inblk   input blocks
     * @param oublk   output blocks
     * @param msnd    messages sent
     * @param mrcv    messages received
     * @param sigs    signals received
     * @param vctx    voluntary context switches
     * @param ictx    involuntary context switches
     * @param sysc    system calls
     * @param ioch    chars read and written
     */
    public void insert(final int lwpid, final int count,
		       final long rtime, final long nrtime,
		       final long utime, final long nutime,
		       final long stime, final long nstime,
		       final long minf, final long majf,
		       final long nswap, final long inblk,
		       final long oublk, final long msnd, final long mrcv,
		       final long sigs, final long vctx, final long ictx,
		       final long sysc, final long ioch) {
	this.lwpid = lwpid;
	this.count = count;
	this.rtime = rtime;
	this.nrtime = nrtime;
	this.utime = utime;
	this.nutime = nutime;
	this.stime = stime;
	this.nstime = nstime;
	this.minf = minf;
	this.majf = majf;
	this.nswap = nswap;
	this.inblk = inblk;
	this.oublk = oublk;
	this.msnd = msnd;
	this.mrcv = mrcv;
	this.sigs = sigs;
	this.vctx = vctx;
	this.ictx = ictx;
	this.sysc = sysc;
	this.ioch = ioch;
    }

    /**
     * Return the number of contributing lwp.
     *
     * @return the elapsed time of this process or lwp
     */
    public int getcount() {
	return count;
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
     * Return the total elapsed time of this process or lwp.
     *
     * @return the elapsed time of this process or lwp
     */
    public double getrtime() {
	return rtime + nrtime / 1000000000.0;
    }

    /**
     * Return the total user time of this process or lwp.
     *
     * @return the user time of this process or lwp
     */
    public double getutime() {
	return utime + nutime / 1000000000.0;
    }

    /**
     * Return the total system time of this process or lwp.
     *
     * @return the system time of this process or lwp
     */
    public double getstime() {
	return stime + nstime / 1000000000.0;
    }

    /**
     * Return the number of minor faults incurred by this process or lwp.
     *
     * @return the number of minor faults incurred by this process or lwp
     */
    public long getminf() {
	return minf;
    }

    /**
     * Return the number of major faults incurred by this process or lwp.
     *
     * @return the number of major faults incurred by this process or lwp
     */
    public long getmajf() {
	return majf;
    }

    /**
     * Return the number of system calls incurred by this process or lwp.
     *
     * @return the number of system calls incurred by this process or lwp
     */
    public long getsysc() {
	return sysc;
    }

    /**
     * Return the number of swaps incurred by this process or lwp.
     *
     * @return the number of swaps incurred by this process or lwp
     */
    public long getnswap() {
	return nswap;
    }

    /**
     * Return the number of input blocks incurred by this process or lwp.
     *
     * @return the number of input blocks incurred by this process or lwp
     */
    public long getinblk() {
	return inblk;
    }

    /**
     * Return the number of output blocks incurred by this process or lwp.
     *
     * @return the number of output blocks incurred by this process or lwp
     */
    public long getoublk() {
	return oublk;
    }

    /**
     * Return the number of messages sent by this process or lwp.
     *
     * @return the number of messages sent by this process or lwp
     */
    public long getmsnd() {
	return msnd;
    }

    /**
     * Return the number of messages received by this process or lwp.
     *
     * @return the number of messages received by this process or lwp
     */
    public long getmrcv() {
	return mrcv;
    }

    /**
     * Return the number of signals received by this process or lwp.
     *
     * @return the number of signals received by this process or lwp
     */
    public long getsigs() {
	return sigs;
    }

    /**
     * Return the number of voluntary context switches incurred by this
     * process or lwp.
     *
     * @return the number of voluntary context switches incurred by this
     * process or lwp
     */
    public long getvctx() {
	return vctx;
    }

    /**
     * Return the number of involuntary context switches incurred by this
     * process or lwp.
     *
     * @return the number of involuntary context switches incurred by this
     * process or lwp
     */
    public long getictx() {
	return ictx;
    }

    /**
     * Return the number of chars read and written by this
     * process or lwp.
     *
     * @return the number of chars read and written by this
     * process or lwp
     */
    public long getioch() {
	return ioch;
    }

    /**
     * Generate a JSON representation of this {@code JProcUsage}.
     *
     * @return A String containing a JSON representation of this
     * {@code JProcUsage}.
     */
    public String toJSON() {
	StringBuilder sb = new StringBuilder(256);
	sb.append("{\"lwpid\":").append(lwpid)
	    .append(",\"count\":").append(count)
	    .append(",\"rtime\":").append(rtime)
	    .append(",\"nrtime\":").append(nrtime)
	    .append(",\"utime\":").append(utime)
	    .append(",\"nutime\":").append(nutime)
	    .append(",\"stime\":").append(stime)
	    .append(",\"nstime\":").append(nstime)
	    .append(",\"minf\":").append(minf)
	    .append(",\"majf\":").append(majf)
	    .append(",\"nswap\":").append(nswap)
	    .append(",\"inblk\":").append(inblk)
	    .append(",\"oublk\":").append(oublk)
	    .append(",\"msnd\":").append(msnd)
	    .append(",\"mrcv\":").append(mrcv)
	    .append(",\"sigs\":").append(sigs)
	    .append(",\"vctx\":").append(vctx)
	    .append(",\"ictx\":").append(ictx)
	    .append(",\"sysc\":").append(sysc)
	    .append(",\"ioch\":").append(ioch)
	    .append('}');
	return sb.toString();
    }
}
