/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

package uk.co.petertribble.jproc.server;

import uk.co.petertribble.jproc.api.*;
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
public class JProcServer {

    private static final JProc jproc = new JProc();

    /**
     * Return the list of processes. The returned data includes JProcInfo
     * data for each process.
     *
     * @return a JSON encoded array of processes
     */
    public String getProcesses() {
	StringBuilder sb = new StringBuilder();
	sb.append('[');
	for (JProcess jp : jproc.getProcesses()) {
	    sb.append(jproc.getInfo(jp).toJSON()).append(",\n");
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
    public String getLwps(int pid) {
	Set <JLwp> lwps = jproc.getLwps(pid);
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
    public String getInfo(int pid) {
	JProcInfo jpi = jproc.getInfo(pid);
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
    public String getLwpInfo(int pid, int lwpid) {
	JProcLwpInfo jpi = jproc.getInfo(pid, lwpid);
	return (jpi == null) ? "" : jpi.toJSON();
    }

    /**
     * Return status of the given process.
     *
     * @param pid the pid of the process to query
     *
     * @return a JSON String containing the status of the given pid
     */
    public String getStatus(int pid) {
	JProcStatus jps = jproc.getStatus(pid);
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
    public String getLwpStatus(int pid, int lwpid) {
	JProcLwpStatus jps = jproc.getStatus(pid, lwpid);
	return (jps == null) ? "" : jps.toJSON();
    }

    /**
     * Return usage details of the given process.
     *
     * @param pid the pid of the process to query
     *
     * @return a JSON String containing the usage details of the given pid
     */
    public String getUsage(int pid) {
	JProcUsage jpu = jproc.getUsage(pid);
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
    public String getLwpUsage(int pid, int lwpid) {
	JProcUsage jpu = jproc.getUsage(pid, lwpid);
	return (jpu == null) ? "" : jpu.toJSON();
    }

    /**
     * Return the user name corresponding to a given numeric uid.
     *
     * @param uid The numeric userid.
     *
     * @return The user name, or the user id if no user matches.
     */
    public String getUserName(int uid) {
	return jproc.getUserName(uid);
    }

    /**
     * Return the user id corresponding to a given username.
     *
     * @param username The username.
     *
     * @return The user id, or -1 if no user matches.
     */
    public int getUserId(String username) {
	return jproc.getUserId(username);
    }

    /**
     * Return the group name corresponding to a given numeric gid.
     *
     * @param gid The numeric groupid.
     *
     * @return The group name, or the groupid if no group matches.
     */
    public String getGroupName(int gid) {
	return jproc.getGroupName(gid);
    }

    /**
     * Return the group id corresponding to a given group name.
     *
     * @param group The group name.
     *
     * @return The group id, or -1 if no group matches.
     */
    public int getGroupId(String group) {
	return jproc.getGroupId(group);
    }

    /**
     * Return the project name corresponding to a given numeric gid.
     *
     * @param projid The numeric project id.
     *
     * @return The project name, or the project id if no project matches.
     */
    public String getProjectName(int projid) {
	return jproc.getProjectName(projid);
    }

    /**
     * Return the project id corresponding to a given project name.
     *
     * @param project The project name.
     *
     * @return The project id, or -1 if no project matches.
     */
    public int getProjectId(String project) {
	return jproc.getProjectId(project);
    }

    /**
     * Retrieves the zone name corresponding to a given numeric zone id.
     *
     * @param zoneid The numeric zone id.
     *
     * @return The zone name, or the zone id if no zone matches.
     */
    public String getZoneName(int zoneid) {
	return jproc.getZoneName(zoneid);
    }

    /**
     * Return the zone id corresponding to a given zone name.
     *
     * @param zone The zone name.
     *
     * @return The zone id, or -1 if no zone matches.
     */
    public int getZoneId(String zone) {
	return jproc.getZoneId(zone);
    }
}
