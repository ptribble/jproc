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

package uk.co.petertribble.jproc.client;

import java.util.Set;
import uk.co.petertribble.jproc.api.JLwp;
import uk.co.petertribble.jproc.api.JProcess;
import uk.co.petertribble.jproc.api.JProcException;
import uk.co.petertribble.jproc.api.JProcInfo;
import uk.co.petertribble.jproc.api.JProcLwpInfo;
import uk.co.petertribble.jproc.api.JProcLwpStatus;
import uk.co.petertribble.jproc.api.JProcStatus;
import uk.co.petertribble.jproc.api.JProcUsage;
import uk.co.petertribble.jproc.api.ProcessInterface;
import uk.co.petertribble.jproc.parse.JSONParser;
import org.apache.xmlrpc.XmlRpcException;

/**
 * An access class for Solaris /proc. Allows details on individual processes
 * to be queried.
 *
 * @author Peter Tribble
 */
public final class XmlRpcJProc extends ProcessInterface {

    private final JProcClient client;

    /**
     * Creates a new NativeJProc object.
     *
     * @param pcc a PClientConfig containing client configuration
     */
    public XmlRpcJProc(final PClientConfig pcc) {
	super();
	client = new JProcClient(pcc);
    }

    /**
     * Return a Set of all processes in the system.
     *
     * @return A Set of all the processes running on the system.
     */
    @Override
    public Set<JProcess> getProcesses() {
	try {
	    String m = (String) client.execute("getProcesses");
	    return JSONParser.getProcesses(m);
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getProcesses failed", e);
	}
    }

    /**
     * Return a Set of JLwp objects representing the lwps in this process.
     * If the process no longer exists, returns null
     *
     * @param pid the pid of the process to query
     *
     * @return A Set of JLwp objects representing the lwps in this process.
     */
    @Override
    public Set<JLwp> getLwps(final int pid) {
	try {
	    String m = (String) client.execute("getLwps",
					new Object[] {pid});
	    return JSONParser.getLwps(m);
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getLwps failed", e);
	}
    }

    /**
     * Retrieves status of a process.
     *
     * @param pid The process pid to query
     *
     * @return A new JProcStatus object populated with current data, or null
     * if the process does not exist
     */
    @Override
    public JProcStatus getStatus(final int pid) {
	try {
	    String m = (String) client.execute("getStatus", new Object[] {pid});
	    return JSONParser.getStatus(m);
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getStatus failed", e);
	}
    }

    /**
     * Retrieves status of an lwp in a process.
     *
     * @param pid The process pid to query
     * @param lwpid The id of the lwp to query
     *
     * @return A new JProcLwpStatus object populated with current data, or null
     * if the process or lwp does not exist
     */
    @Override
    public JProcLwpStatus getLwpStatus(final int pid, final int lwpid) {
	try {
	    String m = (String) client.execute("getLwpStatus",
					new Object[] {pid, lwpid});
	    return JSONParser.getLwpStatus(m);
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getLwpStatus failed", e);
	}
    }

    /**
     * Retrieves information about a process.
     *
     * @param pid The ocprocess pid to query
     *
     * @return A new JProcInfo object populated with current data, or null
     * if the process does not exist
     */
    @Override
    public JProcInfo getInfo(final int pid) {
	try {
	    String m = (String) client.execute("getInfo", new Object[] {pid});
	    return JSONParser.getInfo(m);
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getInfo failed", e);
	}
    }

    /**
     * Retrieves information about an lwp in a process.
     *
     * @param pid The process pid to query
     * @param lwpid The id of the lwp to query
     *
     * @return A new JProcLwpInfo object populated with current data, or null
     * if the process or lwp does not exist
     */
    @Override
    public JProcLwpInfo getLwpInfo(final int pid, final int lwpid) {
	try {
	    String m = (String) client.execute("getLwpInfo",
					new Object[] {pid, lwpid});
	    return JSONParser.getLwpInfo(m);
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getLwpInfo failed", e);
	}
    }

    /**
     * Retrieves usage information about a process.
     *
     * @param pid The process pid to query
     *
     * @return A new JProcUsage object populated with current data, or null
     * if the process does not exist
     */
    @Override
    public JProcUsage getUsage(final int pid) {
	try {
	    String m = (String) client.execute("getUsage", new Object[] {pid});
	    return JSONParser.getUsage(m);
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getUsage failed", e);
	}
    }

    /**
     * Retrieves usage information about an lwp in a process.
     *
     * @param pid The process pid to query
     * @param lwpid The id of the lwp to query
     *
     * @return A new JProcUsage object populated with current data, or null
     * if the process or lwp does not exist
     */
    @Override
    public JProcUsage getLwpUsage(final int pid, final int lwpid) {
	try {
	    String m = (String) client.execute("getLwpUsage",
					new Object[] {pid, lwpid});
	    return JSONParser.getUsage(m);
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getLwpUsage failed", e);
	}
    }

    /**
     * Retrieves the user name corresponding to a given numeric uid.
     *
     * @param uid The numeric userid.
     *
     * @return The user name, or null if no user matches.
     */
    @Override
    public String getUserName(final int uid) {
	try {
	    return (String) client.execute("getUserName", new Object[] {uid});
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getUserName failed", e);
	}
    }

    /**
     * Retrieves the user id corresponding to a given username.
     *
     * @param username The username.
     *
     * @return The userid, or -1 if no user matches.
     */
    @Override
    public int getUserId(final String username) {
	try {
	    return (Integer) client.execute("getUserId",
					new Object[] {username});
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getUserId failed", e);
	}
    }

    /**
     * Retrieves the group name corresponding to a given numeric gid.
     *
     * @param gid The numeric groupid.
     *
     * @return The group name, or null if no group matches.
     */
    @Override
    public String getGroupName(final int gid) {
	try {
	    return (String) client.execute("getGroupName", new Object[] {gid});
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getGroupName failed", e);
	}
    }

    /**
     * Retrieves the group id corresponding to a given group name.
     *
     * @param group The group name.
     *
     * @return The groupid, or -1 if no group matches.
     */
    @Override
    public int getGroupId(final String group) {
	try {
	    return (Integer) client.execute("getGroupId", new Object[] {group});
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getGroupId failed", e);
	}
    }

    /**
     * Retrieves the project name corresponding to a given numeric project id.
     *
     * @param projid The numeric project id.
     *
     * @return The project name, or null if no project matches.
     */
    @Override
    public String getProjectName(final int projid) {
	try {
	    return (String) client.execute("getProjectName",
					new Object[] {projid});
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getProjectName failed", e);
	}
    }

    /**
     * Retrieves the project id corresponding to a given project name.
     *
     * @param project The project name.
     *
     * @return The project id, or -1 if no project matches.
     */
    @Override
    public int getProjectId(final String project) {
	try {
	    return (Integer) client.execute("getProjectId",
					new Object[] {project});
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getProjectId failed", e);
	}
    }

    /**
     * Retrieves the zone name corresponding to a given numeric zone id.
     *
     * @param zoneid The numeric zone id.
     *
     * @return The zone name, or null if no zone matches.
     */
    @Override
    public String getZoneName(final int zoneid) {
	try {
	    return (String) client.execute("getZoneName",
					new Object[] {zoneid});
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getZoneName failed", e);
	}
    }

    /**
     * Retrieves the zone id corresponding to a given zone name.
     *
     * @param zone The zone name.
     *
     * @return The zone id, or -1 if no zone matches.
     */
    @Override
    public int getZoneId(final String zone) {
	try {
	    return (Integer) client.execute("getZoneId", new Object[] {zone});
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getZoneId failed", e);
	}
    }
}
