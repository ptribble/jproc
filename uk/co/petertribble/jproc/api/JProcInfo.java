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
 * A class for representing information on a Solaris process, matching the
 * psinfo_t structure.
 *
 * @author Peter Tribble
 */
public class JProcInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The number of active lwps of this process.
     */
    private int prNlwp;
    /**
     * The process id of this process.
     */
    private int prPid;
    /**
     * The parent process id of this process.
     */
    private int prPpid;
    /**
     * The uid of this process.
     */
    private int prUid;
    /**
     * The effective uid of this process.
     */
    private int prEuid;
    /**
     * The gid of this process.
     */
    private int prGid;
    /**
     * The effective gid of this process.
     */
    private int prEgid;
    /**
     * The size of this process.
     */
    private long prSize;
    /**
     * The resident set size of this process.
     */
    private long prRssize;
    /**
     * The start time of this process.
     */
    private long stime;
    /**
     * The cpu used by this process (seconds part).
     */
    private long etime;
    /**
     * The cpu used by this process (nanoseconds part).
     */
    private long ntime;
    /**
     * The cpu used by this process and children (seconds part).
     */
    private long ectime;
    /**
     * The cpu used by this process and children (nanoseconds part).
     */
    private long nctime;
    /**
     * The task id of this process.
     */
    private int prTaskid;
    /**
     * The project of this process.
     */
    private int prProjid;
    /**
     * The zone id of this process.
     */
    private int prZoneid;
    /**
     * The contract id of this process.
     */
    private int prContract;
    /**
     * The name of file executed for this process.
     */
    private String prFname;

    /**
     * Populate this object with data. This routine should never be called
     * by clients, and is only for the JNI layer to interface with.
     *
     * FIXME pr_pgid pr_sid
     *
     * @param prPid  the process id
     * @param prPpid  process id of parent
     * @param prUid  real user id
     * @param prEuid  effective user id
     * @param prGid  real group id
     * @param prEgid  effective group id
     * @param prNlwp  number of active lwps in the process
     * @param prSize  size of process image in Kbytes
     * @param prRssize  resident set size in Kbytes
     * @param stime  start time
     * @param etime  execution time
     * @param ntime  execution time, nanosecond part
     * @param ectime  reaped children execution time
     * @param nctime  reaped children execution time, nanosecond part
     * @param prTaskid  task id
     * @param prProjid  project id
     * @param prZoneid  zone id
     * @param prContract  process contract
     * @param prFname  name of execed file
     */
    public void insert(int prPid, int prPpid, int prUid, int prEuid,
			int prGid, int prEgid, int prNlwp,
			long prSize, long prRssize, long stime,
			long etime, long ntime, long ectime, long nctime,
			int prTaskid, int prProjid, int prZoneid,
			int prContract, String prFname) {
	this.prPid = prPid;
	this.prPpid = prPpid;
	this.prUid = prUid;
	this.prEuid = prEuid;
	this.prGid = prGid;
	this.prEgid = prEgid;
	this.prNlwp = prNlwp;
	this.prSize = prSize;
	this.prRssize = prRssize;
	this.stime = stime;
	this.etime = etime;
	this.ntime = ntime;
	this.ectime = ectime;
	this.nctime = nctime;
	this.prTaskid = prTaskid;
	this.prProjid = prProjid;
	this.prZoneid = prZoneid;
	this.prContract = prContract;
	this.prFname = prFname;
    }

    /*
     * Accessors.
     */

    /**
     * Return the process id.
     *
     * @return the process id
     */
    public int getpid() {
	return prPid;
    }

    /**
     * Return the parent process id.
     *
     * @return the parent process id
     */
    public int getppid() {
	return prPpid;
    }

    /**
     * Return the real userid.
     *
     * @return the real userid
     */
    public int getuid() {
	return prUid;
    }

    /**
     * Return the effective userid.
     *
     * @return the effective userid
     */
    public int geteuid() {
	return prEuid;
    }

    /**
     * Return the real group id.
     *
     * @return the real group id
     */
    public int getgid() {
	return prGid;
    }

    /**
     * Return the effective group id.
     *
     * @return the effective group id
     */
    public int getegid() {
	return prEgid;
    }

    /**
     * Return the number of lwps in the process.
     *
     * @return the number of lwps in the process
     */
    public int getnlwp() {
	return prNlwp;
    }

    /**
     * Return the process size in Kbytes.
     *
     * @return the process size in Kbytes
     */
    public long getsize() {
	return prSize;
    }

    /**
     * Return the resident size in Kbytes.
     *
     * @return the resident size in Kbytes
     */
    public long getrssize() {
	return prRssize;
    }

    /**
     * Return the start time of the process. This is measured in seconds
     * since the epoch.
     *
     * @return the start time of the process
     */
    public long getstime() {
	return stime;
    }

    /**
     * Return the execution time of the process. This is measured in seconds
     * and includes usr+sys cpu time.
     *
     * @return the execution time of the process
     */
    public double gettime() {
	return (double) etime + ((double) ntime) / 1000000000.0;
    }

    /**
     * Return the execution time of reaped children of this process. This is
     * measured in seconds and includes usr+sys cpu time.
     *
     * @return the execution time of reaped children of this process
     */
    public double getctime() {
	return (double) ectime + ((double) nctime) / 1000000000.0;
    }

    /**
     * Return the task id of the process.
     *
     * @return the task id of the process
     */
    public int gettaskid() {
	return prTaskid;
    }

    /**
     * Return the project id of the process.
     *
     * @return the project id of the process
     */
    public int getprojid() {
	return prProjid;
    }

    /**
     * Return the zone id of the process.
     *
     * @return the zone id of the process
     */
    public int getzoneid() {
	return prZoneid;
    }

    /**
     * Return the process contract.
     *
     * @return the process contract
     */
    public int getcontract() {
	return prContract;
    }

    /**
     * Return the name of the execed file of this process.
     *
     * @return the name of the execed file
     */
    public String getfname() {
	return prFname;
    }

    /**
     * Generate a JSON representation of this {@code JProcInfo}.
     *
     * @return A String containing a JSON representation of this
     * {@code JProcInfo}.
     */
    public String toJSON() {
	StringBuilder sb = new StringBuilder(256);
	sb.append("{\"fname\":\"").append(prFname)
	    .append("\",\"pid\":").append(prPid)
	    .append(",\"ppid\":").append(prPpid)
	    .append(",\"uid\":").append(prUid)
	    .append(",\"euid\":").append(prEuid)
	    .append(",\"gid\":").append(prGid)
	    .append(",\"egid\":").append(prEgid)
	    .append(",\"nlwp\":").append(prNlwp)
	    .append(",\"size\":").append(prSize)
	    .append(",\"rssize\":").append(prRssize)
	    .append(",\"stime\":").append(stime)
	    .append(",\"etime\":").append(etime)
	    .append(",\"ntime\":").append(ntime)
	    .append(",\"ectime\":").append(ectime)
	    .append(",\"nctime\":").append(nctime)
	    .append(",\"taskid\":").append(prTaskid)
	    .append(",\"projid\":").append(prProjid)
	    .append(",\"zoneid\":").append(prZoneid)
	    .append(",\"contract\":").append(prContract)
	    .append('}');
	return sb.toString();
    }
}
