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

package uk.co.petertribble.jproc.client;

import java.util.Set;
import uk.co.petertribble.jproc.api.*;
import uk.co.petertribble.jproc.parse.JSONParser;
import org.apache.xmlrpc.XmlRpcException;

/**
 * An access class for Solaris /proc. Allows details on individual processes
 * to be queried.
 *
 * @author Peter Tribble
 */
public class XmlRpcJProc extends ProcessInterface {

    private JProcClient client;

    /**
     * Creates a new NativeJProc object.
     *
     * @param pcc a PClientConfig containing client configuration
     */
    public XmlRpcJProc(PClientConfig pcc) {
	super();
	client = new JProcClient(pcc);
    }

    /**
     * Return a Set of all processes in the system.
     *
     * @return A Set of all the processes running on the system.
     */
    @Override
    public Set <JProcess> getProcesses() {
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
    public Set <JLwp> getLwps(int pid) {
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
    public JProcStatus getStatus(int pid) {
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
    public JProcLwpStatus getLwpStatus(int pid, int lwpid) {
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
    public JProcInfo getInfo(int pid) {
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
    public JProcLwpInfo getLwpInfo(int pid, int lwpid) {
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
    public JProcUsage getUsage(int pid) {
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
    public JProcUsage getLwpUsage(int pid, int lwpid) {
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
    public String getUserName(int uid) {
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
    public int getUserId(String username) {
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
    public String getGroupName(int gid) {
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
    public int getGroupId(String group) {
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
    public String getProjectName(int projid) {
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
    public int getProjectId(String project) {
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
    public String getZoneName(int zoneid) {
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
    public int getZoneId(String zone) {
	try {
	    return (Integer) client.execute("getZoneId", new Object[] {zone});
	} catch (XmlRpcException e) {
	    throw new JProcException("XmlRpcJProc getZoneId failed", e);
	}
    }
}
