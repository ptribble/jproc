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

package uk.co.petertribble.jproc.server;

import uk.co.petertribble.jproc.api.JLwp;
import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcess;
import uk.co.petertribble.jproc.api.JProcInfo;
import uk.co.petertribble.jproc.api.JProcLwpInfo;
import uk.co.petertribble.jproc.api.JProcLwpStatus;
import uk.co.petertribble.jproc.api.JProcStatus;
import uk.co.petertribble.jproc.api.JProcUsage;
import java.util.Set;

/**
 * This is the core of the JProc server.
 *
 * XML-RPC has significant limitations on available data types. Using JSON as
 * the serialized form means we just pass Strings, avoiding the limitations.
 * If a process or LWP exits we return the empty string.
 *
 * @author Peter Tribble
 */
public final class JProcServer {

    private static final JProc JPROC = new JProc();

    /**
     * Return the list of processes. The returned data includes JProcInfo
     * data for each process.
     *
     * @return a JSON encoded array of processes
     */
    public String getProcesses() {
	StringBuilder sb = new StringBuilder();
	sb.append('[');
	for (JProcess jp : JPROC.getProcesses()) {
	    sb.append(JPROC.getInfo(jp).toJSON()).append(",\n");
	}
	sb.append(']');
	return sb.toString();
    }

    /**
     * Return the list of lwps for the given process.
     *
     * @param pid the pid of the process to query
     *
     * @return a JSON encoded array of lwps
     */
    public String getLwps(final int pid) {
	Set<JLwp> lwps = JPROC.getLwps(pid);
	if (lwps == null) {
	    return "";
	}
	StringBuilder sb = new StringBuilder();
	sb.append('[');
	for (JLwp jlwp : lwps) {
	    sb.append(jlwp.toJSON()).append(',');
	}
	sb.append(']');
	return sb.toString();
    }

    /**
     * Return info on the given process.
     *
     * @param pid the pid of the process to query
     *
     * @return a JSON String containing info on the given pid
     */
    public String getInfo(final int pid) {
	JProcInfo jpi = JPROC.getInfo(pid);
	return (jpi == null) ? "" : jpi.toJSON();
    }

    /**
     * Return info on the given lwp.
     *
     * @param pid the pid of the process to query
     * @param lwpid the id of the lwp
     *
     * @return a JSON String containing info on the given pid
     */
    public String getLwpInfo(final int pid, final int lwpid) {
	JProcLwpInfo jpi = JPROC.getInfo(pid, lwpid);
	return (jpi == null) ? "" : jpi.toJSON();
    }

    /**
     * Return status of the given process.
     *
     * @param pid the pid of the process to query
     *
     * @return a JSON String containing the status of the given pid
     */
    public String getStatus(final int pid) {
	JProcStatus jps = JPROC.getStatus(pid);
	return (jps == null) ? "" : jps.toJSON();
    }

    /**
     * Return status of the given lwp.
     *
     * @param pid the pid of the process to query
     * @param lwpid the id of the lwp
     *
     * @return a JSON String containing the status of the given pid
     */
    public String getLwpStatus(final int pid, final int lwpid) {
	JProcLwpStatus jps = JPROC.getStatus(pid, lwpid);
	return (jps == null) ? "" : jps.toJSON();
    }

    /**
     * Return usage details of the given process.
     *
     * @param pid the pid of the process to query
     *
     * @return a JSON String containing the usage details of the given pid
     */
    public String getUsage(final int pid) {
	JProcUsage jpu = JPROC.getUsage(pid);
	return (jpu == null) ? "" : jpu.toJSON();
    }

    /**
     * Return usage details of the given lwp.
     *
     * @param pid the pid of the process to query
     * @param lwpid the id of the lwp
     *
     * @return a JSON String containing the usage details of the given pid and
     * lwp
     */
    public String getLwpUsage(final int pid, final int lwpid) {
	JProcUsage jpu = JPROC.getUsage(pid, lwpid);
	return (jpu == null) ? "" : jpu.toJSON();
    }

    /**
     * Return the user name corresponding to a given numeric uid.
     *
     * @param uid The numeric userid.
     *
     * @return The user name, or the user id if no user matches.
     */
    public String getUserName(final int uid) {
	return JPROC.getUserName(uid);
    }

    /**
     * Return the user id corresponding to a given username.
     *
     * @param username The username.
     *
     * @return The user id, or -1 if no user matches.
     */
    public int getUserId(final String username) {
	return JPROC.getUserId(username);
    }

    /**
     * Return the group name corresponding to a given numeric gid.
     *
     * @param gid The numeric groupid.
     *
     * @return The group name, or the groupid if no group matches.
     */
    public String getGroupName(final int gid) {
	return JPROC.getGroupName(gid);
    }

    /**
     * Return the group id corresponding to a given group name.
     *
     * @param group The group name.
     *
     * @return The group id, or -1 if no group matches.
     */
    public int getGroupId(final String group) {
	return JPROC.getGroupId(group);
    }

    /**
     * Return the project name corresponding to a given numeric gid.
     *
     * @param projid The numeric project id.
     *
     * @return The project name, or the project id if no project matches.
     */
    public String getProjectName(final int projid) {
	return JPROC.getProjectName(projid);
    }

    /**
     * Return the project id corresponding to a given project name.
     *
     * @param project The project name.
     *
     * @return The project id, or -1 if no project matches.
     */
    public int getProjectId(final String project) {
	return JPROC.getProjectId(project);
    }

    /**
     * Retrieves the zone name corresponding to a given numeric zone id.
     *
     * @param zoneid The numeric zone id.
     *
     * @return The zone name, or the zone id if no zone matches.
     */
    public String getZoneName(final int zoneid) {
	return JPROC.getZoneName(zoneid);
    }

    /**
     * Return the zone id corresponding to a given zone name.
     *
     * @param zone The zone name.
     *
     * @return The zone id, or -1 if no zone matches.
     */
    public int getZoneId(final String zone) {
	return JPROC.getZoneId(zone);
    }
}
