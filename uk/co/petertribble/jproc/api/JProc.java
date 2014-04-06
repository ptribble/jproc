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

package uk.co.petertribble.jproc.api;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import uk.co.petertribble.jproc.client.PClientConfig;
import uk.co.petertribble.jproc.client.XmlRpcJProc;
import uk.co.petertribble.jproc.client.JsonJProc;

/**
 * A class for representing Solaris processes, enumerating them, and
 * obtaining information on them.
 *
 * @author Peter Tribble
 */
public class JProc {

    /*
     * An underlying ProcessInterface to query for process information.
     */
    private ProcessInterface njp;

    /*
     * Maps to cache id to name lookups, to avoid going into native code
     * repeatedly. We also have methods to convert name to id, but those
     * aren't cached. The reason for this is that everything internal is
     * coded to use the id. The normal usage pattern is expected to be that
     * you convert from name to id once, and do a lot of work using the id,
     * but that the id is converted many times to a name for presentation.
     */
    private Map <Integer, String> uMap = new HashMap <Integer, String> ();
    private Map <Integer, String> gMap = new HashMap <Integer, String> ();
    private Map <Integer, String> projMap = new HashMap <Integer, String> ();
    private Map <Integer, String> zMap = new HashMap <Integer, String> ();

    /**
     * Create a new JProc object, that can be queried for information about
     * processes.
     */
    public JProc() {
	this(new NativeJProc());
    }

    /**
     * Create a new JProc object, that can be queried for information about
     * processes.
     *
     * @param njp a ProcessInterface to query for process information
     */
    public JProc(NativeJProc njp) {
	this.njp = njp;
    }

    /**
     * Create a new JProc object, that can be queried for information about
     * processes.
     *
     * @param pcc  The configuration specifying how to contact the server.
     */
    public JProc(PClientConfig pcc) {
	if (pcc.getProtocol() == PClientConfig.CLIENT_XMLRPC) {
	    njp = new XmlRpcJProc(pcc);
	} else if (pcc.getProtocol() == PClientConfig.CLIENT_REST) {
	    njp = new JsonJProc(pcc);
	} else {
	    throw new JProcException("Invalid remote protocol");
	}
    }

    /**
     * Return a Set of all processes in the system.
     *
     * @return A Set of all the processes running on the system.
     */
    public Set <JProcess> getProcesses() {
	return njp.getProcesses();
    }

    /**
     * Return a Set of JLwp objects representing the lwps in the given process.
     * If the process no longer exists, returns null
     *
     * @param jp the JProcess to query
     *
     * @return A Set of JLwp objects representing the lwps in this process.
     */
    public Set <JLwp> getLwps(JProcess jp) {
	return getLwps(jp.getPid());
    }

    /**
     * Return a Set of JLwp objects representing the lwps in the given process.
     * If the process no longer exists, returns null
     *
     * @param pid the pid of the process to query
     *
     * @return A Set of JLwp objects representing the lwps in this process.
     */
    public Set <JLwp> getLwps(int pid) {
	return njp.getLwps(pid);
    }

    /**
     * Return Status of the given process. The status information is
     * restricted, and is only available to the process owner or root.
     *
     * @param jp the JProcess to query
     *
     * @return A JProcStatus object containing the status of the process,
     * if available, or null if it is not available or the process no
     * longer exists.
     */
    public JProcStatus getStatus(JProcess jp) {
	return getStatus(jp.getPid());
    }

    /**
     * Return Status of the given process. The status information is
     * restricted, and is only available to the process owner or root.
     *
     * @param pid the pid of the process to query
     *
     * @return A JProcStatus object containing the status of the process,
     * if available, or null if it is not available or the process no
     * longer exists.
     */
    public JProcStatus getStatus(int pid) {
	return njp.getStatus(pid);
    }

    /**
     * Return Status of the given lwp. The status information is restricted,
     * and is only available to the process owner or root.
     *
     * @param jlwp the JLwp to query
     *
     * @return A JProcLwpStatus object containing the status of the given
     * lwp, or null if the process or lwp no longer exists.
     */
    public JProcLwpStatus getStatus(JLwp jlwp) {
	return getStatus(jlwp.getPid(), jlwp.getLWPid());
    }

    /**
     * Return Status of the given lwp. The status information is restricted,
     * and is only available to the process owner or root.
     *
     * @param pid the pid of the process to query
     * @param lwpid the lwpid to query
     *
     * @return A JProcLwpStatus object containing the status of the given
     * lwp, or null if the process or lwp no longer exists.
     */
    public JProcLwpStatus getStatus(int pid, int lwpid) {
	return njp.getLwpStatus(pid, lwpid);
    }

    /**
     * Return Information on the given process.
     *
     * @param jp the JProcess to query
     *
     * @return A JProcInfo object containing information about this process,
     * or null if this process no longer exists.
     */
    public JProcInfo getInfo(JProcess jp) {
	JProcInfo jpi = njp.getInfo(jp.getPid());
	jp.updateInfo(jpi);
	return jpi;
    }

    /**
     * Return Information on the given process.
     *
     * @param pid the pid of the process to query
     *
     * @return A JProcInfo object containing information about this process,
     * or null if this process no longer exists.
     */
    public JProcInfo getInfo(int pid) {
	return njp.getInfo(pid);
    }

    /**
     * Return Information on the given lwp.
     *
     * @param jlwp the JLwp to query
     *
     * @return A JProcLwpInfo object containing information about this lwp,
     * or null if this process or lwp no longer exists.
     */
    public JProcLwpInfo getInfo(JLwp jlwp) {
	return getInfo(jlwp.getPid(), jlwp.getLWPid());
    }

    /**
     * Return Information on the given lwp.
     *
     * @param pid the pid of the process to query
     * @param lwpid the lwpid to query
     *
     * @return A JProcLwpInfo object containing information about this lwp,
     * or null if this process or lwp no longer exists.
     */
    public JProcLwpInfo getInfo(int pid, int lwpid) {
	return njp.getLwpInfo(pid, lwpid);
    }

    /**
     * Return Usage of the given process.
     *
     * @param jp the JProcess to query
     *
     * @return A JProcUsage object containing usage information about this
     * process, or null if this process no longer exists.
     */
    public JProcUsage getUsage(JProcess jp) {
	return getUsage(jp.getPid());
    }

    /**
     * Return Usage of the given process.
     *
     * @param pid the pid of the process to query
     *
     * @return A JProcUsage object containing usage information about this
     * process, or null if this process no longer exists.
     */
    public JProcUsage getUsage(int pid) {
	return njp.getUsage(pid);
    }

    /**
     * Return Usage of the given lwp.
     *
     * @param jlwp the JLwp to query
     *
     * @return A JProcUsage object containing usage information about this
     * lwp, or null if this process or lwp no longer exists.
     */
    public JProcUsage getUsage(JLwp jlwp) {
	return getUsage(jlwp.getPid(), jlwp.getLWPid());
    }

    /**
     * Return Usage of the given lwp.
     *
     * @param pid the pid of the process to query
     * @param lwpid the lwpid to query
     *
     * @return A JProcUsage object containing usage information about this
     * lwp, or null if this process or lwp no longer exists.
     */
    public JProcUsage getUsage(int pid, int lwpid) {
	return njp.getLwpUsage(pid, lwpid);
    }

    /**
     * Return the user name corresponding to a given numeric uid. These are
     * cached to avoid frequent excursions into native code.
     *
     * @param uid The numeric userid.
     *
     * @return The user name, or the userid if no username matches.
     */
    public String getUserName(int uid) {
	if (!uMap.containsKey(uid)) {
	    String name = njp.getUserName(uid);
	    uMap.put(uid, (name == null) ? Integer.toString(uid) : name);
	}
	return uMap.get(uid);
    }

    /**
     * Return the user id corresponding to a given username.
     *
     * @param username The username.
     *
     * @return The user id, or -1 if no user matches.
     */
    public int getUserId(String username) {
	return njp.getUserId(username);
    }

    /**
     * Return the group name corresponding to a given numeric gid. These are
     * cached to avoid frequent excursions into native code.
     *
     * @param gid The numeric groupid.
     *
     * @return The group name, or the groupid if no group matches.
     */
    public String getGroupName(int gid) {
	if (!gMap.containsKey(gid)) {
	    String name = njp.getGroupName(gid);
	    gMap.put(gid, (name == null) ? Integer.toString(gid) : name);
	}
	return gMap.get(gid);
    }

    /**
     * Return the group id corresponding to a given group name.
     *
     * @param group The group name.
     *
     * @return The group id, or -1 if no group matches.
     */
    public int getGroupId(String group) {
	return njp.getGroupId(group);
    }

    /**
     * Return the project name corresponding to a given numeric gid. These are
     * cached to avoid frequent excursions into native code.
     *
     * @param projid The numeric project id.
     *
     * @return The project name, or the project id if no project matches.
     */
    public String getProjectName(int projid) {
	if (!projMap.containsKey(projid)) {
	    String name = njp.getProjectName(projid);
	    projMap.put(projid,
			(name == null) ? Integer.toString(projid) : name);
	}
	return projMap.get(projid);
    }

    /**
     * Return the project id corresponding to a given project name.
     *
     * @param project The project name.
     *
     * @return The project id, or -1 if no project matches.
     */
    public int getProjectId(String project) {
	return njp.getProjectId(project);
    }

    /**
     * Retrieves the zone name corresponding to a given numeric zone id. These
     * are cached to avoid frequent excursions into native code.
     *
     * @param zoneid The numeric zone id.
     *
     * @return The zone name, or the zone id if no zone matches.
     */
    public String getZoneName(int zoneid) {
	if (!zMap.containsKey(zoneid)) {
	    String name = njp.getZoneName(zoneid);
	    zMap.put(zoneid, (name == null) ? Integer.toString(zoneid) : name);
	}
	return zMap.get(zoneid);
    }

    /**
     * Return the zone id corresponding to a given zone name.
     *
     * @param zone The zone name.
     *
     * @return The zone id, or -1 if no zone matches.
     */
    public int getZoneId(String zone) {
	return njp.getZoneId(zone);
    }
}
